package main.java.com.hey.city.stats;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.other.ACS2021DataReader;
import main.java.com.hey.other.ACS2021DataReader.Result;

public class BachelorsDegree extends CityStats {

	private static Map<String, Result> mapOfFipsCodeToResult;

	private static String[] variables = new String[] { ACS2021DataReader.BACHELORS_DEGREE,
			ACS2021DataReader.MASTERS_DEGREE, ACS2021DataReader.PROFESSIONAL_DEGREE, ACS2021DataReader.DOCTORATE_DEGREE,
			ACS2021DataReader.NUM_PEOPLE_OVER_25_EDUCATION };

	public static void main(String[] args) throws Exception {
		// number of people with a bachelors or higher is number of people where
		// bachelors is the highest they have + num people with masters + doctoral, etc
		// all divided by the number of people over the age of 25
		mapOfFipsCodeToResult = ACS2021DataReader.getResults(variables);
		BachelorsDegree bd = new BachelorsDegree();
		bd.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (data.cityName.equals("Weston") && data.stateName.equals("Florida")) {
			Result result = mapOfFipsCodeToResult.get(data.fipsCode);
			double numPeepsWithDegrees = Integer.valueOf(result.results.get(ACS2021DataReader.BACHELORS_DEGREE))
					+ Integer.valueOf(result.results.get(ACS2021DataReader.MASTERS_DEGREE))
					+ Integer.valueOf(result.results.get(ACS2021DataReader.PROFESSIONAL_DEGREE))
					+ Integer.valueOf(result.results.get(ACS2021DataReader.DOCTORATE_DEGREE));
			double numPeeps = Integer.valueOf(result.results.get(ACS2021DataReader.NUM_PEOPLE_OVER_25_EDUCATION));
			if (numPeeps < 1) {
				return;
			}
			double fraction = (numPeepsWithDegrees/numPeeps)*100;
			fraction = Math.round(fraction * 100.0) / 100.0;
			System.out.println("fraction: " + fraction);
			System.out.println(data.percentWithAtleastBachelors);
			System.out.println(mapOfFipsCodeToResult.get(data.fipsCode));
		}

	}

}
