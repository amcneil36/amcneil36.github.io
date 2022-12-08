package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class UnemploymentRate extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Unemployment rate in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population||Unemployment rate|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { ACS2021DataReader.POPULATION, "B23025_003E(in civilian labor force)",
				"B23025_005E(unemployed in civilian labor force)" };
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

	private static double getDouble(Map<String, String> map, String key) {
		return Double.valueOf(map.get(key));
	}

	private static double getUnemploymentRate(Map<String, String> map) {
		double numUnemployed = getDouble(map, "B23025_005E(unemployed in civilian labor force)");
		double inLaborForce = getDouble(map, "B23025_003E(in civilian labor force)");
		double unemploymentRate = Util.roundTwoDecimalPlaces((numUnemployed / inLaborForce) * 100);
		return unemploymentRate;
	}

	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double unemploymentRate = getUnemploymentRate(map1);
		double unemploymentRate2 = getUnemploymentRate(map2);
		if (unemploymentRate > unemploymentRate2) {
			return 1;
		} else if (unemploymentRate2 > unemploymentRate) {
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
		double unemploymentRate = getUnemploymentRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), unemploymentRate + "%" };
	}

	public static void main(String[] args) throws Exception {
		new UnemploymentRate().doEverything();
	}
}
