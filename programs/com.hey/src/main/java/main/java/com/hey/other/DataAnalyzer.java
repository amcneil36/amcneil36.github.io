package main.java.com.hey.other;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;

public class DataAnalyzer extends DataAnalyzerSuper {
	
	@Override
	public boolean isDataValid(CityStats.Data data) {
		if (Integer.valueOf(data.population) < 300000) {
			return false;
		}
		if (data.fbiViolentCrimeRate.equals("N/A") || data.fbiPropertyCrimeRate.equals("N/A")) {
			return false;
		}
		return true;
	}
	
	@Override
	public int calculateScore(CityStats.Data data) {
		return Integer.valueOf(data.fbiViolentCrimeRate) + Integer.valueOf(data.fbiPropertyCrimeRate);
	}

	@Override
	public void printHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printRow(Data data) {
		System.out.println(data.cityName + ", " + data.stateName + ": " + calculateScore(data));
	}
	
	public static void main(String[] args) throws Exception {
		new DataAnalyzer().doEverything();
	}

}
