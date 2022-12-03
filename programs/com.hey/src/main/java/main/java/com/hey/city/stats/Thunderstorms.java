package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class Thunderstorms extends CityStats{

	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	private static final List<ThunderstormData> THUNDERSTORM_LIST = new ArrayList<>();
	private static final Set<String> REMOVE_THESE = new HashSet<>(Arrays.asList(" STATE AP", " MUNICIPAL AP", " FAA AP", " WSO AP", " WSO"));

	public static class ThunderstormData {
		double latitude;
		double longitude;
		double numberOfThunderstorms;
	}
	

	public static void main(String[] args) throws Exception {
		List<String> text = Util
				.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\numberOfThunderstormsPerYear.csv");
		text.remove(0);

		Map<String, Data> mapOfKeyToData = populateMapOfKeyToData();
		for (String line : text) {
			line = line.replace("\"", "");
			int lastIdx = line.lastIndexOf(",");
			String numberOfThunderstorms = line.substring(lastIdx+1);
			if (numberOfThunderstorms.isEmpty()) {
				break;
			}
			double numThunderStormsDouble = Double.valueOf(numberOfThunderstorms);
			line = line.substring(0, lastIdx);
			String stateAbbreviation = line.substring(line.length()-2);
			line = line.substring(0, line.length()-3);
			line = line.replace(",", "");
			String city = "";
		
			if (line.contains("/")) {
				city = line.substring(0,line.indexOf("/"));
			}
			else {
				for (String st : REMOVE_THESE) {
					line = line.replace(st, "");
				}
				city = line;
			}
			ThunderstormData thunderstormData = new ThunderstormData();
			thunderstormData.numberOfThunderstorms = numThunderStormsDouble;
			String key = getKey(city, stateAbbreviation);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					thunderstormData.latitude = Double.valueOf(data.latitude);
					thunderstormData.longitude = Double.valueOf(data.longitude);
					THUNDERSTORM_LIST.add(thunderstormData);
				}
			}
		}
		Thunderstorms thunderstorm = new Thunderstorms();
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
		data.numberOfThunderstormsPerYear = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		ThunderstormData bestThunderstormData = new ThunderstormData();
		for (ThunderstormData thunderstormData : THUNDERSTORM_LIST) {
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
