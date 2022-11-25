package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.other.ACS2021DataReader;
import main.java.com.hey.other.ACS2021DataReader.Result;

public class Population extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	public static void main(String[] args) throws Exception {
		String[] variables = new String[] {"B01003_001E(population)"};
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);

	}
	
	static int numMatches = 0;

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String fips = data.fipsCode;
		if (mapOfFipsCodeToResult.containsKey(fips)) {
			System.out.println(numMatches++);
		}
		
	}

}
