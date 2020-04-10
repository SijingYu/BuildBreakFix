//package bi2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Reader {
	public enum Right {
		READ,
		WRITE,
		DELEGATE,
		TOGGLE
	}
	
	static ArrayList<User> userList;
	static ArrayList<Variable> variables;
	static ArrayList<Variable> local;
	static User default_delegator;
	
	static ArrayList<Rule> rules;
	static ArrayList<Rule> activated;
	
	ArrayList<User> userList_copy;
	ArrayList<Variable> variables_copy;
	ArrayList<Variable> local_copy;
	User default_delegator_copy;
	ArrayList<Rule> rules_copy;
	ArrayList<Rule> activated_copy;
	ArrayList<String> output;
	
	User current_prin = null;
	public void revert()/*(ArrayList<User> ucopy,ArrayList<Variable> vcopy, ArrayList<Rule> rcopy,
			User dcopy, ArrayList<Rule> acopy) */{
		//System.out.println("REVERTINg");
		userList = deepCopyUser(userList_copy);
		variables = deepCopyVariable(variables_copy);
		rules = deepCopyRule(rules_copy);
		default_delegator.User_copy(default_delegator_copy);
		activated = deepCopyRule(activated_copy);
	}

	public ArrayList<String> getOutput() {
		return this.output;
	}
		
	public void init() {
		userList = new ArrayList<User>();
		variables = new ArrayList<Variable>();
		default_delegator = new User("default_delegator", "\"default\"");
		rules = new ArrayList<Rule>();
		activated = new ArrayList<Rule>();

		User admin = new User("admin", "\"admin\"");
		userList.add(admin);
		
		User hub = new User("hub", "\"hub\"");
		userList.add(hub);
		variables.add(new Variable("temperature","80"));
		variables.add(new Variable("home_energy","50"));
		variables.add(new Variable("smoke","0"));
		variables.add(new Variable("owner_location","0"));
		variables.add(new Variable("lights","0"));
		variables.add(new Variable("energy","2000"));
		variables.add(new Variable("alarm_status","1"));
		variables.add(new Variable("air_conditioning","0"));
		
		for (int i = 0; i < variables.size(); i++) {
			admin.add_all_rights(variables.get(i));
			hub.add_all_rights(variables.get(i));
		}
	}
	
	public void send(ArrayList<String> s) {
		// resets local every time new input is had
		local = new ArrayList<Variable>();
		//
	//	for (int i = 0; i < s.size() ; i++) {
		//	System.out.println(s.get(i));
		//}
		System.out.println();
		//
		this.output = new ArrayList<String>();
		userList_copy = deepCopyUser(userList);
		variables_copy = deepCopyVariable(variables);
		local_copy = deepCopyVariable(local);
		default_delegator_copy = new User("default_delegator_copy", "\"default\"");
		default_delegator_copy.User_copy(default_delegator);
		rules_copy = deepCopyRule(rules);
		activated_copy = deepCopyRule(activated);
		
		if (s.get(0).startsWith("as principal ")) {
			ArrayList<String> buffer = new ArrayList<String>();
			String[] spl = (s.get(0).replace("as principal ", "").trim()).split(" ");
			//spl[0] = principal name, spl[2] = principal password
			if (login(spl[0], spl[2])) {
				current_prin = getUser(spl[0]);
				for (int i = 1; i < s.size(); i++) {
					String curr = s.get(i);
					System.out.println(s.get(i));
					if (curr.startsWith("create principal ")) {
						curr = curr.replace("create principal ", "").trim();
						buffer.add(create_prin(curr));	
					} else if (curr.startsWith("default delegator")) {
						curr = curr.replace("default delegator ", "").trim();
						buffer.add(def_del(curr));
					} else if (curr.startsWith("set rule")) {
						curr = curr.replace("set rule", "").trim();
						buffer.add(set_rule(curr));
					} else if (curr.startsWith("set delegation ")) {
						curr = curr.replace("set delegation ", "").trim();
						buffer.add(set_del(curr));
					} else if (curr.startsWith("set ")) {
						curr = curr.replace("set ", "").trim();
						buffer.add(set(curr));
					} else if (curr.startsWith("return ")) {
						curr = curr.replace("return ", "");
						buffer.add(rtrn(curr));
					} else if (curr.startsWith("exit")) {
						if(!this.current_prin.name.equals("admin")) {
							buffer.add("DENIED_WRITE");
						}else {
							buffer.add("EXITING");
						}
					} else if (curr.startsWith("activate rule")) {
						curr = curr.replace("activate rule", "").trim();
						buffer.add(activate_rule(curr));
					} else if (curr.startsWith("deactivate rule")){
						curr = curr.replace("deactivate rule", "").trim();
						buffer.add(deactivate_rule(curr));
					} else if (curr.startsWith("local ")) {
						curr = curr.replace("local ", "").trim();
						buffer.add(local(curr));
					} else if (curr.startsWith("change password ")) {
						curr = curr.replace("change password ", "");
						buffer.add(change_pw(curr));
					} else if (curr.startsWith("if")) {
						curr = curr.replace("if", "").trim();
						if (evalThen(curr).equals("COND_NOT_TAKEN")) {	
							buffer.add("COND_NOT_TAKEN");
						}else {
							s.set(i, evalThen(curr));
							i -= 1;
						}
					} else if (curr.startsWith("delete delegation")) {
						curr = curr.replace("delete delegation ", "");
						buffer.add(del_del(curr));
					} else if (curr.startsWith("print ")) {
						curr = curr.replace("print ", "");
						buffer.add(print(curr));
					} else if (curr.startsWith("FAILED")) {
						buffer.add("FAILED");
					}
				}
				//System.out.println("OUTPUT: " + output);
			} else {
				buffer.add("FAILED");
			}
			// The end 
			boolean fail = false;
			System.out.println("BUFFER");
			for (int i = 0; i < buffer.size(); i++) {
				System.out.println(buffer.get(i));
				if (buffer.get(i).contains("FAILED") || buffer.get(i).contains("DENIED")) {
					fail = true;
					resetLocal();
					/*System.out.println("BEFORE REVERT COPY");
					printVars(variables_copy);
					printUsers(userList_copy);
					System.out.println("Original");
					printVars(variables);
					printUsers(userList);
					revert(userList_copy, variables_copy,rules_copy,default_delegator_copy,
							activated_copy); */
					revert();
					String temp = buffer.get(i);
					this.output.add(temp);
					return;
				}
			}
			if (!fail) {
				for (int j = 0; j  < buffer.size(); j++) {
						//System.out.print("Original");
						String temp = buffer.get(j);
						this.output.add(temp);
				}
				//return;
			} 
			
			
			ArrayList<String> rule_out = evalRule();
			
			for (int i = 0; i < rule_out.size(); i++) {
				// We only pass things that start with activerule, if it doesn't it means the condition failed for some
				// reason and we fail and rollback
				if (!rule_out.get(i).startsWith("activerule")) {
					resetLocal();
					//revert(userList_copy, variables_copy,rules_copy,default_delegator_copy,
					//		activated_copy);
					revert();
					output.add(rule_out.get(i));
					return;
				}
				String[] spl2 = rule_out.get(i).replace("activerule ", "").trim().split(" ");
				
				//System.out.println(rule_out.get(i));
				output.add(eval(rule_out.get(i).replace("activerule " + spl2[0], "").trim()) + " " + spl2[0]);
			}
			
			printRules(activated);
			resetLocal();
		}
	}
	
	private String eval(String curr) {
		/*for (int i = 0; i < s.size(); i++) {
			String curr = s.get(i);
			System.out.println(s.get(i));*/
			//System.out.println(curr);
			if (curr.startsWith("create principal ")) {
				curr = curr.replace("create principal ", "").trim();
				return create_prin(curr);			
			} else if (curr.startsWith("set rule")) {
				curr = curr.replace("set rule", "").trim();
				return set_rule(curr);
			} else if (curr.startsWith("set delegation ")) {
				curr = curr.replace("set delegation ", "").trim();
				return set_del(curr);
			} else if (curr.startsWith("set ")) {
				curr = curr.replace("set ", "").trim();
				return set(curr);
			} else if (curr.startsWith("return ")) {
				curr = curr.replace("return ", "");
				return rtrn(curr);
			} else if (curr.startsWith("activate rule")) {
				curr = curr.replace("activate rule", "").trim();
				return activate_rule(curr);
			} else if (curr.startsWith("deactivate rule")){
				curr = curr.replace("deactivate rule", "").trim();
				return deactivate_rule(curr);
			} else if (curr.startsWith("local ")) {
				curr = curr.replace("local ", "").trim();
				return local(curr);
			} else if (curr.startsWith("change password ")) {
				curr = curr.replace("change password ", "");
				return change_pw(curr);
			}/*else if (curr.startsWith("if")) {
				curr = curr.replace("if", "").trim();
				if (evalThen(curr).equals("COND_NOT_TAKEN")) {	
					output.add("COND_NOT_TAKEN");
				}else {
					s.set(i, evalThen(curr));
					i -= 1;
				}
			}*/ else if (curr.startsWith("delete delegation")) {
				curr = curr.replace("delete delegation ", "");
				return del_del(curr);
			} else if (curr.startsWith("print ")) {
				curr = curr.replace("print ", "");
				return print(curr);
			}
		//}
		return "";
	}
	
	private String create_prin(String str) {
		String[] spl = str.split(" ");
	/*	// Check the prinname and pw to see if it is valid
		if (!x_valid(spl[0]) || badword(str) || !s_valid(spl[1])) {
			System.out.println("CREATE_PRIN CONTAINS ILLEGAL STRINGS");
			return "FAILED";
		}
		// Makes sure follows format of "change password p s"
		if (!spl[1].equals("password") || spl.length != 2) {
			System.out.println("MALFORMED CREATE_PRIN PASSWORD");
			return "FAILED";
		} */
		int index = getUserIndex(spl[0]);
		
		if (current_prin.name.equals("admin")) {
			if (index == -1) {
				User new_user = new User(spl[0], spl[1]);
				new_user.copyRules(default_delegator);
				userList.add(new_user);
				return "CREATE_PRINCIPAL";
			} else {
				return "FAILED";
			}
		}
		return "DENIED_WRITE";
	}
	
	private String set(String str) {
		String[] spl = str.split(" ");
		
		if (badword(spl[0]) || !x_valid(spl[0])) {
			System.out.println("SET CONTAINS AN INVALID VARIABLE NAME");
			return "FAILED";
		}
		
		// Check if name is already taken by non variables => return Failed if it is
		if (nameTaken(spl[0], -1)) {
			return "FAILED";
		}
		
		int index = getVarIndex(spl[0]);
		
		if (spl.length == 3 && !spl[2].contains(".")) { // Case 1: X = 1
			if (index == -1) {
				Variable var = new Variable(spl[0], spl[2]);
				variables.add(var);
				
				if (!current_prin.name.equals("admin")) { // If user is not the current admin, give the admin rights.
					User admin = getUser("admin"); 
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights.
				return "SET";
			} else {
				Variable var = getVariable(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2]));
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		} else if (spl.length == 3 && spl[2].contains(".")) { // Case 2 X.Y
			if (index == -1) { // Index = -1 so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2]));
				variables.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not the admin, give the admin rights.
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights.
				return "SET";
			} else { // Index != -1 so add the value into the ArrayList of the variables values.
				Variable var = getVariable(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2])); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		}  else if (spl.length == 4) { // Case 3 Func X
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				variables.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getVariable(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2] + " " + spl[3])); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		} else if (spl.length == 5) {
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				variables.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getVariable(spl[0]);
				//System.out.println(var.name);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4])));
					return "SET";
				} else {
				//	System.out.print("fdsfd"+var.name+"fds");
					//System.out.println("Ccdsfuresr");
					//System.out.println(current_prin.rightString());
					return "DENIED_WRITE";
				}
			}
			
		} else if (spl.length == 6) { // Case 4 Func X I I
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4] + " " + spl[5]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				variables.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getVariable(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4] + " " + spl[5]))); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		}
		return "FAILED";
	}
	
	private String set_del(String str) {
		// Spl[0] = X, Spl[1] = from, Spl[2] = right, spl[3] = -> Spl[4] = to
		String[] spl = str.split(" ");
		int index = 0;
		boolean all = false;
		if (!spl[0].trim().equals("all")) {
			index = getVarIndex(spl[0]);
		}else {
			all = true;
			index = 1;
		}
		int index2 = getUserIndex(spl[1]);
		int index3 = getUserIndex(spl[4]);
		if (index == -1 | index2 == -1 | index3 == -1 ) {
			return "FAILED";
		}else {

			User from = getUser(spl[1]);
			User to = getUser(spl[4]);
			Right r = null;
			if (spl[2].equals("read")) {
				r = Right.READ;
			} else if (spl[2].equals("write")) {
				r = Right.WRITE;
			} else if (spl[2].equals("delegate")) {
				r = Right.DELEGATE;
			} else if (spl[2].equals("toggle")) {
				r = Right.TOGGLE;
			}
			if (current_prin.name.equals("admin")|current_prin.name.equals(from.name)) {
				if(all) {
					for (Variable v: variables) {
						if (from.hasRight(v, Right.DELEGATE)) {
							to.add_right(v, r);
						}
					}
					return "SET_DELEGATION";
				}else {
					Variable var = getVariable(spl[0]);
					if (from.hasRight(var, Right.DELEGATE)) {
						to.add_right(var, r);
						return "SET_DELEGATION";
					}else {
						return "DENIED_WRITE";
					}
				}
			}else {
				return "DENIED_WRITE";
			}
		}
	}
	
/*	private String set_del(String str) {
		// Spl[0] = X, Spl[1] = from, Spl[2] = right, spl[3] = -> Spl[4] = to
		String[] spl = str.split(" ");
		int index = getVarIndex(spl[0]);
		//System.out.println(str);
		int index2 = getUserIndex(spl[1]);
		int index3 = getUserIndex(spl[3]);
		
		if (index == -1 | index2 == -1 | index3 == -1 ) {
			return "FAILED";
		} else {
			Variable var = getVariable(spl[0]);
			User from = getUser(spl[1]);
			User to = getUser(spl[4]);
			Right r = null;
			if (current_prin.name)
			if (!spl[0].trim().equals("all")) {		
				if (spl[2].equals("read")) {
					r = Right.READ;
				} else if (spl[2].equals("write")) {
					r = Right.WRITE;
				} else if (spl[2].equals("delegate")) {
					r = Right.DELEGATE;
				} else if (spl[2].equals("toggle")) {
					r = Right.TOGGLE;
				}
				if (from.hasRight(var, Right.DELEGATE)) {
					to.add_right(var, r);
					return "SET_DELEGATION";
				}else {
					return "DENIED_WRITE";
				}
			}else {
				ArrayList<String> del_all = new ArrayList<String>();
				for (Variable v: variables) {
					del_all.add(set_del2for_all(v, from, to, r));
				}
				
			}
		}
	}
	
	private String set_del2for_all(Variable var, User from, User to, Right r) {
		// Spl[0] = X, Spl[1] = from, Spl[2] = right, spl[3] = -> Spl[4] = to
		int index = getVarIndex(var.name);
		if (index == -1) {
			return "FAILED";
		} else {
				if (from.hasRight(var, Right.DELEGATE)) {
					to.add_right(var, r);
					return "SET_DELEGATION";
				}else {
					return "FAILED";
				}
		}
	}
	
	private String del_del(String str) {
		// Spl[0] = X, Spl[1] = from, Spl[2] = right, spl[3] = -> Spl[4] = to
		String[] spl = str.split(" ");
		int index = getVarIndex(spl[0]);
		int uindex1 = getUserIndex(spl[1]);
		int uindex2 = getUserIndex(spl[4]);
		//System.out.println(str);
		if (index == -1 | uindex1 == -1 | uindex2 == -1) {
			return "FAILED";
		} else {
			Variable var = getVariable(spl[0]);
			User from = getUser(spl[1]);
			User to = getUser(spl[4]);
			Right r = null;
			if (spl[2].equals("read")) {
				r = Right.READ;
			} else if (spl[2].equals("write")) {
				r = Right.WRITE;
			} else if (spl[2].equals("delegate")) {
				r = Right.DELEGATE;
			} else if (spl[2].equals("toggle")) {
				r = Right.TOGGLE;
			}
			if (from.hasRight(var, r)) {
				to.del_right(var, r);
				return "DELETE_DELEGATION";
			}
			return "FAILED";
		}
	}
	*/
	private String del_del(String str) {
		// Spl[0] = X, Spl[1] = from, Spl[2] = right, spl[3] = -> Spl[4] = to
		String[] spl = str.split(" ");
		int index = 0;
		boolean all = false;
		if (!spl[0].trim().equals("all")) {
			index = getVarIndex(spl[0]);
		}else {
			all = true;
			index = 1;
		}
		int index2 = getUserIndex(spl[1]);
		int index3 = getUserIndex(spl[4]);
		if (index == -1 | index2 == -1 | index3 == -1 ) {
			return "FAILED";
		}else {
			User from = getUser(spl[1]);
			User to = getUser(spl[4]);
			Right r = null;
			if (spl[2].equals("read")) {
				r = Right.READ;
			} else if (spl[2].equals("write")) {
				r = Right.WRITE;
			} else if (spl[2].equals("delegate")) {
				r = Right.DELEGATE;
			} else if (spl[2].equals("toggle")) {
				r = Right.TOGGLE;
			}
			if (current_prin.name.equals("admin")|current_prin.name.equals(from.name)) {
				if(all) {
					for (Variable v: variables) {
						if (from.hasRight(v, Right.DELEGATE)) {
							to.del_right(v, r);
						}
					}
					return "DELETE_DELEGATION";
				}else {
					Variable var = getVariable(spl[0]);
					if (from.hasRight(var, Right.DELEGATE)) {
						to.del_right(var, r);
						return "DELETE_DELEGATION";
					}else {
						return "DENIED_WRITE";
					}
				}
			}else {
				return "DENIED_WRITE";
			}
		}
	}
	
	
	private String set_rule(String rule) {
		System.out.println(rule);
		String spl[] = rule.split(" ");
		// Change to use the getVariable()
		// Check if name is already taken by non variables => return Failed if it is
		if (nameTaken(spl[0], 1)) {
			return "FAILED";
		}
		
		int index = getRuleIndex(spl[0]);
		
		if (index == -1) { // Does = -1 so it does not exist -> make the new rule
			Rule newRule = new Rule(rule);
			rules.add(newRule);
			
			if (!current_prin.name.equals("admin")) {
				User admin = getUser("admin");
				admin.rule_add_all_rights(newRule);
				current_prin.rule_add_all_rights(newRule);
				return "SET_RULE";
			} else {
				current_prin.rule_add_all_rights(newRule);
				return "SET_RULE";
			}
		} else { // Already exists -> Check for write right
			Rule current_rule = getRule(spl[0]);
			if (current_prin.rule_hasRight(current_rule, Right.WRITE)) {
				Rule new_rule = new Rule(rule);
				current_rule.copyValues(new_rule);
				return "SET_RULE";
			}
			return "DENIED_WRITE";
		}
	}
	
	private String activate_rule(String rule) {
		int index = getRuleIndex(rule);
		
		if (index == -1) { // Does = -1 so it does not exist -> return fail
			return "FAILED";
		} else { // Already exists -> Check for toggle right
			Rule current_rule = getRule(rule);
			if (current_prin.rule_hasRight(current_rule, Right.TOGGLE)) {
				activated.add(current_rule);
				return "ACTIVATE_RULE";
			}
			return "DENIED_WRITE";
		}
	}
	
	private String deactivate_rule(String rule) {
		for (int i = 0; i < variables.size(); i++) {
			if(variables.get(i).name.equals(rule)) {
				return "FAILED";
			}
		}
		
		int index = getActivatedIndex(rule);
		
		if (index == -1) { // Does = -1 so it does not exist -> return fail
			return "FAILED";
		} else { // Already exists -> Check for toggle right
			Rule current_rule = getActivated(rule);
			if (current_prin.rule_hasRight(current_rule, Right.TOGGLE)) {
				activated.remove(index);
				return "DEACTIVATE_RULE";
			}
			return "DENIED_WRITE";
		}
	}
	
	private String local(String str) {
		String[] spl = str.split(" ");
		
		// Check if name is already taken by non variables => return Failed if it is
		if (nameTaken(spl[0], 0)) {
			return "FAILED";
		}
		
		int index = getLocalIndex(spl[0]);
		
		// Case 1: X = 1
		if (spl.length == 3 && !spl[2].contains(".")) {
			if (index == -1) {
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				local.add(var);
				
				if (!current_prin.name.equals("admin")) {
					User admin = getUser("admin");
					admin.add_all_rights(var);
					current_prin.add_all_rights(var);
					return "LOCAL";
				} else {
					current_prin.add_all_rights(var);
					return "LOCAL";
				}
			} else {
				Variable var = getLocal(spl[0]);
				
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2]));
					
					if (var.fail) { // Instantly fail if the variable cannot be evaluated
						return "FAILED"; 
					}
					
					return "LOCAL";
				} else {
					return "DENIED_WRITE";
				}
			}
		} else if (spl.length == 3 && spl[2].contains(".")) { // Case 2 X.Y
			if (index == -1) { // Index = -1 so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2]));
				local.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not the admin, give the admin rights.
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights.
				return "SET";
			} else { // Index != -1 so add the value into the ArrayList of the variables values.
				Variable var = getLocal(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2])); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		}  else if (spl.length == 4) { // Case 3 Func X
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				local.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getLocal(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(spl[2] + " " + spl[3])); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		} else if (spl.length == 5) {
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				local.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getLocal(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4])));
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		} else if (spl.length == 6) { // Case 4 Func X I I
			if (index == -1) { // Index == -1, so it does not exist -> Make the variable.
				Variable var = new Variable(spl[0], Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4] + " " + spl[5]));
				
				if (var.fail) { // Instantly fail if the variable cannot be evaluated
					return "FAILED"; 
				}
				
				local.add(var);
				if (!current_prin.name.equals("admin")) { // If user is not admin, give admin the rights
					User admin = getUser("admin");
					admin.add_all_rights(var);
				}
				current_prin.add_all_rights(var); // Always give the current principal rights
				return "SET";
			} else { // Index != -1, so it does exist -> add the value into the old variable
				Variable var = getLocal(spl[0]);
				if (current_prin.hasRight(var, Right.WRITE)) {
					var.addValue(Evaluator.eval(Evaluator.eval(spl[2] + " " + spl[3] + " " + spl[4] + " " + spl[5]))); // <========= TEMP CODE CHANGE IT SO IT EVALUATES
					return "SET";
				} else {
					return "DENIED_WRITE";
				}
			}
		}
		return "FAILED";
	}
	
	private ArrayList<String> evalRule() {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < activated.size();i++) {
			Rule r = activated.get(i);
			if (rightOnCond(r.cond, current_prin)) {
				//System.out.print(r.cond + " RIGHTONCOND " + r.name);
				if (evalCond(r.cond)) {
					if (r.name.equals("brule1")){
						System.out.println("2222222CONDTRUE BRULE1 IS HERE!!!");
					}
					//System.out.print(r.cond + " THIS IS TRUE " + r.name);
					output.add("activerule " + r.name + " " + r.primcmd);
				} else {
					//System.out.println(r.cond + " IS FALSE ");
				}
			} else {
				//System.out.print(r.cond + "NO RIGHTONCOND " + r.name);
				int index = getActivatedIndex(r.name);
				activated.remove(index);
				output.add("DENIED_READ");
				//activated.removeIf(ar -> (ar.name.equals(r.name)));
				//System.out.println("NO RIGHTS REMOVE " + r.name);
				//output.add(r.name + " " + "DENIED_READ");
			}
		}
		return output;
	}
	
	private boolean rightOnCond(String cond, User u) {
		String spl[] = cond.split(" ");
		String val1 = spl[0].trim();
		String val2 = spl[2].trim() ;
		boolean secure = true;
		if (val1.matches("^\\d+(\\.\\d+)?")) {
			if (val2.matches("^\\d+(\\.\\d+)?")) {
				secure = true;
			}else {
				Variable var = getVariable(val2);
				if (u.hasRight(var, Right.READ)) {
					secure = true;
				}else {
					secure = false;
				}
			}	
		}else {
			if (val2.matches("^\\d+(\\.\\d+)?")) {
				Variable var = getVariable(val1);
				if (u.hasRight(var, Right.READ)) {
					secure = true;
				}else {
					secure = false;
				}
			}else{
				Variable var = getVariable(val1);
				Variable var2 = getVariable(val2);
				if (u.hasRight(var, Right.READ) && u.hasRight(var2, Right.READ)) {
					secure = true;
				}else {
					secure = false;
				}
			}
		}
		return secure;
	}
	
	private String print(String str) {
		String out = Evaluator.eval(str);
		
		if (out.equals("FAILED")) {
			return Evaluator.eval(str);
		}
		return "PRINT " + Evaluator.eval(str);
	}
	
	private String change_pw(String str) {
		String[] spl = str.split(" ");
		
		if (!x_valid(spl[0]) || !s_valid(spl[1])) {
			return "FAILED";
		}

		int index = getUserIndex(spl[0]);
		
		if (index == -1 ) { // Index = -1, so does not exist so fail.
			return "FAILED";
		}
		
		// If the current principal is not the admin or the user themself, deny
		if (!current_prin.name.equals("admin") && !current_prin.name.equals(spl[0])) {
			return "DENIED_WRITE";
		}
		
		// Elsethey are allowed, so change their password
		User toChange = getUser(spl[0]);
		toChange.pw = spl[1];
		return "CHANGE_PASSWORD";
	}
	
	private boolean evalCond(String cond) {
		if (cond.contains(">=")) {
			String spl[] = cond.split(">=");
			int val1 = Integer.parseInt(Evaluator.eval(spl[0].trim()));
			int val2 = Integer.parseInt(Evaluator.eval(spl[1].trim()));
			return val1 >= val2;
		}else if (cond.contains("<=")) {
			String spl[] = cond.split("<=");
			int val1 = Integer.parseInt(Evaluator.eval(spl[0].trim()));
			int val2 = Integer.parseInt(Evaluator.eval(spl[1].trim()));
			return val1 <= val2;
		}else if (cond.contains("=")) {
			String spl[] = cond.split("=");
			int val1 = Integer.parseInt(Evaluator.eval(spl[0].trim()));
			int val2 = Integer.parseInt(Evaluator.eval(spl[1].trim()));
			return val1 == val2;
		}else if (cond.contains(">")) {
			String spl[] = cond.split(">");
			int val1 = Integer.parseInt(Evaluator.eval(spl[0].trim()));
			int val2 = Integer.parseInt(Evaluator.eval(spl[1].trim()));
			return val1 > val2;
		}else if (cond.contains("<")) {
			String spl[] = cond.split("<");
			int val1 = Integer.parseInt(Evaluator.eval(spl[0].trim()));
			int val2 = Integer.parseInt(Evaluator.eval(spl[1].trim()));
			//System.out.println(val1 + " < " + val2);
			//System.out.println(val1 < val2);
			return val1 < val2;
		}
		return false;
	}
	
	private String def_del(String str) {
		str = str.replace("=", "").trim();
		int index = getUserIndex(str);
		if (index == -1) {
			return "FAILED";
		}else {
			if (this.current_prin.name.equals("admin")){
				User def = getUser(str);
				default_delegator.copyRules(def);
				return "DEFAULT_DELEGATOR";
			}else {
				return "DENIED_WRITE";
			}
		}
	}
	
	private String evalThen(String s) {
		String[] spl = s.split("then");
		String cond = spl[0].trim();
		if (evalCond(cond)) {
			return spl[1].replace("begin","").replace("end", "").trim();
		}else {
			return "COND_NOT_TAKEN";
		}
	}
	
	private String getVarFrom(String name, ArrayList<Variable> vars) {
		String s = "";
		for (int i = 0; i <vars.size();i++ ) {
			if (vars.get(i).name.equals(name)) {
				s = Integer.toString(vars.get(i).value.get(0));
			}
		}
		return s;
	}
	
	private String rtrn(String str) {
		String out = Evaluator.eval(str);
		if (out.equals("FAILED")) {
			return out;
		}
		return "RETURNING " + Evaluator.eval(str);

		/*if (index == -1) { // VARIABLE DOES NOT EXIST => RETURN
			return "RETURNING " + str;
		} else if (current_prin.hasRight(variables.get(index), Right.READ)){
			ArrayList<Integer> variable_vals = getVariable(str).value;
			return "RETURNING " + Integer.toString(variable_vals.get(variable_vals.size()-1));
		} else {
			return "DENIED_READ";
		}*/
	}
	
	static int getVarIndex(String var) {
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).name.equals(var)) {
				return i;
			}
		}
		return -1;
	}
	
	public static Variable getVariable(String str) {
		return Reader.variables.get(getVarIndex(str));
	}
	
	public static int getLocalIndex(String var) {
		for (int i = 0; i < local.size(); i++) {
			if (local.get(i).name.equals(var)) {
				return i;
			}
		}
		return -1;
	}
	
	public static Variable getLocal(String str) {
		return Reader.local.get(getLocalIndex(str));
	}
	
	private int getActivatedIndex(String var) {
		for (int i = 0; i < activated.size(); i++) {
			if (activated.get(i).name.equals(var)) {
				return i;
			}
		}
		return -1;
	}
	
	private Rule getActivated(String str) {
		return Reader.activated.get(getActivatedIndex(str));
	}
	
	private int getRuleIndex(String var) {
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).name.equals(var)) {
				return i;
			}
		}
		return -1;
	}
	
	private Rule getRule(String str) {
		return Reader.rules.get(getRuleIndex(str));
	}
	
	private boolean nameTaken(String str, int id) {
		// ID is an identifier to know what to arrays to check.
		// id = -1 excludes variables, id = 0 excludes local, id = 1 excludes rules
		if (id == -1 && getRuleIndex(str) == -1 && getLocalIndex(str) == -1) { 
			return false;
		} else if (id == 0 && getRuleIndex(str) == -1 && getVarIndex(str) == -1) {
			return false;
		} else if (id == 1 && getLocalIndex(str) == -1 && getVarIndex(str) == -1) {
			return false;
		}
		return true;
	}
	
	private int getUserIndex(String username) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).name.equals(username)) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean login(String username, String password) {
		int index = getUserIndex(username);
		if (index != -1 && userList.get(index).pw.equals(password)) {
			return true;
		}
		return false;
	}
	
	private void resetLocal() {
		for (int i = 0; i < userList.size(); i++) {
			userList.get(i).removeLocals(local);
		}
	}
	
	private ArrayList<Rule> deepCopyRule(ArrayList<Rule> old) {
		ArrayList<Rule> copy = new ArrayList<Rule>(); 
		for (int i = 0; i < old.size(); i++) {
			Rule rule_copy = new Rule(old.get(i).name,old.get(i).cond,old.get(i).primcmd);
			copy.add(rule_copy);
		}
		return copy;
	}
	
	private ArrayList<Variable> deepCopyVariable(ArrayList<Variable> old) {
		ArrayList<Variable> copy = new ArrayList<Variable>();
		for (int i = 0; i < old.size(); i++) {
			ArrayList<Integer> int_copy = new ArrayList<Integer>();
			for (int j = 0; j < old.get(i).value.size();j++) {
				int_copy.add(old.get(i).value.get(j));
			}
			
			Variable var_copy = new Variable(old.get(i).name, int_copy);
			copy.add(var_copy);
		}
		return copy;
	}
	
	private Variable deepVar(Variable v) {
		ArrayList<Integer> int_copy = new ArrayList<Integer>();
		for (int j = 0; j < v.value.size();j++) {
			int_copy.add(v.value.get(j));
		}
		
		Variable var_copy = new Variable(v.name, int_copy);
		return var_copy;
	}
	
	private Rule deepRule(Rule r) {
		Rule r_copy = new Rule(r.name,r.cond,r.primcmd);
		return r_copy;
	}
	
	private ArrayList<User> deepCopyUser(ArrayList<User> old) {
		ArrayList<User> copy_userlist = new  ArrayList<User>();
		for (int i = 0; i < old.size();i++ ) {
			String name = old.get(i).name;
			String pw = old.get(i).pw;
			//User copy_user = new User(name,pw);
			
			Map<Variable, int[]> copy_rights = new HashMap<Variable, int[]>();
			Map<Rule, int[]> copy_rule_rights = new HashMap<Rule, int[]>();
			for (Variable v: old.get(i).rights.keySet()) {
				Variable v_copy = deepVar(v);
				int[] rights_ints = {0,0,0,0};
				for (int j = 0; j < old.get(i).rights.get(v).length; j++) {
					rights_ints[j] = old.get(i).rights.get(v)[j];
				}
				copy_rights.put(v_copy, rights_ints);
				//copy_user.add_all_rights(v);
			}

			for (Rule r: old.get(i).rule_rights.keySet()) {
				Rule r_copy = deepRule(r);
				int[] rule_rights_ints = {0,0,0,0};
				for (int j = 0; j < old.get(i).rule_rights.get(r).length; j++) {
					rule_rights_ints[j] = old.get(i).rule_rights.get(r)[j];
				}
				copy_rule_rights.put(r_copy, rule_rights_ints);
				//copy_user.rule_add_all_rights(r);
			}
			User copy_user = new User(copy_rights,copy_rule_rights,name,pw);
			copy_userlist.add(copy_user);
		}
		return copy_userlist;
	}
	
	// passwords s
	private boolean s_valid(String s) {
		if (s.matches("[A-Za-z0-9_ ,;\\.?!-]*") && !(s.length() > 65535)) {
			return true;	
		}
		return false;
	}
	
	// Variables x,p,q,
	private boolean x_valid(String s) {
		if (s.matches("[A-Za-z][A-Za-z0-9_]*") && !(s.length() > 255)) {
			return true;
		}
		return false;
	}
	
	// i ints
	private boolean i_valid(String s) {
		if (s.matches("-?[0-9]+") && !(Integer.parseInt(s) >  2147483647) && ! (Integer.parseInt(s) <  2147483647)) {
			return true;
		}
		return false;
	}
	
	private User getUser(String str) {
		return Reader.userList.get(getUserIndex(str));
	}
	private boolean badword(String s) {
		String[] bad = {"activate", "all", "as", "begin",
				"change", "count", "create", "deactivate", "default",
				"delegation", "delegator", "do", "end", "exit", "for", "if", "max",
				"mean", "min", "password", "principal", "print", "read", "reset",
				"return", "rule", "set", "then", "to", "toggle", "write",
				"***"};
		for (int i = 0; i < bad.length; i++) {
			if (bad[i].equals(s)) {
				return true;
			}
		}
		return false;
	}
	private void printUsers(ArrayList<User> users) {
		for (int i = 0; i < users.size();i++) {
			System.out.println("name " + users.get(i).name);
			System.out.println(users.get(i).rightString());
		}	
	}
	public void printVars(ArrayList<Variable> vars) {
		for (int i = 0; i < vars.size();i++) {
			System.out.println(vars.get(i).name);
			System.out.println(vars.get(i).value);
		}
	}
	public void printRules(ArrayList<Rule> rules) {
		for (int i = 0; i < rules.size();i++) {
			System.out.print("NAME ");
			System.out.println(rules.get(i).name);
			System.out.print("COND ");
			System.out.println(rules.get(i).cond);
			System.out.print("PRIMCMD ");
			System.out.println(rules.get(i).primcmd);
		}
	}
}
