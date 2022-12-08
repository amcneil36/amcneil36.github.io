package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class AsianMenMedianIncome extends CensusMdGeneratorGeneric{

	@Override
	protected String getMainHeaderText() {
		return "Median annual earnings of Asian men in the USA sorted in descending order by:";
	}
	
	@Override
	protected String getRemainingHeaders() {
		return "Population|% Asian|Median income of Asian men|";
	}
	
	//B20017D_002E
	@Override
	protected String[] getVariables() {
		return new String[] { "B20017D_002E(median earnings by Asian men)", "B02011_001E(number of asian people)",
				ACS2021DataReader.POPULATION };
	}
	
	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numAsianPeople = Integer.valueOf(map.get("B02011_001E(number of asian people)"));
		return numAsianPeople < 1000 || map.get("B20017D_002E(median earnings by Asian men)").contains("-");
	}
	
	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 Asian people  \n- Median annual earnings is defined by the US Census as \"MEDIAN EARNINGS IN THE PAST 12 MONTHS (IN 2018 INFLATION-ADJUSTED DOLLARS) BY SEX BY WORK EXPERIENCE IN THE PAST 12 MONTHS FOR THE POPULATION 16 YEARS AND OVER WITH EARNINGS IN THE PAST 12 MONTHS\"";
	}
	
	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B20017D_002E(median earnings by Asian men)"))
				- Integer.valueOf(a.get("B20017D_002E(median earnings by Asian men)")));
	}
	
	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B20017D_002E(median earnings by Asian men)"));
		double totalAsianPopulation = Integer.valueOf(map.get("B02011_001E(number of asian people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentAsian = Util.roundTwoDecimalPlaces(100 * totalAsianPopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentAsian + "%", "$" + income };
	}
	
	public static void main(String[] args) throws Exception {
		new AsianMenMedianIncome().doEverything();
	}
}
