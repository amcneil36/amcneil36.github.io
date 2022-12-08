package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class PovertyRate extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Poverty rate of people in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|Poverty rate|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { ACS2021DataReader.POPULATION,
				"B17020_001E(num people eligible for poverty)", "B17020_002E(num people in poverty)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numPeople = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		return numPeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 people";
	}

	private static double getPovertyRate(Map<String, String> map) {
		double numPeopleEligible = Integer.valueOf(map.get("B17020_001E(num people eligible for poverty)"));
		double numPeopleInPoverty = Integer.valueOf(map.get("B17020_002E(num people in poverty)"));
		double fraction = (numPeopleInPoverty / numPeopleEligible) * 100;
		fraction = Math.round(fraction * 100.0) / 100.0; // round two decimals
		return fraction;
	}

	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double povertyRate1 = getPovertyRate(map1);
		double povertyRate2 = getPovertyRate(map2);
		if (povertyRate1 > povertyRate2) {
			return 1;
		} else if (povertyRate2 > povertyRate1) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> compare(a, b));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double povertyRate = getPovertyRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), povertyRate + "%" };
	}

	public static void main(String[] args) throws Exception {
		new PovertyRate().doEverything();
	}
}
