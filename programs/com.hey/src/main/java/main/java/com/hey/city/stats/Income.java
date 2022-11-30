package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class Income extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] {ACS2021DataReader.MEDIAN_HOUSEHOLD_INCOME};

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);
		Income pop = new Income();
		pop.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String fips = data.fipsCode;
		if (mapOfFipsCodeToResult.containsKey(fips)) {
			Result result = mapOfFipsCodeToResult.get(fips);
			String population = result.results.get(variables[0]);
			int income = Integer.valueOf(population);
			if (income > 0) {
				data.medianIncome = "$" + income;
			}
		}
	}

}
