package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.us.census.ACSDataReader;
import main.java.com.hey.us.census.ACSDataReader.Result;

public class BachelorsDegree extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { ACSDataReader.BACHELORS_DEGREE,
			ACSDataReader.MASTERS_DEGREE, ACSDataReader.PROFESSIONAL_DEGREE, ACSDataReader.DOCTORATE_DEGREE,
			ACSDataReader.NUM_PEOPLE_OVER_25_EDUCATION };

	public static void main(String[] args) throws Exception {
		// number of people with a bachelors or higher is number of people where
		// bachelors is the highest they have + num people with masters + doctoral, etc
		// all divided by the number of people over the age of 25
		mapOfFipsCodeToResult = ACSDataReader.getPlaceResults(variables);
		BachelorsDegree bd = new BachelorsDegree();
		bd.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.percentWithAtleastBachelors = "N/A";
		if (!mapOfFipsCodeToResult.containsKey(data.fipsCode)) {
			return;
		}
		Result result = mapOfFipsCodeToResult.get(data.fipsCode);
		double numPeepsWithDegrees = Integer.valueOf(result.results.get(ACSDataReader.BACHELORS_DEGREE))
				+ Integer.valueOf(result.results.get(ACSDataReader.MASTERS_DEGREE))
				+ Integer.valueOf(result.results.get(ACSDataReader.PROFESSIONAL_DEGREE))
				+ Integer.valueOf(result.results.get(ACSDataReader.DOCTORATE_DEGREE));
		double numPeeps = Integer.valueOf(result.results.get(ACSDataReader.NUM_PEOPLE_OVER_25_EDUCATION));
		if (numPeeps < 1) {
			return;
		}
		double fraction = (numPeepsWithDegrees / numPeeps) * 100;
		fraction = Math.round(fraction * 100.0) / 100.0; // round two decimals
		data.percentWithAtleastBachelors = fraction + "%";
	}

}
