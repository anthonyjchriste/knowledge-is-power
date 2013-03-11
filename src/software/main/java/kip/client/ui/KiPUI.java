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
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import kip.client.ClientActionManager;
import kip.utils.ConfigurationManager;

/**
 * Main UI Frame.
 * @author anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class KiPUI extends JPanel {
    private static JFrame frame = null;
    private JTabbedPane tabs = null;
    private ControlPanel controlPanel = null;
    private RealTimePanel realTimePanel = null;
    private PacketExplorer packetExplorer = null;
    private ClientActionManager cam = null;

    /**
     * Constructs a new KiPUI object
     */
    public KiPUI() {
        super(new GridLayout(1, 1));
        cam = new ClientActionManager();
        tabs = new JTabbedPane();
        controlPanel = new ControlPanel(cam);
        realTimePanel = new RealTimePanel(cam);
        packetExplorer = new PacketExplorer(cam);
        
        frame.setJMenuBar(new MenuBar(cam, controlPanel));
        
        cam.startServerWorker(controlPanel.getStatusArea());
        cam.startRealTimePlotWorker(realTimePanel);

        initUI();
    }

    /**
     * Inits the UI by adding JPanels to tabs, setting the correct size, and then adding the tabs to root pane.
     */
    public final void initUI() {
        controlPanel.setPreferredSize(new Dimension(1200, 700));
        tabs.addTab("Control", controlPanel);
        tabs.addTab("Real Time Data", realTimePanel);
        tabs.addTab("Packet Explorer", packetExplorer);
        add(tabs);
    }

    private static void createAndShowUI() {
        frame = new JFrame("KiP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new KiPUI(), BorderLayout.CENTER);
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            ConfigurationManager.init(args[0]);
        } else {
            ConfigurationManager.init();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }
}
