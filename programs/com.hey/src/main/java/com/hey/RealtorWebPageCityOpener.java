package com.hey;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.hey.RealtorWebPageCountyOpener.RunnableDemo6;

public class RealtorWebPageCityOpener {

	private static Map<String, String> map = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		populateMap();

		runThread("Texas");
/*		runThread("Alabama");
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
		runThread("Wyoming");*/
	}
	
	public static void runThread(String stateName) throws Exception {
		processState(stateName);
	}

	public static void runThread2(String stateName) throws Exception {
		new RunnableDemo6(stateName).start();
	}

	private static void processState(String stateName) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String startSt = "arr.push(new Data(";
		List<String> lines = new ArrayList<String>();
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			if (!line.contains(startSt)) {
				continue;
			}
			lines.add(line);
		}
		int numTabsOpened = 0;
		for (String line : lines) {
			line = line.substring(startSt.length());
			line = line.substring(0, line.length() - 3);
			String[] arr = line.split(",");
			int population = Integer.valueOf(arr[8].substring(1));
			String val = arr[arr.length - 2];
			if (!val.contains("N/A") || population < 1000) {
				continue;
			}
			String metro = arr[arr.length - 3];
			String city = arr[0];
			city = city.replace("\"", "");
			city = city.replace(" ", "-");
			System.out.println(city);
			System.out.println(city.length());
			int x = 2;
			// greater than 100k done. states with all counties done: california, florida, georgia, washington, hawaii, SC, NC, TN, TX, AZ, NM.
				String suffix = map.get(stateName.toLowerCase());
				// https://www.realtor.com/realestateandhomes-search/Round-Rock_TX/overview
				// https://www.realtor.com/realestateandhomes-search/Harris-County_TX/overview
				String url = "https://www.realtor.com/realestateandhomes-search/" + city + "_" + suffix.toUpperCase() + "/overview";
				Desktop.getDesktop().browse(new URI(url));
				numTabsOpened++;
				while (x == 2) {
					
				}
		}
		System.out.println("number of tabs opened: " + numTabsOpened);
	}
	
	private static void populateMap() {
		map = new HashMap<String, String>();
		fillMapWithItem("al", "Alabama");
		fillMapWithItem("ak", "Alaska");
		fillMapWithItem("az", "Arizona");
		fillMapWithItem("ar", "Arkansas");
		fillMapWithItem("ca", "California"); // overnight
		fillMapWithItem("co", "Colorado");
		fillMapWithItem("ct", "Connecticut");
		fillMapWithItem("de", "Delaware");
		fillMapWithItem("fl", "Florida"); // success
		fillMapWithItem("ga", "Georgia"); // success
		fillMapWithItem("hi", "Hawaii");
		fillMapWithItem("id", "Idaho");
		fillMapWithItem("il", "Illinois");
		fillMapWithItem("in", "Indiana");
		fillMapWithItem("ia", "Iowa");
		fillMapWithItem("ks", "Kansas");
		fillMapWithItem("ky", "Kentucky");
		fillMapWithItem("la", "Louisiana");
		fillMapWithItem("me", "Maine");
		fillMapWithItem("md", "Maryland");
		fillMapWithItem("ma", "Massachusetts");
		fillMapWithItem("mi", "Michigan");
		fillMapWithItem("mn", "Minnesota");
		fillMapWithItem("ms", "Mississippi");
		fillMapWithItem("mo", "Missouri");
		fillMapWithItem("mt", "Montana");
		fillMapWithItem("ne", "Nebraska");
		fillMapWithItem("nv", "Nevada");
		fillMapWithItem("nh", "New Hampshire");
		fillMapWithItem("nj", "New Jersey");
		fillMapWithItem("nm", "New Mexico");
		fillMapWithItem("ny", "New York");
		fillMapWithItem("nc", "North Carolina");
		fillMapWithItem("nd", "North Dakota");
		fillMapWithItem("oh", "Ohio");
		fillMapWithItem("ok", "Oklahoma");
		fillMapWithItem("or", "Oregon");
		fillMapWithItem("pa", "Pennsylvania");
		fillMapWithItem("ri", "Rhode Island");
		fillMapWithItem("sc", "South Carolina");
		fillMapWithItem("sd", "South Dakota");
		fillMapWithItem("tn", "Tennessee");
		fillMapWithItem("tx", "Texas"); // overnight
		fillMapWithItem("ut", "Utah");
		fillMapWithItem("vt", "Vermont");
		fillMapWithItem("va", "Virginia");
		fillMapWithItem("wa", "Washington");
		fillMapWithItem("wv", "West Virginia");
		fillMapWithItem("wi", "Wisconsin");
		fillMapWithItem("wy", "Wyoming");
		fillMapWithItem("dc", "Washington DC");
	}

	private static void fillMapWithItem(String string, String string2) {
		map.put(string2.toLowerCase(), string.toLowerCase());

	}

	static class RunnableDemo6 implements Runnable {
		private Thread t;
		private String stateName;

		RunnableDemo6(String stateName) {
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
