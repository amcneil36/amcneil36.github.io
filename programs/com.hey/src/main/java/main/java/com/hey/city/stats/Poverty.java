package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class Poverty extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] {"B17020_001E(num people eligible for poverty)", "B17020_002E(num people in poverty)"};

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);
		Poverty poverty = new Poverty();
		poverty.processAllStates();
		
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.povertyRate = "N/A";
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		double numPeopleEligible = Integer.valueOf(result.results.get(variables[0]));
		double numPeopleInPoverty = Integer.valueOf(result.results.get(variables[1]));
		double fraction = (numPeopleInPoverty / numPeopleEligible) * 100;
		fraction = Math.round(fraction * 100.0) / 100.0; // round two decimals
		data.povertyRate = fraction + "%";
	}

}
