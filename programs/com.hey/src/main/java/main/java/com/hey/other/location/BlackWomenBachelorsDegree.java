package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class BlackWomenBachelorsDegree extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Percentage of black women over the age of 25 with a bachelor's degree or higher in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|% of black women with a bachelor's degree or higher|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "C15002B_007E(num black women who are eligible)",
				"C15002B_011E(num black women with bachelors)", "B02001_003E(number of black people)",
				ACS2021DataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numBlackPeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 black people";
	}

	private static double getBachelorsDegreePercent(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("C15002B_007E(num black women who are eligible)"));
		double numPeopleQualified = Double.valueOf(map.get("C15002B_011E(num black women with bachelors)"));
		double fraction = (numPeopleQualified / numPeopleEligible) * 100;
		double ret = Util.roundTwoDecimalPlaces(fraction);
		return ret;
	}

	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double num1 = getBachelorsDegreePercent(map1);
		double num2 = getBachelorsDegreePercent(map2);
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
		double percentBachelors = getBachelorsDegreePercent(map);
		double totalPopulationOfRace = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentOfTheRace = Util.roundTwoDecimalPlaces(100 * totalPopulationOfRace / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentOfTheRace + "%", percentBachelors + "%" };
	}

	public static void main(String[] args) throws Exception {
		new BlackMenBachelorsDegree().doEverything();
	}
}
