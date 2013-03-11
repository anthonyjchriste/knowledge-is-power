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

package kip.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Utility methods for packets.
 *
 * Provides packet validation, creation, and inspection methods.
 *
 * @author Christe, Anthony
 */
public class PacketUtils {
    /**
     * Determines if a packet is valid by checking its length, header, footer,
     * and checksum;
     *
     * @param data       the data stored in the packet
     * @param packetType the type of packet being verified (server or client)
     * @return validity of packet
     */
    public static boolean isValidPacket(byte[] data, final int packetType) {
        if (packetType != Constants.SERVER_PACKET && packetType != Constants.CLIENT_PACKET) {
            return false;
        }

        boolean server = (packetType == Constants.SERVER_PACKET);
        int size = (server) ? Constants.SERVER_PACKET_SIZE : Constants.CLIENT_PACKET_SIZE;
        byte header = (server) ? Constants.SERVER_HEADER : Constants.CLIENT_HEADER;
        int headerIndex = (server) ? Constants.SERVER_HEADER_INDEX : Constants.CLIENT_HEADER_INDEX;
        byte footer = (server) ? Constants.SERVER_FOOTER : Constants.CLIENT_FOOTER;
        int footerIndex = (server) ? Constants.SERVER_FOOTER_INDEX : Constants.CLIENT_FOOTER_INDEX;
        int checksumIndex = (server) ? Constants.SERVER_CHECKSUM_INDEX : Constants.CLIENT_CHECKSUM_INDEX;

        if (data.length != size) {
            System.out.println("0");
            System.out.println(data.length + " " + size);
            return false;
        }
        if (data[headerIndex] != header) {
            System.out.println(1);
            return false;
        }
        if (data[footerIndex] != footer) {
            System.out.println(data[footerIndex] + " " + footer);
            System.out.println(2);
            return false;
        }
        if (data[checksumIndex] != checksum(data, packetType)) {
            System.out.println(3);
            return false;
        }
        return true;
    }

    /**
     * Computes the checksum of the packet.
     *
     * Sum all fields in packet except checksum field
     *
     * @param data       the data stored in the packet
     * @param packetType the type of packet
     * @return checksum of packet
     */
    public static byte checksum(byte[] data, final int packetType) {
        byte sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        if (packetType == Constants.SERVER_PACKET) {
            sum -= data[Constants.SERVER_CHECKSUM_INDEX];
        } else {
            sum -= data[Constants.CLIENT_CHECKSUM_INDEX];
        }

        return sum;
    }

    /**
     * Creates client packet based off of command and arguments
     *
     * @param command   the command to sent the board
     * @param arg1      the first argument associated with the command
     * @param arg2_0    Index 0 of second argument
     * @param arg2_1    Index 1 of second argument
     * @param arg2_2    Index 2 of second argument
     * @param arg2_3    Index 3 of second argument
     * @param arg2_4    Index 4 of second argument
     * @param arg2_5    Index 5 of second argument
     * @return          client data ready to insert into UDP packet
     */
    public static synchronized byte[] createClientPacket(byte command, byte arg1,
                                                         byte arg2_0,
                                                         byte arg2_1,
                                                         byte arg2_2,
                                                         byte arg2_3,
                                                         byte arg2_4,
                                                         byte arg2_5) {
        byte[] packet = new byte[Constants.CLIENT_PACKET_SIZE];

        packet[Constants.CLIENT_HEADER_INDEX] = Constants.CLIENT_HEADER;
        packet[Constants.CLIENT_COMMAND_INDEX] = command;
        packet[Constants.CLIENT_ARG1_INDEX] = arg1;
        packet[Constants.CLIENT_ARG2_0_INDEX] = arg2_0;
        packet[Constants.CLIENT_ARG2_1_INDEX] = arg2_1;
        packet[Constants.CLIENT_ARG2_2_INDEX] = arg2_2;
        packet[Constants.CLIENT_ARG2_3_INDEX] = arg2_3;
        packet[Constants.CLIENT_ARG2_4_INDEX] = arg2_4;
        packet[Constants.CLIENT_ARG2_5_INDEX] = arg2_5;
        packet[Constants.CLIENT_FOOTER_INDEX] = Constants.CLIENT_FOOTER;
        packet[Constants.CLIENT_CHECKSUM_INDEX] = checksum(packet,
                                                           Constants.CLIENT_PACKET);
        return packet;
    }

    /**
     * Creates a server (emulator packet).
     *
     * There are three types of data (voltage, current channel 1, and current
     * channel 2). Each data type is 64 samples (128 bytes, 2 bytes per sample).
     * The samples are interleaved.
     *
     * @param id      the id of the board
     * @param voltage the voltage samples
     * @param curCh1  the current channel 1 samples
     * @param curCh2  the current channel 2 samples
     * @return the server packet data ready to be inserted into UDP packet
     */
    public static byte[] createServerPacket(byte id, int[] voltage, int[] curCh1,
                                            int[] curCh2) {
        byte[] packet = new byte[Constants.SERVER_PACKET_SIZE];
        int cnt = 0;
        byte[] DAC;

        for (int i = Constants.SERVER_DAC_START_INDEX; i <= Constants.SERVER_DAC_END_INDEX; i += 6) {
            DAC = createDACMeasurement(voltage[cnt++]);
            packet[i] = DAC[0];
            packet[i + 1] = DAC[1];
        }

        cnt = 0;

        for (int i = Constants.SERVER_DAC_START_INDEX + 2; i <= Constants.SERVER_DAC_END_INDEX; i += 6) {
            DAC = createDACMeasurement(curCh1[cnt++]);
            packet[i] = DAC[0];
            packet[i + 1] = DAC[1];
        }

        cnt = 0;

        for (int i = Constants.SERVER_DAC_START_INDEX + 4; i <= Constants.SERVER_DAC_END_INDEX; i += 6) {
            DAC = createDACMeasurement(curCh2[cnt++]);
            packet[i] = DAC[0];
            packet[i + 1] = DAC[1];
        }

        packet[Constants.SERVER_HEADER_INDEX] = Constants.SERVER_HEADER;
        packet[Constants.SERVER_ID_INDEX] = id;
        packet[Constants.SERVER_FOOTER_INDEX] = Constants.SERVER_FOOTER;
        packet[Constants.SERVER_CHECKSUM_INDEX] = checksum(packet,
                                                           Constants.SERVER_PACKET);
        return packet;
    }

    /**
     * Return the highest DAC value reached by a sample point
     *
     * @param data    the samples to search through
     * @param channel the data source to get the max DAC value for
     * @return the max DAC value for the selected data source
     */
    public static synchronized int getMaxDACValue(byte[] data, int channel) {
        int[] samples = getDACMeasurements(data)[channel];
        ArrayList<Integer> maximas = new ArrayList<Integer>();
        int average = 0;
        for (int i = 2; i < samples.length - 2; i++) {
            if ((samples[i] >= samples[i + 1]) && (samples[i] >= samples[i - 1])) {
                if ((samples[i] > samples[i + 2]) && (samples[i] > samples[i - 2])) {
                    if (samples[i] > 4) {
                        if (maximas.size() > 0) {
                            if (i - maximas.get(maximas.size() - 1) > 5) {
                                maximas.add(i);
                            }
                        } else {
                            maximas.add(i);
                        }
                    }

                }
            }
        }
        
        if(maximas.size() == 0) {
            return 0;
        }
        
        for(Integer i : maximas) {
            average += samples[i];
        }
        
        return average / maximas.size();
    }

    /**
     * Return a 2D array where each row represents a data source.
     *
     * This method takes raw data from a packet, and returns a 2D array of
     * integers representing the samples for the different data sources. The
     * values also need to be de-interleaved.
     *
     * @param data the data to get usable samples from
     * @return a 2d array of usable sample values from all data sources
     */
    public static synchronized int[][] getDACMeasurements(byte[] data) {
        int[][] measurements = new int[3][Constants.SERVER_SAMPLES_PER_CH];
        int idx = 0;

        // Voltage
        for (int i = Constants.SERVER_DAC_START_INDEX; i < Constants.SERVER_DAC_END_INDEX; i += 6) {
            measurements[0][idx++] = getDACMeasurement(data[i], data[i + 1]);
        }

        idx = 0;

        // Current ch 1
        for (int i = Constants.SERVER_DAC_START_INDEX + 2; i < Constants.SERVER_DAC_END_INDEX; i += 6) {
            measurements[1][idx++] = getDACMeasurement(data[i], data[i + 1]);
        }

        idx = 0;

        // Current ch 2
        for (int i = Constants.SERVER_DAC_START_INDEX + 4; i < Constants.SERVER_DAC_END_INDEX; i += 6) {
            measurements[2][idx++] = getDACMeasurement(data[i], data[i + 1]);
        }

        return measurements;
    }

    /**
     * Converts 10-bit DAC measurement into integer
     *
     * @param low  the low byte
     * @param high the high byte
     * @return an integer computed from low and high byte
     */
    public static synchronized int getDACMeasurement(byte high, byte low) {
        int result = 0;
        result |= ((high & 0xFF) << 8);
        result |= (low & 0xFF);
        return result;
    }

    /**
     * Convert integer into 10-bit DAC value
     *
     * @param val the value to convert into a 10-bit DAC measurement
     * @return array of two bytes representing 10-bit DAC measurement
     */
    public static byte[] createDACMeasurement(int val) {
        byte[] lowAndHigh = new byte[2];
        lowAndHigh[0] |= (val & 0xFFFFFFFF) >> 8;
        lowAndHigh[1] |= (val & 0xFFFFFFFF);
        return lowAndHigh;
    }

    /**
     * Creates a properly formatted time stamp
     *
     * @return time stamp
     */
    public static String generateTimestamp() {
        return new SimpleDateFormat("MM/dd/yy HH:mm:ss.SSS").format(
                Calendar.getInstance().getTime());
    }
}
