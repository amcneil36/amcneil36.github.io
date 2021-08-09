package com.hey;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.nodes.Element;

public class AddingInNewTemp {

	static String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };
	
	static Set<String> setOfNumbers = new HashSet<>(Arrays.asList("-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " "));

	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {

	//	runDelaware();
	//	runAllExceptDelaware();
		runCustom();

	}
	
	public static String getStringToAddToLine(String cityName, String stateName) {
		String url = "https://www.bestplaces.net/city/" + stateName + "/" + cityName;
		try {
			List<String> output = new ArrayList<String>();
			String line = SperlingReader.ReadTextFromPage(url);
			line = line.substring(line.indexOf("Time zone: ") + "Time zone: ".length());
			String timeZone = line.substring(0, line.indexOf(" ("));
			if (timeZone.contains("Elevation: ")) {
				timeZone = timeZone.substring(0, timeZone.indexOf(" Elevation: "));
			}
			if (!line.contains("Elevation: ")) {
				output.add("N/A");
				output.add("N/A");
			} else {
				line = line.substring(line.indexOf("Elevation: ") + "Elevation: ".length(), line.indexOf(" ft "));
				line = line.replace("–", "-");
				int elevation = Integer.valueOf(line);
				output.add(timeZone);
				output.add(String.valueOf(elevation));
			}
			String returnSt = getStringForStuff(output);
			return returnSt;
		} catch (Exception ex) {
			System.out.println("The following url failed: " + url);
			throw new RuntimeException(ex);
		}
	}
	///////////////////////////////////////////////////////////////////////////
	
	public static void runCustom() throws Exception {
		runThread("Pennsylvania");
		runThread("Hawaii");
		runThread("Indiana");
	}
	
	public static void runDelaware() throws Exception {
		runThread("Delaware");
	}
	
	public static void runAllExceptDelaware() throws Exception {
		runThread("Alabama");
		runThread("Alaska");
		runThread("Arizona");
		runThread("Arkansas");
		runThread("California");
		runThread("Colorado");
		runThread("Connecticut");
		runThread("Florida");
		runThread("Georgia");
		runThread("Hawaii");
		runThread("Idaho");
		runThread("Illinois");
		runThread("Indiana");
		runThread("Iowa");
		runThread("Kansas");
		runThread("Kentucky");
		runThread("Louisiana");
		runThread("Maine");
		runThread("Maryland");
		runThread("Massachusetts");
		runThread("Michigan");
		runThread("Minnesota");
		runThread("Mississippi");
		runThread("Missouri");
		runThread("Montana");
		runThread("Nebraska");
		runThread("Nevada");
		runThread("New Hampshire");
		runThread("New Jersey");
		runThread("New Mexico");
		runThread("New York");
		runThread("North Carolina");
		runThread("North Dakota");
		runThread("Ohio");
		runThread("Oklahoma");
		runThread("Oregon");
		runThread("Pennsylvania");
		runThread("Rhode Island");
		runThread("South Carolina");
		runThread("South Dakota");
		runThread("Tennessee");
		runThread("Texas");
		runThread("Utah");
		runThread("Vermont");
		runThread("Virginia");
		runThread("Washington");
		runThread("West Virginia");
		runThread("Wisconsin");
		runThread("Wyoming");
	}
	
	
	public static void runAll() throws Exception {
		runThread("Alabama");
		runThread("Alaska");
		runThread("Arizona");
		runThread("Arkansas");
		runThread("California");
		runThread("Colorado");
		runThread("Connecticut");
		runThread("Delaware");
		runThread("Florida");
		runThread("Georgia");
		runThread("Hawaii");
		runThread("Idaho");
		runThread("Illinois");
		runThread("Indiana");
		runThread("Iowa");
		runThread("Kansas");
		runThread("Kentucky");
		runThread("Louisiana");
		runThread("Maine");
		runThread("Maryland");
		runThread("Massachusetts");
		runThread("Michigan");
		runThread("Minnesota");
		runThread("Mississippi");
		runThread("Missouri");
		runThread("Montana");
		runThread("Nebraska");
		runThread("Nevada");
		runThread("New Hampshire");
		runThread("New Jersey");
		runThread("New Mexico");
		runThread("New York");
		runThread("North Carolina");
		runThread("North Dakota");
		runThread("Ohio");
		runThread("Oklahoma");
		runThread("Oregon");
		runThread("Pennsylvania");
		runThread("Rhode Island");
		runThread("South Carolina");
		runThread("South Dakota");
		runThread("Tennessee");
		runThread("Texas");
		runThread("Utah");
		runThread("Vermont");
		runThread("Virginia");
		runThread("Washington");
		runThread("West Virginia");
		runThread("Wisconsin");
		runThread("Wyoming");
	}
	
	private static String getStringForStuff(List<String> strings) {
		String st = "";
		for (String string : strings) {
			String ch = string.substring(0, 1);
			if (!setOfNumbers.contains(ch)) {
				st += "\"" + string + "\"";
			}
			else {
				st += string;
			}
			st += ", ";
		
		}
		st = st.substring(0, st.length()-2);
		return st;
	}

	public static void runThread(String stateName) throws Exception {
		new RunnableDemo5(stateName).start();
	}

	private static void processState(String stateName) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String startSt = "arr.push(new Data(";
		StringBuilder sb = new StringBuilder();
		List<String> lines = new ArrayList<String>();
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			if (!line.contains(startSt)) {
				continue;
			}
			lines.add(line);
		}
		UpdatePrinter updatePrinter = new UpdatePrinter(lines.size(), stateName);
		for (String line : lines) {
			line = line.substring(startSt.length());
			line = line.substring(0, line.length() - 3);
			String[] arr = line.split(",");
			String cityName = arr[0];
			cityName = cityName.replace("\"", "").replace(" ", "_");
			sb.append(startSt);
			for (String st : arr) {
				sb.append(st);
				sb.append(",");
			}
			sb.append(" " + getStringToAddToLine(cityName, stateName));
			sb.append("));");
			sb.append("\n");
			updatePrinter.printUpdateIfNeeded();
		}
		 FileWriter myWriter = new FileWriter(filePath);
		 String st = sb.toString();
		 myWriter.write(st);
		 myWriter.close();
		 System.out.println("wrote to file " + filePath);
		 myReader.close();
	}

	static class RunnableDemo5 implements Runnable {
		private Thread t;
		private String stateName;

		RunnableDemo5(String stateName) {
			this.stateName = stateName;
		}

		public void run() {
			try {
				processState(stateName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void start() {
			if (t == null) {
				t = new Thread(this);
				t.start();
			}
		}

	}
}