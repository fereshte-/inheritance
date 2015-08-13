package generateTest;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
 
public class Check {
 
	public static void main(String[] args) {
 
//		Check obj = new Check();
// 
//		String domainName = "google.com";
// 
//		//in mac oxs
//		String command = "mkdir fer";
// 
//		//in windows
//		//String command = "ping -n 3 " + domainName;
// 
//		String output = obj.executeCommand(command);
// 
//		System.out.println(output);
		
 
	}
 
	private String executeCommand(String command) {
 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec("/Users/fereshte/Dropbox/chaos/src/generateTest/myScript.sh");
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}
 
}