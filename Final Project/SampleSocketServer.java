package com.example.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SampleSocketServer {
	int port = 3102;
	static String encryptedString;
	static String decryptedString;
	
	public SampleSocketServer() {
	}
	
	private void start(int port) 
	{
		
		this.port = port;
		System.out.println("Waiting for client");
		
		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket client = serverSocket.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));) {
			
			System.out.println("Client connected");
				String fromClient ="";
				String toClient="";
				String secretKey = in.readLine();
				
				while ((fromClient = in.readLine()) != null)
				{
					
					encryptedString = SampleSocketServer.encrypt(fromClient,secretKey);
					out.println(encryptedString);
					out.flush();
					
				//checks if the key that client inputed matches the secret key
					if(in.readLine().contentEquals(secretKey)) {
						System.out.println("The key matches: Here is the original text: ");
						decryptedString = SampleSocketServer.decrypt(encryptedString,secretKey);
						out.println(decryptedString);
					}
					
	
					
					if ("kill server".equalsIgnoreCase(fromClient)) {
					
					
						System.out.println("Server received kill command, disconnecting");
						break;
					}
					else {
						out.println(toClient);
					}
				
				}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
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
		int port = 3009;
		
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