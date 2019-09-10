import java.util.Date;

public class MyFirstClass {
	public int myPublicInt;
	private int myPrivateInt;
	protected int test3;
	
	public String myString;
	public char myChar;
	public Date myDate;
	
	
	public static void main(String[] args) {
		MyFirstClass myFirstClassobject = new MyFirstClass();
		myFirstClassobject.myPublicInt = 1 ;
		System.out.print(myFirstClassobject.myPublicInt);
	}
		
	}
