package com.example.sockets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class MyUI {
	
	public static boolean isRunning = true;
	public static void main(String[] args) {
		
		//create frame
		JFrame frame = new JFrame("Encryption Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		//create panel
		JPanel rps = new JPanel();
		rps.setPreferredSize(new Dimension(700,700));
		rps.setLayout(new BorderLayout());
		//create text area for messages
		JTextArea textArea = new JTextArea();
		//don't let the user edit this directly
		textArea.setEditable(false);
		textArea.setText("");
		//create panel to hold multiple controls
		JPanel attemptsArea = new JPanel();
		attemptsArea.setLayout(new BorderLayout());
		//add text area to history/attempts
		attemptsArea.add(textArea, BorderLayout.CENTER);
		attemptsArea.setBorder(BorderFactory.createLineBorder(Color.black));
		//add history/attempts to panel
		rps.add(attemptsArea, BorderLayout.CENTER);
		//create panel to hold multiple controls
		JPanel userInput = new JPanel();
		
		
	
		
		//Interaction will be our instance to interact with
		//socket client
		Interaction interaction = new Interaction();
		Thread clientMessageReader = new Thread() {
			@Override
			public void run() {
				while(isRunning && interaction.isClientConnected()) {
					String m = interaction.getMessage();
					if(m != null) {
						System.out.println("Got message " + m);
						if(m.indexOf("[name]") > -1) {
							String[] n = m.split("]");
							frame.setTitle(frame.getTitle() + " - " + n[1]);
						}
						else {
							System.out.println("Appending to textarea");
							textArea.append(m +"\n");
						}
					}
				
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Message reader thread finished");
			}
			
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel connectionPanel = new JPanel();
		JTextField hostTextField = new JTextField();
		hostTextField.setText("127.0.0.1");
		JTextField portTextField = new JTextField();
		portTextField.setText("3000");
		
     
		
		JTextField errorTextField = new JTextField();
		errorTextField.setText("");
		JButton connect = new JButton();
		connect.setText("Connect");
		
		
		
		connect.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String hostStr = hostTextField.getText();
		        String portString = portTextField.getText();
		        
		      
		      
		        try {
		        	int port = Integer.parseInt(portString.trim());
		        	interaction.connect(hostStr, port, errorTextField);
		        	errorTextField.setText("Success!");
		        	connectionPanel.setVisible(false);
		        	clientMessageReader.start();
		        	System.out.println("Connected");
		        }
		        catch(Exception ex) {
		        	ex.printStackTrace();
		        	errorTextField.setText(ex.getMessage());
		        }
		    }
		});
	
		
		connectionPanel.add(hostTextField);
		connectionPanel.add(portTextField);
		connectionPanel.add(connect);
		connectionPanel.add(errorTextField);
		
		
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  	interaction.client.disconnect();
				    System.exit(0);
				  }
		});
				
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JTextField textField = new JTextField();
				textField.setPreferredSize(new Dimension(190,30));
				
				JLabel keylabel = new JLabel("Enter key: ");
				JTextField keyfield = new JTextField();
				keyfield.setPreferredSize(new Dimension(100,30));
				
				JButton k = new JButton();
				k.setPreferredSize(new Dimension(100,30));
				k.setText("Enter");
				
				//setup submit button
			
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(100,30));
				b.setText("Send");
				b.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String message = textField.getText();
						
						if(message.length() > 0) {
							//append a newline and the text from the textfield
							//to that textarea (simulate simple chatroom)
								
								interaction.sendText(message);
								String m = interaction.getMessage();
								textArea.append("\n ->" + m);
								
								
								/*
								textArea.append("\n -> " + message);
								textField.setText("");
								
								*/
								
							}
							
						}
				
				});
			k.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String clientkey = "password";
					String checkkey = keyfield.getText();
					if(checkkey.contentEquals(clientkey)) {
						textArea.append("\n Sucessful");
						
					}
					}
					
				});
		
		
		userInput.add(textField);
		userInput.add(b);
		userInput.add(keylabel);
		userInput.add(keyfield);// encryption key field
		userInput.add(k);
		
		//add panel to rps panel
		rps.add(userInput, BorderLayout.SOUTH);
		//add rps panel to frame
		frame.add(rps, BorderLayout.CENTER);
		frame.add(connectionPanel, BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
class Interaction {
	SampleSocketClient client;
	public Interaction() {
		
	}
	public void connect(String host, int port, JTextField errorField) throws IOException{
		//thread just so we don't lock up main UI
		Thread connectionThread = new Thread() {
			@Override
			public void run() {
				client = new SampleSocketClient();
				try {
					client.connect(host, port);
					client.start();
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					errorField.setText(e.getMessage());
					errorField.getParent().setVisible(true);
				}//this terminates when client is closed
				
				System.out.println("Connection thread finished");
			}
		};
		connectionThread.start();
	}
	
	
	public void sendText(String text) {
		
		client.sendText(text);
		
		
	}
	
	public boolean isClientConnected() {
		if(client == null) {
			return true;//just so loop doesn't die early
		}
		return client.isStillConnected();
	}
	public String getMessage() {
		if(client.messages == null) {
			return null;
		} 
		else {
		return client.messages.poll();
	}
	}
}