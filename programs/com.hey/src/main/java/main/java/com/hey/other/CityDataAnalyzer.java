package main.java.com.hey.other;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;

public class CityDataAnalyzer extends CityDataAnalyzerSuper {

	@Override
	public boolean isDataValid(CityStats.Data data) {
		return (Integer.valueOf(data.population) > 1000) && !data.fbiViolentCrimeRate.equals("N/A")
				&& !data.fbiPropertyCrimeRate.equals("N/A") && data.stateName.equals("Florida") && data.metro.startsWith("Miami");
	}

	@Override
	public int calculateScore(CityStats.Data data) {
		return Integer.valueOf(data.medianIncome.replace("$", ""));
	}

	@Override
	public void printHeader() {
		// TODO Auto-generated method stub
	}

	@Override
	public void printRow(Data data) {
		System.out.println(data.cityName + ", " + data.stateName + ": " + data.medianIncome + "; " + (2*Integer.valueOf(data.fbiViolentCrimeRate) + Integer.valueOf(data.fbiPropertyCrimeRate)));
	}
	
	@Override
	public boolean sortAscending() {
		return false;
	}

	public static void main(String[] args) throws Exception {
		new CityDataAnalyzer().doEverything();
	}

}
