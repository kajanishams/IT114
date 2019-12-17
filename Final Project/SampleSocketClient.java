package com.example.sockets;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SampleSocketClient {
	public String dkey;
	MyUI ui;
	public String keycheck;
	Scanner si = new Scanner(System.in);
	Socket server;
	static ObjectOutputStream out;
	public Queue<String> messages = new LinkedList<String>();
	public void connect(String address, int port) throws IOException {
		try {
			//create new socket to destination and port
			server = new Socket(address, port);
			System.out.println("Client connected");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void sendText(String text) {
		try {
				out.writeObject(new Payload(PayloadType.MESSAGE, text));
				messages.add(text);
				
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public void start() throws IOException {
		if(server == null) {
			return;
		}
		System.out.println("Client Started");
		//listen to console, server in, and write to server out
		try(ObjectInputStream in = new ObjectInputStream(server.getInputStream());){
			out = new ObjectOutputStream(server.getOutputStream());
			//Thread to listen for responses from server so it doesn't block main thread
			Thread fromServerThread = new Thread() {
				@Override
				public void run() {
					try {
						/*
						keycheck = "";
						System.out.println("Enter encryption key: ");
						keycheck = si.nextLine();	
						out.writeObject(new Payload(PayloadType.KEY, keycheck));
						
						dkey = "";
						System.out.println("Enter decryption key");
						dkey = si.nextLine();
						
						System.out.println("Enter message for server");
						
						
						
						while (!server.isClosed()) 
						{
						
							String toServer ="";
							toServer = si.nextLine();
							
							 if(!"quit".equalsIgnoreCase(toServer) && toServer != null) 
							 {
	                                //grab line and throw it into a payload object
	                                
								 	
	                                out.writeObject(new Payload(PayloadType.MESSAGE, toServer));
	                                
	                               
							 }
						*/
						Payload fromServer;
						
						//while we're connected, listen for payloads from server
						while(!server.isClosed() && (fromServer = (Payload)in.readObject()) != null) 
						{
							
							
							processPayload(fromServer);
							
							
						}
						
				
						
						
						System.out.println("Stopping server listen thread");
					}
					catch (Exception e) {
						if(!server.isClosed()) {
							e.printStackTrace();
							System.out.println("Server closed connection");
						}
						else {
							System.out.println("Connection closed");
						}
					}
					finally {
						close();
					}
				}
				
				
			};
			fromServerThread.start();//start the thread
			
			//Keep main thread alive until the socket is closed
			while(!server.isClosed()) {
				Thread.sleep(50);
			}
			System.out.println("Exited loop");
			throw new Exception("Connection loop exited");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	/**
	 * Handle our different payload types.
	 * You may create functions for each case to help organize code or keep it cleaner
	 * @param p
	 * 
	 */
	
	void processPayload(Payload p) throws IOException {
		System.out.println("Received: " + p.message);
		switch(p.payloadType) {
			case CONNECT:
				System.out.println("A client connected");
				messages.add("A client connected");
				break;
			case DISCONNECT:
				System.out.println("A client disconnected");
				messages.add("A client disconnected");
				break;
			case MESSAGE:
				
				System.out.println(p.message);
				
				messages.add(p.message);

				break;
			case TEXT:
				System.out.println("Got text " + p.message);
				messages.add(p.message);
				break;
			case UPDATE_NAME:
				System.out.println("Got name " + p.message);
				messages.add("[name]"+p.message);
				break;
			default:
				System.out.println("We aren't handling payloadType " + p.payloadType.toString());
				break;
		}
	}

	
	public void sendKeys(String enc, String dec) {
		try {
			out.writeObject(new Payload(PayloadType.CONNECT, enc + ":" + dec));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static SecretKeySpec secretKey;
    private static byte[] key;
    
    
    
	 public static void setKey(String myKey)
	    {
	        
	        MessageDigest sha = null;
	        try {
	            key = myKey.getBytes("UTF-8");
	            sha = MessageDigest.getInstance("SHA-256");
	            key = sha.digest(key);
	            key = Arrays.copyOf(key, 16);
	            secretKey = new SecretKeySpec(key, "AES");
	        }
	        catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	    }
	 
	
	
	public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
        	
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
	/*
	void processFromServer(Payload p) {
		switch (p.payloadType){
			case MESSAGE:
				String[] m = p.message.split(":");
				String msg = m[1].trim();
				System.out.println("The encrypted string is \n" + msg);
				System.out.println();
				
				
				OutputOrAppend(decrypt(msg, keycheck));
				break;
			default:
				System.out.println("error");
				break;
	}
	}
	
*/


	public void disconnect() {
		close();
	}
	private void close() {
		if(out != null) {
			try {
				out.writeObject(new Payload(PayloadType.DISCONNECT, ""));
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(server != null && !server.isClosed()) {
			try {
				server.close();
				System.out.println("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Returns true if never connected or true/false if still connected
	 * @return
	 */
	public boolean isStillConnected() {
		if(server == null || !server.isConnected()) {
			return true;//
		}
		return !server.isClosed();
	}
	
	public static void main(String[] args) {
		//only worry about this if we're running from command line
		//our UI will use connect() and start() methods
		SampleSocketClient client = new SampleSocketClient();
		//grab host:port from commandline
		//TODO this was reworked, please take note
		String host = null;
		int port = -1;

		try{
			//not safe but try-catch will get it
			if(args[0].indexOf(":") > -1) {
				String[] target = args[0].split(":");
				host = target[0].trim();
				port = Integer.parseInt(target[1].trim());
			}
			else {
				System.out.println("Important!: Please pass the argument as hostname:port or ipaddress:port");
			}
		}
		catch(Exception e){
			System.out.println("Error parsing host:port argument[0]");
		}
		if(port == -1 || host == null){
			return;
		}
		
		try {
			client.connect(host, port);
			//if start is private, it's valid here since this main is part of the class
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
