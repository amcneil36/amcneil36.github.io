package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACS2021DataReader;

public class WhiteWomenMedianIncome extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		// TODO Auto-generated method stub
		return "Median annual earnings of white women in the USA sorted in descending order by:";
	}
	
	@Override
	protected String getRemainingHeaders() {
		return "Population|% White|Median income of white women|";
	}
	//B20017A_005E
	@Override
	protected String[] getVariables() {
		return new String[] { "B20017A_005E(median earnings by white women)", "B02001_002E(number of white people)",
				ACS2021DataReader.POPULATION };
	}
	
	@Override
	protected boolean removeRow(Map<String, String> map) {
		int numWhitePeople = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		return numWhitePeople < 1000 || map.get("B20017A_005E(median earnings by white women)").contains("-");
	}
	
	@Override
	protected String getMainFooterText() {
		return "Notes:  \n- I only included places that have at least 1000 white people  \n- Median annual earnings is defined by the US Census as \"MEDIAN EARNINGS IN THE PAST 12 MONTHS (IN 2018 INFLATION-ADJUSTED DOLLARS) BY SEX BY WORK EXPERIENCE IN THE PAST 12 MONTHS FOR THE POPULATION 16 YEARS AND OVER WITH EARNINGS IN THE PAST 12 MONTHS\"";
	}
	
	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B20017A_005E(median earnings by white women)"))
				- Integer.valueOf(a.get("B20017A_005E(median earnings by white women)")));
	}
	
	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		int income = Integer.valueOf(map.get("B20017A_005E(median earnings by white women)"));
		double totalWhitePopulation = Integer.valueOf(map.get("B02001_002E(number of white people)"));
		double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
		double percentWhite = Util.roundTwoDecimalPlaces(100 * totalWhitePopulation / totalPopulation);
		return new Object[] { Util.getIntFromDouble(totalPopulation), percentWhite + "%", "$" + income };
	}
	
	public static void main(String[] args) throws Exception {
		new WhiteWomenMedianIncome().doEverything();

	}
}
