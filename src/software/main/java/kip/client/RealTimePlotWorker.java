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

import javax.swing.SwingWorker;
import kip.client.ui.RealTimePanel;
import kip.utils.ConfigurationManager;

/**
 * Thread that updates real time plots
 * @author Christe, Anthony
 */
public class RealTimePlotWorker extends SwingWorker<Integer, String> {
    private RealTimePanel realTimePanel = null;
    private int realTimeRefresh = 0;
    private PacketHistory packetHistory = null;
    
    /**
     * Constructs the real time plot thread
     * @param realTimePanel the panel that contains the real time plots
     * @param packetHistory the local history of packets
     */
    public RealTimePlotWorker(RealTimePanel realTimePanel,
                              PacketHistory packetHistory) {
        this.realTimePanel = realTimePanel;
        this.realTimeRefresh = Integer.parseInt(ConfigurationManager.getProperty(
                ConfigurationManager.REAL_TIME_REFRESH));
        this.packetHistory = packetHistory;
    }
    
    /**
     * Updates real time plot based on how often the plots are set to refresh.
     * 
     * The thread will attempt to pull from the packet history's buffer. If the
     * buffer is empty, then there is new date, otherwise, the most recent data
     * since the last update is grabbed.
     * @return result of thread run
     * @throws Exception don't allow swing thread manager to eat exceptions
     */
    @Override
    protected Integer doInBackground() throws Exception {
        try {
            while (true) {
                realTimePanel.updatePanels(packetHistory.getBuffer());
                Thread.sleep(1000 / realTimeRefresh);
            }
        } catch (InterruptedException e) {
        }

        return 0;
    }
}
