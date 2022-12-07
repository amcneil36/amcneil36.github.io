package main.java.com.hey.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static main.java.com.hey.CityStats.*;

import main.java.com.hey.Util;

// county fips codes from https://transition.fcc.gov/oet/info/maps/census/fips/fips.txt
public class DataWrapperCountyCsvCreator {

	public static void main(String[] args) throws Exception {
		writeToFile(HOTTEST_MONTHS_HEAT_INDEX_HIGH);
	}
	
	public static void writeToFile(String col) throws Exception{
		// create a map of state, county to fips code
		Map<String, String> mapOfStateCountyToFipsCode = createMapOfStateCountytoFipsCode();
		List<String> text = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CountyStats\\CountyStats.csv");
		Util.writeTextToFile("my filepath", "my text to write");
		StringBuilder sb = new StringBuilder();
		
		Map<String, Integer> mapOfColNameToIdx = new HashMap<>();
		String[] arrFoo = text.get(0).split(",");
		for (int i = 0; i < arrFoo.length; i++) {
			mapOfColNameToIdx.put(arrFoo[i], i);
		}
		text.remove(0);
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (String line : text) {
			String[] arr = line.split(",");
			String countyName = arr[mapOfColNameToIdx.get(COUNTY)].toLowerCase();
			String stateName = arr[mapOfColNameToIdx.get(STATE)];
			String key = getKey(stateName, countyName);
			if (mapOfStateCountyToFipsCode.containsKey(key)) {
				String fipsCode = mapOfStateCountyToFipsCode.get(key);
				String value = arr[mapOfColNameToIdx.get(col)];
				value = value.replace("%", "");
				if (!value.contains("N/A")) {
					double valDouble = Double.valueOf(value);
					min = Math.min(min,  valDouble);
					max = Math.max(max, valDouble);
					sb.append(fipsCode).append(",").append(value).append("\n");	
				}
				
			}
		}
		System.out.println(col + " min/max: " + min + "/" + max);
		Util.writeTextToFile("DataWrapper//dataWrapperCounty.csv", sb.toString());
	}
	
	private static String getKey(String stateName, String countyName) {
		return stateName.toLowerCase() + ", " + countyName.toLowerCase();
	}

	private static Map<String, String> createMapOfStateCountytoFipsCode() throws Exception {
		List<String> text = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\FIPSCodes\\countyFipsCodes.txt");
		int idx = 0;
		while (!text.get(idx).contains("---")) {
			idx++;
		}
		Map<String, String> mapOfStateNumToStateName = new HashMap<>();
		Map<String, String> mapOfStateCountytoFipsCode = new HashMap<>();
		while (!text.get(idx++).contains("WYOMING")) {
			String line = text.get(idx);
			line = line.substring("       ".length());
			String stateNum = line.substring(0, 2);
			line = line.substring("56        ".length());
			String stateName = line.toLowerCase();
			mapOfStateNumToStateName.put(stateNum, stateName);
		}
		while (!text.get(idx).contains("---")) {
			idx++;
		}
		idx++;
		for (int i = idx; i < text.size(); i++) {
			String line = text.get(i);
			if (line.contains("(")) {
				continue;
			}
			line = line.substring("    ".length());
			String stateNum = line.substring(0, 2);
			String fipsCode = line.substring(0, 5);
			line = line.substring("01000        ".length());
			String stateName = mapOfStateNumToStateName.get(stateNum);
			String countyName = line;
			String key = getKey(stateName, countyName);
			mapOfStateCountytoFipsCode.put(key, fipsCode);
		}
		mapOfStateCountytoFipsCode.put("florida, miami-dade county", "12086");
		mapOfStateCountytoFipsCode.put("louisiana, lasalle county", "22059");
		mapOfStateCountytoFipsCode.put("new york, kings (brooklyn) county", "36047");
		mapOfStateCountytoFipsCode.put("new york, richmond (staten island) county", "36085");
		mapOfStateCountytoFipsCode.put("maryland, prince george's county", "24033");
		mapOfStateCountytoFipsCode.put("district of columbia, district of columbia county", "11001");
		mapOfStateCountytoFipsCode.put("indiana, laporte county", "18091");
		mapOfStateCountytoFipsCode.put("indiana, dekalb county", "18033");
		mapOfStateCountytoFipsCode.put("nevada, carson city county", "32510");
		mapOfStateCountytoFipsCode.put("pennsylvania, mckean county", "42083");
		mapOfStateCountytoFipsCode.put("maryland, st. mary's county", "24037");
		mapOfStateCountytoFipsCode.put("maryland, queen anne's county", "24035");
		mapOfStateCountytoFipsCode.put("new mexico, de baca county", "35011");
		mapOfStateCountytoFipsCode.put("illinois, lasalle county", "17099");
		mapOfStateCountytoFipsCode.put("alaska, anchorage municipality county", "02020");
		mapOfStateCountytoFipsCode.put("alaska, juneau city and county", "");
		mapOfStateCountytoFipsCode.put("alaska, sitka city and county", "02220");
		mapOfStateCountytoFipsCode.put("alaska, prince of wales-hyder county", "02201");
		mapOfStateCountytoFipsCode.put("alaska, wrangell city and county", "02280");
		mapOfStateCountytoFipsCode.put("alaska, denali county", "02068");
		mapOfStateCountytoFipsCode.put("alaska, yakutat city and county", "02282");
		
		return mapOfStateCountytoFipsCode;
	}

}
