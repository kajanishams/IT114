package com.example.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Class to hold client connection and prevent it from blocking the main thread of the server
public class ServerThread extends Thread{
	private Socket client;//server's reference to the connect client
	private String clientName;//this connected client's name
	public String encryptionkey;
	public String decryptionkey;
	MyUI ui;
	//Object streams that let us pass objects
	private ObjectInputStream in;//from client
	private ObjectOutputStream out;//to client
	private boolean isRunning = false;//boolean to control termination
	SampleSocketServer server;
	SampleSocketClient cl;
	public ServerThread(Socket myClient, String clientName, SampleSocketServer server) throws IOException {
		this.client = myClient;
		this.clientName = clientName;
		this.server = server;
		isRunning = true;
		out = new ObjectOutputStream(client.getOutputStream());
		in = new ObjectInputStream(client.getInputStream());
		System.out.println("Spawned thread for client " + clientName);
		//broadcast connect to other players
		server.broadcast(new Payload(PayloadType.CONNECT, clientName), clientName);
		
	}
	@Override
	public void run() {
		try{
			Payload fromClient;
			//if disconnected, in.readObject will throw an EOFException
			while(isRunning 
					&& !client.isClosed() 
					&& (fromClient = (Payload)in.readObject()) != null) {
				//received a payload, handle it
				processPayload(fromClient);
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Terminating client");
		}
		finally {
			System.out.println("Server cleaning up IO for " + clientName);
			stopThread();
			cleanup();
		}
	}
	public String getClientName() {
		return this.clientName;
	}
	void processPayload(Payload payload) {
		System.out.println("Received: " + payload);
		switch(payload.payloadType) {
			case MESSAGE:
               // final String secretkey = "passwordpassword";
                String encryptedString = SampleSocketServer.encrypt(payload.message, encryptionkey);
                Payload toClient = new Payload(PayloadType.MESSAGE, encryptedString);
                System.out.println("Sending: " + toClient.toString());
                server.broadcast(toClient,clientName);
                
                break; 
			
			case TEXT:
				//final String secret = "passwordpassword";
				String encryptedString1 = SampleSocketServer.encrypt(payload.message, encryptionkey);
                Payload toClient1 = new Payload(PayloadType.MESSAGE,clientName + ": " + encryptedString1);
                System.out.println("Sending: " + toClient1.toString());
                server.broadcast(toClient1);
				
				break;
			case CONNECT:
				String[] keys = payload.message.split(":");
				encryptionkey = keys[0];
				decryptionkey = keys[1];
				break;
			
			case DISCONNECT:
				System.out.println("Removing client " + clientName);
				server.removeClient(this);
				server.broadcast(new Payload(PayloadType.DISCONNECT,""));
				stopThread();
				break;
			default:
				break;
		}
	}

	
	public void stopThread() {
		isRunning = false;
	}
	/***
	 * Returns true if we lost connection to our client
	 * @return
	 */
	public boolean isClosed() {
		return client.isClosed();
	}
	public void send(Payload p) {
		try {
			System.out.print("Sending" + p.message);
			out.writeObject(p);
		} catch (IOException e) {
			System.out.println("Error sending payload to client");
			e.printStackTrace();
			cleanup();
		}
	}
	private void cleanup() {
		if(in != null) {
			try{in.close();}
			catch(Exception e) { System.out.println("Input already closed");}
		}
		if(out != null) {
			try {out.close();}
			catch(Exception e) {System.out.println("Output already closed");}
		}
		//most likely not necessary since should all be closed already
		if(client != null && !client.isClosed()) {
			//try close input
			try {client.shutdownInput();} 
			catch (IOException e) {System.out.println("Socket/Input already closed");}
			//try close output
			try {client.shutdownOutput();}
			catch (IOException e) {System.out.println("Socket/Output already closed");}
			//try close socket
			try {client.close();}
			catch (IOException e) {System.out.println("Socket already closed");}
		}
		System.out.println("Client " + clientName + " has been cleaned up");
	}
}
