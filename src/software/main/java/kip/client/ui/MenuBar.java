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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import kip.client.ClientActionManager;
import kip.client.Packet;

@SuppressWarnings({"serial", "unchecked"})
/**
 * Menubar object for quitting program and setting options on KiP Board
 */
public class MenuBar extends JMenuBar implements ActionListener {
    private ClientActionManager cam;
    private ControlPanel controlPanel;
    private JMenu fileMenu;
    private JMenu boardMenu;
    private JMenuItem menuSave;
    private JMenuItem menuOpen;
    private JMenuItem menuClearPackets;
    private JMenuItem menuQuit;
    private JMenuItem boardSendIP;
    private JMenuItem boardSendMAC;
    private JMenuItem boardSendID;
    private JMenuItem boardSendDelay;
    private JMenuItem boardSendReset;
    
    /**
     * Constructs a new menu bar object
     * @param cam           the client action manager
     * @param controlPanel  reference to control panel so we can access text fields and status window
     */
    public MenuBar(ClientActionManager cam, ControlPanel controlPanel) {
        super();
        this.cam = cam;
        this.controlPanel = controlPanel;
        init();
    }
    
    /**
     * Inits the menubar
     */
    private void init() {
        fileMenu = new JMenu("File");
        
        menuSave = new JMenuItem("Save Packets");
        menuSave.addActionListener(this);
        menuOpen = new JMenuItem("Open Packets");
        menuOpen.addActionListener(this);
        menuClearPackets = new JMenuItem("Clear Packets");
        menuClearPackets.addActionListener(this);
        menuQuit = new JMenuItem("Quit");
        menuQuit.addActionListener(this);
        
        fileMenu.add(menuSave);
        fileMenu.add(menuOpen);
        fileMenu.addSeparator();
        fileMenu.add(menuClearPackets);
        fileMenu.addSeparator();
        fileMenu.add(menuQuit);
        
        boardMenu = new JMenu("Board Configuration");
        boardSendID = new JMenuItem("Set board ID");
        boardSendID.addActionListener(this);
        boardSendIP = new JMenuItem("Set board IP");
        boardSendIP.addActionListener(this);
        boardSendMAC = new JMenuItem("Set board MAC");
        boardSendMAC.addActionListener(this);
        boardSendDelay = new JMenuItem("Set board delay");
        boardSendDelay.addActionListener(this);
        boardSendReset = new JMenuItem("Reset board");
        boardSendReset.addActionListener(this);
        boardMenu.add(boardSendID);
        boardMenu.add(boardSendIP);
        boardMenu.add(boardSendMAC);
        boardMenu.add(boardSendDelay);
        boardMenu.addSeparator();
        boardMenu.add(boardSendReset);
        
        this.add(fileMenu);
        this.add(boardMenu);
    }
    
    /**
     * Displays a dialog box asking for specific information
     * @param message   the message to display in dialog box   
     * @return          the value stored in text field of dialog box
     */
    private String displayDialog(String message) {
        return JOptionPane.showInputDialog(message + "\n(Changes will take effect after board reset)");
    }
    
    /**
     * Saves packets to file
     * @param file location to save file too
     */
    private void savePackets(File file) {
        BufferedWriter bufferedWriter;
        String line = "";
        byte[] data;
        
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            for(Packet p : cam.getPacketHistory().getHistory()) {
                line = p.getEpoch() + ",";
                data = p.getData();
                for(int i = 0; i < data.length; i++) {
                    line = line + data[i] + ",";
                }
                bufferedWriter.write(line.substring(0, line.length() - 1) + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            controlPanel.getStatusArea().append("Could not save file\n" + e.getMessage());
        }
    }
    
    /**
     * Load packets from file
     * @param file location to load packets from
     */
    private void loadPackets(File file) {
        Scanner in;
        String[] split;
        Packet packet;
        Calendar calendar = null;
        byte[] data;
        
        try {
            in = new Scanner(file);
            
            while(in.hasNext()) {
                split = in.nextLine().split(",");
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(split[0]));
                data = new byte[split.length - 1];
                
                for(int i = 1; i < split.length; i++) {
                    data[i - 1] = Byte.parseByte(split[i]);
                }
                
                packet = new Packet(calendar, data);
                cam.getPacketHistory().add(packet);
            }
            
            in.close();
        } catch (IOException e) {
            controlPanel.getStatusArea().append("Could not open file\n" + e.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        JMenuItem menuItem = (JMenuItem) evt.getSource();
        String addressStr = controlPanel.getAddressStr();
        String portStr = controlPanel.getPortStr();
        JTextArea status = controlPanel.getStatusArea();
        JFileChooser fileChooser = new JFileChooser();
        File file = null;
        String response;
        int returnVal = 0;
        
        if(menuItem == menuSave) {
            returnVal = fileChooser.showSaveDialog(controlPanel);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                savePackets(file);
            }
        }
        if(menuItem == menuOpen) {
            returnVal = fileChooser.showOpenDialog(controlPanel);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                loadPackets(file);
            }
        }
        if(menuItem == menuClearPackets) {
            returnVal = JOptionPane.showConfirmDialog(controlPanel, "Are you sure you want to remove all stored packets?", "Clear Packet History", JOptionPane.YES_NO_OPTION);
            if(returnVal == JOptionPane.YES_OPTION) {
                cam.getPacketHistory().getHistory().clear();
            }
        }
        if(menuItem == menuQuit) {
            System.exit(0);
        }
        if(menuItem == boardSendID) {
            response = displayDialog("New board ID");
            if(response != null) cam.sendId(addressStr, portStr, response, status);
        }
        if(menuItem == boardSendIP) {
            response = displayDialog("New board IP");
            if(response != null) cam.sendIP(addressStr, portStr, response, status);
        }
        if(menuItem == boardSendMAC) {
            response = displayDialog("New board MAC");
            if(response != null) cam.sendMAC(addressStr, portStr, response, status);
        }
        if(menuItem == boardSendDelay) {
            response = displayDialog("New board delay (milliseconds)");
            if(response != null) cam.sendDelay(addressStr, portStr, response, status);
        }
        if(menuItem == boardSendReset) {
            cam.sendReset(addressStr, portStr, status);
        }
    }
}
