package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class MedianHouseholdIncome extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median household income in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|Median household income|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { ACSDataReader.MEDIAN_HOUSEHOLD_INCOME, ACSDataReader.POPULATION };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numPeople = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return numPeople < 1000 || map.get(ACSDataReader.MEDIAN_HOUSEHOLD_INCOME).contains("-");
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 people  \n- Median household income is defined by the US Census as \"MEDIAN HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2019 INFLATION-ADJUSTED DOLLARS)\"";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get(ACSDataReader.MEDIAN_HOUSEHOLD_INCOME))
				- Integer.valueOf(a.get(ACSDataReader.MEDIAN_HOUSEHOLD_INCOME)));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get(ACSDataReader.MEDIAN_HOUSEHOLD_INCOME));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return new Object[] { Util.getIntFromDouble(totalPopulation), "$" + income };
	}

	public static void main(String[] args) throws Exception {
		new MedianHouseholdIncome().doEverything();
	}

}
