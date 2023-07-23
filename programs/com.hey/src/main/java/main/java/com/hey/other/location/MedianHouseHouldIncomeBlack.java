package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class MedianHouseHouldIncomeBlack extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median household income of black households in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Median household income of black households|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B19013B_001E(median household income black families)",
				"B02001_003E(number of black people)", ACSDataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numBlackPeople < 1000 || map.get("B19013B_001E(median household income black families)").contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 black people  \n- Median household income is defined by the US Census as \"MEDIAN HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2019 INFLATION-ADJUSTED DOLLARS)\"";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B19013B_001E(median household income black families)"))
				- Integer.valueOf(a.get("B19013B_001E(median household income black families)")));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B19013B_001E(median household income black families)"));
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentBlack + "%", "$" + income };
	}

	public static void main(String[] args) throws Exception {
		new MedianHouseHouldIncomeBlack().doEverything();

	}

}
