package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class MedianHouseholdIncomeAsian extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median household income of Asian households in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Asian|Median household income of Asian households|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B19013D_001E(median household income asian families)",
				"B02001_005E(number of asian people)", ACSDataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numAsianPeople = Integer.valueOf(map.get("B02001_005E(number of asian people)"));
		return numAsianPeople < 1000 || map.get("B19013D_001E(median household income asian families)").contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 Asian people  \n- Median household income is defined by the US Census as \"MEDIAN HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2019 INFLATION-ADJUSTED DOLLARS)\"";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> Integer.valueOf(b.get("B19013D_001E(median household income asian families)"))
						- Integer.valueOf(a.get("B19013D_001E(median household income asian families)")));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B19013D_001E(median household income asian families)"));
		double totalAsianPopulation = Integer.valueOf(map.get("B02001_005E(number of asian people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentAsian = Util.roundTwoDecimalPlaces(100 * totalAsianPopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentAsian + "%", "$" + income };
	}

	public static void main(String[] args) throws Exception {
		new MedianHouseholdIncomeAsian().doEverything();
	}

}
