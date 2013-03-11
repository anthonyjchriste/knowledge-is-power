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

package kip.emulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import kip.utils.Constants;
import kip.utils.PacketUtils;

/**
 * Emulates UDP packets from KiP board
 * @author Christe, Anthony
 */
public class KiPEmulator {
    private static byte unitID = -1;
    private static byte nextID = -1;
    private static int port = -1;
    private static DatagramSocket socket = null;
    private static Random rand = new Random();

    public static void main(String[] args) {
        // Grab the port number to start emulator
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Unrecognized port");
            }
        } else {
            System.err.println("Port not specified");
        }
        
        // Start the server
        try {
            startServer();
        } catch (IOException e) {
            System.err.println("Problem with server");
            e.printStackTrace();
        }
    }

    /**
     * Start server and wait for packets from KiP client
     * @throws IOException 
     */
    private static void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("Server started on port " + port);
        while (true) {
            byte[] buffer = new byte[Constants.CLIENT_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            handlePacket(packet);
        }
    }

    /**
     * Check packet validity, parse command and arguments, acts on command.
     * 
     * If the command is a set id command, update board id.
     * If the command is a trigger command, take DAC measurement, and to client.
     * @param packet the packet being handled
     */
    private static void handlePacket(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] data = packet.getData();
        String validity = PacketUtils.isValidPacket(data,
                                                    Constants.CLIENT_PACKET) ? "[VALID]" : "[INVALID]";
        byte command = data[Constants.CLIENT_COMMAND_INDEX];
        byte arg1 = data[Constants.CLIENT_ARG1_INDEX];
        byte[] arg2 = new byte[6];
        
        System.arraycopy(data, Constants.CLIENT_ARG2_START_INDEX, arg2, 0, 6);
        System.out.println(
                "Received " + validity + " packet from " + address + ":" + port + "\n\tCMD=" + command + "\n\tARG1=" + arg1 + "\n\tARG2=" + java.util.Arrays.toString(arg2) + "\n\t" + PacketUtils.generateTimestamp() + "\n");

        switch (command) {
            case Constants.CLIENT_CMD_SET_ID:
                setID(data[Constants.CLIENT_ARG1_INDEX]);
                break;
            case Constants.CLIENT_CMD_TRIGGER:
                sendData(address, port);
                break;
            case Constants.CLIENT_CMD_RESET:
                reset();
                break;
            default:
                System.out.println("Unknown command in client packet");
        }
    }

    /**
     * Create a packet emulated DAC measurements and send to client
     * @param address   the ip or host address of the client
     * @param port      the listening port of the client
     */
    private static void sendData(InetAddress address, int port) {
        System.out.println("Responding to " + address + ":" + port + "\n\t" + PacketUtils.generateTimestamp() + "\n");
        int currentAmp = getRandomData(500, 20);
        int currentPhase = getRandomData(0, 180);
        byte[] buffer = PacketUtils.createServerPacket(getID(),
                                                       generateSineWave(getRandomData(
                768, 5), getRandomData(0, 180)), generateSineWave(currentAmp,
                                                                  currentPhase),
                                                       generateSineWave(
                currentAmp, currentPhase + 180));
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                                                   address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending packet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates a sine wave with a selected amplitude and phase
     * @param amp   the amplitude of the sine way
     * @param phase the phase of the sine wave
     * @return      samples of sine wave
     */
    public static int[] generateSineWave(int amp, int phase) {
        int[] vals = new int[64];
        int sample;

        for (int i = 0; i < vals.length; i++) {
            sample = (int) (amp * Math.sin(Math.toRadians(
                    (2 * Math.PI) * i + phase)));
            
            // If x <= 0, truncate to 0. This is a limitation of the hardware
            // on the KiP board
            vals[i] = (sample >= 0) ? sample : 0;
        }

        return vals;
    }

    /**
     * Return a random integer close to a certain base number with an allowed 
     * amount of jitter.
     * 
     * The random number N is generated as follows:
     * (base - jitter) >= N <= (base + jitter)
     * @param base      the starting point to gernerate a random integer with
     * @param jitter    the amount of postive or negative jitter to randomly applet
     * @return          a random number between base - jitter and base + jitter 
     */
    private static int getRandomData(int base, int jitter) {
        if (rand.nextDouble() > 0.5) {
            base += rand.nextInt(jitter);
        } else {
            base -= rand.nextInt(jitter);
        }
        return base;
    }
    
    /**
     * Return the id the board
     * @return the id of the board
     */
    private static byte getID() {
        return unitID;
    }

    /**
     * Set the id of the board
     * 
     * Changes will take effect after a board reset
     * @param id the id to set the board to
     */
    private static void setID(byte id) {
        System.out.println("Setting id = " + id + "\nChanges will take effect after board reset.");
        nextID = id;
    }
    
    /*
     * Resets the board
     */
    private static void reset() {
        System.out.println("Resetting board");
        unitID = nextID;
        System.out.println("id = " + unitID);
    
    }
}
