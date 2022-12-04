package main.java.com.hey.city.stats;

import java.io.File;
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

public class NOAANormalsReader extends CityStats {

	private static final String MONTHLY_AVERAGE_TEMP = "mly-tavg-normal";
	private static final String MONTHLY_MAX_TEMP = "mly-tmax-normal";
	private static final String MONTHLY_MIN_TEMP = "mly-tmin-normal";
	private static final String MONTHLY_INCHES_RAIN = "mly-prcp-normal";
	private static final String MONTHLY_INCHES_SNOW = "mly-snow-normal";
	private static final String MONTHLY_DAYS_OF_RAIN = "mly-prcp-avgnds-ge001hi";
	private static final String MONTHLY_DAYS_OF_SNOW = "mly-snow-avgnds-ge001ti";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";

	private static final Set<String> WEATHER_VARIABLES = new HashSet<>(
			Arrays.asList(MONTHLY_AVERAGE_TEMP, MONTHLY_MAX_TEMP, MONTHLY_MIN_TEMP, MONTHLY_INCHES_RAIN,
					MONTHLY_INCHES_SNOW, MONTHLY_DAYS_OF_RAIN, MONTHLY_DAYS_OF_SNOW));

	public static class TemperatureData{
		double latitude;
		double longitude;
		int hottestMonthAvgHigh;
		int coldestMonthAvgHigh;
		int hottestMonthAvgLow;
		int coldestMonthAvgLow;
	}
	
	private static final List<TemperatureData> TEMPERATURE_DATA_LIST = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		// USC00214546
		// TODO Auto-generated method stub

		String path = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\multivariate\\";
		File directoryPath = new File(path);
		// List of all files and directories
		String[] listArr = directoryPath.list();
		for (String st : listArr) {
			// System.out.println(++idx);
			List<String> text = Util.readTextFromFile(path + st);
			Map<String, Integer> mapOfHeaderToIdx = getMapOfHeaderToIdx(text);
			text.remove(0);
			if (text.size() != 12) {
				continue;
			}
			
			populateTemperatureData(text, mapOfHeaderToIdx);

			// mly-tavg-normal Long-term averages of monthly average temperature
			// mly-tmax-normal Long-term averages of monthly maximum temperature
			// mly-tmin-normal Long-term averages of monthly minimum temperature
			// mly-prcp-normal Long-term averages of monthly precipitation total
			// mly-snow-normal Long-term averages of monthly snowfall total
			// mly-prcp-avgnds-ge001hi Monthly number of days with precipitation >= 0.01
			// inches
			// mly-snow-avgnds-ge001ti Monthly number of days with snowfall >= 0.1 inch
			// what if we do % days of rain?


		}
		NOAANormalsReader reader = new NOAANormalsReader();
		reader.processAllStates();

	}
	
	private static void populateTemperatureData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsMax = mapOfHeaderToIdx.containsKey(MONTHLY_MAX_TEMP);
		// if the .csv has the max temp, it also has the min temp and avg temp.
		if (containsMax) {
			int hottestMonthAvgHigh = -4000;
			int coldestMonthAvgHigh = 4000;
			int hottestMonthAvgLow = -999;
			int coldestMonthAvgLow = -999;
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				int highInt = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MAX_TEMP)]));
				if (highInt > hottestMonthAvgHigh) {
					hottestMonthAvgHigh = Math.max(hottestMonthAvgHigh, highInt);	
					hottestMonthAvgLow = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MIN_TEMP)]));
				}
				int coldInt = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MAX_TEMP)]));
				if (coldInt < coldestMonthAvgHigh) {
					coldestMonthAvgHigh = Math.min(coldestMonthAvgHigh, coldInt);
					coldestMonthAvgLow = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MIN_TEMP)]));
				}
			}
			String[] arr = StringUtils.substringsBetween(text.get(0), "\"", "\"");
			TemperatureData temperatureData = new TemperatureData();
			temperatureData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			temperatureData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			temperatureData.coldestMonthAvgHigh = coldestMonthAvgHigh;
			temperatureData.hottestMonthAvgHigh = hottestMonthAvgHigh;
			temperatureData.hottestMonthAvgLow = hottestMonthAvgLow;
			temperatureData.coldestMonthAvgLow = coldestMonthAvgLow;
			TEMPERATURE_DATA_LIST.add(temperatureData);
		}
	}

	private static Map<String, Integer> getMapOfHeaderToIdx(List<String> text) {
		String[] arr = text.get(0).split(",");
		Map<String, Integer> mapOfHeaderToIdx = new HashMap<>();
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].replace("\"", "").toLowerCase();
			mapOfHeaderToIdx.put(arr[i], i);
		}
		return mapOfHeaderToIdx;
	}
	
	private static final double MAX_ALLOWED_DISTANCE_MILES = 150.0;

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.hottestMonthsHigh = "N/A";
		data.coldestHigh = "N/A";
		data.hottestMonthAvgLow = "N/A";
		data.coldestMonthAvgLow = "N/A";
		data.hottestMonthMinusColdestMonth = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		double minDistance = Double.MAX_VALUE;
		TemperatureData bestTemperatureData = new TemperatureData();
		for (TemperatureData temperatureData : TEMPERATURE_DATA_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(temperatureData.latitude, temperatureData.longitude, dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestTemperatureData = temperatureData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.hottestMonthsHigh = String.valueOf(bestTemperatureData.hottestMonthAvgHigh);
			data.coldestHigh = String.valueOf(bestTemperatureData.coldestMonthAvgHigh);
			data.hottestMonthMinusColdestMonth = String.valueOf(bestTemperatureData.hottestMonthAvgHigh - bestTemperatureData.coldestMonthAvgHigh);
			data.hottestMonthAvgLow = String.valueOf(bestTemperatureData.hottestMonthAvgLow);
			data.coldestMonthAvgLow = String.valueOf(bestTemperatureData.coldestMonthAvgLow);
		}
		
	}

}
