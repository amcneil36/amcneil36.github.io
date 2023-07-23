package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class HomeownershipRate extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Homeownership rate in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|Homeownership rate|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B25008_001E(total in homes)", "B25008_002E(owner occupied)",
				ACSDataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numPeople = Integer.valueOf(map.get(ACSDataReader.POPULATION ));
		return numPeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 people";
	}

	private static double getHomeOwnershipRatePercent(Map<String, String> map) {
		double homeownershipRate = Util
				.roundTwoDecimalPlaces((Double.valueOf(map.get("B25008_002E(owner occupied)")))
						/ ((Double.valueOf(map.get("B25008_001E(total in homes)")))) * 100);
		return homeownershipRate;
	}

	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double num1 = getHomeOwnershipRatePercent(map1);
		double num2 = getHomeOwnershipRatePercent(map2);
		if (num1 > num2) {
			return 1;
		} else if (num2 > num1) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> compare(b, a));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		double percentQualified = getHomeOwnershipRatePercent(map);
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentQualified + "%" };
	}

	public static void main(String[] args) throws Exception {
		new HomeownershipRate().doEverything();
	}
}
