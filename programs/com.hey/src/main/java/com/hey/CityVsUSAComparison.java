package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CityVsUSAComparison {
	
	static class InputData {
		String cityName;
		String stateName;
		int population;
		int populationDensity;
		int augustHigh;
		int decemberHigh;
		int augustHighMinusDecemberHigh;
		int annualInchesOfRain;
		int daysOfRain;
		int sunnyDays;
		int annualSnowfall;
		

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	static Foo<Integer> populationFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return  inputData.population;}};	
	static Foo<Integer> populationDensityFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.populationDensity;}};	
	static Foo<Integer> augustHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.augustHigh;}};	
	static Foo<Integer> decemberHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.decemberHigh;}};	
	static Foo<Integer> augustHighMinusDecemberHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.augustHighMinusDecemberHigh;}};	
	static Foo<Integer> annualInchesOfRainFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.annualInchesOfRain;}};	
	static Foo<Integer> daysOfRainFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.daysOfRain;}};	
	static Foo<Integer> sunnyDaysFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.sunnyDays;}};	
	static Foo<Integer> annualSnowfallFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.annualSnowfall;}};	

	static class AveragesAndMedians {
		int populationAverage;
		int populationMedian;
		int populationDensityAverage;
		int populationDensityMedian;
		int augustHighAverage;
		int augustHighMedian;
		int decemberHighAverage;
		int decemberHighMedian;
		int augustHighMinusDecemberHighAverage;
		int augustHighMinusDecemberHighMedian;
		int annualInchesOfRainAverage;
		int annualInchesOfRainMedian;
		int daysOfRainAverage;
		int daysOfRainMedian;
		int sunnyDaysAverage;
		int sunnyDaysMedian;
		int annualSnowfallAverage;
		int annualSnowfallMedian;
	}

	static class OutputData {
		public String key;
		public Metric populationMetric = new Metric();
		public Metric populationDensityMetric = new Metric();
		public Metric augustHighMetric = new Metric();
		public Metric decemberHighMetric = new Metric();
		public Metric augustHighMinusDecemberHighMetric = new Metric();
		public Metric annualInchesOfRainMetric = new Metric();
		public Metric daysOfRainMetric = new Metric();
		public Metric sunnyDaysMetric = new Metric();
		public Metric annualSnowfallMetric = new Metric();

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	private static List<InputData> readInput() {
		try {
			File myObj = new File("citydatainput.csv");
			Scanner myReader = new Scanner(myObj);
			myReader.nextLine();
			int idx = 0;
			List<InputData> list = new ArrayList<InputData>();
			while (myReader.hasNextLine()) {
				idx++;
				InputData inputData = new InputData();
				String line = myReader.nextLine();
				String[] arr = line.split(",");
				inputData.cityName = arr[0].toLowerCase();
				inputData.stateName = arr[1].toLowerCase();
				inputData.population = Integer.valueOf(arr[3]);
				inputData.populationDensity = Integer.valueOf(arr[4]);
				inputData.augustHigh = Integer.valueOf(arr[5]);
				inputData.decemberHigh = Integer.valueOf(arr[6]);
				inputData.augustHighMinusDecemberHigh = Integer.valueOf(arr[7]);
				inputData.annualInchesOfRain = Integer.valueOf(arr[8]);
				inputData.daysOfRain = Integer.valueOf(arr[9]);
				inputData.sunnyDays = Integer.valueOf(arr[10]);
				inputData.annualSnowfall = Integer.valueOf(arr[11]);
				list.add(inputData);
				if (idx > 10) {
					// break;
				}
			}
			myReader.close();
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("issue in readInput");
		}

	}
	
	private static AveragesAndMedians getAveragesAndMedians(List<InputData> inputDataList) {
		AveragesAndMedians obj = new AveragesAndMedians();
		List<Integer> populations = populationFoo.getGenericList(inputDataList);	
		obj.populationAverage = (int) findMean(populations);
		obj.populationMedian = (int) findMedian(populations);
		List<Integer> populationDensities = populationDensityFoo.getGenericList(inputDataList);
		obj.populationDensityAverage = (int) findMean(populationDensities);
		obj.populationDensityMedian = (int) findMedian(populationDensities);
		List<Integer> augustHighs = augustHighFoo.getGenericList(inputDataList);	
		obj.augustHighAverage = (int) findMean(augustHighs);
		obj.augustHighMedian = (int) findMedian(augustHighs);
		List<Integer> decemberHighs = decemberHighFoo.getGenericList(inputDataList);	
		obj.decemberHighAverage = (int) findMean(decemberHighs);
		obj.decemberHighMedian = (int) findMedian(decemberHighs);
		List<Integer> augHighMinusDecHighs = augustHighMinusDecemberHighFoo.getGenericList(inputDataList);	
		obj.augustHighMinusDecemberHighAverage = (int) findMean(augHighMinusDecHighs);
		obj.augustHighMinusDecemberHighMedian = (int) findMedian(augHighMinusDecHighs);
		List<Integer> inchesOfRainList = annualInchesOfRainFoo.getGenericList(inputDataList);
		obj.annualInchesOfRainAverage = (int) findMean(inchesOfRainList);
		obj.annualInchesOfRainMedian = (int) findMedian(inchesOfRainList);
		
		List<Integer> daysOfRainList = daysOfRainFoo.getGenericList(inputDataList);
		obj.daysOfRainAverage = (int) findMean(daysOfRainList);
		obj.daysOfRainMedian = (int) findMedian(daysOfRainList);
		
		List<Integer> sunnyDaysList = annualInchesOfRainFoo.getGenericList(inputDataList);
		obj.sunnyDaysAverage = (int) findMean(sunnyDaysList);
		obj.sunnyDaysMedian = (int) findMedian(sunnyDaysList);
		
		List<Integer> annualSnowfallList = annualInchesOfRainFoo.getGenericList(inputDataList);
		obj.annualSnowfallAverage = (int) findMean(annualSnowfallList);
		obj.annualSnowfallMedian = (int) findMedian(annualSnowfallList);
		return obj;
	}

	private static void writeAveragesAndMedians(AveragesAndMedians averagesAndMedians) {
		StringBuilder sb = new StringBuilder();
		sb.append(getString("populationAverage", averagesAndMedians.populationAverage));
		sb.append(getString("populationAverage", averagesAndMedians.populationAverage));
		sb.append(getString("populationMedian", averagesAndMedians.populationMedian));
		sb.append(getString("populationDensityAverage", averagesAndMedians.populationDensityAverage));
		sb.append(getString("populationDensityMedian", averagesAndMedians.populationDensityMedian));
		sb.append(getString("augustHighAverage", averagesAndMedians.augustHighAverage));
		sb.append(getString("augustHighMedian", averagesAndMedians.augustHighMedian));
		sb.append(getString("decemberHighAverage", averagesAndMedians.decemberHighAverage));
		sb.append(getString("decemberHighMedian", averagesAndMedians.decemberHighMedian));
		sb.append(getString("augustHighMinusDecemberHighAverage", averagesAndMedians.augustHighMinusDecemberHighAverage));
		sb.append(getString("augustHighMinusDecemberHighMedian", averagesAndMedians.augustHighMinusDecemberHighMedian));

		sb.append(getString("annualInchesOfRainAverage", averagesAndMedians.annualInchesOfRainAverage));
		sb.append(getString("annualInchesOfRainMedian", averagesAndMedians.annualInchesOfRainMedian));
		sb.append(getString("daysOfRainAverage", averagesAndMedians.daysOfRainAverage));
		sb.append(getString("daysOfRainMedian", averagesAndMedians.daysOfRainMedian));
		sb.append(getString("sunnyDaysAverage", averagesAndMedians.sunnyDaysAverage));
		sb.append(getString("sunnyDaysMedian", averagesAndMedians.sunnyDaysMedian));
		sb.append(getString("annualSnowfallAverage", averagesAndMedians.annualSnowfallAverage));
		sb.append(getString("annualSnowfallMedian", averagesAndMedians.annualSnowfallMedian));
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\averagesAndMedians.js", sb);
	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		for (InputData inputData : inputDataList) {
			OutputData outputData = new OutputData();
			outputData.key = inputData.cityName + "," + inputData.stateName;
			outputData.populationMetric = populationFoo.getMetric(inputData.population, inputDataList);
			outputData.populationDensityMetric = populationDensityFoo.getMetric(inputData.populationDensity, inputDataList);
			outputData.augustHighMetric = augustHighFoo.getMetric(inputData.augustHigh, inputDataList);
			outputData.decemberHighMetric = decemberHighFoo.getMetric(inputData.decemberHigh, inputDataList);
			outputData.augustHighMinusDecemberHighMetric = augustHighMinusDecemberHighFoo.getMetric(inputData.augustHighMinusDecemberHigh, inputDataList);
			outputData.annualInchesOfRainMetric = annualInchesOfRainFoo.getMetric(inputData.annualInchesOfRain, inputDataList);
			outputData.daysOfRainMetric = daysOfRainFoo.getMetric(inputData.daysOfRain, inputDataList);
			outputData.sunnyDaysMetric = sunnyDaysFoo.getMetric(inputData.sunnyDays, inputDataList);
			outputData.annualSnowfallMetric = annualSnowfallFoo.getMetric(inputData.annualSnowfall, inputDataList);
		    
		    // maybe have a create metric thing on the Foo class? so it's a one liner and i don't have to instantitae the map. maybe it internally has a singleton map
		    
			outputDataList.add(outputData);
		}
		return outputDataList;
	}

	private static void writeOutput(List<OutputData> outputDataList) {
		StringBuilder sb = new StringBuilder();
		sb.append("var myMap = new Map();\n");
		sb.append("var populationMetric;\n");
		sb.append("var populationDensityMetric;\n");
		sb.append("var augustHighMetric;\n");
		sb.append("var decemberHighMetric;\n");
		sb.append("var augustHighMinusDecemberHighMetric;\n");
		
		sb.append("var annualInchesOfRainMetric;\n");
		sb.append("var daysOfRainMetric;\n");
		sb.append("var sunnyDaysMetric;\n");
		sb.append("var annualSnowfallMetric;\n");
		sb.append("var cityData;\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for (OutputData outputData : outputDataList) {
			appendMetric(sb, df, outputData.populationMetric, "populationMetric");
			appendMetric(sb, df, outputData.populationDensityMetric, "populationDensityMetric");
			appendMetric(sb, df, outputData.augustHighMetric, "augustHighMetric");
			appendMetric(sb, df, outputData.decemberHighMetric, "decemberHighMetric");
			appendMetric(sb, df, outputData.augustHighMinusDecemberHighMetric, "augustHighMinusDecemberHighMetric");
			
			
			appendMetric(sb, df, outputData.annualInchesOfRainMetric, "annualInchesOfRainMetric");
			appendMetric(sb, df, outputData.daysOfRainMetric, "daysOfRainMetric");
			appendMetric(sb, df, outputData.sunnyDaysMetric, "sunnyDaysMetric");
			appendMetric(sb, df, outputData.annualSnowfallMetric, "annualSnowfallMetric");
			
			
			
			sb.append("cityData = new CityData(populationMetric,populationDensityMetric,augustHighMetric,decemberHighMetric,augustHighMinusDecemberHighMetric,annualInchesOfRainMetric,daysOfRainMetric,sunnyDaysMetric,annualSnowfallMetric);\n").append("myMap.set(\"")
					.append(outputData.key).append("\", cityData);\n");
		}
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\map.js", sb);

	}

	// could create map of metric to percentile
	
	// no chages TODO
	
	static class Metric {
		float value;
		float cityPercentile;
		float personPercentile;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	public static void main(String[] args) {
		List<InputData> inputDataList = readInput();
		AveragesAndMedians averagesAndMedians = getAveragesAndMedians(inputDataList);
		writeAveragesAndMedians(averagesAndMedians);
		List<OutputData> outputDataList = generateOutputData(inputDataList);
		writeOutput(outputDataList);

	}

	private static String getString(String varName, int varValue) {
		return "var " + varName + " = " + varValue + ";\n";
	}
	
	public static <T> float findMean(List<Integer> a) {
		int n = a.size();
		int sum = 0;
		for (int i = 0; i < n; i++)
			sum += a.get(i);

		return (float) sum / (float) n;
	}

	public static float findMedian(List<Integer> a) {
		int n = a.size();
		// First we sort the array
		Collections.sort(a);

		// check for even case
		if (n % 2 != 0)
			return a.get(n / 2);

		return (float) ((a.get((n - 1) / 2) + a.get(n / 2)) / 2.0);
	}
	
	abstract static class Foo<T extends Comparable<T>> {
		
		Map<T, Float> mapOfValueToPersonPercentile = new HashMap<T, Float>();
		Map<T, Float> mapOfValueToCityPercentile = new HashMap<T, Float>();
		
		public Metric getMetric(int value, List<InputData> inputDataList) {
			Metric metric = new Metric();
			metric.value = value;
			if (mapOfValueToPersonPercentile.size() == 0) {
				mapOfValueToPersonPercentile = createPersonPercentileMap2(inputDataList);
				mapOfValueToCityPercentile = createCityPercentileMap2(inputDataList);
			}
			metric.personPercentile = mapOfValueToPersonPercentile.get(value);
			metric.cityPercentile = mapOfValueToCityPercentile.get(value);
			return metric;
		}

		abstract T getData(InputData inputData);

		List<T> getGenericList(List<InputData> inputDataList) {
			List<T> list = new ArrayList<T>();
			for (InputData inputData : inputDataList) {
				list.add(getData(inputData));
			}
			Collections.sort(list);
			return list;
		}
		
		private Map<T, Float> createPersonPercentileMap2(List<InputData> inputDataList){
			List<Meh<T>> values = new ArrayList<Meh<T>>();
			Map<T, Float> map = new HashMap<T, Float>();
			for (InputData inputData : inputDataList) {
				Meh<T> meh = new Meh<T>();
				meh.value = getData(inputData);
				meh.population = inputData.population;
				values.add(meh);
			}
			Collections.sort(values);
			float totalPeopleWeHaveMoreThan = 0;
			float totalPopulation = 0;
			for (InputData inputData : inputDataList) {
				totalPopulation += inputData.population;
			}
			for (int i = 0; i < values.size(); i++) {
				T val = values.get(i).value;
				if (!map.containsKey(val)) {
					float percentile = totalPeopleWeHaveMoreThan / totalPopulation;
					map.put(val, percentile);
				}
				totalPeopleWeHaveMoreThan += values.get(i).population;
			}
			return map;
		}
		
		private Map<T, Float> createCityPercentileMap2(List<InputData> inputDataList){
			List<T> values = new ArrayList<T>();
			Map<T, Float> map = new HashMap<T, Float>();
			for (InputData inputData : inputDataList) {
				values.add(getData(inputData));
			}
			Collections.sort(values);
			for (int i = 0; i < values.size(); i++) {
				T val = values.get(i);
				if (!map.containsKey(val)) {
					float percentile = ((float) (i + 1)) / ((float) (values.size() - 1));
					map.put(val, percentile);
				}
			}
			return map;
		}
	}
	
	private static void writeOutput(String filePath, StringBuilder sb) {
		try {
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(sb.toString());
			myWriter.close();
			System.out.println("wrote to file " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	static class Meh<T extends Comparable<T>> implements Comparable<Meh<T>> {
		T value;
		int population;
		public int compareTo(Meh<T> o1) {
			return value.compareTo(o1.value);
		}

	}
	
	private static void appendMetric(StringBuilder sb, DecimalFormat df, Metric metric, String varName) {
		sb.append(varName).append(" = new Metric(").append((int) metric.value).append(",")
				.append(df.format(100 * metric.cityPercentile)).append(", ")
				.append(df.format(100 * metric.personPercentile)).append(");\n");
	}
	
/*
	private static float getCityPercentile(int value, List<Integer> values) {
		for (int i = 0; i < values.size(); i++) {
			int val = values.get(i);
			if (value <= val) {
				return ((float) (i + 1)) / ((float) (values.size() - 1));
			}
		}
		throw new RuntimeException("hi");
	}
*/
}
