package com.hey;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Util {
	
	public static boolean debug = false;
	
	public static String minToString(int minRemaining) {
		String st = "";
		int hrRemaining = (int) Math.floor(minRemaining / 60);
		minRemaining = minRemaining % 60;
		if (hrRemaining > 0) {
			st += hrRemaining + "hr ";
		}
		st += minRemaining + "min";
		return st;
	}

	public static void WriteTextToFile(String stringToWrite, String stateName) {
		try {
			// C:\Users\anmcneil\Desktop\myproject
			// FileWriter myWriter = new FileWriter("datapoints.js");
			String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\States\\" + stateName + ".js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(stringToWrite);
			myWriter.close();
			System.out.println(stateName + " has been successfully written to===================================");
		} catch (IOException e) {
			log("An error occurred trying to write to file for: " + stateName);
			e.printStackTrace();
		}

	}

	public static String ReadTextFromPage(String url) {
		// System.out.println(url);
		return JsoupStuff(url);
	}

	public static Element RetrieveHtmlcodeFromPage(String url) {
		Connection conn = Jsoup.connect(url);
		try {
			Document doc = conn.userAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36")
					.referrer("http://www.google.com").get();
			Element body = doc.body();
			return body;
		} catch (IOException ex) {
			// System.out.println("oh no!");
		//	ex.printStackTrace();
			String stacktrace = ExceptionUtils.getStackTrace(ex);
			if (stacktrace.contains("Status=403")) {
				throw new SecurityException("you are banned from the website");
			}
			log("jsoup couldn't connect to: " + url);
			throw new RuntimeException();
		}
	}
	
	public static Element getElement(String url) throws Exception {
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		return doc.body();
	}

	private static String JsoupStuff(String url) {
		for (int i = 0; i < 10; i++) {
			Connection conn = Jsoup.connect(url);
			try {
				Document doc = conn.get();
				String text = doc.body().text();
				if (text.contains("Oops. This is embarrassing.") && i == 9) {
					System.out.println("jsoup connected to this page but it said oops this is embarassing: " + url);
					continue;
				}
				return text;
			} catch (IOException ex) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i == 9) {
					throw new RuntimeException("jsoup couldn't connect to: " + url, ex);
				}
			}
		}
		throw new RuntimeException("jsoup couldn't connect to: " + url);

	}
	
	public static void log(String str) {
		if (debug) {
			System.out.println(str);
		}
	}

	public static String ReadHtmlCode(String urlParam) {
		StringBuilder sb = new StringBuilder("");
		try {

			URL url = new URL(urlParam);

			// read text returned by server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				sb.append(line);
				sb.append("\n");
			}
			in.close();

		} catch (MalformedURLException e) {
			log("Malformed URL: " + e.getMessage());
			log("failed to read " + urlParam);
			throw new RuntimeException();
		} catch (IOException e) {
			log("I/O Error: " + e.getMessage());
			log("failed to read " + urlParam);
			e.printStackTrace();
			throw new RuntimeException();
		}
		return sb.toString();
	}
	
	private static Map<String, String> map = new HashMap<String, String>();
	
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
	
	public static String getStateAbbreviation(String stateName) {
		if (map.isEmpty()) {
			populateMap();
		}
		return map.get(stateName.toLowerCase());
	}
	
	    

}