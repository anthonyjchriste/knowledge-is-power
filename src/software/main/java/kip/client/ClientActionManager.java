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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JTextArea;
import kip.client.ui.RealTimePanel;

/**
 * Central point that controls the actions and access to the local history between
 * several different UI components.
 * @author Christe, Anthony
 */
public class ClientActionManager {
    private DatagramSocket socket = null;
    private PacketHistory packetHistory = null;

    /**
     * Creates a new client action manager
     */
    public ClientActionManager() {
        packetHistory = new PacketHistory();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Unable to create socket");
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Creates a client worker thread that sends a packet tellings the board to set its delay
     * @param addressStr    the ip or host address of the board
     * @param portStr       the port that the board is listening on
     * @param newDelayStr   the delay in milliseconds to set on the board
     * @param status        reference to main status window for thread to update
     */
    public void sendDelay(String addressStr, String portStr, String newDelayStr, JTextArea status) {
        UDPClientWorker clientWorker;
        InetAddress address = null;
        int port = -1;
        byte id = -1;
        
        // See protocol for information on how 16 bit word is stored
        byte high = 0;
        byte low = 0;
        short delay = Short.parseShort(newDelayStr);
        
        low = (byte) (low | delay);
        delay = (short) (delay >>> 8);
        high = (byte) (high | delay);
        
        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);

            clientWorker = new UDPClientWorker(address, port,
                    high, low, socket, status);
            clientWorker.execute();
        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port or id\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    /**
     * Creates a client worker thread that sends a packet tellings the board to set its MAC address
     * @param addressStr    the ip or host address of the board
     * @param portStr       the port that the board is listening on
     * @param newMACStr     new MAC address to set on board ##:##:##:##:##:##
     * @param status        reference to main status window for thread to update
     */
    public void sendMAC(String addressStr, String portStr, String newMACStr, JTextArea status) {
        UDPClientWorker clientWorker;
        InetAddress address = null;
        int port = -1;
        byte id = -1;
        
        String[] splitStr = newMACStr.trim().split(":");
        byte[] addrBytes = new byte[splitStr.length];
        
        for(int i = 0; i < splitStr.length; i++) {
            addrBytes[i] = Byte.decode("0x" + splitStr[i]);
        }
                
        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);

            clientWorker = new UDPClientWorker(address, port, false,
                    addrBytes[0], addrBytes[1], addrBytes[2], addrBytes[3], 
                    addrBytes[4], addrBytes[5], socket, status);
            clientWorker.execute();
        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port or id\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    /**
     * Creates a client worker thread that sends a packet tellings the board to set its IP address
     * @param addressStr    the ip or host address of the board
     * @param portStr       the board that the board is listening on
     * @param newIPStr      the new IP address to set on the board #.#.#.#
     * @param status        reference to main status window for thread to update
     */
    public void sendIP(String addressStr, String portStr, String newIPStr, JTextArea status) {
        UDPClientWorker clientWorker;
        InetAddress address = null;
        int port = -1;
        byte id = -1;
        
        String[] splitAddr = newIPStr.trim().split("\\.");
        byte[] addrBytes = new byte[splitAddr.length];
        
        for(int i = 0; i < splitAddr.length; i++) {
            addrBytes[i] = Byte.parseByte(splitAddr[i]);
        }
        
        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);
            
            clientWorker = new UDPClientWorker(address, port, true,
                    addrBytes[0], addrBytes[1], addrBytes[2], addrBytes[3], (byte) 0, (byte) 0,
                    socket, status);
            
            clientWorker.execute();
            
        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port or id\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    
    /**
     * Creates a client worker thread that sends and sets the id on the board
     *
     * @param addressStr the ip or host address of the board
     * @param portStr    the board's listening port
     * @param idStr      the id to set on the board
     * @param status     reference to main status window for thread to update
     */
    public void sendId(String addressStr, String portStr, String idStr,
                       JTextArea status) {
        UDPClientWorker clientWorker;
        InetAddress address = null;
        int port = -1;
        byte id = -1;
        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);
            id = Byte.parseByte(idStr);

            clientWorker = new UDPClientWorker(address, port, id, socket, status);
            clientWorker.execute();
        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port or id\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    /**
     * Creates a client work thread that will send a reset packet to the board
     * @param addressStr    the board's listening port
     * @param portStr       the port that the board is listening on
     * @param status        reference to main status window for thread to update
     */
    public void sendReset(String addressStr, String portStr,
                       JTextArea status) {
        UDPClientWorker clientWorker;
        InetAddress address = null;
        int port = -1;
        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);
            

            clientWorker = new UDPClientWorker(address, port, true, socket, status);
            clientWorker.execute();
        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port or id\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    /**
     * Send a specified amount of trigger packets to the board
     * @param addressStr    the ip or host address of the board
     * @param portStr       the board's listening port
     * @param hertzStr      how many times per second to poll the board
     * @param secStr        how many seconds should the board be polled (-1 for infinite)
     * @param status        reference to main window for thread to update
     * @param btnCancel     reference to cancel button to get action performed
     */
    public void sendTriggers(String addressStr, String portStr, String hertzStr,
                             String secStr, JTextArea status, JButton btnCancel) {
        java.net.InetAddress address = null;
        int port = -1;
        int hertz = -1;
        int sec = -1;

        try {
            address = java.net.InetAddress.getByName(addressStr);
            port = Integer.parseInt(portStr);
            hertz = Integer.parseInt(hertzStr);
            sec = Integer.parseInt(secStr);

            final UDPClientWorker clientWorker = new UDPClientWorker(address,
                                                                     port, hertz,
                                                                     sec, socket,
                                                                     status);
            clientWorker.execute();

            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clientWorker.cancel(true);
                }
            });

        } catch (java.net.UnknownHostException e) {
            status.append("Can not connect to host\n");
        } catch (NumberFormatException e) {
            status.append("Unknown port, hertz, durations, or channel\n");
        } catch (Exception e) {
            status.append(e.getMessage());
        }
    }
    
    /**
     * Stored received board packet to local history
     * @param packet the packet being stored
     */
    public synchronized void packetReceived(Packet packet) {
        packetHistory.add(packet);
        //realTimePanel.updatePanels(packet);
    }
    
    /**
     * Starts the UDP Server thread
     * @param status reference to status window for thread updates
     */
    public void startServerWorker(JTextArea status) {
        new UDPServerWorker(socket, status, this).execute();
    }
    
    /**
     * Starts thread that updates the real time plots
     * @param realTimePanel reference to realTimePanel for updates
     */
    public synchronized void startRealTimePlotWorker(RealTimePanel realTimePanel) {
        new RealTimePlotWorker(realTimePanel, packetHistory).execute();
    }
    
    /**
     * Returns entire local packet history
     * @return the entire local packet history
     */
    public synchronized PacketHistory getPacketHistory() {
        return packetHistory;
    }
}
