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

import kip.emulator.KiPEmulator;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author anthony
 */
public class PacketUtilsTest {
    public PacketUtilsTest() {
    }

    /**
     * Test of isValidPacket method, of class PacketUtils.
     */
    @Test
    public void testIsValidPacket() {
        System.out.println("isValidPacket");
        byte[] clientData = PacketUtils.createClientPacket(Constants.CLIENT_CMD_TRIGGER, 
                                                           (byte) 0, 
                                                           (byte) 0);
        byte[] serverData = PacketUtils.createServerPacket((byte) 0, 
                                                           new int[64], 
                                                           new int[64],
                                                           new int[64]);
        
        // Test for rejection on unknown packet types
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], -5));
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], Constants.CLIENT_PACKET - 1));
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], Constants.SERVER_PACKET + 1));
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], 5));
        
        // Test for rejection on incorrect packet lengths
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], Constants.CLIENT_PACKET));
        assertEquals(false, PacketUtils.isValidPacket(new byte[0], Constants.SERVER_PACKET));
        
        // Test for rejection based off of invalid header or footer
        clientData[Constants.CLIENT_HEADER_INDEX] = (byte) 0xFF;
        assertEquals(false, PacketUtils.isValidPacket(clientData, Constants.CLIENT_PACKET));
        clientData[Constants.CLIENT_HEADER_INDEX] = Constants.CLIENT_HEADER;
        assertEquals(true, PacketUtils.isValidPacket(clientData, Constants.CLIENT_PACKET));
        clientData[Constants.CLIENT_FOOTER_INDEX] = (byte) 0xFF;
        assertEquals(false, PacketUtils.isValidPacket(clientData, Constants.CLIENT_PACKET));
        clientData[Constants.CLIENT_FOOTER_INDEX] = Constants.CLIENT_FOOTER;
        assertEquals(true, PacketUtils.isValidPacket(clientData, Constants.CLIENT_PACKET));
        
        serverData[Constants.SERVER_HEADER_INDEX] = (byte) 0xFF;
        assertEquals(false, PacketUtils.isValidPacket(serverData, Constants.SERVER_PACKET));
        serverData[Constants.SERVER_HEADER_INDEX] = Constants.SERVER_HEADER;
        assertEquals(true, PacketUtils.isValidPacket(serverData, Constants.SERVER_PACKET));
        serverData[Constants.SERVER_FOOTER_INDEX] = (byte) 0xFF;
        assertEquals(false, PacketUtils.isValidPacket(serverData, Constants.SERVER_PACKET));
        serverData[Constants.SERVER_FOOTER_INDEX] = Constants.SERVER_FOOTER;
        assertEquals(true, PacketUtils.isValidPacket(serverData, Constants.SERVER_PACKET));
        
        // Test for rejection on bad checksum
        clientData[Constants.CLIENT_CHECKSUM_INDEX] = (byte) 0x0;
        serverData[Constants.SERVER_CHECKSUM_INDEX] = (byte) 0x0;
        assertEquals(false, PacketUtils.isValidPacket(clientData, Constants.CLIENT_PACKET));
        assertEquals(false, PacketUtils.isValidPacket(serverData, Constants.SERVER_PACKET));
    }

    /**
     * Test of checksum method, of class PacketUtils.
     */
    @Test
    public void testChecksum() {
        System.out.println("checksum");
        byte sum;
        byte[] clientData = PacketUtils.createClientPacket(Constants.CLIENT_CMD_TRIGGER, 
                                                           (byte) 0, 
                                                           (byte) 0);
        byte[] serverData = PacketUtils.createServerPacket((byte) 2, 
                                                           new int[64], 
                                                           new int[64],
                                                           new int[64]);
        
        sum = Constants.CLIENT_FOOTER + Constants.CLIENT_HEADER;
        // Add in 1 for the client command trigger
        sum += 1;
        assertEquals(sum, PacketUtils.checksum(clientData, Constants.CLIENT_PACKET));
        
        sum = Constants.SERVER_FOOTER + Constants.SERVER_HEADER;
        // Add in 2 for the board id
        sum += 2;
        assertEquals(sum, PacketUtils.checksum(serverData, Constants.SERVER_PACKET));
    
    }

    /**
     * Test of createClientPacket method, of class PacketUtils.
     */
    @Test
    public void testCreateClientPacket() {
        System.out.println("createClientPacket");
        byte[] packet = PacketUtils.createClientPacket(Constants.CLIENT_CMD_SET_ID, 
                                                         (byte) 5, 
                                                         (byte) 0);
        byte[] expected = new byte[Constants.CLIENT_PACKET_SIZE];
        
        assertEquals(true, PacketUtils.isValidPacket(packet, Constants.CLIENT_PACKET));
        
        expected[Constants.CLIENT_HEADER_INDEX] = Constants.CLIENT_HEADER;
        expected[Constants.CLIENT_COMMAND_INDEX] = Constants.CLIENT_CMD_SET_ID;
        expected[Constants.CLIENT_ARG1_INDEX] = 5;
        expected[Constants.CLIENT_ARG2_INDEX] = 0;
        expected[Constants.CLIENT_CHECKSUM_INDEX] = PacketUtils.checksum(packet, 
                                                                         Constants.CLIENT_PACKET);
        expected[Constants.CLIENT_FOOTER_INDEX] = Constants.CLIENT_FOOTER;
        
        assertArrayEquals(expected, packet);
        
        packet = PacketUtils.createClientPacket(Constants.CLIENT_CMD_TRIGGER, 
                                                         (byte) 0, 
                                                         (byte) 0);
        assertEquals(true, PacketUtils.isValidPacket(packet, Constants.CLIENT_PACKET));
        expected[Constants.CLIENT_COMMAND_INDEX] = Constants.CLIENT_CMD_TRIGGER;
        expected[Constants.CLIENT_ARG1_INDEX] = 0;
        expected[Constants.CLIENT_CHECKSUM_INDEX] = PacketUtils.checksum(packet,
                                                                         Constants.CLIENT_PACKET);
        
        assertArrayEquals(expected, packet);
    }

    /**
     * Test of createServerPacket method, of class PacketUtils.
     * 
     * The main purpose of this test is to make sure the the DAC measurement are
     * interleaved correctly.
     */
    @Test
    public void testCreateServerPacket() {
        System.out.println("createServerPacket");
        int[] voltageSamples = new int[64];
        int[] currentCh1Samples = new int[64];
        int[] currentCh2Samples = new int[64];
        byte[] packet;
        byte[] expected = new byte[Constants.SERVER_PACKET_SIZE];
        byte[] dac1 = PacketUtils.createDACMeasurement(1);
        byte[] dac2 = PacketUtils.createDACMeasurement(2);
        byte[] dac3 = PacketUtils.createDACMeasurement(3);
        
        Arrays.fill(voltageSamples, 1);
        Arrays.fill(currentCh1Samples, 2);
        Arrays.fill(currentCh2Samples, 3);
        
        packet = PacketUtils.createServerPacket((byte) 5, voltageSamples,
                                                currentCh1Samples,
                                                currentCh2Samples);
        
        assertEquals(true, PacketUtils.isValidPacket(packet, Constants.SERVER_PACKET));
        
        expected[Constants.SERVER_HEADER_INDEX] = Constants.SERVER_HEADER;
        expected[Constants.SERVER_ID_INDEX] = 5;
        
        for(int i = Constants.SERVER_DAC_START_INDEX; i <= Constants.SERVER_DAC_END_INDEX; i += 6) {
            expected[i] = dac1[0];
            expected[i + 1] = dac1[1];
            expected[i + 2] = dac2[0];
            expected[i + 3] = dac2[1];
            expected[i + 4] = dac3[0];
            expected[i + 5] = dac3[1];
        }
        
        for(int i = Constants.SERVER_RESERVED_START_INDEX; i <= Constants.SERVER_RESERVED_END_INDEX; i++) {
            expected[i] = 0;
        }
        
        expected[Constants.SERVER_FOOTER_INDEX] = Constants.SERVER_FOOTER;
        expected[Constants.SERVER_CHECKSUM_INDEX] = PacketUtils.checksum(expected,
                                                                       Constants.SERVER_PACKET);
        
        assertArrayEquals(expected, packet);
        
    }

    /**
     * Test of getMaxDACValue method, of class PacketUtils.
     */
    @Test
    public void testGetMaxDACValue() {
        System.out.println("getMaxDACValue");
        
        int[] ch0 = KiPEmulator.generateSineWave(5, 0);
        int[] ch1 = KiPEmulator.generateSineWave(200, 0);
        int[] ch2 = KiPEmulator.generateSineWave(50, 0);
        byte[] data = PacketUtils.createServerPacket((byte) 0, ch0, ch1, ch2);
        int max0 = 0;
        int max1 = 0;
        int max2 = 0;
        
        for(int i = 0; i < ch0.length; i++){
            if(ch0[i] > max0) max0 = ch0[i];
            if(ch1[i] > max1) max1 = ch1[i];
            if(ch2[i] > max2) max2 = ch2[i];
        }
        
        assertEquals(max0, PacketUtils.getMaxDACValue(data, 0));
        assertEquals(max1, PacketUtils.getMaxDACValue(data, 1));
        assertEquals(max2, PacketUtils.getMaxDACValue(data, 2));
    }

    /**
     * Test of getDACMeasurements method, of class PacketUtils.
     */
    @Test
    public void testGetDACMeasurements() {
        System.out.println("getDACMeasurements");
        int[] voltageSamples = new int[64];
        int[] currentCh1Samples = new int[64];
        int[] currentCh2Samples = new int[64];
        int[][] expected = new int[3][64];
        byte[] data;
        
        Arrays.fill(voltageSamples, 1);
        Arrays.fill(currentCh1Samples, 2);
        Arrays.fill(currentCh2Samples, 3);
        expected[0] = voltageSamples;
        expected[1] = currentCh1Samples;
        expected[2] = currentCh2Samples;
        
        data = PacketUtils.createServerPacket((byte) 0, voltageSamples,
                                              currentCh1Samples,
                                              currentCh2Samples);
        
        assertArrayEquals(expected, PacketUtils.getDACMeasurements(data));
    }

    /**
     * Test of getDACMeasurement method, of class PacketUtils.
     */
    @Test
    public void testGetDACMeasurement() {
        System.out.println("getDACMeasurement");
        assertEquals(12, PacketUtils.getDACMeasurement((byte) 0, (byte) 12));
        assertEquals(300, PacketUtils.getDACMeasurement((byte) 1,(byte) 44));
        assertEquals(812, PacketUtils.getDACMeasurement((byte) 3,(byte) 44));
    }

    /**
     * Test of createDACMeasurement method, of class PacketUtils.
     */
    @Test
    public void testCreateDACMeasurement() {
        System.out.println("createDACMeasurement");
        byte[] expected1 = {0, 12};
        byte[] expected2 = {1, 44};
        byte[] expected3 = {3, 44};
        
        assertArrayEquals(expected1, PacketUtils.createDACMeasurement(12));
        assertArrayEquals(expected2, PacketUtils.createDACMeasurement(300));
        assertArrayEquals(expected3, PacketUtils.createDACMeasurement(812));
    }
}
