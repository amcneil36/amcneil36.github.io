package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class WindSpeed extends CityStats {

	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	private static final List<WindspeedData> WINDSPEED_LIST = new ArrayList<>();

	public static class WindspeedData {
		double latitude;
		double longitude;
		double windSpeed;
	}

	public static void main(String[] args) throws Exception {
		List<String> text = Util
				.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\windSpeed.txt");
		text.remove(0); // first line is headers
//        POR        JAN   FEB   MAR   APR   MAY   JUN   JUL   AUG   SEP   OCT   NOV   DEC   ANN
//13876BIRMINGHAM,AL                   196501-197812    46    53    57    65    65    67    59    62    59    66    55    49    58

		Map<String, Data> mapOfKeyToData = populateMapOfKeyToData();
		for (String line : text) {
			int firstCommaIdx = line.indexOf(",");
			String city = line.substring(0, firstCommaIdx);
			for (int i = 0; i <= 9; i++) {
				city = city.replace(String.valueOf(i), "");
			}
			// System.out.println(city);
			String state = line.substring(firstCommaIdx + 1, firstCommaIdx + 3);
			String key = getKey(city, state);
			line = line.substring(firstCommaIdx + 3);
			line = line.substring(line.indexOf("-") + 10);
			System.out.println(line);
			String[] arrFoo = line.split("   ");
			System.out.println(arrFoo.length);
			if (arrFoo.length != 13) {
				continue;
			}
			WindspeedData sunshineData = new WindspeedData();
			sunshineData.windSpeed = Integer.valueOf(arrFoo[12]);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					sunshineData.latitude = Double.valueOf(data.latitude);
					sunshineData.longitude = Double.valueOf(data.longitude);
					WINDSPEED_LIST.add(sunshineData);
				}
			}
		}
		WindSpeed windSpeed = new WindSpeed();
		windSpeed.processAllStates();
	}

	private static Map<String, Data> populateMapOfKeyToData() throws Exception {
		List<Data> dataList = CreateBigCsv.readInput();
		Map<String, Data> mapOfKeyToData = new HashMap<>();
		for (Data data : dataList) {
			String key = getKey(data.cityName, Util.getStateAbbreviation(data.stateName));
			if (mapOfKeyToData.containsKey(key)) {
				int prevPop = Integer.valueOf(mapOfKeyToData.get(key).population);
				int curPop = Integer.valueOf(data.population);
				if (curPop > prevPop) {
					mapOfKeyToData.put(key, data);
				}
			} else {
				mapOfKeyToData.put(key, data);
			}
		}
		return mapOfKeyToData;
	}
	
	private static final Double MAX_ALLOWED_DISTANCE_MILES = 150.0; // in miles

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.annualSunshinePercent = "N/A";
		data.summerSunshinePercent = "N/A";
		data.winterSunshinePercent = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		WindspeedData bestWindspeedData = new WindspeedData();
		for (WindspeedData windspeedData : WINDSPEED_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(windspeedData.latitude, windspeedData.longitude, dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestWindspeedData = windspeedData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.avgYearlyWindspeed = String.valueOf(bestWindspeedData.windSpeed);
		}

	}
	
}
