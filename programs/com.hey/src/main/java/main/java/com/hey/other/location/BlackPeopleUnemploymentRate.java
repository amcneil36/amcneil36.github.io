package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class BlackPeopleUnemploymentRate extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Unemployment rate of black people in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Unemployment rate of black people|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B02001_003E(number of black people)", ACS2021DataReader.POPULATION,
				"C23002B_006E(black men 16-64 in labor force)",
				"C23002B_008E(black men 16-64 in labor force unemployed",
				"C23002B_019E(black women 16-64 in labor force)", "C23002B_021E(black women 16-64 unemployed)" };
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

	private static double getUnemploymentRate(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("C23002B_006E(black men 16-64 in labor force)"))
				+ Double.valueOf(map.get("C23002B_019E(black women 16-64 in labor force)"));
		double numPeopleUnemployed = Double.valueOf(map.get("C23002B_021E(black women 16-64 unemployed)"))
				+ Double.valueOf(map.get("C23002B_008E(black men 16-64 in labor force unemployed"));
		double fraction = (numPeopleUnemployed / numPeopleEligible) * 100;
		return Util.roundTwoDecimalPlaces(fraction);
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
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		double unemploymentRate = getUnemploymentRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentBlack + "%", unemploymentRate + "%" };
	}

	public static void main(String[] args) throws Exception {
		new BlackPeopleUnemploymentRate().doEverything();
	}
}
