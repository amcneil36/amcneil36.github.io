package com.hey;

import java.io.*;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) throws Exception {
        String text = "";
        text += "var arr = [];";
		text += GetStringForState("wa", "Washington");
		 text += GetStringForState("tx", "Texas");
		WriteTextToFile(text);

	}

	private static String GetStringForState(String stateAbbreviation, String stateFullName) throws Exception {
		String text = ReadHtmlCode("https://www.bestplaces.net/find/state.aspx?state=" + stateAbbreviation + "/");

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
		StringBuilder sb = new StringBuilder("");
		for (DataObject obj : siteNames) {
			String mySiteName = obj.siteName;
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
			tempCounter++;

			String text3 = ReadTextFromPage(
					"https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
			int newStartIdx = text3.indexOf("gets ", text3.indexOf("climate in")) + "gets ".length();

			int newEndIdx = text3.indexOf(" ", newStartIdx);
			String numInchesOfRain = text3.substring(newStartIdx, newEndIdx);
			obj.numInchesOfRain = numInchesOfRain;
			sb.append("arr.push(new Data(\"").append(obj.cityName).append("\", \"").append(stateFullName).append("\", ")
					.append(obj.augHi).append(", ").append(obj.decHi).append(", ").append(obj.numInchesOfRain).append("));\n");
			if (tempCounter == 5) {
				return sb.toString();
			}
		}
		return sb.toString();

	}

	private static void WriteTextToFile(String string) {
		try {
			// C:\Users\anmcneil\Desktop\myproject
			//FileWriter myWriter = new FileWriter("datapoints.js");
			String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\datapoints.js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(string);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private static String ReadTextFromPage(String url) throws IOException {
		// Connecting to the web page
		Connection conn = Jsoup.connect(url);
		// executing the get request
		Document doc = conn.get();
		// Retrieving the contents (body) of the web page
		String result = doc.body().text();
		return result;
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
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return sb.toString();
	}

}
