package com.hey.old;

import java.io.*;
import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hey.WebPageReader;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// C:\Users\anmcneil\eclipse-workspace\com.hey

//C:\Users\anmcneil\eclipse-workspace\com.hey\src\main\java\com\hey
//

// shutdown /s /t 4500
// ^ shutdown in 4500 seconds
// finished cali but not texas

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
		String numInchesOfSnow;
		String numSunnyDays;
		String numDaysOfRain;
		String population;
		String populationDensity;
		String medianIncome;
		String medianHomePrice;
		String medianAge;
		String violentCrime;
		String propertyCrime;
		String airQualityIndex;
		String medianHomeAge;
		String homeAppreciationLastYear;
		String homeAppreciationLastFiveYears;
		String homeAppreciationLastTenYears;
		String averageOneWayCommuteTime;
		public String countyName;
		public int augHiMinusDecHi;

	}

	public static void ProcessState(String stateAbbreviation, String stateFullName) throws Exception {
		String text = GetStringForState(stateAbbreviation, stateFullName);
		WebPageReader.WriteTextToFile(text, stateFullName);
	}

	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo(stateAbbreviation, stateFullName).start();
	}

	public static void main(String[] args) throws Exception {
		String txt = WebPageReader.ReadHtmlCode("https://www.bestplaces.net/education/city/california/oceanside");
		System.out.println(txt);
		// maybe have it write to files every so often and can also add in a startCity
		// option for starting again after terminating

		// SperlingReader.ProcessState("ca", "California");

		/*
		 * runThread("al", "Alabama"); runThread("ak", "Alaska"); runThread("az",
		 * "Arizona"); runThread("ar", "Arkansas"); // runThread("ca", "California"); //
		 * overnight runThread("co", "Colorado"); runThread("ct", "Connecticut");
		 * runThread("de", "Delaware"); runThread("fl", "Florida"); // success
		 * runThread("ga", "Georgia"); // success runThread("hi", "Hawaii");
		 */// runThread("id", "Idaho");
			// runThread("il", "Illinois");
			// runThread("in", "Indiana");
			// runThread("ia", "Iowa");
			// runThread("ks", "Kansas");
			// runThread("ky", "Kentucky");
			// runThread("la", "Louisiana");
			// runThread("me", "Maine");
//		runThread("md", "Maryland");
//	runThread("ma", "Massachusetts");
//		runThread("mi", "Michigan");
		// runThread("mn", "Minnesota");
//		runThread("ms", "Mississippi");
//		runThread("mo", "Missouri");
//		runThread("mt", "Montana");
//		runThread("ne", "Nebraska");
//		runThread("nv", "Nevada");
//		runThread("nh", "New Hampshire");
		// runThread("nj", "New Jersey");
//		runThread("nm", "New Mexico");
//		runThread("ny", "New York");
//		runThread("nc", "North Carolina");
//		runThread("nd", "North Dakota");
		// runThread("oh", "Ohio");
		/*
		 * runThread("ok", "Oklahoma"); runThread("or", "Oregon"); runThread("pa",
		 * "Pennsylvania"); runThread("ri", "Rhode Island"); runThread("sc",
		 * "South Carolina"); runThread("sd", "South Dakota"); runThread("tn",
		 * "Tennessee"); // runThread("tx", "Texas"); // overnight runThread("ut",
		 * "Utah"); runThread("vt", "Vermont"); runThread("va", "Virginia");
		 * runThread("wa", "Washington"); runThread("wv", "West Virginia");
		 * runThread("wi", "Wisconsin"); runThread("wy", "Wyoming");
		 */

	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		return Pattern.compile("-?\\d+(\\.\\d+)?").matcher(strNum).matches();
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
		String text2 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/weather/city/" + stateFullName + "/" + mySiteName + "/");

		int startIdx3 = text2.indexOf("August ", text2.indexOf("(°F)")) + "August ".length();
		int endIdx3 = text2.indexOf("°", startIdx3);
		String augHi = text2.substring(startIdx3, endIdx3);
		obj.augHi = augHi;

		int startIdx2 = text2.indexOf("December ", text2.indexOf("(°F)")) + "December ".length();
		int endIdx2 = text2.indexOf("°", startIdx2);
		String decHi = text2.substring(startIdx2, endIdx2);
		obj.decHi = decHi;
		obj.augHiMinusDecHi = Integer.parseInt(obj.augHi) - Integer.parseInt(obj.decHi);
	}

	public static String getNumbersBeforeText(String stringToSearch, String pattern) {
		StringBuilder st = new StringBuilder();
		int idx = stringToSearch.indexOf(pattern);
		if (idx == -1) {
			throw new RuntimeException("pattern not found: " + pattern + " in the following text: " + stringToSearch);
		}
		idx -= 2;
		int counter = 0;
		while (stringToSearch.charAt(idx) != ' ' && stringToSearch.charAt(idx) != '$') {
			char c = stringToSearch.charAt(idx);
			if (c != ',') {
				st.append(c);
			}
			idx--;
			if (counter > 30) {
				throw new RuntimeException("while loop too long");
			}
			counter++;
		}

		st.reverse();
		String st2 = st.toString();
		if (!isNumeric(st2)) {
			throw new RuntimeException("this text didn't come back as numeric: " + pattern);
		}
		return st2;
	}

	public static String getNumbersImmediatelyBeforeText(String stringToSearch, String pattern) {
		StringBuilder st = new StringBuilder();
		int idx = stringToSearch.indexOf(pattern);
		idx -= 1;
		int counter = 0;
		while (stringToSearch.charAt(idx) != ' ') {
			char c = stringToSearch.charAt(idx);
			if (c != ',') {
				st.append(c);
			}
			idx--;
			if (counter > 30) {
				throw new RuntimeException("while loop too long");
			}
			counter++;
		}

		st.reverse();
		String st2 = st.toString();
		if (!isNumeric(st2)) {
			throw new RuntimeException("this text didn't come back as numeric: " + pattern);
		}
		return st2;
	}

	private static void addClimateDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/climate/city/" + stateFullName + "/" + mySiteName + "/");
		String numInchesOfRain = getNumbersBeforeText(text3, "inches of rain");
		obj.numInchesOfRain = numInchesOfRain;
		String numInchesOfSnow = getNumbersBeforeText(text3, "inches of snow");
		obj.numInchesOfSnow = numInchesOfSnow;
		String numSunnyDays = getNumbersBeforeText(text3, "sunny days per");
		obj.numSunnyDays = numSunnyDays;
		String numDaysOfRain = getNumbersBeforeText(text3, "days per year. Precipitation");
		obj.numDaysOfRain = numDaysOfRain;
	}

	private static void addPeopleDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/people/city/" + stateFullName + "/" + mySiteName + "/");
		String population = getNextNumberAfterText(text3, "The population in");
		obj.population = population;
		String populationDensity = getNumbersBeforeText(text3, "people per square mile");
		obj.populationDensity = populationDensity;
	}

	private static void addOverviewDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage("https://www.bestplaces.net/city/" + stateFullName + "/" + mySiteName + "/");
		String medianIncome = getNextNumberAfterText(text3, "Median Income");
		obj.medianIncome = medianIncome;
		String medianHomePrice = getNextNumberAfterText(text3, "Home Price");
		obj.medianHomePrice = medianHomePrice;
		String medianAge = getNextNumberAfterText(text3, "Median Age");
		obj.medianAge = medianAge;
		obj.countyName = text3.substring(text3.indexOf("County: ") + "County: ".length(),
				text3.indexOf(" Metro Area: "));
	}

	private static void addCrimeDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/crime/city/" + stateFullName + "/" + mySiteName + "/");
		String violentCrime = getNextNumberAfterText(text3, "violent crime is");
		obj.violentCrime = violentCrime;
		String propertyCrime = getNextNumberAfterText(text3, "property crime is");
		obj.propertyCrime = propertyCrime;
	}

	private static void addHealthDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/health/city/" + stateFullName + "/" + mySiteName + "/");
		String airQualityIndex = getNextNumberAfterText(text3, "Air Quality Index ");
		obj.airQualityIndex = airQualityIndex;
	}

	private static void addHousingDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/housing/city/" + stateFullName + "/" + mySiteName + "/");
		// Median Home Age
		String medianHomeAge = getNextNumberAfterText(text3, "Median Home Age");
		obj.medianHomeAge = medianHomeAge;
		obj.homeAppreciationLastYear = getNextNumberAfterText(text3, "Home Appr. Last 12 months");
		obj.homeAppreciationLastFiveYears = getNextNumberAfterText(text3, "Home Appr. Last 5 yrs");
		obj.homeAppreciationLastTenYears = getNextNumberAfterText(text3, "Home Appr. Last 10 yrs");

	}

	private static void addCommuteDataToObj(DataObject obj, String stateFullName, String mySiteName) {
		String text3 = WebPageReader.ReadTextFromPage(
				"https://www.bestplaces.net/transportation/city/" + stateFullName + "/" + mySiteName + "/");
		obj.averageOneWayCommuteTime = getNextNumberAfterText(text3, "one-way commute in");

	}

	public static String getNextNumberAfterText(String text3, String pattern) {
		int idx = text3.indexOf(pattern) + pattern.length();
		if (idx == -1) {
			throw new RuntimeException("didnt find pattern: " + pattern);
		}
		char c = text3.charAt(idx);
		int counter = 0;
		while (!Character.isDigit(c) && !(c == '-' && Character.isDigit(text3.charAt(idx + 1)))) {
			idx++;
			c = text3.charAt(idx);
			counter++;
			if (counter > 700) {
				throw new RuntimeException("never found pattern after 700 tries: " + pattern);
			}
		}
		counter = 0;
		StringBuilder sb = new StringBuilder();
		while (Character.isDigit(c) || c == ',' || c == '-' || c == '.') {
			counter++;
			if (c != ',') {
				sb.append(c);
			}
			idx++;
			c = text3.charAt(idx);
			if (counter > 80) {
				throw new RuntimeException("number is apparently more than 80 characters for this pattern: " + pattern);
			}
		}
		String st = sb.toString();
		if (st.charAt(st.length() - 1) == '.') {
			st = st.substring(0, st.length() - 1);
		}
		return st;
	}

	private static String GetStringForState(String stateAbbreviation, String stateFullName) {
		if (stateFullName.contains(" ")) {
			stateFullName = stateFullName.replace(" ", "_");
		}
		String text = WebPageReader.ReadHtmlCode("https://www.bestplaces.net/find/state.aspx?state=" + stateAbbreviation + "/");
		List<DataObject> siteNames = getSiteNames(text, stateAbbreviation);
		StringBuilder sb = new StringBuilder("");
		int counter = 0;
		long initTime = System.currentTimeMillis();
		int size = siteNames.size();
		int numToUpdateOn = 20;
		for (DataObject obj : siteNames) {
			String mySiteName = obj.siteName;
			try {
				addWeatherDataToObj(obj, stateFullName, mySiteName);
				addClimateDataToObj(obj, stateFullName, mySiteName);
				addPeopleDataToObj(obj, stateFullName, mySiteName);
				addOverviewDataToObj(obj, stateFullName, mySiteName);
				addCrimeDataToObj(obj, stateFullName, mySiteName);
				addHealthDataToObj(obj, stateFullName, mySiteName);
				addHousingDataToObj(obj, stateFullName, mySiteName);
				addCommuteDataToObj(obj, stateFullName, mySiteName);
			} catch (Exception ex) {
				continue;
			}

			if (stateFullName.contains("_")) {
				stateFullName = stateFullName.replace("_", " ");
			}
			sb.append("arr.push(new Data(\"").append(obj.cityName).append("\", \"").append(stateFullName).append("\", ")
					.append(obj.augHi).append(", ").append(obj.decHi).append(", ").append(obj.numInchesOfRain)
					.append(", ").append(obj.numInchesOfSnow).append(", ").append(obj.numSunnyDays).append(", ")
					.append(obj.numDaysOfRain).append(", ").append(obj.population).append(", ")
					.append(obj.populationDensity).append(", ").append(obj.medianIncome).append(", ")
					.append(obj.medianHomePrice).append(", ").append(obj.medianAge).append(", ")
					.append(obj.violentCrime).append(", ").append(obj.propertyCrime).append(", ")
					.append(obj.airQualityIndex).append(", ").append(obj.medianHomeAge).append(", ")
					.append(obj.homeAppreciationLastYear).append(", ").append(obj.homeAppreciationLastFiveYears)
					.append(", ").append(obj.homeAppreciationLastTenYears).append(", ")
					.append(obj.averageOneWayCommuteTime).append(", \"").append(obj.countyName).append("\", ")
					.append(obj.augHiMinusDecHi).append("));\n");
			counter++;
			if (counter % numToUpdateOn == 0) {
				long secondsTakenForLastTen = (System.currentTimeMillis() - initTime) / 1000;
				int numRemainingCities = size - counter;
				long minRemaining = secondsTakenForLastTen * numRemainingCities / (numToUpdateOn * 60);
				System.out.println(stateFullName + " time remaining: " + WebPageReader.minToString((int) minRemaining));
				initTime = System.currentTimeMillis();
			}
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

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}