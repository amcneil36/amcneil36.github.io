package main.java.com.hey.other;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;

public class BlackWomanMedianIncome extends CensusMdGeneratorGeneric {

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B20017B_001E(median earnings by black women)"));
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		return getRowArrayCompact(map, income, totalPopulation, percentBlack);
	}

	protected Object[] getRowArrayCompact(Map<String, String> map, int income, double totalPopulation,
			double percentBlack) {
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentBlack + "%", "$" + income };
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B20017B_001E(median earnings by black women)"))
				- Integer.valueOf(a.get("B20017B_001E(median earnings by black women)")));
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		if (numBlackPeople < 1000 || map.get("B20017B_001E(median earnings by black women)").contains("-")) {
			return true;
		}
		return false;
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B20017B_001E(median earnings by black women)", "B02001_003E(number of black people)",
				ACS2021DataReader.POPULATION };
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Median income of black women|";
	}

	public static void main(String[] args) throws Exception {
		new BlackWomanMedianIncome().doEverything();

	}

	@Override
	protected String getReadMeHeaderText() {
		// TODO Auto-generated method stub
		return "Median annual earnings of black women in the USA sorted in descending order by:";
	}

	@Override
	protected String getReadMeNotesText() {
		// TODO Auto-generated method stub
		return "Note: I only included places that have at least 1000 black people";
	}

}
