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
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import kip.utils.ConfigurationManager;
import kip.utils.Constants;
import kip.utils.PacketUtils;

/**
 * Thread class to send UDP packets from client to board
 * @author Christe, Anthony
 */
public class UDPClientWorker extends SwingWorker<Integer, String> {
    private DatagramSocket socket = null;
    private InetAddress serverAddress = null;
    private int serverPort = -1;
    private int packetsSent = 0;
    private byte id = 0;
    private int hertz = 0;
    private int sec = 0;
    private boolean doSendId = false;
    private boolean doSendIP = false;
    private boolean IPorMAC = false; //TODO: Messy, fix
    private boolean doSendReset = false;
    private boolean doSendDelay = false;
    private byte arg2_0, arg2_1, arg2_2, arg2_3, arg2_4, arg2_5;
    private JTextArea status = null;
    
    /**
     * Constructor used when sending ID to board
     * @param serverAddress the host or ip address to board
     * @param serverPort    the board's listening port
     * @param id            the id to set on the board
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    public UDPClientWorker(InetAddress serverAddress, int serverPort, byte id,
                           DatagramSocket socket, JTextArea status) {
        init(serverAddress, serverPort, socket, status);
        this.id = id;
        doSendId = true;
    }
    
    /**
     * Constructor used when sending a reset command to the board
     * @param serverAddress the host or ip address to board
     * @param serverPort    the board's listening port
     * @param doSendReset   boolean value indicating if we're sending a reset
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    public UDPClientWorker(InetAddress serverAddress, int serverPort, boolean doSendReset,
                           DatagramSocket socket, JTextArea status) {
        init(serverAddress, serverPort, socket, status);
        this.doSendReset = true;
    }
    
    /**
     * Constructor used when sending triggers to board
     * @param serverAddress the host or ip address to board
     * @param serverPort    the board's listening port
     * @param hertz         how many times per second to send a trigger packet
     * @param sec           how many seconds to trigger board
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    public UDPClientWorker(InetAddress serverAddress, int serverPort, int hertz,
                           int sec, DatagramSocket socket, JTextArea status) {
        init(serverAddress, serverPort, socket, status);
        this.hertz = hertz;
        this.sec = sec;
        doSendId = false;
    }
    
    /**
     * Constructor used for sending a delay to the board
     * @param serverAddress the host or ip address to board
     * @param serverPort    the port that the boar is listening on
     * @param high          the high byte of 16 bit word (see protocol)
     * @param low           the low byte of 16 bit word (see protocol)
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    public UDPClientWorker(InetAddress serverAddress, int serverPort, byte high, byte low, DatagramSocket socket, JTextArea status) {
        init(serverAddress, serverPort, socket, status);
        doSendDelay = true;
        arg2_0 = high;
        arg2_1 = low;
        arg2_2 = (byte) 0;
        arg2_3 = (byte) 0;
        arg2_4 = (byte) 0;
        arg2_5 = (byte) 0;
    }
    
    /**
     * Constructor used for sending new IP or MAC to board
     * @param serverAddress the host or ip address to board
     * @param serverPort    the port that the board is listening on
     * @param doSendIP      boolean value if we're sending IP (true) or MAC (false)
     * @param arg2_0        cmd argument 2 index 0
     * @param arg2_1        cmd argument 2 index 1
     * @param arg2_2        cmd argument 2 index 2
     * @param arg2_3        cmd argument 2 index 3
     * @param arg2_4        cmd argument 2 index 4
     * @param arg2_5        cmd argument 2 index 5
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    public UDPClientWorker(InetAddress serverAddress, int serverPort, 
                                                      boolean doSendIP, 
                                                      byte arg2_0, byte arg2_1,
                                                      byte arg2_2, byte arg2_3,
                                                      byte arg2_4, byte arg2_5, 
                                                      DatagramSocket socket,
                                                      JTextArea status) {
        
        init(serverAddress, serverPort, socket, status);
        this.doSendIP = doSendIP;
        this.IPorMAC = true;
        this.arg2_0 = arg2_0;
        this.arg2_1 = arg2_1;
        this.arg2_2 = arg2_2;
        this.arg2_3 = arg2_3;
        this.arg2_4 = arg2_4;
        this.arg2_5 = arg2_5;
    }
    
    /**
     * Stores instance variables passed in from constructor
     * @param serverAddress the host or ip address to board
     * @param serverPort    the board's listening port
     * @param socket        the socket being used to send the packet
     * @param status        reference to status window for updates from this thread
     */
    private void init(InetAddress serverAddress, int serverPort,
                      DatagramSocket socket, JTextArea status) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.socket = socket;
        this.status = status;
    }

    /**
     * Either send id packet or trigger packet
     * @return result of thread completion
     * @throws Exception don't allow Swing thread manager to eat exceptions
     */
    @Override
    protected Integer doInBackground() throws Exception {
        try {
            if(doSendReset) {
                sendPacket(PacketUtils.createClientPacket(Constants.CLIENT_CMD_RESET, (byte) 0, 
                                                          (byte) 0, (byte) 0, (byte) 0, 
                                                          (byte) 0, (byte) 0, (byte) 0));
            } else if (doSendId) {
                sendPacket(PacketUtils.createClientPacket(
                        Constants.CLIENT_CMD_SET_ID, id, 
                        (byte) 0, (byte) 0, (byte) 0, 
                        (byte) 0, (byte) 0, (byte) 0));
            } else if(IPorMAC && doSendIP) {
                sendPacket(PacketUtils.createClientPacket(Constants.CLIENT_CMD_SET_IP, (byte) 0, arg2_0,
                                                          arg2_1, arg2_2, arg2_3,
                                                          arg2_4, arg2_5));
            } else if(IPorMAC && !doSendIP) {
                sendPacket(PacketUtils.createClientPacket(Constants.CLIENT_CMD_SET_MAC, (byte) 0, 
                                                          arg2_0, arg2_1, arg2_2, 
                                                          arg2_3, arg2_4, arg2_5));
            } else if(doSendDelay) {
                sendPacket(PacketUtils.createClientPacket(Constants.CLIENT_CMD_SET_DELAY, (byte) 0, 
                                                          arg2_0, arg2_1, arg2_2, 
                                                          arg2_3, arg2_4, arg2_5));
            } else {
                sendTriggers();
            }
        } catch (InterruptedException e) {
        }

        return packetsSent;
    }

    /**
     * Push messages to status window
     * @param chunks the chunks of string to send to status window
     */
    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            status.append(
                    "[" + PacketUtils.generateTimestamp() + "] " + message + "\n");
        }
    }

    /**
     * Create and send new datagram packet.
     * @param data the data to insert into UDP packet
     */
    protected synchronized void sendPacket(byte[] data) {        
        DatagramPacket packet = new DatagramPacket(data, data.length,
                                                   serverAddress, serverPort);
        String debug = (ConfigurationManager.debug()) ? ("\n\t[DEBUG][size " + packet.getLength() + "]" + Arrays.toString(
                data)) : "";
        
        try {
            publish("<SND> " + serverAddress + ":" + serverPort + debug);
            socket.send(packet);
        } catch (IOException e) {
            publish("Problem sending packet");
        }
        packetsSent++;
    }

    /**
     * Send trigger packets to board.
     * @throws InterruptedException 
     */
    private void sendTriggers() throws InterruptedException {
        int cnt = 0;
        int duration = (sec > 0) ? sec : 1;

        while (cnt < hertz * duration) {
            sendPacket(PacketUtils.createClientPacket(
                    Constants.CLIENT_CMD_TRIGGER, (byte) 0, 
                    (byte) 0, (byte) 0,(byte) 0, (byte) 0, (byte) 0, (byte) 0));

            if (sec >= 0) {
                cnt++;
            }

            Thread.sleep(1000 / hertz);
        }
    }
}
