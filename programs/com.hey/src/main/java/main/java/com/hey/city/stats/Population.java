package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class Population extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] {"B01003_001E(population)"};
	
	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getPlaceResults(variables);
		Population pop = new Population();
		pop.processAllStates();
	}
	
	static int numMatches = 0;

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String fips = data.fipsCode;
		if (mapOfFipsCodeToResult.containsKey(fips)) {
			Result result = mapOfFipsCodeToResult.get(fips);
			String population = result.results.get(variables[0]);
			int pop = Integer.valueOf(population);
			if (pop > 0) {
				data.population = population;
			}
		}
		
	}

}
