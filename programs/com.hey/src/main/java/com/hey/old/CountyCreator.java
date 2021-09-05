package com.hey.old;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CountyCreator {

	private static Map<String, String> map = new HashMap<String, String>();
	// Coffee County,Alabama
	private static Map<String, Integer> mapOfCountyNameToCode = new HashMap<String, Integer>();
	private static Map<Integer, Row> mapOfCountyCodeToRow = new HashMap<Integer, Row>();

	private static final Set<Integer> fipsCodesToExclude = new HashSet<Integer>(
			Arrays.asList(2201, 2232, 2270, 2280, 46113));

//////////////////////////////////////////////////////////////

	private static class Row {
		int population = 0;
		String countyName;
		String stateName;
		Num annualRainfall = new Num();
		Num annualDaysOfPrecipitation = new Num();
		Num annualDaysOfSunshine = new Num();
		Num annualSnowfall = new Num();
		Num averageAugustHigh = new Num();
		Num averageDecemberHigh = new Num();
		Num augustHighMinusDecemberHigh = new Num();
		Num averageYearlyHumidity = new Num();
		Num averageWindspeed = new Num();
		Num violentCrimeIndex = new Num();
		Num propertyCrimeIndex = new Num();
		Num bachelors = new Num();
		Num medianAge = new Num();
		Num householdIncome = new Num();
		Num medianHouseCost = new Num();
		Num homeAppreciationTenYrs = new Num();
		Num airQuality = new Num();
		Num unemploymentRate = new Num();
		Num populationGrowthSince2010 = new Num();
		Num percentDemocrat = new Num();
		Num percentRepublican = new Num();
		Num percentAsian = new Num();
		Num percentBlack = new Num();
		Num percentWhite = new Num();
		Num percentHispanic = new Num();
	}

	static String headerString = "fips county code,county name,state name,Population,Average August High (F),Average December High (F),aug_hi-dec_hi,Annual Rainfall (in),Annual Days of Precipitation,Annual Days of Sunshine,Annual Snowfall (in),"
			+ "Average yearly humidity,windspeed,violentcrime,propertyCrime,medianage,bachelors,householdincome,houseCost,homeappreciation,airquality,unemploymentRate,populationgrowth,"
			+ "percentDemocrat,percentRepublican,asian,black,white,hispanic\n";

	private static void addRowDataToStringBuilder(String countyCode, StringBuilder sb, Row row, DecimalFormat df) {
		sb.append(countyCode).append(",").append(row.countyName).append(",").append(row.stateName).append(",")
				.append(row.population).append(",").append(df.format(row.averageAugustHigh.getAvg())).append(",")
				.append(df.format(row.averageDecemberHigh.getAvg())).append(",")
				.append(df.format(row.augustHighMinusDecemberHigh.getAvg())).append(",")
				.append(df.format(row.annualRainfall.getAvg())).append(",")
				.append(df.format(row.annualDaysOfPrecipitation.getAvg())).append(",")
				.append(df.format(row.annualDaysOfSunshine.getAvg())).append(",")
				.append(df.format(row.annualSnowfall.getAvg())).append(",");
		if (row.averageYearlyHumidity.getNumEntries() != 0) {
			sb.append(df.format(row.averageYearlyHumidity.getAvg()));
		}
		sb.append(",");
		if (row.averageWindspeed.getNumEntries() != 0) {
			sb.append(df.format(row.averageWindspeed.getAvg()));
		}
		sb.append(",").append(df.format(row.violentCrimeIndex.getAvg()));
		sb.append(",").append(df.format(row.propertyCrimeIndex.getAvg()));
		sb.append(",").append(df.format(row.medianAge.getAvg()));
		sb.append(",");
		if (row.bachelors.getNumEntries() != 0) {
			sb.append(df.format(row.bachelors.getAvg()));
		}
		sb.append(",").append(df.format(row.householdIncome.getAvg()));
		sb.append(",").append(df.format(row.medianHouseCost.getAvg()));
		sb.append(",").append(df.format(row.homeAppreciationTenYrs.getAvg()));
		sb.append(",").append(df.format(row.airQuality.getAvg()));
		sb.append(",").append(df.format(row.unemploymentRate.getAvg()));
		sb.append(",").append(df.format(row.populationGrowthSince2010.getAvg()));
		sb.append(",").append(df.format(row.percentDemocrat.getAvg()));
		sb.append(",").append(df.format(row.percentRepublican.getAvg()));
		sb.append(",").append(df.format(row.percentAsian.getAvg()));
		sb.append(",").append(df.format(row.percentBlack.getAvg()));
		sb.append(",").append(df.format(row.percentWhite.getAvg()));
		sb.append(",").append(df.format(row.percentHispanic.getAvg()));
		sb.append("\n");

	}

	private static void populateRow(String[] arr, Row row) {
		int rainFall = Integer.valueOf(arr[8]);
		int daysOfPrecipitation = Integer.valueOf(arr[9]);
		int daysOfSunshine = Integer.valueOf(arr[10]);
		int snowfall = Integer.valueOf(arr[11]);
		row.population += Integer.valueOf(arr[3]);
		row.annualRainfall.updateAverage(rainFall);
		row.annualDaysOfPrecipitation.updateAverage(daysOfPrecipitation);
		row.annualDaysOfSunshine.updateAverage(daysOfSunshine);
		row.annualSnowfall.updateAverage(snowfall);
		row.averageAugustHigh.updateAverage(Integer.valueOf(arr[5]));
		row.averageDecemberHigh.updateAverage(Integer.valueOf(arr[6]));
		row.augustHighMinusDecemberHigh.updateAverage(Integer.valueOf(arr[7]));
		String humidity = arr[12];
		if (!"N/A".equals(humidity)) {
			humidity = humidity.substring(0, humidity.length() - 1);
			row.averageYearlyHumidity.updateAverage(Integer.valueOf(humidity));
		}
		String windspeed = arr[13];
		if (!"N/A".equals(windspeed) && !"0".equals(windspeed)) {
			row.averageWindspeed.updateAverage(Integer.valueOf(windspeed));
		}
		row.violentCrimeIndex.updateAverage(Integer.valueOf(arr[14]));
		row.propertyCrimeIndex.updateAverage(Integer.valueOf(arr[15]));
		row.medianAge.updateAverage(Integer.valueOf(arr[16]));
		String bachelors = arr[17];
		if (!"N/A".equals(bachelors) && !"0".equals(bachelors)) {
			bachelors = bachelors.substring(0, bachelors.length() - 1);
			row.bachelors.updateAverage(Double.valueOf(bachelors));
		}
		row.householdIncome.updateAverage(Integer.valueOf(arr[18]));
		row.medianHouseCost.updateAverage(Integer.valueOf(arr[19]));
		String homeAppreciationLastTenYrs = arr[23];
		if (!"N/A".equals(homeAppreciationLastTenYrs) && !"0".equals(homeAppreciationLastTenYrs)) {
			homeAppreciationLastTenYrs = homeAppreciationLastTenYrs.substring(0,
					homeAppreciationLastTenYrs.length() - 1);
			row.homeAppreciationTenYrs.updateAverage(Double.valueOf(homeAppreciationLastTenYrs));
		}
		row.airQuality.updateAverage(Integer.valueOf(arr[24]));
		String unemploymentRate = arr[25];
		if (!"N/A".equals(unemploymentRate) && !"0".equals(unemploymentRate)) {
			unemploymentRate = unemploymentRate.substring(0, unemploymentRate.length() - 1);
			row.unemploymentRate.updateAverage(Double.valueOf(unemploymentRate));
		}
		String populationGrowth = arr[27];
		if (!"N/A".equals(populationGrowth) && !"0".equals(populationGrowth)) {
			populationGrowth = populationGrowth.substring(0, populationGrowth.length() - 1);
			row.populationGrowthSince2010.updateAverage(Double.valueOf(populationGrowth));
		}
		String percentLiberal = arr[28];
		if (!"N/A".equals(percentLiberal) && !"0".equals(percentLiberal)) {
			percentLiberal = percentLiberal.substring(0, percentLiberal.length() - 1);
			row.percentDemocrat.updateAverage(Double.valueOf(percentLiberal));
		}
		String percentConservative = arr[29];
		if (!"N/A".equals(percentConservative) && !"0".equals(percentConservative)) {
			percentConservative = percentConservative.substring(0, percentConservative.length() - 1);
			row.percentRepublican.updateAverage(Double.valueOf(percentConservative));
		}
		String asian = arr[30];
		if (!"N/A".equals(asian) && !"0".equals(asian)) {
			asian = asian.substring(0, asian.length() - 1);
			row.percentAsian.updateAverage(Double.valueOf(asian));
		}
		String black = arr[31];
		if (!"N/A".equals(black) && !"0".equals(black)) {
			black = black.substring(0, black.length() - 1);
			row.percentBlack.updateAverage(Double.valueOf(black));
		}
		String white = arr[32];
		if (!"N/A".equals(white) && !"0".equals(white)) {
			white = white.substring(0, white.length() - 1);
			row.percentWhite.updateAverage(Double.valueOf(white));
		}
		String hispanic = arr[33];
		if (!"N/A".equals(hispanic) && !"0".equals(hispanic)) {
			hispanic = hispanic.substring(0, hispanic.length() - 1);
			row.percentHispanic.updateAverage(Double.valueOf(hispanic));
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////

	private static class Data {
		int countyCode;
		String countyName;
		String stateName;
	}

	public static void main(String[] args) {
		populateMap();
		populateMapOfCountyNameToCode();
		populateMapOfCountyCodeToRow();
		writeOutput();
	}

	private static void writeOutput() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(headerString);
			for (Map.Entry<Integer, Row> entry : mapOfCountyCodeToRow.entrySet()) {
				if (fipsCodesToExclude.contains(entry.getKey())) {
					continue;
				}
				String countyCode = entry.getKey().toString();
				if (countyCode.length() == 4) {
					countyCode = "0" + countyCode;
				}
				Row row = entry.getValue();
				DecimalFormat df = new DecimalFormat("#");
				addRowDataToStringBuilder(countyCode, sb, row, df);
			}
			String outputTitle = "county_data_";
			outputTitle += System.currentTimeMillis() + ".csv";
			FileWriter myWriter = new FileWriter(outputTitle);
			myWriter.write(sb.toString());
			myWriter.close();
			System.out.println("wrote to file " + outputTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void populateMapOfCountyCodeToRow() {
		try {
			File myObj = new File("citydatainput.csv");
			Scanner myReader = new Scanner(myObj);
			myReader.nextLine();
			int idx = 0;
			while (myReader.hasNextLine()) {
				idx++;
				String line = myReader.nextLine();
				String[] arr = line.split(",");
				String countyName = arr[2].toLowerCase();
				if (!countyName.contains("county")) {
					countyName += " county";
				}
				String mapOfCountyNameToCodeKey = countyName + "," + arr[1].toLowerCase();
				if (!mapOfCountyNameToCode.containsKey(mapOfCountyNameToCodeKey)) {
					System.out.println("no key found for: " + mapOfCountyNameToCodeKey);
				}
				int countyCode = mapOfCountyNameToCode.get(mapOfCountyNameToCodeKey);
				Row row;
				if (!mapOfCountyCodeToRow.containsKey(countyCode)) {
					row = new Row();
					row.countyName = arr[2];
					row.stateName = arr[1];
				} else {
					row = mapOfCountyCodeToRow.get(countyCode);
				}
				populateRow(arr, row);
				mapOfCountyCodeToRow.put(countyCode, row);
			}
			myReader.close();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("issue in readInput");
		}

	}

	private static void populateMapOfCountyNameToCode() {
		try {
			File myObj = new File("countycodes.csv");
			Scanner myReader = new Scanner(myObj);
			myReader.nextLine();
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				String[] arr = line.split(",");
				Data data = new Data();
				data.countyCode = Integer.valueOf(arr[0]);
				data.countyName = arr[1].toLowerCase() + " county";
				if (map.get(arr[2].toLowerCase()) == null) {
					System.out.println(line);
					System.out.println(arr[2].toLowerCase());
				}
				data.stateName = map.get(arr[2].toLowerCase()).toLowerCase();
				String key = data.countyName + "," + data.stateName;
				mapOfCountyNameToCode.put(key, data.countyCode);
			}
			myReader.close();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("issue in readInput");
		}
	}

	private static void populateMap() {
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
		map.put(string.toLowerCase(), string2.toLowerCase());

	}

}
