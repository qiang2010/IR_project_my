package test;

import java.util.ArrayList;



public class Test_arrayList_remove {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testArray();
	}
	static void testArray(){
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(2);
		a.add(4);
		a.add(7);
		a.add(9);
		a.remove(1);
		System.out.println(a+ " "+ a.get(2));
		
	}
}
