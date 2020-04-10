//package bi2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
//import javax.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class ToJson {
	
public static String toJson(ArrayList<String> outputs) {
		String result = "";
		for (int i = 0; i < outputs.size();i++) {
			String[] spl = outputs.get(i).split(" ");
			if (spl.length > 1) {
				JSONObject line = new JSONObject();
				if (outputs.get(i).contains("rule")) {
					line.put("rule", spl[1]);
				}else {
					line.put("output", spl[1]);
				}
				line.put("status", spl[0]);
				result += line.toJSONString() + "\n";
			}else {
				JSONObject line2 = new JSONObject();
				line2.put("status", outputs.get(i));
				result += line2.toJSONString() + "\n";
			}
		}
		return result;
	}
}
