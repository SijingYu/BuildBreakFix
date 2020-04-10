import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {
	String name;
	String cond;
	String primcmd;
	// s = "if .... then ...."
	public Rule(String s) {
		String pattern = "(.*) = if (.*) then (.*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		if (m.find()) {
			this.name = m.group(1);
			this.cond = m.group(2);
			this.primcmd = m.group(3);
		}else{
			System.out.println("Invalid Expr");
		}
	}
	public Rule(String name, String cond, String primcmd) {
		this.name = name;
		this.cond = cond;
		this.primcmd = primcmd;
	}
	
	public void copyValues(Rule rule) {
		this.name = rule.name;
		this.cond = rule.cond;
		this.primcmd = rule.primcmd;
	}
}
