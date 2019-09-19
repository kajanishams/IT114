import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;


public class QueueHW {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Queue<Test> q = new LinkedList<Test>();
		List<String> t = new ArrayList<String>();
		t.add("apple");
		t.add("orange");
		t.add("banana");
		t.add("kiwi");
		for (int i=0; i <10; i++) {
			q.add(new Test(i, ""));
		}
		
		System.out.println("Show queue: " +q);
		System.out.println();
		Test remove  = q.remove();
		System.out.println("Removed queue: " +remove);
		
		System.out.println(q);
		System.out.println();
		
		Test peek = q.peek();
		System.out.println("First in queue: " +peek);
		
		System.out.println();
		
		int size = q.size();
		System.out.println("Size of queue: " +size);
		System.out.println();
		
		System.out.println("Queue now: " +q);

		System.out.println();
		Test poll = q.poll();
		System.out.println("Head of queue:"+ poll);
		
	}

}
class Test{
	public int n;
	public String value;
	public Test(int k, String v) {
		this.n =k;
		this.value =v;
		
	}
	@Override
	public String toString () {
		return "{'key':'" + this.n + "', 'value':'"+ this.value + "'}";
	}
}
