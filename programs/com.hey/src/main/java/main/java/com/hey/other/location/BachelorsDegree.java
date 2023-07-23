package main.java.com.hey.other.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.other.CensusMdGeneratorGeneric;
import main.java.com.hey.us.census.ACSDataReader;

public class BachelorsDegree extends CensusMdGeneratorGeneric {

	@Override
	protected String getMainHeaderText() {
		return "Percentage of people over the age of 25 with a bachelor's degree or higher in the USA sorted in descending order by";
	}

	@Override
	protected String getRemainingHeaders() {
		return "Population|% of people with a bachelor's degree or higher|";
	}

	@Override
	protected String[] getVariables() {
		return new String[] { ACSDataReader.BACHELORS_DEGREE,
				ACSDataReader.MASTERS_DEGREE, ACSDataReader.PROFESSIONAL_DEGREE, ACSDataReader.DOCTORATE_DEGREE,
				ACSDataReader.NUM_PEOPLE_OVER_25_EDUCATION, ACSDataReader.POPULATION };
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
	
	private static double getBachelorsDegreePercent(Map<String, String> map) {
		double numPeepsWithDegrees = Integer.valueOf(map.get(ACSDataReader.BACHELORS_DEGREE))
				+ Integer.valueOf(map.get(ACSDataReader.MASTERS_DEGREE))
				+ Integer.valueOf(map.get(ACSDataReader.PROFESSIONAL_DEGREE))
				+ Integer.valueOf(map.get(ACSDataReader.DOCTORATE_DEGREE));
		double numPeeps = Integer.valueOf(map.get(ACSDataReader.NUM_PEOPLE_OVER_25_EDUCATION));
		double fraction = (numPeepsWithDegrees / numPeeps) * 100;
		fraction = Math.round(fraction * 100.0) / 100.0; // round two decimals
		return fraction;
	}

	private static int compare(Map<String, String> map1, Map<String, String> map2) {
		double num1 = getBachelorsDegreePercent(map1);
		double num2 = getBachelorsDegreePercent(map2);
		if (num1 > num2) {
			return 1;
		}
		else if (num2 > num1) {
			return -1;
		}
		else {
			return 0;
		}
	}

	@Override
	protected void sortCollection(List<Map<String, String>> elementsList) {
		Collections.sort(elementsList,
				(a, b) -> compare(b,a));
	}

	@Override
	protected Object[] getRemainingRowArray(Map<String, String> map) {
		double percentBachelors = getBachelorsDegreePercent(map);
		double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
		return new Object[] { Util.getIntFromDouble(totalPopulation),percentBachelors + "%" };
	}

	public static void main(String[] args) throws Exception {
		new BachelorsDegree().doEverything();
	}
}
