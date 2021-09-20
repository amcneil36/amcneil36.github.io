package main.java.com.hey;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats.Data;

public class SperlingCsvReader extends CityStats {
	
	static Map<String, Sperling> mapOfCityToData = new HashMap<>();
	static DecimalFormat df = new DecimalFormat("#.#");
	static class Sperling {
		String povertyRate = "N/A";
		String percentOfIncomeLostToHousingCosts = "N/A";
		String foreignBornPercent = "N/A";
		
	}
	
	private static String getKey(String city, String state) {
		return city.toLowerCase() + "," + state.toLowerCase();
	}
	
	private static void fillMap() throws Exception {
     String filePath = "C:\\Users\\anmcneil\\Downloads\\population\\population.csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine(); // skipHeader
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			String cityName = "";
			if (line.contains("?")) {
				continue;
			}
			if (arr[0].contains(" city")) {
				cityName = arr[0].substring(0, arr[0].indexOf(" city"));
		//		System.out.println(cityName);
			}
			else if (arr[0].contains(" CDP")){
				cityName = arr[0].substring(0, arr[0].indexOf(" CDP"));
			}
			else if (arr[0].contains(" town")){
				cityName = arr[0].substring(0, arr[0].indexOf(" town"));
			}
			else if (arr[0].contains(" borough")){
				cityName = arr[0].substring(0, arr[0].indexOf(" borough"));
			}
			else if (arr[0].contains(" village")){
				cityName = arr[0].substring(0, arr[0].indexOf(" village"));
			}
			else if (arr[0].contains(" municipality")){
				cityName = arr[0].substring(0, arr[0].indexOf(" municipality"));
			}
			else {
				continue;
			}
			arr[0] = arr[0].substring(arr[0].indexOf(";") + 2);
			cityName = cityName.toLowerCase();
			String stateName = arr[0].toLowerCase();
			double population = Integer.valueOf(arr[1]);
			if (population == 0) {
				continue;
			}
			Sperling sperling = new Sperling();
			if (arr[3].length() > 0 && arr[2].length() > 0 && !arr[2].equals("0")) {
				sperling.povertyRate = String.valueOf(df.format(Double.valueOf(arr[3])/Double.valueOf(arr[2]))) + "%";
				
			}
			try {
				sperling.percentOfIncomeLostToHousingCosts = String.valueOf(Double.valueOf(arr[4])) + "%";
			}
			catch(Exception ex) {
				
			}
			sperling.foreignBornPercent = String.valueOf(df.format(Double.valueOf(arr[5])/Double.valueOf(population))) + "%";
			String key = getKey(cityName, stateName);
			mapOfCityToData.put(key, sperling);
			
		}
		myReader.close();
	}

	public static void main(String[] args) throws Exception {
		fillMap();
		SperlingCsvReader s = new SperlingCsvReader();
		s.processAllStates();
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
