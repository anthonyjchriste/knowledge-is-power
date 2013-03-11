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

import kip.utils.PacketUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christe, Anthony
 */
public class PacketHistoryTest {
    private PacketHistory history;
    private Packet packet;
    public PacketHistoryTest() {
        byte[] data = PacketUtils.createServerPacket((byte) 0, new int[64], new int[64], new int[64]);
        packet = new Packet(java.util.Calendar.getInstance(), data);
        history = new PacketHistory();
    }

    /**
     * Test of getBuffer method, of class PacketHistory.
     */
    @Test
    public void testGetBuffer() {
        System.out.println("getBuffer");
        assertNull(history.getBuffer());
        history.add(packet);
        assertNotNull(history.getBuffer());
        assertNull(history.getBuffer());
        history.add(packet);
        assertNotNull(history.getBuffer());
        assertNull(history.getBuffer());
    }
}
