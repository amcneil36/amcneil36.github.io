package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class BlackMenMedianIncome extends CensusMdGeneratorGeneric{

	@Override
	protected String getMainHeaderText() {
		// TODO Auto-generated method stub
		return "Median annual earnings of black men in the USA sorted in descending order by:";
	}
	
	@Override
	protected String getRemainingHeaders() {
		return "Population|% Black|Median income of black men|";
	}
	
	@Override
	protected String[] getVariables() {
		return new String[] { "B20017B_002E(median earnings by black men)", "B02001_003E(number of black people)",
				ACS2021DataReader.POPULATION };
	}
	
	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		return numBlackPeople < 1000 || map.get("B20017B_002E(median earnings by black men)").contains("-");
	}
	
	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 black people  \n- Median annual earnings is defined by the US Census as \"MEDIAN EARNINGS IN THE PAST 12 MONTHS (IN 2018 INFLATION-ADJUSTED DOLLARS) BY SEX BY WORK EXPERIENCE IN THE PAST 12 MONTHS FOR THE POPULATION 16 YEARS AND OVER WITH EARNINGS IN THE PAST 12 MONTHS\"";
	}
	
	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B20017B_002E(median earnings by black men)"))
				- Integer.valueOf(a.get("B20017B_002E(median earnings by black men)")));
	}
	
	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B20017B_002E(median earnings by black men)"));
		double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentBlack + "%", "$" + income };
	}
	
	public static void main(String[] args) throws Exception {
		new BlackMenMedianIncome().doEverything();

	}
	
}
