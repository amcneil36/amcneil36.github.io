package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class RelativeHumidity extends CityStats {

	private static final List<RelativeHumidityData> RH_LIST = new ArrayList<>();

	public static class RelativeHumidityData {
		double latitude;
		double longitude;
		int summerHumidityPercent;
		int annualHumidityPercent;

		@Override
		public String toString() {
			return "latitude: " + latitude + "; longitude: " + longitude + "; summer humidity: "
					+ summerHumidityPercent;
		}
	}

	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	public static void main(String[] args) throws Exception {
		List<String> text = Util.readTextFromFile(
				"C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\relativeHumidity.txt");
		text.remove(0); // first line is headers
		text.remove(0); // second line is headers too
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
			line = line.substring(line.indexOf("-") + 9);
			String[] arrFoo = line.split("  ");
			// System.out.println(arrFoo.length); // 26 elements
			if (arrFoo.length != 26) {
				continue;
			}

			RelativeHumidityData rhData = new RelativeHumidityData();
			rhData.summerHumidityPercent = getSummerAvg(arrFoo);
			rhData.annualHumidityPercent = Integer.valueOf(arrFoo[12 * 2 + 1]);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					rhData.latitude = Double.valueOf(data.latitude);
					rhData.longitude = Double.valueOf(data.longitude);
					RH_LIST.add(rhData);
				}
			}
		}

		RelativeHumidity rh = new RelativeHumidity();
		rh.processAllStates();
	}

	private static int getSummerAvg(String[] arrFoo) {
		// TODO Auto-generated method stub
		// 11, 13, 15
		double summerTotal = Double.valueOf(arrFoo[5 * 2 + 1]) + Double.valueOf(arrFoo[6 * 2 + 1])
				+ Double.valueOf(arrFoo[7 * 2 + 1]);
		double summerAvg = summerTotal / 3;
		int summerAvgInt = Util.getIntFromDouble(summerAvg);
		return summerAvgInt;
	}

	private static final Double MAX_ALLOWED_DISTANCE_MILES = 150.0; // in miles

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.annualHumidityPercent = "N/A";
		data.summerHumidityPercent = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		RelativeHumidityData bestHumidityData = new RelativeHumidityData();
		for (RelativeHumidityData humidityData : RH_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(humidityData.latitude, humidityData.longitude, dataLatitude,
					dataLongitude);
			if (distance < minDistance) {
				bestHumidityData = humidityData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.annualHumidityPercent = bestHumidityData.annualHumidityPercent + "%";
			data.summerHumidityPercent = bestHumidityData.summerHumidityPercent + "%";
		}
	}

	static int numMatches = 0;

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

}
