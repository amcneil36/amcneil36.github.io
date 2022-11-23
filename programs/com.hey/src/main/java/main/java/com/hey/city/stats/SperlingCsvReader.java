package main.java.com.hey.city.stats;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats;

public class SperlingCsvReader extends CityStats {

	static Map<String, Sperling> mapOfCityToData = new HashMap<>();
	static DecimalFormat df = new DecimalFormat("#.#");

	static class Sperling {
		String povertyRate = "N/A";
		String percentOfIncomeLostToHousingCosts = "N/A";
		String foreignBornPercent = "N/A";

	}

	public static String getKey(String city, String state) {
		return city.toLowerCase() + "," + state.toLowerCase();
	}

	static String getCityName(String st) {
		if (st.contains(" city")) {
			return st.substring(0, st.indexOf(" city")).toLowerCase();
		} else if (st.contains(" CDP")) {
			return st.substring(0, st.indexOf(" CDP")).toLowerCase();
		} else if (st.contains(" town")) {
			return st.substring(0, st.indexOf(" town")).toLowerCase();
		} else if (st.contains(" borough")) {
			return st.substring(0, st.indexOf(" borough")).toLowerCase();
		} else if (st.contains(" village")) {
			return st.substring(0, st.indexOf(" village")).toLowerCase();
		} else if (st.contains(" municipality")) {
			return st.substring(0, st.indexOf(" municipality")).toLowerCase();
		} else {
			return "N/A";
		}
	}

	private static String getStateName(String st) {
		st = st.substring(st.indexOf(";") + 2);
		return st.toLowerCase();
	}

	public static String getKey(String st) {
		String cityName = getCityName(st);
		if (cityName.equals("N/A")) {
			return "N/A";
		}
		String stateName = getStateName(st);
		return getKey(cityName, stateName);
	}

	private static void fillMap() throws Exception {
		String filePath = "C:\\Users\\anmcneil\\Downloads\\population\\population.csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine(); // skipHeader
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			String key = getKey(arr[0]);
			if ("N/A".equals(key)) {
				continue;
			}
			double population = Integer.valueOf(arr[1]);
			if (population == 0) {
				continue;
			}
			Sperling sperling = new Sperling();
			if (arr[3].length() > 0 && arr[2].length() > 0 && !arr[2].equals("0")) {
				sperling.povertyRate = String.valueOf(df.format(100 * Double.valueOf(arr[3]) / Double.valueOf(arr[2])))
						+ "%";

			}
			try {
				sperling.percentOfIncomeLostToHousingCosts = String.valueOf(Double.valueOf(arr[4])) + "%";
			} catch (Exception ex) {

			}
			sperling.foreignBornPercent = String
					.valueOf(df.format(100 * Double.valueOf(arr[5]) / Double.valueOf(population))) + "%";
			mapOfCityToData.put(key, sperling);

		}
		myReader.close();
	}

	public static void main(String[] args) throws Exception {
		fillMap();
		SperlingCsvReader s = new SperlingCsvReader();
		s.processAllStates();
		// s.processState("Delaware");
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String key = getKey(data.cityName, data.stateName);
		if (mapOfCityToData.containsKey(key)) {
			Sperling sperling = mapOfCityToData.get(key);
			data.povertyRate = sperling.povertyRate;
			data.percentOfIncomeLostToHousingCosts = sperling.percentOfIncomeLostToHousingCosts;
			data.foreignBornPercent = sperling.foreignBornPercent;
		}

	}

}
