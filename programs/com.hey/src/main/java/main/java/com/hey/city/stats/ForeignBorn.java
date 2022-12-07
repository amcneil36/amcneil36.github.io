package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACS2021DataReader;
import main.java.com.hey.us.census.ACS2021DataReader.Result;

public class ForeignBorn extends CityStats {
	
	private static Map<String, Result> mapOfFipsCodeToResult;
	private static String[] variables = new String[] { "B05006_001E(foreign born amount)", ACS2021DataReader.POPULATION};
	
	public static void main(String[] args) throws Exception {
		// number of people with a bachelors or higher is number of people where
		// bachelors is the highest they have + num people with masters + doctoral, etc
		// all divided by the number of people over the age of 25
		mapOfFipsCodeToResult = ACS2021DataReader.getPlaceResults(variables);
		ForeignBorn foreignBorn = new ForeignBorn();
		foreignBorn.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.foreignBornPercent = "N/A";
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		double numPeeps = Integer.valueOf(result.results.get(variables[1]));
		double numForeignBorn = Integer.valueOf(result.results.get(variables[0]));
		double fraction = (numForeignBorn / numPeeps) * 100;
		fraction = Math.round(fraction * 100.0) / 100.0; // round two decimals
		data.foreignBornPercent = fraction + "%";
	}

}
