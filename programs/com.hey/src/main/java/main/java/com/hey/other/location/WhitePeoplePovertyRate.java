package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class WhitePeoplePovertyRate  extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Poverty rate of white people in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% White|Poverty rate of white people|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B17001A_001E(number of white people eligible for poverty)",
				"B02001_002E(number of white people)", ACSDataReader.POPULATION, "B17001A_002E(number of white people in poverty)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numWhitePeople = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		return numWhitePeople < 1000 || map.get("B17001A_002E(number of white people in poverty)").contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 white people";
	}
	
	private static double getPovertyRate(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("B17001A_001E(number of white people eligible for poverty)"));
		double numPeopleInPoverty = Double.valueOf(map.get("B17001A_002E(number of white people in poverty)"));
		double fraction = (numPeopleInPoverty / numPeopleEligible) * 100;
		double ret = Util.roundTwoDecimalPlaces(fraction);
		return ret;
	}
	
	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double povertyRate1 = getPovertyRate(map1);
		double povertyRate2 = getPovertyRate(map2);
		if (povertyRate1 > povertyRate2) {
			return 1;
		}
		else if (povertyRate2 > povertyRate1) {
			return -1;
		}
		else {
			return 0;
		}
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> compare(a,b));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		double totalWhitePopulation = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentWhite = Util.roundTwoDecimalPlaces(100 * totalWhitePopulation / totalPopulation);
		double povertyRate = getPovertyRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentWhite + "%", povertyRate + "%"};
	}

	public static void main(String[] args) throws Exception {
		new WhitePeoplePovertyRate().doEverything();
	}
}
