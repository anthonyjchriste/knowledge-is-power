/*
 * Copyright 2012 Christe, Anthony
 * 
 * This file is part of KiP.
 *
 * KiP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * KiP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with KiP.  If not, see <http://www.gnu.org/licenses/>.
 */

package kip.ssl;

import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.*;

/**
 * Connect to real time DB over SSL as an alternative to the local data store
 * @author Christe, Anthony
 */
public class SSLClient {
    private SSLSocketFactory socketFactory = null;
    private SSLSocket socket = null;
    private SSLContext sslContext = null;
    private KeyManagerFactory keyFactory = null;
    private TrustManagerFactory trustFactory = null;
    private KeyStore keyStore = null;
    private KeyStore trustStore = null;
    private BufferedWriter out = null;
    private BufferedReader in = null;
    private String clientKeyStore = null;
    private String clientTrustStore = null;
    private String host = null;
    private int port = -1;
    private char[] passphrase;

    /**
     * Construct the SSLClient
     * @param host              the real time database host or ip address
     * @param port              the real time database's listening port
     * @param clientKeyStore    the location of the client key store
     * @param clientTrustStore  the locations of the client trust store
     * @param trustKeyStore     the location of the client trust key store
     * @param passphrase        the passphrase needed for keystores
     * @throws IOException      
     */
    public SSLClient(String host, int port, String clientKeyStore, String clientTrustStore,
                     String trustKeyStore, String passphrase) throws IOException {
        this.host = host;
        this.port = port;
        this.passphrase = passphrase.toCharArray();
        this.clientKeyStore = clientKeyStore;
        this.clientTrustStore = clientTrustStore;

        try {
            initSSL();
        } catch (Exception e) {
            System.err.println("Error intializing SSL connection");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } 
    }

    /**
     * Set up SSL context, SSL socket, and I/O streams
     * @throws Exception 
     */
    private void initSSL() throws Exception {
        sslContext = SSLContext.getInstance("TLS");
        keyFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        trustFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        keyStore = KeyStore.getInstance("JKS");
        trustStore = KeyStore.getInstance("JKS");

        keyStore.load(new FileInputStream(clientKeyStore), passphrase);
        trustStore.load(new FileInputStream(clientTrustStore), passphrase);
        keyFactory.init(keyStore, passphrase);
        trustFactory.init(trustStore);
        sslContext.init(keyFactory.getKeyManagers(),
                        trustFactory.getTrustManagers(), null);

        socketFactory = sslContext.getSocketFactory();
        socket = (SSLSocket) socketFactory.createSocket(host, port);

        socket.startHandshake();

        out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Write packets to SSL server
     * @param data an array of bytes to write to SSL server
     * @throws IOException 
     */
    public void writeToServer(byte[] data) throws IOException {
        writeToServer(new String(data));
    }

    /**
     * Write data to SSL server
     * @param msg an array of characters to write to server
     * @throws IOException 
     */
    public void writeToServer(char[] msg) throws IOException {
        out.write(msg);
        out.flush();
    }

    /**
     * Write data to SSL server
     * @param msg writes a string message to SSL server
     * @throws IOException 
     */
    public void writeToServer(String msg) throws IOException {
        out.write(msg);
        out.flush();
    }

    /**
     * Reads a string from SSL server
     * @return a string from SSL server
     * @throws IOException 
     */
    public String readFromServer() throws IOException {
        String line;
        String result = "";

        while ((line = in.readLine()) != null) {
            result = line + "\n";
        }

        return result;
    }

    /**
     * Reads data from SSL server
     * @param buf array to read data into from SSL server
     * @throws IOException 
     */
    public void readFromServer(char[] buf) throws IOException {
        in.read(buf);
    }
    /*
     * public static void main(String[] args) { try { //new SSLClient(args[0],
     * Integer.parseInt(args[1])); } catch (IOException e) {
     * System.err.println("IOException"); e.printStackTrace(); } }
     */
}
