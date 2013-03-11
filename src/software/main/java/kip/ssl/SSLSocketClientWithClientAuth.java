
package kip.ssl;

import java.io.*;
import javax.net.ssl.*;
import java.security.KeyStore;

/*
 * This example shows how to set up a key manager to do client authentication if
 * required by server.
 *
 * This program assumes that the client is not inside a firewall. The
 * application can be modified to connect to a server outside the firewall by
 * following SSLSocketClientWithTunneling.java.
 */
public class SSLSocketClientWithClientAuth {
    public static void main(String[] args) throws Exception {
        String host = null;
        int port = -1;
        String msg = null;

        if (args.length < 3) {
            System.out.println(
                    "USAGE: java SSLSocketClientWithClientAuth host port message");
            System.exit(-1);
        }

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            msg = args[2];
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "USAGE: java SSLSocketClientWithClientAuth host port message");
            System.exit(-1);
        }

        try {
            /*
             * Set up a key manager for client authentication if asked by the
             * server. Use the implementation's default TrustStore and
             * secureRandom routines.
             */
            SSLSocketFactory factory = null;
            try {
                SSLContext ctx;
                KeyManagerFactory kmf;
                TrustManagerFactory tmf;
                KeyStore ks;
                KeyStore ts;

                char[] passphrase = "123456".toCharArray();

                ctx = SSLContext.getInstance("TLS");

                kmf = KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm());//"SunX509");
                ks = KeyStore.getInstance("JKS");
                ks.load(new FileInputStream("/home/anthony/clientKeyStore.jks"),
                        passphrase);

                //java.util.Enumeration<String> aliases = ks.aliases();

                /*
                 * while(aliases.hasMoreElements()) {
                 * System.out.println(aliases.nextElement()); }
                 */

                kmf.init(ks, passphrase);

                tmf = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());//"SunX509");//TrustManagerFactory.getDefaultAlgorithm());
                ts = KeyStore.getInstance("JKS");
                ts.load(new FileInputStream("/home/anthony/clientTrustStore.jks"),
                        passphrase);
                tmf.init(ts);

                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }


            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

            /*
             * send http request
             *
             * See SSLSocketClient.java for more information about why there is
             * a forced handshake here when using PrintWriters.
             */
            socket.startHandshake();

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
            //out.println(msg);
            //out.println();
            //out.flush();

            /*
             * Make sure there were no surprises
             */
            //if (out.checkError())
            //	System.out.println("SSLSocketClient: java.io.PrintWriter error");

            /*
             * read response
             */
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    socket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
