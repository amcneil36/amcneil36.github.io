package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class MedianHomeAge extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { "B25035_001E(median year structure built)" };

	// name: B25035_001E; label: estimate!!median year structure built; concept:
	// median year structure built
	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getPlaceResults(variables);
		MedianHomeAge ma = new MedianHomeAge();
		ma.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.medianHomeAge = "N/A";
		if (mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			Result result = mapOfFipsCodeToResult.get(data.fipsCode);
			String homeAge = result.results.get(variables[0]);
			int yearHomeBuilt = Integer.valueOf(homeAge);
			int homeAgeInt = 2020-yearHomeBuilt;
			if (homeAgeInt > 1 && homeAgeInt < 1000) {
				data.medianHomeAge = String.valueOf(homeAgeInt);	
			}
		}

	}

}
