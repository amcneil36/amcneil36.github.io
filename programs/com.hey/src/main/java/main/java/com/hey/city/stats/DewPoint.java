package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class DewPoint extends CityStats {
	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	private static final List<DewPointData> DEW_POINT_LIST = new ArrayList<>();
	private static final Set<String> REMOVE_THESE = new HashSet<>(
			Arrays.asList(" STATE AP", " MUNICIPAL AP", " FAA AP", " WSO AP", " WSO"));

	public static class DewPointData {
		double latitude;
		double longitude;
		int summerDewPoint;
		int annualDewPoint;
	}

	public static void main(String[] args) throws Exception {
		List<String> text = Util
				.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\dewPoint.csv");
		text.remove(0);

		Map<String, Data> mapOfKeyToData = populateMapOfKeyToData();
		for (String line : text) {
			String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
			DewPointData dewPointData = new DewPointData();
			dewPointData.summerDewPoint = Util.getIntFromDouble(
					(Double.valueOf(arr[18]) + Double.valueOf(arr[20]) + Double.valueOf(arr[22])) / 3);
			dewPointData.annualDewPoint = Util.getIntFromDouble(Double.valueOf(arr[32]));
			String cityAndState = arr[1];
			String stateAbbreviation = cityAndState.substring(cityAndState.length() - 2);
			cityAndState = cityAndState.substring(0, cityAndState.length() - 3);
			cityAndState = cityAndState.replace(",", "");
			String city = "";

			if (cityAndState.contains("/")) {
				city = cityAndState.substring(0, cityAndState.indexOf("/"));
			} else {
				for (String st : REMOVE_THESE) {
					cityAndState = cityAndState.replace(st, "");
				}
				city = cityAndState;
			}
			String key = getKey(city, stateAbbreviation);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					dewPointData.latitude = Double.valueOf(data.latitude);
					dewPointData.longitude = Double.valueOf(data.longitude);
					DEW_POINT_LIST.add(dewPointData);
				}
			}
		}
		DewPoint thunderstorm = new DewPoint();
		thunderstorm.processAllStates();
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
	public void updateData(Data data, String stateName) throws Exception {
		data = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		DewPointData bestThunderstormData = new DewPointData();
		for (DewPointData thunderstormData : DEW_POINT_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(thunderstormData.latitude, thunderstormData.longitude, dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestThunderstormData = thunderstormData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.numberOfThunderstormsPerYear = String.valueOf(bestThunderstormData.numberOfThunderstorms);
		}

	}
}
