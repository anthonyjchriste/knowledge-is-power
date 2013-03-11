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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import kip.client.ClientActionManager;
import kip.client.Packet;

/**
 * UI that allows users to explore details of individual packets
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class PacketExplorer extends JPanel implements TreeSelectionListener {
    private ClientActionManager cam = null;
    // Tree components
    private JScrollPane scrollPane = null;
    private JPanel treePanel = new JPanel(new BorderLayout(), false);
    private final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
            "Packets");
    private JTree tree = new JTree(top);
    private JButton btnRefresh = new JButton("Refresh");
    private Border treeEmptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private TitledBorder treeTitledBorder = BorderFactory.createTitledBorder(
            "Packets by Date");
    // Tab components
    JTabbedPane tabs = new JTabbedPane();
    // Plot components
    private JPanel plotPanel = new JPanel(new GridLayout(0, 1));
    private PacketExplorerPlot voltagePlot = new PacketExplorerPlot("Voltage PK");
    private PacketExplorerPlot curCh1Plot = new PacketExplorerPlot(
            "Current CH1 PK");
    private PacketExplorerPlot curCh2Plot = new PacketExplorerPlot(
            "Current CH2 PK");
    private Border plotEmptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private TitledBorder plotTitledBorder = BorderFactory.createTitledBorder(
            "Packet Data");
    private PacketExplorerDetails details = new PacketExplorerDetails();

    /**
     * Constructs a new Packet Explorer object
     * @param cam   the client action manager
     */
    public PacketExplorer(ClientActionManager cam) {
        super(new BorderLayout(), false);
        this.cam = cam;
        initTree();
        initPlots();
    }

    /**
     * Inits the tree value
     */
    private void initTree() {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTree(top);
            }
        });

        treePanel.add(btnRefresh, BorderLayout.NORTH);
        scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(210, 0));
        scrollPane.setMinimumSize(null);
        scrollPane.setMaximumSize(null);
        treePanel.add(scrollPane, BorderLayout.CENTER);
        treePanel.setBorder(BorderFactory.createCompoundBorder(treeEmptyBorder,
                                                               treeTitledBorder));
        
        this.add(treePanel, BorderLayout.WEST);
    }

    /**
     * Inits the different plots in Packet Explorer
     */
    private void initPlots() {
        //plotPanel.add(voltagePlot); // Commented out until voltage is working
        plotPanel.add(curCh1Plot);
        plotPanel.add(curCh2Plot);
        plotPanel.setBorder(BorderFactory.createCompoundBorder(plotEmptyBorder,
                                                               plotTitledBorder));
        tabs.addTab("Plots", plotPanel);
        tabs.addTab("Details", details);
        this.add(tabs, BorderLayout.CENTER);
    }

    /**
     * Update plots and details for selected packet
     * @param packet the packet used for updating details
     */
    private void updatePlots(Packet packet) {
        voltagePlot.update(packet.getSamples(Packet.VOLTAGE),
                           (int) packet.getVrms());
        curCh1Plot.update(packet.getSamples(Packet.CURRENT_CHANNEL_1), -1);
        curCh2Plot.update(packet.getSamples(Packet.CURRENT_CHANNEL_2), -1);
        details.update(packet);
        this.repaint();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            updatePlots((Packet) node.getUserObject());
        }
    }

    /**
     * Builds a tree in linear time representing packet data with a hierarchy based on 
     * granularity of time.
     * 
     * For each granularity of time, determine if a root for that subtree exists,
     * if not, create a new root for that subtree, otherwise, move on to next
     * granularity of time
     * @param top the root of the tree
     */
    private void refreshTree(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode year = null;
        DefaultMutableTreeNode month = null;
        DefaultMutableTreeNode day = null;
        DefaultMutableTreeNode hour = null;
        DefaultMutableTreeNode minute = null;
        DefaultMutableTreeNode second = null;
        int curYear = 0;
        int curMonth = 0;
        int curDay = 0;
        int curHour = 0;
        int curMinute = 0;
        int curSecond = 0;
        LinkedList<Packet> history = cam.getPacketHistory().getHistory();
        
        // Rebuild from scratch after each refresh
        top.removeAllChildren();

        for (Packet packet : history) {
            curYear = packet.getYear();
            curMonth = packet.getMonth();
            curDay = packet.getDay();
            curHour = packet.getHour();
            curMinute = packet.getMinute();
            curSecond = packet.getSecond();

            if (canMatchField(top, curYear) < 0) {
                year = new DefaultMutableTreeNode(curYear);
                top.add(year);
            }
            if (canMatchField(year, curMonth) < 0) {
                month = new DefaultMutableTreeNode(curMonth);
                year.add(month);
            }
            if (canMatchField(month, curDay) < 0) {
                day = new DefaultMutableTreeNode(curDay);
                month.add(day);
            }
            if (canMatchField(day, curHour) < 0) {
                hour = new DefaultMutableTreeNode(curHour);
                day.add(hour);
            }
            if (canMatchField(hour, curMinute) < 0) {
                minute = new DefaultMutableTreeNode(curMinute);
                hour.add(minute);
            }
            if (canMatchField(minute, curSecond) < 0) {
                second = new DefaultMutableTreeNode(curSecond);
                minute.add(second);
            }
            second.add(new DefaultMutableTreeNode(packet));
        }

        tree.updateUI();
    }

    /**
     * Does a root node for val exists?
     * @param top the root of the tree
     * @param val the value we're checking for existence
     * @return index of found node, or -1 if it doesn't exist
     */
    private int canMatchField(DefaultMutableTreeNode top, int val) {
        int idx = -1;
        Enumeration<DefaultMutableTreeNode> children = top.children();
        DefaultMutableTreeNode node = null;

        while (children.hasMoreElements()) {
            node = children.nextElement();
            if (((Integer) node.getUserObject()) == val) {
                return top.getIndex(node);
            }
        }

        return idx;
    }
}
