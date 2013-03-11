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

/**
 * Various constants used KiP
 * 
 * @author Christe, Anthony
 */
public class Constants {
    /**
     * Client packet time
     */
    public static final int CLIENT_PACKET = 0;
    
    /**
     * Number of bytes in client packet
     */
    public static final int CLIENT_PACKET_SIZE = 11;
    
    /**
     * Header word of client packet
     */
    public static final byte CLIENT_HEADER = (byte) 0xBC;
    
    /**
     * Footer word of client packet
     */
    public static final byte CLIENT_FOOTER = (byte) 0xCB;
    
    /**
     * Command word that tells board to send data to client
     */
    public static final byte CLIENT_CMD_TRIGGER = (byte) 0x1;
    
    /**
     * Command word that tells board to set its id
     */
    public static final byte CLIENT_CMD_SET_ID = (byte) 0x2;
    
    /**
     * Command word that tells board to set its ip
     */
    public static final byte CLIENT_CMD_SET_IP = (byte) 0x3;
    
    /**
     * Command word that tells board to set its mac address
     */
    public static final byte CLIENT_CMD_SET_MAC = (byte) 0x4;
    
    /**
     * Command word that tells the board to set its delay field
     */
    public static final byte CLIENT_CMD_SET_DELAY = (byte) 0x5;
    
    /**
     * Command word that tells board to reset (reverts back to default ID/IP/MAC
     */
    public static final byte CLIENT_CMD_RESET = (byte) 0xF;
    
    /**
     * Location of header in client packet
     */
    public static final int CLIENT_HEADER_INDEX = 0;
    
    /**
     * Location of command in client packet
     */
    public static final int CLIENT_COMMAND_INDEX = 1;
    
    /**
     * Location of the first argument in client packet
     */
    public static final int CLIENT_ARG1_INDEX = 2;
    
    /**
     * Location of argument 2 index 0
     */
    public static final int CLIENT_ARG2_0_INDEX = 3;
    
    /**
     * Location of argument 2 index 1
     */
    public static final int CLIENT_ARG2_1_INDEX = 4;
    
    /**
     * Location of argument 2 index 2
     */
    public static final int CLIENT_ARG2_2_INDEX = 5;
    
    /**
     * Location of argument 2 index 3
     */
    public static final int CLIENT_ARG2_3_INDEX = 6;
    
    /**
     * Location of argument 2 index 4
     */
    public static final int CLIENT_ARG2_4_INDEX = 7;
    
    /**
     * Location of argument 2 index 5
     */
    public static final int CLIENT_ARG2_5_INDEX = 8;
    
    /**
     * Start location (inclusive) of the second argument in client packet
     */
    public static final int CLIENT_ARG2_START_INDEX = 3;
    
    /**
     * End location (inclusive) of the second argument in client packet
     */
    public static final int CLIENT_ARG2_END_INDEX = 8;
    
    /**
     * Location of the checksum in client packet
     */
    public static final int CLIENT_CHECKSUM_INDEX = 9;
    
    /**
     * Location of the footer in client packet
     */
    public static final int CLIENT_FOOTER_INDEX = 10;
    
    /**
     * Server packet type
     */
    public static final int SERVER_PACKET = 1;
    
    /**
     * Number of bytes in server packet
     */
    public static final int SERVER_PACKET_SIZE = 402;
    
    /**
     * Header word for for server packet
     */
    public static final byte SERVER_HEADER = (byte) 0xBF;
    
    /**
     * Footer word for server packet
     */
    public static final byte SERVER_FOOTER = (byte) 0xFB;
    
    /**
     * Number of data samples per data source (voltage, current channel 1,  current channel 2)
     */
    public static final int SERVER_SAMPLES_PER_CH = 64;
    
    /**
     * Location of header in server packet
     */
    public static final int SERVER_HEADER_INDEX = 0;
    
    /**
     * Location of board id in server packet
     */
    public static final int SERVER_ID_INDEX = 1;
    
    /**
     * Starting location of DAC measurements
     */
    public static final int SERVER_DAC_START_INDEX = 2;
    
    /**
     * Final location of DAC measurements (inclusive)
     */
    public static final int SERVER_DAC_END_INDEX = 385;
    
    /**
     * Total words used for DAC measurements
     */
    public static final int SERVER_DAC_RANGE = SERVER_DAC_END_INDEX - SERVER_DAC_START_INDEX + 1;
    
    /**
     * Locations of server delay
     */
    public static final int SERVER_DELAY_INDEX_HIGH = 386;
    
    /**
     * Location of server delay
     */
    public static final int SERVER_DELAY_INDEX_LOW = 387;
    
    /**
     * Start location of reserved words
     */
    public static final int SERVER_RESERVED_START_INDEX = 388;
    
    /**
     * Final location of reserved words (inclusive)
     */
    public static final int SERVER_RESERVED_END_INDEX = 399;
    
    /**
     * Location of checksum in server packet
     */
    public static final int SERVER_CHECKSUM_INDEX = 400;
    
    /**
     * Location of footer in server packet
     */
    public static final int SERVER_FOOTER_INDEX = 401;
    
    /**
     * The square root of 2
     */
    public static final double SQRT_OF_2 = 1.41421356237;
    
    /**
     * Voltage
     */
    public static final double VOLTAGE = 84.853;
}
