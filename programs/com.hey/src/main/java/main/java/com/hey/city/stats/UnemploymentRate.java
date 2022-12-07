package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class UnemploymentRate extends CityStats {

//	name: B23025_001E; label: estimate!!total:; concept: employment status for the population 16 years and over
//	name: B23025_002E; label: estimate!!total:!!in labor force:; concept: employment status for the population 16 years and over
//	name: B23025_003E; label: estimate!!total:!!in labor force:!!civilian labor force:; concept: employment status for the population 16 years and over
//	name: B23025_004E; label: estimate!!total:!!in labor force:!!civilian labor force:!!employed; concept: employment status for the population 16 years and over
//	name: B23025_005E; label: estimate!!total:!!in labor force:!!civilian labor force:!!unemployed; concept: employment status for the population 16 years and over
//	name: B23025_006E; label: estimate!!total:!!in labor force:!!armed forces; concept: employment status for the population 16 years and over
//	name: B23025_007E; label: estimate!!total:!!not in labor force; concept: employment status for the population 16 years and over

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { "B23025_003E(in civilian labor force)",
			"B23025_005E(unemployed in civilian labor force)" };

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getPlaceResults(variables);

		UnemploymentRate ur = new UnemploymentRate();
		ur.processAllStates();

	}

	private static double getDouble(Result result, String key) {
		return Double.valueOf(result.results.get(key));
	}

	private static String getUnemploymentRateFromResult(Result result) {
		double numUnemployed = getDouble(result, "B23025_005E(unemployed in civilian labor force)");
		double inLaborForce = getDouble(result, "B23025_003E(in civilian labor force)");
		double unemploymentRate = Util.roundTwoDecimalPlaces((numUnemployed/inLaborForce)*100);
		return unemploymentRate + "%";
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.unemploymentRate = "N/A";
		if (mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			Result result = mapOfFipsCodeToResult.get(data.fipsCode);
			data.unemploymentRate = getUnemploymentRateFromResult(result);
		}

	}

}
