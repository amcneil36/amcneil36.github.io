package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class SinglePopulation extends CityStats{
	
	private static Map<String, Result> mapOfFipsCodeToResult;

	//name: B12001_001E; label: estimate!!total:; concept: sex by marital status for the population 15 years and over
	//name: B12001_004E; label: estimate!!total:!!male:!!now married:; concept: sex by marital status for the population 15 years and over
	//name: B12001_013E; label: estimate!!total:!!female:!!now married:; concept: sex by marital status for the population 15 years and over
	
	private static String[] variables = new String[] { "B12001_001E(population over 15)", "B12001_004E(married men over 15)", "B12001_013E(married women over 15)"};

	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getPlaceResults(variables);
		SinglePopulation bd = new SinglePopulation();
		bd.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.singlePopulation = "N/A";
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		double totalPop = Double.valueOf(result.results.get("B12001_001E(population over 15)"));
		double marriedMen = Double.valueOf(result.results.get("B12001_004E(married men over 15)"));
		double marriedWomen = Double.valueOf(result.results.get("B12001_013E(married women over 15)"));
		double percentSingle = (totalPop-marriedMen-marriedWomen)/totalPop;
		percentSingle *= 100;
		percentSingle = Util.roundTwoDecimalPlaces(percentSingle);
		data.singlePopulation = percentSingle + "%";
		
	}

}
