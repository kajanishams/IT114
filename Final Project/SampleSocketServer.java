package com.example.sockets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SampleSocketServer{
	int port = -1;
	//private Thread clientListenThread = null;
	List<ServerThread> clients = new ArrayList<ServerThread>();
	static String encryptedString;
	static String decryptedString;
	public static boolean isRunning = true;
	public SampleSocketServer() {
		isRunning = true;
	}
	/***
	 * Send the same payload to all connected clients.
	 * @param payload
	 */
	public synchronized void broadcast(Payload payload) {
		//iterate through all clients and attempt to send the message to each
		System.out.println("Sending message to " + clients.size() + " clients");
		//TODO ensure closed clients are removed from the list
		for(int i = 0; i < clients.size(); i++) {
			clients.get(i).send(payload);
		}
	}
	public void removeClient(ServerThread client) {
		Iterator<ServerThread> it = clients.iterator();
		while(it.hasNext()) {
			ServerThread s = it.next();
			if(s == client) {
				System.out.println("Matched client");
				it.remove();
			}
			
		}
	}
	void cleanupClients() {
		if(clients.size() == 0) {
			//we don't need to loop or spam if we don't have clients
			return;
		}
		//use an iterator here so we can remove elements mid loop/iteration
		Iterator<ServerThread> it = clients.iterator();
		System.out.println("Start Cleanup count " + clients.size());
		while(it.hasNext()) {
			ServerThread s = it.next();
			if(s.isClosed()) {
				//payload should have some value to tell all "other" clients which client disconnected
				//so they can clean up any local tracking/refs or show some sort of feedback
				broadcast(new Payload(PayloadType.DISCONNECT, null));
				s.stopThread();
				it.remove();
			}
		}
		System.out.println("End Cleanup count " + clients.size());
	}
	/***
	 * Send a payload to a client based on index (basically in order of connection)
	 * @param index
	 * @param payload
	 */
	public synchronized void sendToClientByIndex(int index, Payload payload) {
		//TODO validate index is in bounds
		clients.get(index).send(payload);
	}
	/***
	 * Send a payload to a client based on a value defined in ServerThread
	 * @param name
	 * @param payload
	 */
	public synchronized void sendToClientByName(String name, Payload payload) {
		for(int i = 0; i < clients.size(); i++) {
			if(clients.get(i).getClientName().equals(name)) {
				clients.get(i).send(payload);
				break;//jump out of loop
			}
		}
	}
	/***
	 * Separate thread to periodically check to clean up clients that may have been disconnected
	 */
	void runCleanupThread() {
		Thread cleanupThread = new Thread() {
			@Override
			public void run() {
				while(SampleSocketServer.isRunning) {
					cleanupClients();
					try {
						Thread.sleep(1000*30);//30 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Cleanup thread exited");
			}
		};
		cleanupThread.start();
	}
	private void start(int port) {
		this.port = port;
		System.out.println("Waiting for client");
		runCleanupThread();
		try(ServerSocket serverSocket = new ServerSocket(port);){
			while(SampleSocketServer.isRunning) {
				try {
					Socket client = serverSocket.accept();
					System.out.println("Client connected");
					
					
					ServerThread thread = new ServerThread(client, 
							"Client[" + clients.size() + "]",
							this);
					thread.start();//start client thread
					clients.add(thread);//add to client pool
					
					
                    
					
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isRunning = false;
				Thread.sleep(50);
				System.out.println("closing server socket");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
 
            
        
    
    
    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
           
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
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
   
	
	public static void main(String[] arg) {
		System.out.println("Starting Server");
		SampleSocketServer server = new SampleSocketServer();
		int port = -1;//port should be coming from command line arguments
		if(arg.length > 0){
			try{
				port = Integer.parseInt(arg[0]);
			}
			catch(Exception e){
				System.out.println("Invalid port: " + arg[0]);
			}		
		}
		if(port > -1){
			System.out.println("Server listening on port " + port);
			server.start(port);
		}
		System.out.println("Server Stopped");
	}
}
