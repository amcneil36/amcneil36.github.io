package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class MedianAge extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { "B06002_001E(median age)" };

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);
		MedianAge ma = new MedianAge();
		ma.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.medianAge = "N/A";
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		int age = (int)(Math.round(Double.valueOf(result.results.get(variables[0]))));
		if (age > 0) {
			data.medianAge = String.valueOf(age);	
		}
	}
}
