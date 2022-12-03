package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class Sunshine extends CityStats {

	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	private static final List<SunshineData> SUNSHINE_LIST = new ArrayList<>();

	public static class SunshineData {
		double latitude;
		double longitude;
		int annualSunshinePercent;
		int summerSunshinePercent;
		int winterSunshinePercent;
	}

	public static void main(String[] args) throws Exception {
		List<String> text = Util
				.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\sunshine.txt");
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
			line = line.substring(line.indexOf("-") + 11);
			String[] arrFoo = line.split("    ");
			if (arrFoo.length != 13) {
				continue;
			}

			SunshineData sunshineData = new SunshineData();
			sunshineData.summerSunshinePercent = getSummerAvg(arrFoo);
			sunshineData.winterSunshinePercent = getWinterAvg(arrFoo);
			sunshineData.annualSunshinePercent = Integer.valueOf(arrFoo[12]);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					sunshineData.latitude = Double.valueOf(data.latitude);
					sunshineData.longitude = Double.valueOf(data.longitude);
					SUNSHINE_LIST.add(sunshineData);
				}
			}
		}
		Sunshine sunshine = new Sunshine();
		sunshine.processAllStates();
	}

	private static int getWinterAvg(String[] arrFoo) {
		// winter is December, January, February. 0, 1, 11
		double winterTotal = Double.valueOf(arrFoo[0]) + Double.valueOf(arrFoo[1]) + Double.valueOf(arrFoo[11]);
		double winterAvg = winterTotal / 3;
		int winterAvgInt = Util.getIntFromDouble(winterAvg);
		return winterAvgInt;
	}

	private static int getSummerAvg(String[] arrFoo) {
		// meteorologists consider june july august to be summer months since they are
		// hotter and longer
		// idx 5, 6, 7
		double summerTotal = Double.valueOf(arrFoo[5]) + Double.valueOf(arrFoo[6]) + Double.valueOf(arrFoo[7]);
		double summerAvg = summerTotal / 3;
		int summerAvgInt = Util.getIntFromDouble(summerAvg);
		return summerAvgInt;
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
		data.annualPercentSunshine = "N/A";
		data.averageSummerSunshine = "N/A";
		data.averageWinterSushine = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		SunshineData bestSunshineData = new SunshineData();
		for (SunshineData sunshineData : SUNSHINE_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = getDistance(sunshineData, dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestSunshineData = sunshineData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.annualPercentSunshine = bestSunshineData.annualSunshinePercent + "%";
			data.averageSummerSunshine = bestSunshineData.summerSunshinePercent + "%";
			data.averageWinterSushine = bestSunshineData.winterSunshinePercent + "%";
		}

	}

	// result is in km
	private double getDistance(SunshineData sunshineData, double dataLatitude, double dataLongitude) {
		double kmDistance = haversine(sunshineData.latitude, sunshineData.longitude, dataLatitude, dataLongitude);
		double mileDistance = kmDistance * 0.621371 ;
		return mileDistance;
	}

	private static double haversine(double lat1, double lon1, double lat2, double lon2) {
// distance between latitudes and longitudes
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

// convert to radians
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

// apply formulae
		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double rad = 6371;
		double c = 2 * Math.asin(Math.sqrt(a));
		return rad * c;
	}

}
