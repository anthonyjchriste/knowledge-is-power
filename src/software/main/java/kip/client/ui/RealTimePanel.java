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

import java.awt.GridLayout;
import java.util.Calendar;
import javax.swing.JPanel;
import kip.client.ClientActionManager;
import kip.client.Packet;

/**
 * Real time panel which holds multiple real time plots
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class RealTimePanel extends JPanel {
    private RealTimePlot frequencyPanel = null;
    private RealTimePlot currentPanel = null;
    private RealTimePlot wattPanel = null;
    private ClientActionManager cam = null;
    private double freqBuf = 0.0;
    
    public RealTimePanel(ClientActionManager cam) {
        super(new GridLayout(0, 1), true);
        this.cam = cam;
        init();
    }

    /**
     * Initialized the real time panel by filling it with plots
     */
    protected void init() {
        frequencyPanel = new RealTimePlot("Frequency", "Time", "Vrms", 70, 10);
        currentPanel = new RealTimePlot("Current", "Time", "Current", 1, 2);
        wattPanel = new RealTimePlot("Watts", "Time", "Watts", 1, 100);
        this.add(frequencyPanel);
        this.add(wattPanel);
        this.add(currentPanel);
    }

    /**
     * Update all real time plots stored in this panel. 
     * 
     * Data is pulled from local buffer. Once the buffer is polled, it becomes
     * empty. When that happens, we can just update all the plots with the current
     * time and 0's for y-values. Otherwise, we get the most recent buffered packet
     * since the last update.
     * @param packet packet used to update real time plots
     */
    public void updatePanels(Packet packet) {
        long epoch = Calendar.getInstance().getTimeInMillis();
        double freq = 0;
        
        if (packet == null) {
            frequencyPanel.updatePlot(epoch, 0);
            currentPanel.updatePlot(epoch, 0);
            wattPanel.updatePlot(epoch, 0);
        } else {
            freq = packet.getFreqCh1();
            if(freq > 0) {
                freqBuf = freq;
            }
            frequencyPanel.updatePlot(epoch, freqBuf);
            currentPanel.updatePlot(epoch, packet.getCurrentCh1());
            wattPanel.updatePlot(epoch, packet.getWatts());
        }
    }
}
