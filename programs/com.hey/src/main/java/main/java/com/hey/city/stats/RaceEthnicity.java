package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class RaceEthnicity extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { ACS2021DataReader.POPULATION, "B03002_003E(non-hispanic white)",
			"B02001_003E(black)", "B02001_005E(asian)", "B03001_003E(hispanic or latino)" };

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);
		RaceEthnicity rn = new RaceEthnicity();
		rn.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		double population = Double.valueOf(result.results.get(ACS2021DataReader.POPULATION));
		double nonHispanicWhite = Double.valueOf(result.results.get("B03002_003E(non-hispanic white)"));
		double black = Double.valueOf(result.results.get("B02001_003E(black)"));
		double asian = Double.valueOf(result.results.get("B02001_005E(asian)"));
		double hispanic = Double.valueOf(result.results.get("B03001_003E(hispanic or latino)"));
		double percentNonHispanicWhite = Math.round(((nonHispanicWhite / population) * 100) * 100.0) / 100.0; // round two decimals
		double percentBlack = Math.round(((black / population) * 100) * 100.0) / 100.0; // round two decimals
		double percentAsian = Math.round(((asian / population) * 100) * 100.0) / 100.0; // round two decimals
		double percentHispanic = Math.round(((hispanic / population) * 100) * 100.0) / 100.0; // round two decimals
		data.percentWhite = percentNonHispanicWhite + "%";
		data.percentBlack = percentBlack+ "%";
		data.percentAsian = percentAsian+ "%";
		data.percentHispanic = percentHispanic+ "%";

	}

}
