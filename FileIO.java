
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class FileIO {
	public static void main(String[] args) throws IOException {
		
		File file = new File("C:\\Users\\kajani_shams\\Documents\\IT114\\IT114\\text.txt");
		  
		//Num 1
		if (file.createNewFile())
		{
		    System.out.println("File is created");
		} else {
		    System.out.println("File already exists");
		}
		 

		//Num 2 and 3
		if (file.canRead()) {
			System.out.println(file + " is readable");
		}
		else {
			System.out.println(file + " is not readable");
		
		}
		if(file.canWrite()) {
			System.out.println(file + " is writable");
		}
		else {
			System.out.println(file + " is not writable");
		}
		
		
		FileWriter fw = new FileWriter(file);
		fw.write("This is number 2 and 3. 2 + 2 = 4");
		fw.close();
	
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		  
		String string; 
		string = br.readLine();
		System.out.println(string); 
		
		//Num 4
		String takeoutnum = string.replaceAll("[1-9]", "#");
		String noequals = takeoutnum.replaceAll(" =", " equals ");
		System.out.println("Strings in file: " + noequals);
	
		
		
	   
		br.close();
		
	}
}