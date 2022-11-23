package main.java.com.hey.city.stats;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;

public class DewPointCsvReaderYearly extends CityStats{

	static Map<String, String> mapOfCityToData = new HashMap<>();
	static DecimalFormat df = new DecimalFormat("#.#");
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String key = SperlingCsvReader.getKey(data.cityName, data.stateName);
		if (mapOfCityToData.containsKey(key)) {
			data.avgAnnualDewPoint = mapOfCityToData.get(key);
		}
		
	}
	
	private static void fillMap() throws Exception {
		String filePath = "C:\\Users\\anmcneil\\Downloads\\dewPointYearly\\dewPointYearly.csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine(); // skipHeader
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			if (arr.length != 4) {
				continue;
			}
			String key = SperlingCsvReader.getKey(arr[0]);
			if ("N/A".equals(key)) {
				continue;
			}
			double population = Integer.valueOf(arr[1]);
			if (population == 0) {
				continue;
			}
            
			String value = "";
			double yearlyDewPointAvg = (Double.valueOf(arr[3]));
			value = df.format(yearlyDewPointAvg);
			mapOfCityToData.put(key, value);

		}
		myReader.close();
	}
	
	public static void main(String[] args) throws Exception {
		fillMap();
		DewPointCsvReaderYearly s = new DewPointCsvReaderYearly();
		s.processAllStates();
		s.processState("Delaware");
	}

}
