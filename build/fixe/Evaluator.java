import java.util.ArrayList;

public class Evaluator {
	public static double mean(ArrayList<Integer> ints) {
		if (ints.size() != 0) {
			int curr = 0;
			for (int i = 0; i < ints.size(); i++) {
				curr += ints.get(i);
			}
			return curr/ints.size();
		}
		return -1;
	}
	public static int max(ArrayList<Integer> ints) {
		int curr = -1;
		for (int i = 0; i < ints.size(); i++) {
			if (ints.get(i) > curr) {
				curr = ints.get(i);
			}
		}
		return curr;
	}
	public static int min(ArrayList<Integer> ints) {
		int curr = Integer.MAX_VALUE;
		for (int i = 0; i < ints.size(); i++) {
			if (ints.get(i) < curr) {
				curr = ints.get(i);
			}
		}
		return curr;
	}
	public static String math_result (String func, Variable x, int i, int j, boolean four) {
		ArrayList<Integer> vals = x.value;
		ArrayList<Integer> sub;
		if (four) {
			sub = sublist(x.value, i, j);
			if (i > vals.size() | j > vals.size() | i < 0 | j < 0 | i <= j) {
				return "FAILED";
			}
		}
		sub = x.value;
		if (func.equals("mean")) {
			return Double.toString(mean(sub));
		}else if (func.equals("min")) {
			return Integer.toString(min(sub));
		}else if (func.equals("max")) {
			return Integer.toString(max(sub));
		}else if (func.equals("count")) {
			return Integer.toString(count(sub));
		}
		return "";
	}
	public static ArrayList<Integer> sublist (ArrayList<Integer> list, int i, int j){ // i <= j
		ArrayList<Integer> sub = new ArrayList<Integer>();
		for (int k = i; k <= j; k++) {
			sub.add(list.get(k));
		}
		return sub;
	}
	public static int count(ArrayList<Integer> value) {
		return value.size();
	}

	public static String eval(String s) {
		//System.out.println(s);
		String[] spl = s.trim().split(" ");
		if (spl.length == 1) { // value: x or i or x.i or
			if (spl[0].matches("d[-+]?\\d+(\\.\\d+)?")) { //value:i
				return s;
			}else if (s.contains(".")){ //value: x.i
				String[] spl2 = s.trim().split("\\.");
				//int index = Integer.parseInt(spl2[1]);
				int index = Integer.parseInt(eval(spl2[1]));
				
				int varIndex = Reader.getVarIndex(spl2[0]);
				int locIndex = Reader.getLocalIndex(spl2[0]);
				
				if (varIndex != -1) {
					return Integer.toString(Reader.getVariable(spl2[0]).value.get(index));
				} else if (locIndex != -1) {
					return Integer.toString(Reader.getLocal(spl2[0]).value.get(index));
				}
			} else if (spl[0].matches("-?\\d+(\\.\\d+)?")) { // returns a literal number
				return spl[0];
			} else if (spl[0].matches("[\\w]+")) { // returns the value of x
				int varIndex = Reader.getVarIndex(spl[0]);
				int locIndex = Reader.getLocalIndex(spl[0]);
				if (varIndex != -1) {
					return Integer.toString(Reader.getVariable(spl[0]).value.get(0));
				} else if (locIndex != -1) {
					return Integer.toString(Reader.getLocal(spl[0]).value.get(0));
				}
			} else {
				return "FAILED";
			}
		}else if (spl.length == 2) { // func x
			String func = spl[0];
			Variable var = Reader.getVariable(spl[1]);
			return math_result(func, var, 0, 0, false);
		}else if (spl.length == 3) { // X + Y
			int val1;
			int val2;
			if (!spl[0].matches("-?\\d+(\\.\\d+)?")) {
				val1 = Integer.parseInt(eval(spl[0]));
			} else {
				val1 = Integer.parseInt(spl[0]);
			}
			
			if (!spl[2].matches("-?\\d+(\\.\\d+)?")) {
				val2 = Integer.parseInt(eval(spl[2]));
			} else {
				val2 = Integer.parseInt(spl[2]);
			}
			
			if (s.contains("+")) {
				return Integer.toString(val1+val2);
			}else if (s.contains("-")) {
				return Integer.toString(val1-val2);
			}else if (s.contains("*")) {
				return Integer.toString(val1*val2);
			}else if (s.contains("/")) {
				return Double.toString(val1/val2);
			}
		}else if (spl.length == 4) {
			String func = spl[0];
			Variable var = Reader.getVariable(spl[1]);
			int i = Integer.parseInt(spl[2]);
			int j = Integer.parseInt(spl[3]);
			return math_result(func, var, j, i, false);
		}
		return "FAILED";
	}
}
