package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class WhitePeopleOutEarningBlackPeople extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median household income of white households minus median household income of black households in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% White|% Black|Median household income of white households|Median household income of black households|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B19013A_001E(median household income white families)",
				"B02001_002E(number of white people)", ACS2021DataReader.POPULATION,
				"B19013B_001E(median household income black families)", "B02001_003E(number of black people)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numWhitePeople = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numWhitePeople < 1000 || numBlackPeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 white people and at least 1000 black people  \n- Median household income is defined by the US Census as \"MEDIAN HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2019 INFLATION-ADJUSTED DOLLARS)\"";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> (Integer.valueOf(b.get("B19013A_001E(median household income white families)")) - Integer.valueOf(b.get("B19013B_001E(median household income black families)")))
						- (Integer.valueOf(a.get("B19013A_001E(median household income white families)")) - Integer.valueOf(a.get("B19013B_001E(median household income black families)"))));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int whiteIncome = Integer.valueOf(map.get("B19013A_001E(median household income white families)"));
		int blackIncome = Integer.valueOf(map.get("B19013B_001E(median household income black families)"));
		double totalWhitePopulation = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentWhite = Util.roundTwoDecimalPlaces(100 * totalWhitePopulation / totalPopulation);
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentWhite + "%", percentBlack + "%", "$" + whiteIncome, "$" + blackIncome};
	}

	public static void main(String[] args) throws Exception {
		new WhitePeopleOutEarningBlackPeople().doEverything();
	}
}
