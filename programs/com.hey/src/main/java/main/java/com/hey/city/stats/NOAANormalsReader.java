package main.java.com.hey.city.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class NOAANormalsReader extends CityStats {

	private static final String MONTHLY_MAX_TEMP = "mly-tmax-normal";
	private static final String MONTHLY_MIN_TEMP = "mly-tmin-normal";
	private static final String MONTHLY_INCHES_RAIN = "mly-prcp-normal";
	private static final String MONTHLY_INCHES_SNOW = "mly-snow-normal";
	private static final String MONTHLY_DAYS_OF_RAIN = "mly-prcp-avgnds-ge001hi";
	private static final String MONTHLY_DAYS_OF_SNOW = "mly-snow-avgnds-ge001ti";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";

	public static class TemperatureData {
		double latitude;
		double longitude;
		int hottestMonthAvgHigh;
		int coldestMonthAvgHigh;
		int hottestMonthAvgLow;
		int coldestMonthAvgLow;
	}

	public static class RainInchesData {
		double latitude;
		double longitude;
		double inchesOfRainPerYear;
		double inchesOfRainPerSummer;
		double inchesOfRainPerWinter;
	}

	public static class SnowInchesData extends Util.Coordinate {
		double inchesOfSnowPerYear;
	}
	
	public static class RainDaysData extends Util.Coordinate{
		double percentOfSummerDaysOfRain;
		double percentOfWinterDaysOfRain;
		double percentOfAnnualDaysOfRain;
	}

	private static final List<TemperatureData> TEMPERATURE_DATA_LIST = new ArrayList<>();
	private static final List<RainInchesData> RAIN_INCHES_LIST = new ArrayList<>();
	private static final List<SnowInchesData> _LIST = new ArrayList<>();
	private static final List<RainDaysData> RAIN_DAYS_LIST = new ArrayList<>();

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

			// populateTemperatureData(text, mapOfHeaderToIdx);
			// populateRainInchesData(text, mapOfHeaderToIdx);
			//populateSnowInchesData(text, mapOfHeaderToIdx);
			populateRainDaysData(text, mapOfHeaderToIdx);

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

	private static void populateRainDaysData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsRainDays = mapOfHeaderToIdx.containsKey(MONTHLY_DAYS_OF_RAIN);
		// 5 6 7 for summer
		// 0 1 11 for winter
		if (containsRainDays) {
			Set<Integer> summerMonths = new HashSet<>(Arrays.asList(5, 6, 7));
			Set<Integer> winterMonths = new HashSet<>(Arrays.asList(0, 1, 11));
			double totalSummerDays = 30+30+31;
			double totalWinterDays = 31 + 28 +31;
			double totalDaysOfRain = 0;
			double totalSummerDaysOfRain = 0;
			double totalWinterDaysOfRain = 0;
			int idx = 0;
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				totalDaysOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_DAYS_OF_RAIN)]);
				if (summerMonths.contains(idx)) {
					totalSummerDaysOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_DAYS_OF_RAIN)]);
				} else if (winterMonths.contains(idx)) {
					totalWinterDaysOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_DAYS_OF_RAIN)]);
				}
				idx++;
			}
			String[] arr = StringUtils.substringsBetween(text.get(0), "\"", "\"");
			RainDaysData rainDaysData = new RainDaysData();
			rainDaysData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			rainDaysData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			rainDaysData.percentOfAnnualDaysOfRain = Util.roundTwoDecimalPlaces(totalDaysOfRain/365);
			rainDaysData.percentOfSummerDaysOfRain = Util.roundTwoDecimalPlaces(totalSummerDaysOfRain/totalSummerDays);
			rainDaysData.percentOfWinterDaysOfRain = Util.roundTwoDecimalPlaces(totalWinterDaysOfRain/totalWinterDays);
			RAIN_DAYS_LIST.add(rainDaysData);
		}		
	}

	private static void populateSnowInchesData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsSnowInches = mapOfHeaderToIdx.containsKey(MONTHLY_INCHES_SNOW);
		if (containsSnowInches) {
			double totalInchesOfSnow = 0;
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				totalInchesOfSnow += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_INCHES_SNOW)]);
			}
			String[] arr = StringUtils.substringsBetween(text.get(0), "\"", "\"");
			SnowInchesData snowInchesData = new SnowInchesData();
			snowInchesData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			snowInchesData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			snowInchesData.inchesOfSnowPerYear = Util.roundTwoDecimalPlaces(totalInchesOfSnow);
			_LIST.add(snowInchesData);
		}
	}

	private static void populateRainInchesData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsRainInches = mapOfHeaderToIdx.containsKey(MONTHLY_INCHES_RAIN);
		// 5 6 7 for summer
		// 0 1 11 for winter
		if (containsRainInches) {
			Set<Integer> summerMonths = new HashSet<>(Arrays.asList(5, 6, 7));
			Set<Integer> winterMonths = new HashSet<>(Arrays.asList(0, 1, 11));
			double totalInchesOfRain = 0;
			double totalSummerInchesOfRain = 0;
			double totalWinterInchesOfRain = 0;
			int idx = 0;
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				totalInchesOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_INCHES_RAIN)]);
				if (summerMonths.contains(idx)) {
					totalSummerInchesOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_INCHES_RAIN)]);
				} else if (winterMonths.contains(idx)) {
					totalWinterInchesOfRain += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_INCHES_RAIN)]);
				}
				idx++;
			}
			String[] arr = StringUtils.substringsBetween(text.get(0), "\"", "\"");
			RainInchesData rainInchesData = new RainInchesData();
			rainInchesData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			rainInchesData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			rainInchesData.inchesOfRainPerYear = Util.roundTwoDecimalPlaces(totalInchesOfRain);
			rainInchesData.inchesOfRainPerSummer = Util.roundTwoDecimalPlaces(totalSummerInchesOfRain);
			rainInchesData.inchesOfRainPerWinter = Util.roundTwoDecimalPlaces(totalWinterInchesOfRain);
			RAIN_INCHES_LIST.add(rainInchesData);
		}
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
					hottestMonthAvgLow = Util
							.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MIN_TEMP)]));
				}
				int coldInt = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MAX_TEMP)]));
				if (coldInt < coldestMonthAvgHigh) {
					coldestMonthAvgHigh = Math.min(coldestMonthAvgHigh, coldInt);
					coldestMonthAvgLow = Util
							.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MIN_TEMP)]));
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
		data.numInchesOfRain = "N/A";
		data.numInchesOfRainPerSummer = "N/A";
		data.numInchesOfRainPerWinter = "N/A";
		data.annualSnowfall = "N/A";
		data.percentOfDaysWithRain = "N/A";
		data.percentOfSummerDaysWithRain = "N/A";
		data.percentOfWinterDaysWithRain = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		// updateTemperatureData(data);
		// updateRainInchesData(data);
		//updateSnowInchesData(data);
		updateRainDaysData(data);

	}

	private void updateRainDaysData(Data data) {
		Optional<RainDaysData> rainDaysData = Util.findBestCoordinate(RAIN_DAYS_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (rainDaysData.isPresent()) {
			data.percentOfDaysWithRain = String.valueOf(Util.roundTwoDecimalPlaces(rainDaysData.get().percentOfAnnualDaysOfRain)) + "%";
			data.percentOfSummerDaysWithRain = String.valueOf(Util.roundTwoDecimalPlaces(rainDaysData.get().percentOfSummerDaysOfRain)) + "%";
			data.percentOfWinterDaysWithRain = String.valueOf(Util.roundTwoDecimalPlaces(rainDaysData.get().percentOfWinterDaysOfRain)) + "%";
		}
	}

	private void updateSnowInchesData(Data data) {
		Optional<SnowInchesData> snowInchesData = Util.findBestCoordinate(_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (snowInchesData.isPresent()) {
			data.annualSnowfall = String.valueOf(Util.roundTwoDecimalPlaces(snowInchesData.get().inchesOfSnowPerYear));
		}

	}

	private void updateRainInchesData(Data data) {
		double minDistance = Double.MAX_VALUE;
		RainInchesData bestrainInchesData = new RainInchesData();
		for (RainInchesData rainInchesData : RAIN_INCHES_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(rainInchesData.latitude, rainInchesData.longitude,
					dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestrainInchesData = rainInchesData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.numInchesOfRain = String.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.inchesOfRainPerYear));
			data.numInchesOfRainPerSummer = String
					.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.inchesOfRainPerSummer));
			data.numInchesOfRainPerWinter = String
					.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.inchesOfRainPerWinter));
		}
	}

	private void updateTemperatureData(Data data) {
		double minDistance = Double.MAX_VALUE;
		TemperatureData bestTemperatureData = new TemperatureData();
		for (TemperatureData temperatureData : TEMPERATURE_DATA_LIST) {
			double dataLatitude = Double.valueOf(data.latitude);
			double dataLongitude = Double.valueOf(data.longitude);
			double distance = Util.milesBetweenCoordinates(temperatureData.latitude, temperatureData.longitude,
					dataLatitude, dataLongitude);
			if (distance < minDistance) {
				bestTemperatureData = temperatureData;
				minDistance = distance;
			}
		}
		if (minDistance < MAX_ALLOWED_DISTANCE_MILES) {
			data.hottestMonthsHigh = String.valueOf(bestTemperatureData.hottestMonthAvgHigh);
			data.coldestHigh = String.valueOf(bestTemperatureData.coldestMonthAvgHigh);
			data.hottestMonthMinusColdestMonth = String
					.valueOf(bestTemperatureData.hottestMonthAvgHigh - bestTemperatureData.coldestMonthAvgHigh);
			data.hottestMonthAvgLow = String.valueOf(bestTemperatureData.hottestMonthAvgLow);
			data.coldestMonthAvgLow = String.valueOf(bestTemperatureData.coldestMonthAvgLow);
		}
	}

}
