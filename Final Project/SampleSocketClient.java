package com.example.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SampleSocketClient {
	Socket server;
	public SampleSocketClient() {
		
	}
	public void connect(String address, int port) {
		try {
			server = new Socket(address, port);
			System.out.println("Client connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start() throws IOException {
		if(server == null) {
			return;
		}
		System.out.println("Listening for input");
		try(Scanner si = new Scanner(System.in);
				PrintWriter out = new PrintWriter(server.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));){
			String keycheck = "";
			String fromServer = "";
			
			int lineInput = 0;
			
			
			
			System.out.println("Enter message for server");
			String mess = si.nextLine();
			out.println(mess);
			
			while(true) {
				
				if (lineInput == 0) {
					System.out.println("Waiting for input key");
					lineInput+=1;
				}
				else {
					System.out.println("Enter key to decrypt");
					String dkey = si.nextLine();
					out.println(dkey);
				
				}
				
				keycheck = si.nextLine();
				
				try {
					
					if(!"quit".equalsIgnoreCase(keycheck)) {
						out.println(keycheck);
					}
					else {
						break;
					}
					
					fromServer = in.readLine();
					
					if(fromServer != null) {
						System.out.println("Reply from server: " + fromServer);
						
					}
					else {
						System.out.println("Server disconnected");
						break;
					}
				}
				catch(IOException e) {
					System.out.println("Connection dropped");
					break;
				}
			}
			System.out.println("Exited loop");
			}
		
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	private void close() {
		if(server != null) {
			try {
				server.close();
				System.out.println("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		SampleSocketClient client = new SampleSocketClient();
		int port = 3009;
		try{
			//not safe but try-catch will get it
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e){
			System.out.println("Invalid port");
		}
		if(port == -1){
			return;
		}
		client.connect("127.0.0.1", port);
		try {
			//if start is private, it's valid here since this main is part of the class
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}