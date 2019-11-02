import java.security.Key;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Test {
	

	public static byte[] encrypt(String value) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter key");
		
		final String key = sc.nextLine();
		final String initVector = "encryptionIntVec";
		
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			byte bytes[] = {1,3,7,8,1,2,5,6,3,4,2,9,1,2,3,4};
			SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encode(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(byte[] encryptedString) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter key");
		
		String key = sc.nextLine();
		String initVector = "encryptionIntVec";
		
		
		
			
		
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			byte bytes[] = {1,3,7,8,1,2,5,6,3,4,2,9,1,2,3,4};
			SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
			
			return new String(original);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("Enter something: ");
		Scanner sc = new Scanner(System.in);
		String originalString = sc.nextLine();
		System.out.println("Original String to encrypt - " + originalString);
		byte[] encryptedString = encrypt(originalString);
		System.out.println("Encrypted String - " + encryptedString);
		String decryptedString = decrypt(encryptedString);
		System.out.println("After decryption - " + decryptedString);
	}
}
	
	
