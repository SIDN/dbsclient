/*
 * $Id: DbsRestClient.java $
 *
 * Copyright (c) 2015, SIDN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * 
 * @author: $Author: $
 * @version: $Revision:  $
 *  
 */
package nl.sidn.dbsrestclient;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.MediaType;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import nl.sidn.dbsrestclient.elements.CompanyFeedResponse;
import nl.sidn.dbsrestclient.elements.FeedResponse;
import nl.sidn.dbsrestclient.elements.FeedResult;
import nl.sidn.dbsrestclient.elements.RestcallFailedException;

/**
 * DbsRestClient
 * A Java Swing GUI interface for the REST client for DBS 
 */
public class DbsRestClient extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_DBS_URL = "http://dbs.domain-registry.nl/rest/results";
	private static final String[] COLUMNNAMES = {"Domain name", "Registration date", "Modification date", "Detection date", "Status", "Keyword"};
	
	//GUI components
	private JButton sendButton;
	private JTextField url;
	private JTextField username;
	private JPasswordField password;
	private JDatePanelImpl datePickerPanel;
	private JDatePickerImpl date;
	private JRadioButton json;
	private JRadioButton xml;
	private JLabel companyName;
	private JButton nextCompanyButton;
	private JButton previousCompanyButton;
	private JTable outputTable;
	private DefaultTableModel tableModel;
	private JLabel noError;
	private JLabel error;
	
	private int companyView;
	
	//The DBS service
	private DbsService dbsService;
	private FeedResponse feedResponse;
	
	/**
	 * Constructor. Lays out all swing components on this JFrame.
	 */
	public DbsRestClient() {
		dbsService = new DbsService();
		
		JPanel myPanel = new JPanel();
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));
		myPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		//Input header
		createHeader(myPanel, "Input");
		
		//Input panel
		createInputPanel(myPanel);
		
		//Output header
		createHeader(myPanel, "Output");
        
		//Output panel
		createOutputPanel(myPanel);

		this.add(myPanel);
		
		this.setTitle("DBS REST client");
	}
	
	/**
	 * Creates a header
	 * @param parent Jpanel to add this header to
	 * @param title Title of this header to be created
	 */
	private void createHeader(JPanel parent, String title) {
		parent.add(Box.createVerticalStrut(5));
		parent.add(new JSeparator(SwingConstants.HORIZONTAL));		
		parent.add(new JLabel(title));
		parent.add(new JSeparator(SwingConstants.HORIZONTAL));
		parent.add(Box.createVerticalStrut(5));
	}
	
	/**
	 * Creates an input panel containing input swing elements.
	 * @param parent to add this InputPanel to
	 */
	private void createInputPanel(JPanel parent) {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(0, 2, 10, 10));
		
		//URL
		inputPanel.add(new JLabel("DBS URL:"));
		url = new JTextField();
		url.setText(DEFAULT_DBS_URL);
		inputPanel.add(url);
		
		//Username
		inputPanel.add(new JLabel("Username:"));
		username = new JTextField();
		inputPanel.add(username);
		
		//Password
		inputPanel.add(new JLabel("Password:"));
		password = new JPasswordField();
		inputPanel.add(password);
		
		//Date
		inputPanel.add(new JLabel("Only retrieve results detected from:"));
		JPanel datePanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		UtilDateModel model = new UtilDateModel();
		datePickerPanel = new JDatePanelImpl(model);
		date = new JDatePickerImpl(datePickerPanel);
		datePanel2.add(date);
		inputPanel.add(datePanel2);
		
		//Media type
		JPanel mediaTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel("Media-type:"));
		ButtonGroup group = new ButtonGroup();
		json = new JRadioButton("Json");
		json.setSelected(true);
		mediaTypePanel.add(json);
		xml = new JRadioButton("XML");
		mediaTypePanel.add(xml);
		group.add(json);
		group.add(xml);
		inputPanel.add(mediaTypePanel);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		inputPanel.add(sendButton);
		
		parent.add(inputPanel);
	}
	
	/**
	 * Creates an output panel containing input swing elements.
	 * @param parent to add this OutputPanel to
	 */
	private void createOutputPanel(JPanel parent) {
		JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//Add ok text
		noError = new JLabel("HTTP-200 Ok");
		noError.setForeground(Color.GREEN);
		noError.setVisible(false);
		messagePanel.add(noError);
		
		//Add error text
		error = new JLabel("");
		error.setForeground(Color.red);
		messagePanel.add(error);
		parent.add(messagePanel);
		
		//Add company name
		JPanel companyPanel = new JPanel();
		companyPanel.setLayout(new GridLayout(0, 4, 10, 10));
		companyPanel.add(new JLabel("Company:"));
		companyName = new JLabel("");
		companyPanel.add(companyName);
		
		//Add next + previous button
		previousCompanyButton = new JButton("<< Previous company");
		previousCompanyButton.addActionListener(this);
		previousCompanyButton.setEnabled(false);
		companyPanel.add(previousCompanyButton);
		nextCompanyButton = new JButton("Next company >>");
		nextCompanyButton.addActionListener(this);
		nextCompanyButton.setEnabled(false);
		companyPanel.add(nextCompanyButton);
		
		//Add Outputtable
		tableModel = new DefaultTableModel(COLUMNNAMES, 0);
		outputTable = new JTable(tableModel);
		outputTable.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(outputTable);
		parent.add(companyPanel);
		parent.add(scrollPane);
	}
	
	/**
	 * Actionperformed method. Called when the user clicks on a button.
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == sendButton) {
			//If Send button was clicked
			
			//Set parameters for DBS call
			dbsService.setUrl(url.getText());
			dbsService.setUsername(username.getText());
			dbsService.setPassword(new String(password.getPassword()));
			dbsService.setMediaType(xml.isSelected()?MediaType.APPLICATION_XML_TYPE:MediaType.APPLICATION_JSON_TYPE);
			Date fromDate = (Date) datePickerPanel.getModel().getValue();
			
			companyView = 0;			
			try {
				error.setVisible(false);
				noError.setVisible(true);

				//Call DBS
				feedResponse = dbsService.callDBSService(fromDate);
			
				//Call went fine. Display results.
				updateOutputFields();
			}
			catch (RestcallFailedException ex) {
				//Something went wrong. Display error.
				error.setText(ex.getMessage());
				error.setVisible(true);
				noError.setVisible(false);
				feedResponse = null;
				updateOutputFields();
			}			
			
		} else if (event.getSource() == nextCompanyButton) {
			//Next button was clicked
			++companyView;
			updateOutputFields();
		} else if (event.getSource() == previousCompanyButton) {
			//Previous button was clicked
			--companyView;
			updateOutputFields();
		}
	}
	
	/**
	 * Updates all Swing output components in the JFrame with the input from variable 'feedResponse'.
	 */
	private void updateOutputFields() {
		previousCompanyButton.setEnabled(companyView > 0);
		
		if (feedResponse != null && feedResponse.getCompanies().size() > companyView) {
			nextCompanyButton.setEnabled(companyView < feedResponse.getCompanies().size() - 1);
			CompanyFeedResponse company = feedResponse.getCompanies().get(companyView);					
			
			companyName.setText(company.getCompanyName());
			
			List<FeedResult> results = company.getResults();
			if (results == null) {
				tableModel.setNumRows(0);
			} else {
				tableModel.setNumRows(results.size());
	
				int rownum = 0;
				for (FeedResult result : results) {
					tableModel.setValueAt(result.getDomainName(), rownum, 0);
					tableModel.setValueAt(result.getRegistrationDate(), rownum, 1);
					tableModel.setValueAt(result.getModificationDate(), rownum, 2);
					tableModel.setValueAt(result.getDetectionDate(), rownum, 3);
					tableModel.setValueAt(result.getDomainState(), rownum, 4);
					tableModel.setValueAt(result.getKeyword(), rownum, 5);
					++rownum;
				}
			}
		} else {
			//Erase all fields
			companyName.setText("");
			tableModel.setNumRows(0);
			previousCompanyButton.setEnabled(false);
			nextCompanyButton.setEnabled(false);
		}
	}

	
	/**
	 * Main method to start the DBS Rest client
	 * @param args
	 */
	public static void main(String[] args) {
        DbsRestClient myClient = new DbsRestClient();
        myClient.setSize(800, 800);
        myClient.setLocationRelativeTo(null);
        myClient.setVisible(true);
        myClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
