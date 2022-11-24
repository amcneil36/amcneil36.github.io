package main.java.com.hey.summaries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.city.stats.NoopCityStats;

public class MetroCsvs {
	
	public static String getMetroKey(CityStats.Data data) {
		return data.metro + "," + data.metroPopulation;
	}

	public static void main(String[] args) throws Exception {
		NoopCityStats cityStats = new NoopCityStats();
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, List<CityStats.Data>> mapOfMetroNameToData = new HashMap<>();
		for (CityStats.Data data : dataList) {
			String metroKey = getMetroKey(data);
			if (!data.metro.contains("None") && Integer.valueOf(data.metroPopulation) > 100000) {
				if (!mapOfMetroNameToData.containsKey(metroKey)) {
					mapOfMetroNameToData.put(metroKey, new ArrayList<CityStats.Data>());
				}
				List<CityStats.Data> dataListFromMap = mapOfMetroNameToData.get(metroKey);
				dataListFromMap.add(data);
				mapOfMetroNameToData.put(metroKey, dataListFromMap);
			}
		}
		Set<String> keys = mapOfMetroNameToData.keySet();
		for (String key : keys) {
			List<CityStats.Data> metroDataList = mapOfMetroNameToData.get(key);
			String dominantState = getDominantState(metroDataList);
			String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\Metros\\"
					+ metroDataList.get(0).metro + " " + dominantState + ".csv";
			cityStats.writeDataToPath(metroDataList, filePath, true);
		}
	}

	private static String getDominantState(List<Data> metroDataList) {
		Map<String, Integer> mapOfStateToPopulation = new HashMap<>();
		for (CityStats.Data data : metroDataList) {
			String state = data.stateName;
			if (!mapOfStateToPopulation.containsKey(state)) {
				mapOfStateToPopulation.put(state, 0);
			}
			int pop = mapOfStateToPopulation.get(state);
			pop += Integer.valueOf(data.population);
			mapOfStateToPopulation.put(state, pop);
		}
		Set<String> keys = mapOfStateToPopulation.keySet();
		
		String mostPopulatedState = "";
		int maxPopulation = -1;
		for (String key : keys) {
			int currentPop = mapOfStateToPopulation.get(key);
			if (currentPop > maxPopulation) {
				maxPopulation = currentPop;
				mostPopulatedState = key;
			}
		}
		if (maxPopulation == -1) {
			throw new RuntimeException("didn't find most populated state");
		}
		return mostPopulatedState;
	}

}
