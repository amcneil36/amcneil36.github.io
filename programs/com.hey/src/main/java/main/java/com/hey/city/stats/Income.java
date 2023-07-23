package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACSDataReader;
import main.java.com.hey.us.census.ACSDataReader.Result;

public class Income extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] {ACSDataReader.MEDIAN_HOUSEHOLD_INCOME};

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACSDataReader.getPlaceResults(variables);
		Income pop = new Income();
		pop.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.medianIncome = "N/A";
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
