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

package kip.client.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import kip.client.Packet;

/**
 * Details tab of packet explorer
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class PacketExplorerDetails extends JPanel {
    private JTextArea infoArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(infoArea);
	
    public PacketExplorerDetails() {
        super(new BorderLayout());
        init();
    }
    
    /**
     * Inits packet explorer JTextArea with fields
     */
    private void init() {
        infoArea.setEditable(false);
        
        appendInfo("Timestamp YYYY-MM-DD HH:MM:SS.MS", "N/A");
        appendInfo("Frequency Channel 1", "N/A");
        appendInfo("Frequency Channel 2", "N/A");
        appendInfo("Current Channel 1", "N/A");
        appendInfo("Current Channel 2", "N/A");
        appendInfo("Amperes Channel 1", "N/A");
        appendInfo("Amperes Channel 2", "N/A");
        appendInfo("Watts", "N/A");
        appendInfo("Voltage DACs", "N/A");
        appendInfo("Current Channel 1 DACs", "N/A");
        appendInfo("Current Channel 2 DACs", "N/A");
        
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Appends a new key/value pair to JTextArea for details
     * @param key   the key
     * @param val   the value
     */
    private void appendInfo(String key, String val) {
            infoArea.append(key + "\n" + val + "\n\n");
    }
	
    /**
     * Update details based off information in packet
     * @param packet the packet used to update the details
     */
    public void update(Packet packet) {
		infoArea.setText("");
		appendInfo("Timestamp YYYY-MM-DD HH:MM:SS.MS", packet.getYear() + "-" + packet.getMonth() + "-" + packet.getDay() + " " + packet.getHour() + ":" + packet.getMinute() + ":" + packet.getSecond() + "." + packet.getMilliSeconds());
        appendInfo("Frequency Channel 1", Double.toString(packet.getFreqCh1()));
        appendInfo("Frequency Channel 2", Double.toString(packet.getFreqCh2()));
        appendInfo("Current Channel 1", Integer.toString(packet.getCurCh1PK()));
        appendInfo("Current Channel 2", Integer.toString(packet.getCurCh2PK()));
        appendInfo("Amperes Channel 1", Double.toString(packet.getCurrentCh1()));
        appendInfo("Amperes Channel 2", Double.toString(packet.getCurrentCh2()));
        appendInfo("Watts", Double.toString(packet.getWatts()));
        appendInfo("Voltage DACs", Arrays.toString(packet.getSamples(Packet.VOLTAGE)));
        appendInfo("Current Channel 1 DACs", Arrays.toString(packet.getSamples(Packet.CURRENT_CHANNEL_1)));
        appendInfo("Current Channel 2 DACs", Arrays.toString(packet.getSamples(Packet.CURRENT_CHANNEL_2)));
    }
}
