package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class BlackPeoplePovertyRate   extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Poverty rate of black people in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Poverty rate of black people|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B17001B_002E(number of black people eligible for poverty)",
				"B02001_003E(number of black people)", ACSDataReader.POPULATION, "B17001B_003E(number of black people in poverty)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numBlackPeople < 1000 || map.get("B17001B_003E(number of black people in poverty)").contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 black people";
	}
	
	private static double getPovertyRate(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("B17001B_002E(number of black people eligible for poverty)"));
		double numPeopleInPoverty = Double.valueOf(map.get("B17001B_003E(number of black people in poverty)"));
		double fraction = (numPeopleInPoverty / numPeopleEligible) * 100;
		return Util.roundTwoDecimalPlaces(fraction);
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
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		double povertyRate = getPovertyRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentBlack + "%", povertyRate + "%"};
	}

	public static void main(String[] args) throws Exception {
		new BlackPeoplePovertyRate().doEverything();
	}
}
