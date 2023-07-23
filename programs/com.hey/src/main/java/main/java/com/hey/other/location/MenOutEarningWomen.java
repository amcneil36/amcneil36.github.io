package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class MenOutEarningWomen extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Median income of men minus median income of women in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|Median household income of men|Median income of women|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { "B19325_002E(median income men)", ACSDataReader.POPULATION,
				"B19325_049E(median income women)" };
	}

	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numPeople = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return numPeople < 1000;
	}

	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 people";
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> (Integer.valueOf(b.get("B19325_002E(median income men)"))
						- Integer.valueOf(b.get("B19325_049E(median income women)")))
						- (Integer.valueOf(a.get("B19325_002E(median income men)"))
								- Integer.valueOf(a.get("B19325_049E(median income women)"))));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int menIncome = Integer.valueOf(map.get("B19325_002E(median income men)"));
		int womenIncome = Integer.valueOf(map.get("B19325_049E(median income women)"));
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return new Object[] { Util.getIntFromDouble(totalPopulation), "$" + menIncome, "$" + womenIncome };
	}

	public static void main(String[] args) throws Exception {
		new MenOutEarningWomen().doEverything();
	}
}
