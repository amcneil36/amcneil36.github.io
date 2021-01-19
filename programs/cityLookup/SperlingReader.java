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
	
	public static boolean debug = false;
	
	public static void log(String str) {
		if (debug) {
			System.out.println(str);
		}
	}

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
		WriteTextToFile(text, stateFullName);
	}
	
	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo(stateAbbreviation, stateFullName).start();
	}

	public static void main(String[] args) throws Exception {
	    runThread("al", "Alabama");
	    runThread("ak", "Alaska");
	    runThread("az", "Arizona");
	    runThread("ar", "Arkansas");
	    runThread("ca", "California");
	    runThread("co", "Colorado");
	    runThread("ct", "Connecticut");
	    runThread("de", "Delaware");
	    runThread("fl", "Florida");
	    runThread("ga", "Georgia");
	    runThread("hi", "Hawaii");
	    runThread("id", "Idaho");
	    runThread("il", "Illinois");
	    runThread("in", "Indiana");
	    runThread("ia", "Iowa");
	    runThread("ks", "Kansas");
	    runThread("ky", "Kentucky");
	    runThread("la", "Louisiana");
	    runThread("me", "Maine");
	    runThread("md", "Maryland");
	    runThread("ma", "Massachusetts");
	    runThread("mi", "Michigan");
	    runThread("mn", "Minnesota");
	    runThread("ms", "Mississippi");
	    runThread("mo", "Missouri");
	    runThread("mt", "Montana");
	    runThread("ne", "Nebraska");
	    runThread("nv", "Nevada");
	    runThread("nh", "New Hampshire");
	    runThread("nj", "New Jersey");
	    runThread("nm", "New Mexico");
	    runThread("ny", "New York");
	    runThread("nc", "North Carolina");
	    runThread("nd", "North Dakota");
	    runThread("oh", "Ohio");
	    runThread("ok", "Oklahoma");
	    runThread("or", "Oregon");
	    runThread("pa", "Pennsylvania");
	    runThread("ri", "Rhode Island");
	    runThread("sc", "South Carolina");
	    runThread("sd", "South Dakota");
	    runThread("tn", "Tennessee");
	    runThread("tx", "Texas");
	    runThread("ut", "Utah");
	    runThread("vt", "Vermont");
	    runThread("va", "Virginia");
	    runThread("wa", "Washington");
	    runThread("wv", "West Virginia");
	    runThread("wi", "Wisconsin");
	    runThread("wy", "Wyoming");
	    
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return  Pattern.compile("-?\\d+(\\.\\d+)?").matcher(strNum).matches();
	}
	
	private static List<DataObject> getSiteNames(String text, String stateAbbreviation) {
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
		return siteNames;
	}
	
	private static void addWeatherDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text2 = ReadTextFromPage(
				"https://www.bestplaces.net/weather/city/" + stateFullName + "/" + mySiteName + "/");

		int startIdx3 = text2.indexOf("August ", text2.indexOf("(°F)")) + "August ".length();
		int endIdx3 = text2.indexOf("°", startIdx3);
		String augHi = text2.substring(startIdx3, endIdx3);
		obj.augHi = augHi;

		int startIdx2 = text2.indexOf("December ", text2.indexOf("(°F)")) + "December ".length();
		int endIdx2 = text2.indexOf("°", startIdx2);
		String decHi = text2.substring(startIdx2, endIdx2);
		obj.decHi = decHi;
	}
	
	private static void addClimateDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = ReadTextFromPage(
				"https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
		int newStartIdx = text3.indexOf("gets ", text3.indexOf("climate in")) + "gets ".length();

		int newEndIdx = text3.indexOf(" ", newStartIdx);
		String numInchesOfRain = text3.substring(newStartIdx, newEndIdx);
		if (!isNumeric(numInchesOfRain)) {
			log("inches of rain didn't come as numeric for: https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
			throw new RuntimeException();
		}
		obj.numInchesOfRain = numInchesOfRain;
	}

	private static String GetStringForState(String stateAbbreviation, String stateFullName) {
		if (stateFullName.contains(" ")) {
			stateFullName = stateFullName.replace(" ", "_");
		}
		String text = ReadHtmlCode("https://www.bestplaces.net/find/state.aspx?state=" + stateAbbreviation + "/");
		List<DataObject> siteNames = getSiteNames(text, stateAbbreviation);
		StringBuilder sb = new StringBuilder("");
		for (DataObject obj : siteNames) {
			String mySiteName = obj.siteName;
			try {
            addWeatherDataToObj(obj, stateFullName, mySiteName);
            addClimateDataToObj(obj, stateFullName, mySiteName);
			}
			catch (Exception ex) {
				continue;
			}

			if (stateFullName.contains("_")) {
				stateFullName = stateFullName.replace("_", " ");
			}
			sb.append("arr.push(new Data(\"").append(obj.cityName).append("\", \"").append(stateFullName).append("\", ")
					.append(obj.augHi).append(", ").append(obj.decHi).append(", ").append(obj.numInchesOfRain).append("));\n");
		}
		return sb.toString();

	}

	private static void WriteTextToFile(String stringToWrite, String stateName) {
		try {
			// C:\Users\anmcneil\Desktop\myproject
			//FileWriter myWriter = new FileWriter("datapoints.js");
			String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\States\\" + stateName + ".js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(stringToWrite);
			myWriter.close();
			System.out.println("Successfully wrote to the file for: " + stateName);
		} catch (IOException e) {
			log("An error occurred.");
			e.printStackTrace();
		}

	}

	private static String ReadTextFromPage(String url) {
		try {
			return JsoupStuff(url);	
		}
		catch(Exception ex) {
			log("An error occurred from jsoup. Retrying: " + url);
			try {
				return JsoupStuff(url);
			}
			catch(Exception ex2) {
				log("An error occurred from jsoup. Giving up on: " + url);
				throw new RuntimeException();
			}
		}
	}

	private static String JsoupStuff(String url) throws IOException {
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		String text = doc.body().text();
		if (text.contains("Oops. This is embarrassing.")) {
			log("couldn't find information for: " + url);
			throw new RuntimeException();
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
			log("Malformed URL: " + e.getMessage());
			log("failed to read " + urlParam);
			throw new RuntimeException();
		} catch (IOException e) {
			log("I/O Error: " + e.getMessage());
			log("failed to read " + urlParam);
			throw new RuntimeException();
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
