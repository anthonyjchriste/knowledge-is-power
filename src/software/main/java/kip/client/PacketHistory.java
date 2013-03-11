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

import java.util.LinkedList;

/**
 * Stores all packets locally
 * @author Christe, Anthony
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PacketHistory {
    private LinkedList<Packet> history = null;
    private Packet buffer = null;

    /**
     * Creates a new packet history object
     */
    public PacketHistory() {
        history = new LinkedList<Packet>();
    }
    
    /**
     * Adds a packet to the local history
     * @param packet the packet to add to the local history
     */
    public synchronized void add(Packet packet) {
        history.addLast(packet);
        buffer = packet;
    }

    /**
     * Returns the local history
     * @return the local history
     */
    public synchronized LinkedList<Packet> getHistory() {
        return history;
    }
    
    /**
     * Returns the most recently stored packet.
     * 
     * Once a packet is read from the buffer, the buffer becomes empty until a
     * new packet is added to the buffer. This allows us to construct our real
     * time panels correctly.
     * @return the most recent packet added to local history
     */
    public synchronized Packet getBuffer() {
        Packet p;

        if (buffer == null) {
            return null;
        } else {
            p = buffer;
            buffer = null;
            return p;
        }
    }
}
