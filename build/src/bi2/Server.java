package bi2;
/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server {
	public static String inputSanitize(String s) {
    	String str = s.replaceAll("( )+"," ").trim(); // Removes all whitespace that consists of more than 1 space
    	//System.out.println("ABCDEFG:");
    	//System.out.println(str);
    	if (str.length() > 65535) {
    		return "FAILED";
    	}
    	
    	if (!str.contains("//")) { // Checks for comments
    		//System.out.println(str);
    		return str;
    	}
    	return str.substring(0, s.indexOf("//"));
    }
	
    public static void main(String[] args) throws IOException, ParseException {
        Reader read = new Reader();
        read.init();
        int portNumber = Integer.parseInt(args[0]);
        
      /*  String jsonfile = args[1];
        Object obj = new JSONParser().parse(new FileReader(args[1]));
    	JSONObject jo = (JSONObject) obj; 
        
        Map map = ((Map)jo.get("sensors"));
        Iterator<Map.Entry> itr1 = map.entrySet().iterator(); 
        while (itr1.hasNext()) { 
            Map.Entry pair = itr1.next();
            Variable v = new Variable(pair.getKey().toString().trim(), pair.getValue().toString().trim());
        	Reader.variables.add(v);
        } 
        Map map2 = ((Map)jo.get("output_devices"));
        Map<String, String> vars2 = new HashMap<>(); 
        Iterator<Map.Entry> itr2 = map.entrySet().iterator(); 
        while (itr2.hasNext()) { 
            Map.Entry pair = itr2.next();
            Variable v = new Variable(pair.getKey().toString().trim(), pair.getValue().toString().trim());
            Reader.variables.add(v);
        } */
        while (true) {
	        try ( 
	            ServerSocket serverSocket = new ServerSocket(portNumber);
	            Socket clientSocket = serverSocket.accept();
	            PrintWriter out =
	                new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(
	                new InputStreamReader(clientSocket.getInputStream()));
	        ) {
	        
	            String inputLine;
	            ArrayList<String> input = new ArrayList<String>();
	            int count = 0;
	            long startTime = 0;

	            while (!(inputLine = in.readLine()).isEmpty()) {
	            	inputLine = inputSanitize(inputLine);
	                if (!inputLine.equals("***")) {	
	             
	                	System.out.println(inputLine);
	                	if (inputLine.length() > 0) {
	                	//startTime = System.nanoTime();
		                	input.add(inputSanitize(inputLine));
		                	count += inputLine.length();
	                	}
		            	//read.send(inputSanitize(inputLine));
	                } else {
	                	if (!(count > 1000000)) {
	                		//System.out.println("CURRENTLY: " + inputLine);
	                		System.out.println("END");
	                		read.send(input);
		                	ArrayList<String> output = read.getOutput();
		                	out.println(ToJson.toJson(output));
	                	}
	                	
	                	clientSocket.close();
	                	System.out.println();
	                }
	            	//if (startTime > 30000000) {
	            	//	clientSocket.close();
	            	//}
	            }

	            //clientSocket.close();
	        } catch (IOException e) {
	           // System.out.println("Exception caught when trying to listen on port "
	            //    + portNumber + " or listening for a connection");
	            //System.out.println(e.getMessage());
	        }
    	}
    }
}