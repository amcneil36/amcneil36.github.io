package main.java.com.hey.city.stats;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class FBICrimeStats extends CityStats{

	public static class FBIData{
		String violentCrimeRate = "N/A";
		String propertyCrimeRate = "N/A";
	}
	Map<String, Map<String, FBIData>> mapOfStateToMap = new HashMap<>();
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.fbiPropertyCrimeRate = "N/A";
		data.fbiViolentCrimeRate = "N/A";
		if (!mapOfStateToMap.containsKey(stateName)) {
			populateMapWithState(stateName);
		}
		Map<String, FBIData> mapOfCityToData = mapOfStateToMap.get(stateName);
		String city = Util.removeStuffFromCityName(data.cityName);
		if (mapOfCityToData.containsKey(city)) {
			FBIData fbiData = mapOfCityToData.get(city);
			data.fbiPropertyCrimeRate = String.valueOf(fbiData.propertyCrimeRate);
			data.fbiViolentCrimeRate = String.valueOf(fbiData.violentCrimeRate);
		}
		
	}
	
	private void populateMapWithState(String stateName) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CrimeRates\\" + stateName + ".csv";
		File myObj = new File(filePath);
		try (Scanner myReader = new Scanner(myObj)) {
			String header = "";
			int ctr = 0;
			while(true) {
				String line = myReader.nextLine();
				header += line + " ";
				if (line.startsWith("theft\",Arson")) {
					break;
				}
				ctr++;
				if (ctr > 100) {
					throw new RuntimeException("fix text file for " + stateName);
				}
			}
			String[] arr = header.split(",");
			Map<String, Integer> mapOfHeaderToIdx = new HashMap<>();
			for (int i = 0; i < arr.length; i++) {
				mapOfHeaderToIdx.put(arr[i], i);
			}
			int cityIdx = mapOfHeaderToIdx.get("﻿City");
			int populationIdx = mapOfHeaderToIdx.get("Population");
			int violentCrimeIdx = mapOfHeaderToIdx.get("\"Violent crime\"");
			int propertyCrimeIdx = mapOfHeaderToIdx.get("\"Property crime\"");
			Map<String, Integer> map = new HashMap<>();
			for (int i = 0; i < arr.length; i++) {
				map.put(arr[i],  i);
			}
			Map<String, FBIData> mapOfCityToFbiData = new HashMap<>();
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				line = line.replace("\"Boston3,4\"", "Boston");
				arr = line.split(",");
				String cityName = arr[cityIdx].replace("2", "");
				cityName = cityName.replace("3", "");
				cityName = cityName.replace("4", "");
				if (arr[populationIdx].contains("County")) { // some cities are called Reno, X County for example. we want to skip these
					continue;
				}
				cityName = Util.removeStuffFromCityName(cityName);
				String populationSt = arr[populationIdx];
				if (populationSt.isEmpty()) {
					continue;
				}
				double population = Integer.valueOf(arr[populationIdx]);
				String violentCrimeSt = arr[violentCrimeIdx];
				FBIData fbi = new FBIData();
				if (!violentCrimeSt.isEmpty()) {
					double violentCrimes = Integer.valueOf(arr[violentCrimeIdx]);	
					fbi.violentCrimeRate = String.valueOf((int)((violentCrimes/population)*100000));
				}
				String propertyCrimesSt = arr[propertyCrimeIdx];
				if (!propertyCrimesSt.isEmpty()) {
					double propertyCrimes = Integer.valueOf(arr[propertyCrimeIdx]);
					fbi.propertyCrimeRate = String.valueOf((int)((propertyCrimes/population)*100000));	
				}
				mapOfCityToFbiData.put(cityName, fbi);
			}
			mapOfStateToMap.put(stateName, mapOfCityToFbiData);
			myReader.close();
		}
		
	}


	public static void main(String[] args) throws Exception {
		FBICrimeStats st = new FBICrimeStats();
		st.processAllStates();
	}

}
