package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class Rent extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { "B25064_001E(median gross rent)" };

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);

		Rent rent = new Rent();
		rent.processAllStates();

	}

	private static String getRent(Result result) {
		return "$" + result.results.get("B25064_001E(median gross rent)");
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.avgApartmentRent = "N/A";
		if (mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			Result result = mapOfFipsCodeToResult.get(data.fipsCode);
			String rent = getRent(result);
			if (rent.contains("-")) {
				return;
			}
			data.avgApartmentRent = rent;
		}
	}

}
