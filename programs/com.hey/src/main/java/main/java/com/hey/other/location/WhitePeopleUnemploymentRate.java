package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class WhitePeopleUnemploymentRate extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Unemployment rate of white people in the USA sorted in ascending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% White|Unemployment rate of white people|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B02001_002E(number of white people)", ACSDataReader.POPULATION,
				"C23002A_006E(white men 16-64 in labor force)",
				"C23002A_008E(white men 16-64 in labor force unemployed",
				"C23002A_019E(white women 16-64 in labor force)", "C23002A_021E(white women 16-64 unemployed)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numWhitePeople = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		return numWhitePeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 white people";
	}

	private static double getUnemploymentRate(Map<String, String> map) {
		double numPeopleEligible = Double.valueOf(map.get("C23002A_006E(white men 16-64 in labor force)"))
				+ Double.valueOf(map.get("C23002A_019E(white women 16-64 in labor force)"));
		double numPeopleUnemployed = Double.valueOf(map.get("C23002A_021E(white women 16-64 unemployed)"))
				+ Double.valueOf(map.get("C23002A_008E(white men 16-64 in labor force unemployed"));
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
		double totalWhitePopulation = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentWhite = Util.roundTwoDecimalPlaces(100 * totalWhitePopulation / totalPopulation);
		double unemploymentRate = getUnemploymentRate(map);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentWhite + "%", unemploymentRate + "%" };
	}

	public static void main(String[] args) throws Exception {
		new WhitePeopleUnemploymentRate().doEverything();
	}
}
