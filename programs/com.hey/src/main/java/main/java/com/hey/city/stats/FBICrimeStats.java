package main.java.com.hey.city.stats;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats;

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
		if (mapOfCityToData.containsKey(data.cityName)) {
			FBIData fbiData = mapOfCityToData.get(data.cityName);
			data.fbiPropertyCrimeRate = String.valueOf(fbiData.propertyCrimeRate);
			data.fbiViolentCrimeRate = String.valueOf(fbiData.violentCrimeRate);
		}
		
	}
	
	private void populateMapWithState(String stateName) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CrimeRates\\" + stateName + ".csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
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
			int population = Integer.valueOf(arr[populationIdx]);
			int violentCrimes = Integer.valueOf(arr[violentCrimeIdx]);
			int propertyCrimes = Integer.valueOf(arr[propertyCrimeIdx]);
			FBIData fbi = new FBIData();
			fbi.violentCrimeRate = (violentCrimes/population)*100000;
			fbi.propertyCrimeRate = (propertyCrimes/population)*100000;
			mapOfCityToFbiData.put(cityName, fbi);
		}
		mapOfStateToMap.put(stateName, mapOfCityToFbiData);
		myReader.close();
		
	}

	public static void main(String[] args) throws Exception {
		FBICrimeStats st = new FBICrimeStats();
		st.processAllStates();
	}

}
