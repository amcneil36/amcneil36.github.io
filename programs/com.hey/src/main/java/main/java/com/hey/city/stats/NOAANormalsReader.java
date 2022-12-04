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
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class NOAANormalsReader extends CityStats {

	private static final String MONTHLY_MAX_TEMP = "mly-tmax-normal";
	private static final String MONTHLY_MIN_TEMP = "mly-tmin-normal";
	private static final String MONTHLY_INCHES_RAIN = "mly-prcp-normal";
	private static final String MONTHLY_INCHES_SNOW = "mly-snow-normal";
	private static final String MONTHLY_DAYS_OF_RAIN = "mly-prcp-avgnds-ge001hi";
	private static final String MONTHLY_DAYS_OF_SNOW = "mly-snow-avgnds-ge001ti";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String ELEVATION = "elevation";

	public static class TemperatureData extends Util.Coordinate{
		int hottestMonthAvgHigh;
		int coldestMonthAvgHigh;
		int hottestMonthAvgLow;
		int coldestMonthAvgLow;
		List<Integer> averageHighs = new ArrayList<>();
	}

	public static class RainInchesData extends Util.Coordinate {
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
	
	public static class SnowyDaysData extends Util.Coordinate{
		int daysOfSnowPerYear;
	}
	
	public static class ElevationData extends Util.Coordinate{
		int elevation;
	}

	public static class RelativeHumidityData extends Util.Coordinate{
		int summerHumidityPercent;
		int annualHumidityPercent;
		List<Integer> relativeHumidities = new ArrayList<>();
	}

	private static final List<TemperatureData> TEMPERATURE_DATA_LIST = new ArrayList<>();
	private static final List<RainInchesData> RAIN_INCHES_LIST = new ArrayList<>();
	private static final List<SnowInchesData> SNOW_INCHES_LIST = new ArrayList<>();
	private static final List<RainDaysData> RAIN_DAYS_LIST = new ArrayList<>();
	private static final List<SnowyDaysData> SNOWY_DAYS_LIST = new ArrayList<>();
	private static final List<ElevationData> ELEVATION_LIST = new ArrayList<>();
	private static final List<RelativeHumidityData> RH_LIST = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		String path = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\WeatherData\\multivariate\\";
		File directoryPath = new File(path);
		String[] listArr = directoryPath.list();
		for (String st : listArr) {
			List<String> text = Util.readTextFromFile(path + st);
			Map<String, Integer> mapOfHeaderToIdx = getMapOfHeaderToIdx(text);
			text.remove(0);
			if (text.size() != 12) {
				continue;
			}

			populateTemperatureData(text, mapOfHeaderToIdx);
			populateRainInchesData(text, mapOfHeaderToIdx);
			populateSnowInchesData(text, mapOfHeaderToIdx);
			populateRainDaysData(text, mapOfHeaderToIdx);
			populateSnowyDaysData(text, mapOfHeaderToIdx);
			populateElevationData(text, mapOfHeaderToIdx);
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
		populateRHList();
		NOAANormalsReader reader = new NOAANormalsReader();
		reader.processAllStates();
	}

	private static void populateElevationData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsElevation = mapOfHeaderToIdx.containsKey(ELEVATION);
		if (containsElevation) {
			String line = text.get(0);
			String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
			ElevationData elevationData = new ElevationData();
			elevationData.elevation = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(ELEVATION)]))*3; // multinplying by 3 cuz it looks like they give it in yards
			elevationData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			elevationData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			ELEVATION_LIST.add(elevationData);
		}		
	}

	private static void populateSnowyDaysData(List<String> text, Map<String, Integer> mapOfHeaderToIdx) {
		boolean containsSnowDays = mapOfHeaderToIdx.containsKey(MONTHLY_DAYS_OF_SNOW);
		if (containsSnowDays) {
			int totalDaysOfSnow = 0;
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				totalDaysOfSnow += Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_DAYS_OF_SNOW)]);
			}
			String[] arr = StringUtils.substringsBetween(text.get(0), "\"", "\"");
			SnowyDaysData snowyDaysData = new SnowyDaysData();
			snowyDaysData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			snowyDaysData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			snowyDaysData.daysOfSnowPerYear = totalDaysOfSnow;
			SNOWY_DAYS_LIST.add(snowyDaysData);
		}	
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
			rainDaysData.percentOfAnnualDaysOfRain = Util.roundTwoDecimalPlaces(100*totalDaysOfRain/365);
			rainDaysData.percentOfSummerDaysOfRain = Util.roundTwoDecimalPlaces(100*totalSummerDaysOfRain/totalSummerDays);
			rainDaysData.percentOfWinterDaysOfRain = Util.roundTwoDecimalPlaces(100*totalWinterDaysOfRain/totalWinterDays);
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
			SNOW_INCHES_LIST.add(snowInchesData);
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
			TemperatureData temperatureData = new TemperatureData();
			for (String line : text) {
				String[] arr = StringUtils.substringsBetween(line, "\"", "\"");
				int highInt = Util.getIntFromDouble(Double.valueOf(arr[mapOfHeaderToIdx.get(MONTHLY_MAX_TEMP)]));
				temperatureData.averageHighs.add(highInt);
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
			temperatureData.latitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LATITUDE)]);
			temperatureData.longitude = Double.valueOf(arr[mapOfHeaderToIdx.get(LONGITUDE)]);
			temperatureData.coldestMonthAvgHigh = coldestMonthAvgHigh;
			temperatureData.hottestMonthAvgHigh = hottestMonthAvgHigh;
			temperatureData.hottestMonthAvgLow = hottestMonthAvgLow;
			temperatureData.coldestMonthAvgLow = coldestMonthAvgLow;
			TEMPERATURE_DATA_LIST.add(temperatureData);
		}
	}
	
	private static void populateRHList() throws Exception {
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
			String state = line.substring(firstCommaIdx + 1, firstCommaIdx + 3);
			String key = getKey(city, state);
			line = line.substring(firstCommaIdx + 3);
			line = line.substring(line.indexOf("-") + 9);
			String[] arrFoo = line.split("  ");
			if (arrFoo.length != 26) {
				continue;
			}

			RelativeHumidityData rhData = new RelativeHumidityData();
			rhData.summerHumidityPercent = getSummerAvg(arrFoo);
			rhData.annualHumidityPercent = Integer.valueOf(arrFoo[12 * 2 + 1]);
			rhData.relativeHumidities = getRelativeHumidities(arrFoo);
			if (mapOfKeyToData.containsKey(key)) {
				Data data = mapOfKeyToData.get(key);
				if (!data.latitude.equals("N/A")) {
					rhData.latitude = Double.valueOf(data.latitude);
					rhData.longitude = Double.valueOf(data.longitude);
					RH_LIST.add(rhData);
				}
			}
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
		data.daysOfSnowPerYear = "N/A";
		data.feetAboveSeaLevel = "N/A";
		data.annualHumidityPercent = "N/A";
		data.summerHumidityPercent = "N/A";
		data.hottestMonthsHeatIndexHigh = "N/A";
		if (data.longitude.equals("N/A")) {
			return;
		}
		updateTemperatureData(data);
		updateRainInchesData(data);
		updateSnowInchesData(data);
		updateRainDaysData(data);
		updateSnowyDaysData(data);
		updateElevationData(data);
		updateHumidityData(data);
		updateHeatIndexData(data);

	}

	private void updateHeatIndexData(Data data) {
		Optional<RelativeHumidityData> bestHumidityData = Util.findBestCoordinate(RH_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		Optional<TemperatureData> bestTemperatureData = Util.findBestCoordinate(TEMPERATURE_DATA_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (bestHumidityData.isPresent() && bestTemperatureData.isPresent()) {
			List<Integer> humidities = bestHumidityData.get().relativeHumidities;
			List<Integer> temperatures = bestTemperatureData.get().averageHighs;
			int maxHeatIndex = Integer.MIN_VALUE;
			for (int i = 0; i < humidities.size(); i++) {
				int heatIndex = getHeatIndex(temperatures.get(i), humidities.get(i));
				maxHeatIndex = Math.max(maxHeatIndex, heatIndex);
			}
			data.hottestMonthsHeatIndexHigh = String.valueOf(maxHeatIndex);
		}
	}

	private void updateHumidityData(Data data) {
		Optional<RelativeHumidityData> bestHumidityData = Util.findBestCoordinate(RH_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (bestHumidityData.isPresent()) {
			data.annualHumidityPercent = bestHumidityData.get().annualHumidityPercent + "%";
			data.summerHumidityPercent = bestHumidityData.get().summerHumidityPercent + "%";
		}
	}

	private void updateElevationData(Data data) {
		Optional<ElevationData> elevationData = Util.findBestCoordinate(ELEVATION_LIST, data,
				50);
		if (elevationData.isPresent()) {
			data.feetAboveSeaLevel = String.valueOf(elevationData.get().elevation);
		}
	}

	private void updateSnowyDaysData(Data data) {
		Optional<SnowyDaysData> rainDaysData = Util.findBestCoordinate(SNOWY_DAYS_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (rainDaysData.isPresent()) {
			data.daysOfSnowPerYear = String.valueOf(rainDaysData.get().daysOfSnowPerYear);
		}
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
		Optional<SnowInchesData> snowInchesData = Util.findBestCoordinate(SNOW_INCHES_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (snowInchesData.isPresent()) {
			data.annualSnowfall = String.valueOf(Util.roundTwoDecimalPlaces(snowInchesData.get().inchesOfSnowPerYear));
		}

	}

	private void updateRainInchesData(Data data) {
		
		Optional<RainInchesData> bestrainInchesData = Util.findBestCoordinate(RAIN_INCHES_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (bestrainInchesData.isPresent()) {
			data.numInchesOfRain = String.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.get().inchesOfRainPerYear));
			data.numInchesOfRainPerSummer = String
					.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.get().inchesOfRainPerSummer));
			data.numInchesOfRainPerWinter = String
					.valueOf(Util.roundTwoDecimalPlaces(bestrainInchesData.get().inchesOfRainPerWinter));
		}
	}

	private void updateTemperatureData(Data data) {
		Optional<TemperatureData> bestTemperatureData = Util.findBestCoordinate(TEMPERATURE_DATA_LIST, data,
				MAX_ALLOWED_DISTANCE_MILES);
		if (bestTemperatureData.isPresent()) {
			data.hottestMonthsHigh = String.valueOf(bestTemperatureData.get().hottestMonthAvgHigh);
			data.coldestHigh = String.valueOf(bestTemperatureData.get().coldestMonthAvgHigh);
			data.hottestMonthMinusColdestMonth = String
					.valueOf(bestTemperatureData.get().hottestMonthAvgHigh - bestTemperatureData.get().coldestMonthAvgHigh);
			data.hottestMonthAvgLow = String.valueOf(bestTemperatureData.get().hottestMonthAvgLow);
			data.coldestMonthAvgLow = String.valueOf(bestTemperatureData.get().coldestMonthAvgLow);			
		}
	}
	
	private static int getSummerAvg(String[] arrFoo) {
		// 11, 13, 15
		double summerTotal = Double.valueOf(arrFoo[5 * 2 + 1]) + Double.valueOf(arrFoo[6 * 2 + 1])
				+ Double.valueOf(arrFoo[7 * 2 + 1]);
		double summerAvg = summerTotal / 3;
		int summerAvgInt = Util.getIntFromDouble(summerAvg);
		return summerAvgInt;
	}
	
	private static List<Integer> getRelativeHumidities(String[] arrFoo) {
		List<Integer> humidities = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			humidities.add(Util.getIntFromDouble(Double.valueOf(arrFoo[i*2+1])));
		}
		return humidities;
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
	
	private static String getKey(String cityName, String stateAbbreviation) {
		return cityName.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}
	
    // copied from https://www.wpc.ncep.noaa.gov/html/heatindex.shtml
    public static int getHeatIndex(double temperatureInF, double relativeHumidity) {
    	double hi = 0;
        if(relativeHumidity > 100){
            throw new RuntimeException("humidity too high");
        }
        else if (relativeHumidity < 0) {
            throw new RuntimeException("humidity too low");
        }
        else if (temperatureInF <= 40.0) {
            hi = temperatureInF;
        }
        else {
            double hitemp = 61.0+((temperatureInF-68.0)*1.2)+(relativeHumidity*0.094);
            double fptemp = (temperatureInF);
            double hifinal = 0.5*(fptemp+hitemp);

            if(hifinal > 79.0){
                hi = -42.379+2.04901523*temperatureInF+10.14333127*relativeHumidity-0.22475541*temperatureInF*relativeHumidity-6.83783*(Math.pow(10, -3))*(Math.pow(temperatureInF, 2))-5.481717*(Math.pow(10, -2))*(Math.pow(relativeHumidity, 2))+1.22874*(Math.pow(10, -3))*(Math.pow(temperatureInF, 2))*relativeHumidity+8.5282*(Math.pow(10, -4))*temperatureInF*(Math.pow(relativeHumidity, 2))-1.99*(Math.pow(10, -6))*(Math.pow(temperatureInF, 2))*(Math.pow(relativeHumidity,2));
                if((relativeHumidity <= 13) && (temperatureInF >= 80.0) && (temperatureInF <= 112.0)) {
                    double adj1 = (13.0-relativeHumidity)/4.0;
                    double adj2 = Math.sqrt((17.0-Math.abs(temperatureInF-95.0))/17.0);
                    double adj = adj1 * adj2;
                    hi = hi - adj;
                }
                else if ((relativeHumidity > 85.0) && (temperatureInF >= 80.0) && (temperatureInF <= 87.0)) {
                    double adj1 = (relativeHumidity-85.0)/10.0;
                    double adj2 = (87.0-temperatureInF)/5.0;
                    double adj = adj1 * adj2;
                    hi = hi + adj;
                }
            }
            else{
                hi = hifinal;
            }
        }

        return Util.getIntFromDouble(hi);
    }

}
