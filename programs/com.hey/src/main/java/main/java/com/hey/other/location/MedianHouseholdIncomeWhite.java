package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class MedianHouseholdIncomeWhite extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median household income of white households in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% White|Median household income of white households|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B19013A_001E(median household income white families)",
				"B02001_002E(number of white people)", ACS2021DataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numWhitePeople = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		return numWhitePeople < 1000 || map.get("B19013A_001E(median household income white families)").contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 white people  \n- Median household income is defined by the US Census as \"MEDIAN HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2019 INFLATION-ADJUSTED DOLLARS)\"";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> Integer.valueOf(b.get("B19013A_001E(median household income white families)"))
						- Integer.valueOf(a.get("B19013A_001E(median household income white families)")));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B19013A_001E(median household income white families)"));
		double totalWhitePopulation = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentWhite = Util.roundTwoDecimalPlaces(100 * totalWhitePopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentWhite + "%", "$" + income };
	}

	public static void main(String[] args) throws Exception {
		new MedianHouseholdIncomeWhite().doEverything();
	}

}
