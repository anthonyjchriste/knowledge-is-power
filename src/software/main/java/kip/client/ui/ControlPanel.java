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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import kip.client.ClientActionManager;
import kip.utils.ConfigurationManager;

/**
 * UI for controlling the sending of packets and connection parameters with board
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class ControlPanel extends JPanel {
    private final JPanel controlParameters = new JPanel(new BorderLayout());
    private final JPanel labels = new JPanel(new GridLayout(0, 1, 3, 3));
    private final JPanel inputs = new JPanel(new GridLayout(0, 1, 3, 3));
    private final JPanel controlButtonsAndStatus = new JPanel(new BorderLayout());
    private final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private final JTextField txtAddress = new JTextField(ConfigurationManager.getProperty(
            ConfigurationManager.DEFAULT_BOARD_IP));
    private final JTextField txtPort = new JTextField(ConfigurationManager.getProperty(
            ConfigurationManager.DEFAULT_BOARD_PORT));
    private final JTextField txtId = new JTextField(ConfigurationManager.getProperty(
            ConfigurationManager.DEFAULT_BOARD_ID));
    private final JTextField txtHertz = new JTextField(ConfigurationManager.getProperty(
            ConfigurationManager.DEFAULT_BOARD_POLL));
    private final JTextField txtDuration = new JTextField(ConfigurationManager.getProperty(
            ConfigurationManager.DEFAULT_BOARD_DURATION));
    private final JTextArea status = new JTextArea();
    //private final JButton btnSendId = new JButton("Send ID");
    private final JButton btnSendTriggers = new JButton("Send Triggers");
    private final JButton btnCancel = new JButton("Cancel");
    /*
    private final JButton btnMAC = new JButton("DEBUG: Send MAC");
    private final JButton btnIP = new JButton("DEBUG: Send IP");
    private final JButton btnReset = new JButton("DEBUG: Reset");
    */
    private ClientActionManager cam = null;

    /**
     * Constructs a new Control Panel object
     * @param cam the client action manager
     */
    public ControlPanel(ClientActionManager cam) {
        super(new BorderLayout(), true);
        this.cam = cam;
        initPanel();
    }
    
    /**
     * Sets up components in panel
     */
    protected void initPanel() {
        labels.add(new JLabel("Address"));
        labels.add(new JLabel("Port"));
        labels.add(new JLabel("Hertz"));
        labels.add(new JLabel("Duration (sec)"));

        inputs.add(txtAddress);
        inputs.add(txtPort);
        inputs.add(txtHertz);
        inputs.add(txtDuration);

        controlParameters.add(labels, BorderLayout.WEST);
        controlParameters.add(inputs, BorderLayout.CENTER);
        this.add(controlParameters, BorderLayout.NORTH);

        buttons.add(btnSendTriggers);
        buttons.add(btnCancel);

        controlButtonsAndStatus.add(buttons, BorderLayout.NORTH);

        status.setEditable(false);
        status.setBorder(new TitledBorder("Status"));
        controlButtonsAndStatus.add(new JScrollPane(status));

        this.add(controlButtonsAndStatus, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnSendTriggers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cam.sendTriggers(txtAddress.getText(), txtPort.getText(),
                                 txtHertz.getText(), txtDuration.getText(),
                                 status, btnCancel);
            }
        });
    }
    
    /**
     * Get value stored in address field
     * @return The address stored in the address field
     */
    public String getAddressStr() {
        return txtAddress.getText();
    }
    
    /**
     * Get value stored in port field
     * @return The port stored in the port field
     */
    public String getPortStr() {
        return txtPort.getText();
    }
    
    /**
     * Get the status area to allow threads to update status
     * @return 
     */
    public JTextArea getStatusArea() {
        return status;
    }
}
