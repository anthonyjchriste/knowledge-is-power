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

package kip.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import kip.utils.ConfigurationManager;
import kip.utils.Constants;
import kip.utils.PacketUtils;

/**
 * Thread that listens for packets from board
 * @author Christe, Anthony
 */
public class UDPServerWorker extends SwingWorker<Integer, String> {
    private int packetsReceived = 0;
    private JTextArea status = null;
    private DatagramSocket socket = null;
    private ClientActionManager cam = null;
    
    /**
     * Constructs a new udp server thread
     * @param socket    socket object for connection to board
     * @param status    reference to status window for updating from thread
     * @param cam       reference to ClientActionManager
     */
    public UDPServerWorker(DatagramSocket socket, JTextArea status,
                           ClientActionManager cam) {
        this.socket = socket;
        this.status = status;
        this.cam = cam;
    }

    /**
     * Sit and wait for packets
     * @return result of thread execution
     * @throws Exception don't like Swing thread manager eat exception 
     */
    @Override
    protected Integer doInBackground() throws Exception {
        try {
            receivePacket();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return packetsReceived;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            status.append(
                    "[" + PacketUtils.generateTimestamp() + "] " + message + "\n");
        }
    }

    /**
     * Receive packets and hand them off to packet handler
     */
    private void receivePacket() {
        byte[] buffer = new byte[Constants.SERVER_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            while (true) {
                socket.receive(packet);
                packetsReceived++;
                handlePacket(packet);
            }
        } catch (SocketException e) {
            publish("Problem creating socket");
            publish(e.getMessage());

            for (StackTraceElement ste : e.getStackTrace()) {
                publish(ste.toString());
            }

        } catch (IOException e) {
            publish("Problem receiving response");
        }
    }
    
    /**
     * Display packet data to status window and let the client action manager
     * know that we've received a packet
     * @param packet 
     */
    private void handlePacket(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] data = packet.getData();
        byte id = data[Constants.SERVER_ID_INDEX];
        String validity = PacketUtils.isValidPacket(data, Constants.SERVER_PACKET) ? "[VALID]" : "[INVALID]";
        String debug = (ConfigurationManager.debug()) ? "\n\t[DEBUG][size " + packet.getLength() + "]" + java.util.Arrays.toString(data) : "";
        //debug += (ConfigurationManager.debug()) ? "\n\t[DEBUG][size " + packet.getLength() + "]" + java.util.Arrays.toString(data) : "";        
        publish("<RCV> " + validity + " " + address + ":" + port + " ID=" + id + debug);
        cam.packetReceived(new Packet(Calendar.getInstance(), data));
    }
}
