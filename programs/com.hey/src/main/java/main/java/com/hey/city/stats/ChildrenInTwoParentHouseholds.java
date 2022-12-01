package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class ChildrenInTwoParentHouseholds extends CityStats {

	//S0901_C01_001E	Estimate!!Total!!Children under 18 years in households
	//S0901_C02_001E	Estimate!!In married-couple family household!!Children under 18 years in households
	//S0901_C03_001E	Estimate!!In male householder, no spouse present, family household!!Children under 18 years in households
	//S0901_C04_001E	Estimate!!In female householder, no spouse present, family household!!Children under 18 years in households
	
	
	private static String[] variables = new String[] {"S0901_C02_001E(total in married couple household)", "S0901_C01_001E(total)"};

	private static Map<String, Result> mapOfFipsCodeToResult;
	
	public static void main(String[] args) throws Exception {
		mapOfFipsCodeToResult = ACS2021DataReader.getACSSubject2020Results(variables);
		ChildrenInTwoParentHouseholds pop = new ChildrenInTwoParentHouseholds();
		pop.processAllStates();
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String fips = data.fipsCode;
		if (mapOfFipsCodeToResult.containsKey(fips)) {
			Result result = mapOfFipsCodeToResult.get(fips);
			double totalPop = Double.valueOf(result.results.get("S0901_C01_001E(total)")); 
			double marriedHouseHoldPop = Double.valueOf(result.results.get("S0901_C02_001E(total in married couple household)")); 
			double percentInTwoParent = (marriedHouseHoldPop/totalPop)*100;
			data.percentOfChildrenInTwoParentHouseholds = percentInTwoParent + "%";
			
		}
	}

}
