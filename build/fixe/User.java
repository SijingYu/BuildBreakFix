//package src.bi2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class User {
	String name;
	String pw;
	
	Map<Variable, int[]> rights;
	Map<Rule, int[]> rule_rights;
	
	public User(String name, String pw) {
		this.name = name;
		this.pw = pw;
		
		this.rights = new HashMap<Variable, int[]>();
		this.rule_rights = new HashMap<Rule, int[]>();
	}
	
	public User(Map<Variable, int[]> rights, Map<Rule, int[]> rule_rights, String name, String pw) {
		this.rights = rights;
		this.rule_rights = rule_rights;
		this.name = name;
		this.pw = pw;
	}
	
	
	
	public void add_right(Variable var, Reader.Right right) {
		// indexes correspond: 
		// 0:Read 1:Write 2:Delegate 3:Toggle
		int[] r = {0,0,0,0};
		boolean exists = false;
		
		if (rights.keySet().contains(var)) {
			r = rights.get(var);
			exists = true;
		}
		if (right == Reader.Right.READ) {
			r[0] = 1;
		} else if (right == Reader.Right.WRITE) {
			r[1] = 1;
		} else if (right == Reader.Right.DELEGATE) {
			r[2] = 1;
		} else if (right == Reader.Right.TOGGLE) {
			r[3] = 1;
		}
		if (exists) {
			rights.replace(var, r);
		} else {
			rights.put(var, r);
		}
		//System.out.println(rightString());
	}
	
	public void User_copy(User u) {
		this.rights = u.rights;
		this.name = u.name;
		this.pw = u.pw;
		this.rule_rights = u.rule_rights;
	}
	
	public void copyRules(User u) {
		this.rights = u.rights;
		this.rule_rights = u.rule_rights;
	}
	
	public boolean hasRight(Variable var, Reader.Right right) {
		boolean out = false;
		Set<Variable> keys = this.rights.keySet();
		Iterator<Variable> iter = keys.iterator();
		while (iter.hasNext()) {
			Variable curr = iter.next();
			if (curr.name.equals(var.name)){
				int[] r = rights.get(curr);
				if (right == Reader.Right.READ && r[0] == 1) {
					out = true;
				} else if (right == Reader.Right.WRITE && r[1] == 1) {
					out = true;
				} else if (right == Reader.Right.DELEGATE && r[2] == 1) {
					out = true;
				} else if (right == Reader.Right.TOGGLE && r[3] == 1) {
					out = true;
				}
			}
		}
		return out;
	}
	
	public void add_all_rights(Variable var) {
		int[] r = {1, 1, 1, 1};
		boolean exists = false;
		if (rights.keySet().contains(var)) {
			exists = true;
		}
		if (exists) {
			rights.replace(var, r);
		} else {
			rights.put(var, r);
		}
		//System.out.println(rightString());
	}
	
	public void rule_add_right(Rule rule, Reader.Right right) {
		// indexes correspond:
		// 0:Read 1:Write 2:Delegate 3:Toggle
		int[] r = {0,0,0,0};
		boolean exists = false;
		if (rule_rights.keySet().contains(rule)) {
			r = rule_rights.get(rule);
			exists = true;
		}
		if (right == Reader.Right.READ) {
			r[0] = 1;
		} else if (right == Reader.Right.WRITE) {
			r[1] = 1;
		} else if (right == Reader.Right.DELEGATE) {
			r[2] = 1;
		} else if (right == Reader.Right.TOGGLE) {
			r[3] = 1;
		}
		if (exists) {
			rule_rights.replace(rule, r);
		} else {
			rule_rights.put(rule, r);
		}
		//System.out.println(rule_rightString());
	}
	
	public boolean rule_hasRight(Rule rule, Reader.Right right) {
		boolean out = false;
		Set<Rule> keys = this.rule_rights.keySet();
		Iterator<Rule> iter = keys.iterator();
		while (iter.hasNext()) {
			Rule curr = iter.next();
			if (curr.name.equals(rule.name)){
				int[] r = rule_rights.get(curr);
				if (right == Reader.Right.READ && r[0] == 1) {
					out = true;
				} else if (right == Reader.Right.WRITE && r[1] == 1) {
					out = true;
				} else if (right == Reader.Right.DELEGATE && r[2] == 1) {
					out = true;
				} else if (right == Reader.Right.TOGGLE && r[3] == 1) {
					out = true;
				}
			}
		}
		return out;
	}
	
	public void rule_add_all_rights(Rule rule) {
		int[] r = {1, 1, 1, 1};
		boolean exists = false;
		if (rule_rights.keySet().contains(rule)) {
			exists = true;
		}
		if (exists) {
			rule_rights.replace(rule, r);
		} else {
			rule_rights.put(rule, r);
		}
		System.out.println(rule_rightString());
	}
	
	public String rule_rightString() {
		String out = this.name + " ";
		Set<Rule> keys = this.rule_rights.keySet();
		Iterator<Rule> iter = keys.iterator();
		while (iter.hasNext()) {
			Rule curr = iter.next();
			out += curr.name;
			int[] r = rule_rights.get(curr);
			if (r[0] == 1) {
				out += " READ";
			}
			if (r[1] == 1) {
				out += " WRITE";
			}
			if (r[2] == 1) {
				out += " DELEGATE";
			}
			if (r[3] == 1) {
				out += " TOGGLE";
			}
			//out += " " + r.get(i).toString();
			out += ", ";
		}
		return out;
	}
	
	public void removeLocals(ArrayList<Variable> locals) {
		for (int i = 0; i < locals.size(); i++) {
			if (this.rights.containsKey(locals.get(i))) {
				this.rights.remove(locals.get(i));
			}
		}
	}
	
	public void del_right(Variable var, Reader.Right right) {
		// indexes correspond:
		// 0:Read 1:Write 2:Delegate 3:Toggle
		int[] r = {0,0,0,0};

		r = rights.get(var);
		if (right == Reader.Right.READ) {
			r[0] = 0;
		} else if (right == Reader.Right.WRITE) {
			r[1] = 0;
		} else if (right == Reader.Right.DELEGATE) {
			r[2] = 0;
		} else if (right == Reader.Right.TOGGLE) {
			r[3] = 0;
		}
		rights.replace(var, r);
		//System.out.println(rightString());
	}

	
	public String rightString() {
		String out = this.name + " ";
		Set<Variable> keys = this.rights.keySet();
		Iterator<Variable> iter = keys.iterator();
		
		while (iter.hasNext()) {
			Variable curr = iter.next();
			out += "NAME";
			out += curr.name;
			
			int[] r = rights.get(curr);
			if (r[0] == 1) {
				out += " READ";
			}
			if (r[1] == 1) {
				out += " WRITE";
			}
			if (r[2] == 1) {
				out += " DELEGATE";
			}
			if (r[3] == 1) {
				out += " TOGGLE";
			}
			out += ", ";
		}
		return out;
	}
}
