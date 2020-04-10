//package src.bi2;

import java.util.ArrayList;

public class Variable {
	String name;
	ArrayList<Integer> value;
	boolean fail = false;
	
	public Variable(String name, String value) {
		this.name = name;
		this.value = new ArrayList<Integer>();
		
		if (value.contains(".")) {
			this.value.add((int) Double.parseDouble(value));
		} else if (value.equals("FAILED")){
			this.fail = true;
		} else {
			//System.out.println(value);
			this.value.add(Integer.parseInt(value));
		}
	}

	public Variable(String name, ArrayList<Integer> ints) {
		this.name = name;
		this.value = ints;
	}
	
	public void addValue(String value) {
		if (value.contains(".")) {
			this.value.add(0, (int) Double.parseDouble(value));
		} else if (value.equals("FAILED")){
			this.fail = true;
		} else {
			System.out.println(value);
			this.value.add(0, Integer.parseInt(value));
		}
		//System.out.println(this.value);
	}
	
	public void empty() {
		this.value.clear();
	}
	
	/*
	 * Returns the latest value ie. the first value.
	 */
	public int getLatest() {
		return value.get(0);
	}
	
	public ArrayList<Integer> sublist (ArrayList<Integer> list, int i, int j){ // i <= j
		ArrayList<Integer> sub = new ArrayList<Integer>();
		for (int k = i; k <= j; k++) {
			sub.add(list.get(k));
		}
		return sub;
	}
	
	public int count() {
		return value.size();
	}
	
}
