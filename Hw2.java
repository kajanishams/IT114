import java.util.*;
import java.util.Random;

public class Hw2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//List of strings
		List<String> theStrings = new ArrayList<String>(); 
		theStrings.add("Eel");
		theStrings.add("Cat");
		theStrings.add("Dog");
		theStrings.add("Fish");
		theStrings.add("Lizard");
		theStrings.add("Ant");
		
		//reverse sort
		Collections.sort(theStrings, Collections.reverseOrder());
		
		System.out.println("Before: " +theStrings);
		
		//shuffle list
		Collections.shuffle(theStrings);
		
		
		System.out.println("After: " +theStrings);
		//List of numbers
		
		List<Integer> theInt = new ArrayList<Integer>();
		for (int i=0; i < 10; i++) {
			theInt.add(i);
		}
		int total =0;
		for (int i=0; i < theInt.size(); i++) {
			total += theInt.get(i);
		}
		System.out.println();
		System.out.println("The Total is " + total);
		System.out.println();
		
		//Separate Test (odd or even)
		for (int i =0; i <10; i++) {
			if(i%2==0) {
				System.out.println(i +" is Even");
			}else {
				System.out.println(i +" is Odd");
				
			}
		
		}
		
		Random rand = new Random();
		int randomInt = rand.nextInt(theStrings.size());
		
		for (int x=0; x < theStrings.size(); x++) {
			Collections.swap(theStrings, x, randomInt);	
		}
		System.out.println();
		System.out.println(theStrings);
			
	
	}
}
