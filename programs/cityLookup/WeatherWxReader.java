package com.hey;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class WeatherWxReader {

	static class HumidityStats {
		String augustHumidity = "N/A";
		String decemberHumidity = "N/A";
		String averageHumidity = "N/A";
	}

	public static void main(String[] args) throws Exception {
			runThread("al", "Alabama");
	/*	runThread("ak", "Alaska");
		runThread("az", "Arizona");
		runThread("ar", "Arkansas");
		runThread("ca", "California"); // overnight
		runThread("co", "Colorado");
		runThread("ct", "Connecticut");
		runThread("de", "Delaware");
		runThread("fl", "Florida"); // success
		runThread("ga", "Georgia"); // success
	runThread("hi", "Hawaii");
		runThread("id", "Idaho");
		runThread("il", "Illinois");
	//	runThread("in", "Indiana");
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
	    runThread("tx", "Texas"); // overnight
		runThread("ut", "Utah");
		runThread("vt", "Vermont");
		runThread("va", "Virginia");
		runThread("wa", "Washington"); 
		runThread("wv", "West Virginia");
		runThread("wi", "Wisconsin");
		runThread("wy", "Wyoming"); */
    

	}
	
	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo2(stateAbbreviation, stateFullName).start();
	}
	
	public static void ProcessState(String stateAbbreviation, String stateName) {
		List<String> list = readFileToList(stateName);
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		int numToUpdateOn = 20;
		int size = list.size();
		long initTime = System.currentTimeMillis();
		boolean isBanned = false;
		for (String line : list) {
			if (isBanned) {
				sb.append(line).append("\n");
				continue;
			}
			int count = StringUtils.countMatches(line, ",");
			if (count > 22) {
				sb.append(line).append("\n");
				continue;
			}
			HumidityStats stats = new HumidityStats();
			String windSpeed = "N/A";
			try {
			String cityName = extractCityName(line);
			String url = "https://www.weatherwx.com/hazardoutlook/" + stateAbbreviation + "/" + cityName.replace(" ", "+").replace("-", "+") + ".html";
			String webPageText = SperlingReader.ReadTextFromPage(url);
			System.out.println(webPageText);
			windSpeed = getAverageYearlyWindspeed(webPageText);
			stats = getHumidityStats(webPageText);
			}
			catch (SecurityException ex) {
				System.out.println("banned!");
				isBanned = true;
				sb.append(line).append("\n");
				continue;
			}
			catch(Exception ex) {
				// couldn't find data but not banned
			}
			line = line.substring(0, line.length() - 3);
			sb.append(line).append(", \"").append(windSpeed).append("\", \"").append(stats.augustHumidity)
					.append("\", \"").append(stats.decemberHumidity).append("\", \"").append(stats.averageHumidity).append("\"));\n");
			counter++;
			if (counter % numToUpdateOn == 0) {
				long secondsTakenForLastTen = (System.currentTimeMillis() - initTime)/1000;
				int numRemainingCities = size - counter;
				long minRemaining = secondsTakenForLastTen*numRemainingCities/(numToUpdateOn*60);
				System.out.println(stateName + " time remaining: " + SperlingReader.minToString((int)minRemaining));
				initTime = System.currentTimeMillis();
			}
		}
		SperlingReader.WriteTextToFile(sb.toString(), stateName);
	}

	private static HumidityStats getHumidityStats(String webPageText) {
		try {
			int humidityTotal = Integer.parseInt(SperlingReader.getNumbersImmediatelyBeforeText(webPageText, "%"));
			HumidityStats stats = new HumidityStats();
			for (int i = 0; i < 11; i++) {
				webPageText = webPageText.substring(webPageText.indexOf("%") + 1, webPageText.length());
				int humidity = Integer.parseInt(webPageText.substring(1, webPageText.indexOf("%")));
				if (i == 6) {
					stats.augustHumidity = String.valueOf(humidity) + "%";
				}
				if (i == 10) {
					stats.decemberHumidity = String.valueOf(humidity) + "%";
				}
				humidityTotal += humidity;
			}
			stats.averageHumidity = humidityTotal / 12 + "%";
			return stats;
		} catch (Exception ex) {
			HumidityStats stats = new HumidityStats();
			return stats;
		}
	}

	private static String getAverageYearlyWindspeed(String webPageText) {
		try {
			int totalWindSpeed = 0;
			String windSpeed = SperlingReader.getNumbersBeforeText(webPageText, "mph");

			totalWindSpeed += Integer.parseInt(windSpeed);

			for (int i = 0; i < 11; i++) {
				webPageText = webPageText.substring(webPageText.indexOf(" mph") + 1, webPageText.length());
				totalWindSpeed += Integer.parseInt(SperlingReader.getNextNumberAfterText(webPageText, "mph"));
			}
			String retVal = String.valueOf(totalWindSpeed / 12) + "mph";
			return retVal;
		} catch (Exception ex) {
			return "N/A";
		}
	}

	private static String extractCityName(String line) {
		int startIdx = 19;
		int endIdx = line.indexOf("\"", startIdx);
		return line.substring(startIdx, endIdx);
	}

	private static List<String> readFileToList(String stateName) {
		String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\States\\" + stateName + ".js";
		List<String> list = new LinkedList<String>();
		try {
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				list.add(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return list;
	}

}

class RunnableDemo2 implements Runnable {
	private Thread t;
	private String stateAbbreviation;
	private String stateFullName;

	RunnableDemo2(String stateAbbreviation, String stateFullName) {
		this.stateAbbreviation = stateAbbreviation;
		this.stateFullName = stateFullName;
	}

	public void run() {
		try {
			WeatherWxReader.ProcessState(stateAbbreviation, stateFullName);
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
