package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class BlackPeopleHomeOwnershipRate  extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Homeownership rate of black households in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Homeownership rate of black people|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B25003B_001E(num black people who are eligible)",
				"B25003B_002E(num black ppl who own homes)", "B02001_003E(number of black people)",
				ACSDataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numPeopleOfRace = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numPeopleOfRace < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 black people";
	}

	private static double getHomeOwnershipRatePercent(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("B25003B_001E(num black people who are eligible)"));
		double numPeopleQualified = Double.valueOf(map.get("B25003B_002E(num black ppl who own homes)"));
		;
		double fraction = (numPeopleQualified / numPeopleEligible) * 100;
		double ret = Util.roundTwoDecimalPlaces(fraction);
		return ret;
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
		double totalPopulationOfRace = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentOfTheRace = Util.roundTwoDecimalPlaces(100 * totalPopulationOfRace / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentOfTheRace + "%", percentQualified + "%" };
	}

	public static void main(String[] args) throws Exception {
		new BlackPeopleHomeOwnershipRate().doEverything();
	}
}
