package main.java.com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetroPopulation extends CityStats {

	static Map<String, Integer> mapOfMetroToPopulation = new HashMap<>();
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String key = getKey(data);
		if (mapOfMetroToPopulation.containsKey(key)) {
			int metroPopulation = mapOfMetroToPopulation.get(key);
			data.metroPopulation = String.valueOf(metroPopulation);	
		}
		else {
			System.out.println("huh?");
		}
	}
	
	private static String getKey(Data data) {
		return data.metro + "," + data.timeZone;
	}
	
	private static void fillMap() throws Exception {
		List<Data> dataList = CreateBigCsv.readInput();
		for (Data data : dataList) {
			String key = getKey(data);
			int cityPopulation = Integer.valueOf(data.population);
			if (mapOfMetroToPopulation.containsKey(key)) {
				int population = mapOfMetroToPopulation.get(key);
				population += cityPopulation;
				mapOfMetroToPopulation.put(key, population);
			}
			else {
				mapOfMetroToPopulation.put(key, Integer.valueOf(cityPopulation));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		fillMap();
		DewPointCsvReader s = new DewPointCsvReader();
		s.processAllStates();
		//s.processState("Delaware");
	}

}
