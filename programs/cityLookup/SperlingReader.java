package com.hey;

import java.io.*;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

// C:\Users\anmcneil\eclipse-workspace\com.hey

//C:\Users\anmcneil\eclipse-workspace\com.hey\src\main\java\com\hey
//

public class SperlingReader {

	public static class DataObject {
		String siteName;
		String cityName;
		String stateName;
		String augHi;
		String decHi;
		String numInchesOfRain;

	}
	
	public static void ProcessState(String stateAbbreviation, String stateFullName) throws Exception {
		String text = GetStringForState(stateAbbreviation, stateFullName);
		if (text == null) {
			return;
		}
		WriteTextToFile(text, stateFullName);
	}
	
	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo(stateAbbreviation, stateFullName).start();
	}

	public static void main(String[] args) throws Exception {
	    runThread("wa", "Washington");
	    runThread("tx", "Texas");
	    runThread("ks", "Kansas");
	    runThread("mo", "Missouri");
	    runThread("ca", "California");
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return  Pattern.compile("-?\\d+(\\.\\d+)?").matcher(strNum).matches();
	}

	private static String GetStringForState(String stateAbbreviation, String stateFullName) throws Exception {
		String text = ReadHtmlCode("https://www.bestplaces.net/find/state.aspx?state=" + stateAbbreviation + "/");
        if (text == null) {
        	return null;
        }
		int length = ("<a href=../city/" + stateAbbreviation + "//").length();
		int endIdx = 0;
		List<DataObject> siteNames = new ArrayList<DataObject>();
		while (true) {
			int startIdx = text.indexOf("<a href=../city/" + stateAbbreviation + "//", endIdx);
			if (startIdx == -1) {
				break;
			}
			endIdx = text.indexOf(">", startIdx);
			String siteName = text.substring(startIdx + length, endIdx);
			DataObject obj = new DataObject();
			obj.siteName = siteName;
			siteNames.add(obj);
			startIdx = text.indexOf("<u>", endIdx);
			endIdx = text.indexOf("</u>", startIdx);
			String cityName = text.substring(startIdx + "<u>".length(), endIdx);
			obj.cityName = cityName;
		}
		int tempCounter = 0;
		int numFailedAttempts = 0;
		StringBuilder sb = new StringBuilder("");
		for (DataObject obj : siteNames) {
			String mySiteName = obj.siteName;
			String text2 = ReadTextFromPage(
					"https://www.bestplaces.net/weather/city/" + stateFullName + "/" + mySiteName + "/");
			if (text2 == null) {
				numFailedAttempts++;
				if (numFailedAttempts > 50) {
					return sb.toString();
				}
				continue;
			}
			int startIdx3 = text2.indexOf("August ", text2.indexOf("(°F)")) + "August ".length();
			int endIdx3 = text2.indexOf("°", startIdx3);
			String augHi = text2.substring(startIdx3, endIdx3);
			obj.augHi = augHi;

			int startIdx2 = text2.indexOf("December ", text2.indexOf("(°F)")) + "December ".length();
			int endIdx2 = text2.indexOf("°", startIdx2);
			String decHi = text2.substring(startIdx2, endIdx2);
			obj.decHi = decHi;
			String text3 = ReadTextFromPage(
					"https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
			if (text3 == null) {
				numFailedAttempts++;
				if (numFailedAttempts > 50) {
					return sb.toString();
				}
				continue;
			}
			tempCounter++;
			if (tempCounter == 50) {
				//return sb.toString();
			}
			int newStartIdx = text3.indexOf("gets ", text3.indexOf("climate in")) + "gets ".length();

			int newEndIdx = text3.indexOf(" ", newStartIdx);
			String numInchesOfRain = text3.substring(newStartIdx, newEndIdx);
			if (!isNumeric(numInchesOfRain)) {
				System.out.println("inches of rain didn't come as numeric for: https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
				numFailedAttempts++;
				continue;
			}
			obj.numInchesOfRain = numInchesOfRain;
			sb.append("arr.push(new Data(\"").append(obj.cityName).append("\", \"").append(stateFullName).append("\", ")
					.append(obj.augHi).append(", ").append(obj.decHi).append(", ").append(obj.numInchesOfRain).append("));\n");
		}
		return sb.toString();

	}

	private static void WriteTextToFile(String stringToWrite, String stateName) {
		try {
			// C:\Users\anmcneil\Desktop\myproject
			//FileWriter myWriter = new FileWriter("datapoints.js");
			String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\" + stateName + ".js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(stringToWrite);
			myWriter.close();
			System.out.println("Successfully wrote to the file for: " + stateName);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private static String ReadTextFromPage(String url) throws IOException {
		try {
			return JsoupStuff(url);	
		}
		catch(Exception ex) {
			System.out.println("An error occurred from jsoup. Retrying: " + url);
			try {
				return JsoupStuff(url);
			}
			catch(Exception ex2) {
				System.out.println("An error occurred from jsoup. Giving up on: " + url);
				return null;
			}
		}
	}

	private static String JsoupStuff(String url) throws IOException {
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		String text = doc.body().text();
		if (text.contains("Oops. This is embarrassing.")) {
			System.out.println("couldn't find information for: " + url);
			return null;
		}
		return text;
	}

	private static String ReadHtmlCode(String urlParam) {
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
			System.out.println("Malformed URL: " + e.getMessage());
			System.out.println("failed to read " + urlParam);
			return null;
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
			System.out.println("failed to read " + urlParam);
			return null;
		}
		return sb.toString();
	}

}

class RunnableDemo implements Runnable {
	   private Thread t;
	   private String stateAbbreviation;
	   private String stateFullName;
	   
	   RunnableDemo(String stateAbbreviation, String stateFullName) {
	      this.stateAbbreviation = stateAbbreviation;
	      this.stateFullName = stateFullName;
	   }
	   
	   public void run() {
		   try {
			SperlingReader.ProcessState(stateAbbreviation, stateFullName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   
	   public void start () {
	      if (t == null) {
	         t = new Thread (this);
	         t.start ();
	      }
	   }
	}
