package com.hey;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.jsoup.nodes.Element;

import com.hey.SperlingReader.DataObject;

public class ZipCode {

	public static class DataObject2 {
		String zipCode;
		String cityName;
		String stateName;
		public String countyName;
		String population;
		String populationDensity;
		String augHi; //
		String decHi; //
		public int augHiMinusDecHi;
		String violentCrime = "N/A";
		String propertyCrime = "N/A";
		String medianAge;
		String medianIncome;
		String medianHomePrice;
		String percentAsian = "N/A";
		String percentBlack = "N/A";
		String percentWhite = "N/A";
		String percentHispanic = "N/A";

		String numInchesOfRain = "-1";
		String numInchesOfSnow = "-1";
		String numSunnyDays = "-1";
		String numDaysOfRain = "-1";

		private String getEndingUrl() {
			return this.stateName + "/" + this.cityName + "/" + this.zipCode;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}

	// keytool -import -alias example4 -keystore "C:\Program Files
	// (x86)\Java\jre1.8.0_281\lib\security\cacerts" -file myCer3.cer

	public static void main(String[] args) throws InterruptedException {
		String stateName = "georgia";
		String stateAbbreviation = "ga";
		Element el = SperlingReader
				.RetrieveHtmlcodeFromPage("https://www.bestplaces.net/find/zip.aspx?st=" + stateAbbreviation);
		String[] data = el.getElementsByAttribute("href").toString().split("\n");
		List<String> data2 = new ArrayList<String>();
		for (String st : data) {
			if (st.contains("<a href=\"../zip-code/")) {
				data2.add(st);
			}
		}
		data = null;
		System.out.println("num zip codes in " + stateName + ": " + data2.size());
		int numThreads = 30;
		List<RunnableDemo> demos = new ArrayList<RunnableDemo>();
		List<DataObject2> list = new ArrayList<DataObject2>();
		for (int i = 0; i < numThreads; i++) {
			demos.add(retrieveOutputAsync(stateName, data2, numThreads, i));
		}
		for (RunnableDemo demo : demos) {
			demo.t.join();
		}
		for (RunnableDemo demo : demos) {
			List<DataObject2> objs = demo.getOutput();
			for (DataObject2 obj : objs) {
				list.add(obj);
			}
		}
		writeOutput(list);

	}

	private static RunnableDemo retrieveOutputAsync(String stateName, List<String> data2, int divisor, int remainder) {
		RunnableDemo rd = new RunnableDemo(stateName, data2, divisor, remainder);
		rd.start();
		return rd;
	}

	private static List<DataObject2> retrieveAllData(String stateName, List<String> data2, int divisor, int remainder) {
		List<DataObject2> list = new ArrayList<DataObject2>();
		float numCompletedZipCodes = 0;
		float numZipCodes = data2.size()/divisor;
		long startTime = System.currentTimeMillis();
		int idx = 0;
		for (String st : data2) {
			if (idx%divisor != remainder) {
				idx++;
				continue;
			}
			if (numCompletedZipCodes > 2) {
				break;
			}
			DataObject2 obj = populateDataObject(stateName, st);
			list.add(obj);
			numCompletedZipCodes++;
			idx++;
			long secondsSinceStart = (System.currentTimeMillis() - startTime) / 1000;
			float numRemainingZipCodes = (numZipCodes - numCompletedZipCodes);
			float minRemaining = (numRemainingZipCodes * secondsSinceStart / (numCompletedZipCodes)) / 60;
			System.out.println("thread id: " + Thread.currentThread().getId() + ". completed zip code " + obj.zipCode + ". " + (int)numCompletedZipCodes + " of " + (int)(numZipCodes)
					+ " zip codes have been completed for " + stateName + ". Time remaining: "
					+ SperlingReader.minToString((int) minRemaining));

		}
		return list;
	}

	public static DataObject2 populateDataObject(String stateName, String st) {
		st = st.substring(("<a href=\"../zip-code/" + stateName + "/").length(), st.indexOf("<u>") - 2);
		String[] temp = st.split("/");
		String city = temp[0];
		String zip = temp[1];
		DataObject2 obj = new DataObject2();
		obj.zipCode = zip;
		obj.cityName = city;
		obj.stateName = stateName;
		addTemperatureDataToObj2(obj);
		addPeopleDataToObj2(obj);
		addOverviewDataToObj2(obj);
		addCrimeDataToObj2(obj);
		addClimateDataToObj(obj);
		return obj;
	}

	private static void addClimateDataToObj(DataObject2 obj) {
		String url = "https://www.bestplaces.net/climate/zip-code/" + obj.getEndingUrl();
		String text3 = SperlingReader.ReadTextFromPage(url);
		if (text3.contains("sunny days per")) {
			obj.numInchesOfRain = SperlingReader.getNumbersBeforeText(text3, "inches of rain");
			obj.numInchesOfSnow = SperlingReader.getNumbersBeforeText(text3, "inches of snow");
			obj.numSunnyDays = SperlingReader.getNumbersBeforeText(text3, "sunny days per");
			obj.numDaysOfRain = SperlingReader.getNumbersBeforeText(text3, "days per year. Precipitation");
		} else {
			obj.numInchesOfRain = SperlingReader.getNextNumberAfterText(text3, "Rainfall");
			obj.numInchesOfSnow = SperlingReader.getNextNumberAfterText(text3, "Snowfall");
			obj.numSunnyDays = SperlingReader.getNextNumberAfterText(text3, "Sunny");
			obj.numDaysOfRain = SperlingReader.getNextNumberAfterText(text3, "Precipitation");
		}
	}

	static class AndrewStringBuilder {
		private StringBuilder sb = new StringBuilder();

		private String getString() {
			return sb.toString();
		}

		private AndrewStringBuilder appendComma(String st) {
			sb.append(st).append(",");
			return this;
		}

		private AndrewStringBuilder appendNewLine(String st) {
			sb.append(st).append("\n");
			return this;
		}
	}

	private static void writeOutput(List<DataObject2> list) {
		try {
			AndrewStringBuilder sb = new AndrewStringBuilder();
			sb.appendNewLine(
					"zipCode,cityName,stateName,countyName,population,populationDensity,augHi,decHi,augHiMinusDecHi,numInchesOfRain,numDaysOfRain,numSunnyDays,numInchesOfSnow,numviolentCrime,propertyCrime,medianAge,medianIncome,medianHomePrice,%Asian,%Black,%White,%Hispanic");
			for (DataObject2 obj : list) {
				addRowDataToStringBuilder(sb, obj);
			}
			String outputTitle = list.get(0).stateName + "_";
			outputTitle += System.currentTimeMillis() + ".csv";
			FileWriter myWriter = new FileWriter(outputTitle);
			myWriter.write(sb.getString());
			myWriter.close();
			System.out.println("wrote to file " + outputTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addRowDataToStringBuilder(AndrewStringBuilder sb, DataObject2 obj) {
		sb.appendComma(obj.zipCode).appendComma(obj.cityName).appendComma(obj.stateName).appendComma(obj.countyName)
				.appendComma(obj.population).appendComma(obj.populationDensity).appendComma(obj.augHi)
				.appendComma(obj.decHi).appendComma(String.valueOf(obj.augHiMinusDecHi)).appendComma(obj.numInchesOfRain)
				.appendComma(obj.numDaysOfRain).appendComma(obj.numSunnyDays).appendComma(obj.numInchesOfSnow)
				.appendComma(obj.violentCrime).appendComma(obj.propertyCrime).appendComma(obj.medianAge)
				.appendComma(obj.medianIncome).appendComma(obj.medianHomePrice).appendComma(obj.percentAsian)
				.appendComma(obj.percentBlack).appendComma(obj.percentWhite).appendNewLine(obj.percentHispanic);
	}

	private static void addTemperatureDataToObj2(DataObject2 obj) {
		String text2 = SperlingReader
				.ReadTextFromPage("https://www.bestplaces.net/weather/zip-code/" + obj.getEndingUrl());

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

	private static void addPeopleDataToObj2(DataObject2 obj) {
		String url = "https://www.bestplaces.net/people/zip-code/" + obj.getEndingUrl();
		String text3 = SperlingReader.ReadTextFromPage(url);
		if (text3.contains("claim Hispanic")) {
			obj.population = SperlingReader.getNumbersBeforeText(text3, " There are ");
			obj.populationDensity = SperlingReader.getNumbersBeforeText(text3, "people per square mile");
			obj.percentHispanic = Reader
					.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " claim Hispanic"));
			obj.percentWhite = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are white"));
			obj.percentBlack = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are black"));
			obj.percentAsian = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are asian"));
		} else {
			obj.population = SperlingReader.getNextNumberAfterText(text3, "Population");
			obj.populationDensity = SperlingReader.getNextNumberAfterText(text3, "Population Density");
			obj.percentHispanic = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Hispanic"));
			obj.percentWhite = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "White"));
			obj.percentBlack = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Black"));
			obj.percentAsian = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Asian"));
		}
	}

	private static void addOverviewDataToObj2(DataObject2 obj) {
		String text3 = SperlingReader.ReadTextFromPage("https://www.bestplaces.net/zip-code/" + obj.getEndingUrl());
		String medianIncome = SperlingReader.getNextNumberAfterText(text3, "Median Income");
		obj.medianIncome = medianIncome;
		String medianHomePrice = SperlingReader.getNextNumberAfterText(text3, "Home Price");
		obj.medianHomePrice = medianHomePrice;
		String medianAge = SperlingReader.getNextNumberAfterText(text3, "Median Age");
		obj.medianAge = medianAge;
		obj.countyName = text3.substring(text3.indexOf("County: ") + "County: ".length(),
				text3.indexOf(" Metro Area: "));
	}

	private static void addCrimeDataToObj2(DataObject2 obj) {
		String text3 = SperlingReader
				.ReadTextFromPage("https://www.bestplaces.net/crime/zip-code/" + obj.getEndingUrl());
		if (text3.contains("violent crime is")) {
			String violentCrime = SperlingReader.getNextNumberAfterText(text3, "violent crime is");
			obj.violentCrime = violentCrime;
			String propertyCrime = SperlingReader.getNextNumberAfterText(text3, "property crime is");
			obj.propertyCrime = propertyCrime;
		}
	}
	static class RunnableDemo implements Runnable {
		private Thread t;
		private String stateName;
		private List<String> data2;
		private int divisor;
		private int remainder;
		private List<DataObject2> output = new ArrayList<DataObject2>();

		RunnableDemo(String stateName, List<String> data2, int divisor, int remainder) {
			this.stateName = stateName;
			this.data2 = data2;
			this.divisor = divisor;
			this.remainder = remainder;
		}

		public void run() {
			try {
				output = ZipCode.retrieveAllData(stateName, data2, divisor, remainder);
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
		
		public List<DataObject2> getOutput(){
			return output;
		}
	}
}

