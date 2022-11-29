package main.java.com.hey.city.stats;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class FBICrimeStats extends CityStats{

	public static class FBIData{
		int violentCrimeRate;
		int propertyCrimeRate;
	}
	Map<String, Map<String, FBIData>> mapOfStateToMap = new HashMap<>();
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
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
				if (arr.length != map.size() || line.contains(",,")) {
					continue;
				}
				arr = line.split(",");
				String cityName = arr[cityIdx].replace("2", "");
				if (arr[populationIdx].contains("County")) { // some cities are called Reno, X County for example. we want to skip these
					continue;
				}
				cityName = Util.removeStuffFromCityName(cityName);
				double population = Integer.valueOf(arr[populationIdx]);
				double violentCrimes = Integer.valueOf(arr[violentCrimeIdx]);
				double propertyCrimes = Integer.valueOf(arr[propertyCrimeIdx]);
				FBIData fbi = new FBIData();
				fbi.violentCrimeRate = (int)((violentCrimes/population)*100000);
				fbi.propertyCrimeRate = (int)((propertyCrimes/population)*100000);
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
