package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACSDataReader;
import main.java.com.hey.us.census.ACSDataReader.Result;

public class HomeOwnershipRate extends CityStats {

//	name: B25008_001E; label: estimate!!total:; concept: total population in occupied housing units by tenure
//	name: B25008_002E; label: estimate!!total:!!owner occupied; concept: total population in occupied housing units by tenure
//	name: B25008_003E; label: estimate!!total:!!renter occupied; concept: total population in occupied housing units by tenure

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { "B25008_001E(total in homes)", "B25008_002E(owner occupied)" };

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACSDataReader.getPlaceResults(variables);
		HomeOwnershipRate hr = new HomeOwnershipRate();
		hr.processAllStates();

	}

	private static String getHomeOwnershipRateFromResult(Result result) {
		double homeownershipRate = Util
				.roundTwoDecimalPlaces((Double.valueOf(result.results.get("B25008_002E(owner occupied)")))
						/ ((Double.valueOf(result.results.get("B25008_001E(total in homes)")))) * 100);
		return homeownershipRate + "%";
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.homeOwnershipRate = "N/A";
		if (mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			Result result = mapOfFipsCodeToResult.get(data.fipsCode);
			data.homeOwnershipRate = getHomeOwnershipRateFromResult(result);
		}

	}

}
