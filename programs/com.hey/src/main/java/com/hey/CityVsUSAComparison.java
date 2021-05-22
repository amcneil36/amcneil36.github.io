package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CityVsUSAComparison {
	
	static class InputData {
		String cityName;
		String stateName;
		int population;
		int populationDensity;
		int augustHigh;
		int decemberHigh;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	static Foo<Float> populationSorter = new Foo<Float>() { @Override Float getData(InputData inputData) { return (float) inputData.population;}};	
	static Foo<Float> populationDensitySorter = new Foo<Float>() { @Override Float getData(InputData inputData) { return (float) inputData.populationDensity;}};	
	static Foo<Float> augustHighSorter = new Foo<Float>() { @Override Float getData(InputData inputData) { return (float) inputData.augustHigh;}};	
	static Foo<Float> decemberHighSorter = new Foo<Float>() { @Override Float getData(InputData inputData) { return (float) inputData.decemberHigh;}};	

	static class AveragesAndMedians {
		int populationAverage;
		int populationMedian;
		int populationDensityAverage;
		int populationDensityMedian;
		int augustHighAverage;
		int augustHighMedian;
		int decemberHighAverage;
		int decemberHighMedian;
	}

	static class OutputData {
		public String key;
		public Metric populationMetric = new Metric();
		public Metric populationDensityMetric = new Metric();
		public Metric augustHighMetric = new Metric();
		public Metric decemberHighMetric = new Metric();

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
		List<Float> populations = populationSorter.getGenericList(inputDataList);	
		obj.populationAverage = (int) findMean(populations);
		obj.populationMedian = (int) findMedian(populations);
		List<Float> populationDensities = populationDensitySorter.getGenericList(inputDataList);
		obj.populationDensityAverage = (int) findMean(populationDensities);
		obj.populationDensityMedian = (int) findMedian(populationDensities);
		List<Float> augustHighs = augustHighSorter.getGenericList(inputDataList);	
		obj.augustHighAverage = (int) findMean(augustHighs);
		obj.augustHighMedian = (int) findMedian(augustHighs);
		List<Float> decemberHighs = decemberHighSorter.getGenericList(inputDataList);	
		obj.decemberHighAverage = (int) findMean(decemberHighs);
		obj.decemberHighMedian = (int) findMedian(decemberHighs);
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
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\averagesAndMedians.js", sb);
	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		List<Float> populations = populationSorter.getGenericList(inputDataList);	
		List<Float> populationDensities = populationDensitySorter.getGenericList(inputDataList);
		List<Float> augustHighs = augustHighSorter.getGenericList(inputDataList);	
		List<Float> decemberHighs = decemberHighSorter.getGenericList(inputDataList);	
		Bar<Integer> populationBar = new Bar<Integer>() { @Override Integer getData2(InputData inputData) {return inputData.population;}}; 
		Bar<Integer> populationDensityBar = new Bar<Integer>() {@Override Integer getData2(InputData inputData) {return inputData.populationDensity;}}; 
		Bar<Integer> augustHighBar = new Bar<Integer>() {@Override Integer getData2(InputData inputData) {return inputData.augustHigh;}}; 
		Bar<Integer> decemberHighBar = new Bar<Integer>() {@Override Integer getData2(InputData inputData) {return inputData.decemberHigh;}}; 
		for (InputData inputData : inputDataList) {
			OutputData outputData = new OutputData();
			outputData.key = inputData.cityName + "," + inputData.stateName;
			
			outputData.populationMetric.value = inputData.population;
			outputData.populationMetric.cityPercentile = getCityPercentile(inputData.population, populations);
		    outputData.populationMetric.personPercentile = populationBar.getPersonPercentile(inputData.population, inputDataList);
			
		    outputData.populationDensityMetric.personPercentile = populationDensityBar.getPersonPercentile(inputData.populationDensity, inputDataList);
			outputData.populationDensityMetric.value = inputData.populationDensity;
			outputData.populationDensityMetric.cityPercentile = getCityPercentile(inputData.populationDensity, populationDensities);
			
			outputData.augustHighMetric.value = inputData.augustHigh;
			outputData.augustHighMetric.cityPercentile = getCityPercentile(inputData.augustHigh, augustHighs);
		    outputData.augustHighMetric.personPercentile = augustHighBar.getPersonPercentile(inputData.augustHigh, inputDataList);
		    
			outputData.decemberHighMetric.value = inputData.decemberHigh;
			outputData.decemberHighMetric.cityPercentile = getCityPercentile(inputData.decemberHigh, decemberHighs);
		    outputData.decemberHighMetric.personPercentile = decemberHighBar.getPersonPercentile(inputData.decemberHigh, inputDataList);

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
		sb.append("var cityData;\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for (OutputData outputData : outputDataList) {
			appendMetric(sb, df, outputData.populationMetric, "populationMetric");
			appendMetric(sb, df, outputData.populationDensityMetric, "populationDensityMetric");
			appendMetric(sb, df, outputData.augustHighMetric, "augustHighMetric");
			appendMetric(sb, df, outputData.decemberHighMetric, "decemberHighMetric");
			sb.append("cityData = new CityData(populationMetric,populationDensityMetric,augustHighMetric,decemberHighMetric);\n").append("myMap.set(\"")
					.append(outputData.key).append("\", cityData);\n");
		}
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\map.js", sb);

	}

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
	
	public static float findMean(List<Float> a) {
		int n = a.size();
		int sum = 0;
		for (int i = 0; i < n; i++)
			sum += a.get(i);

		return (float) sum / (float) n;
	}

	public static float findMedian(List<Float> a) {
		int n = a.size();
		// First we sort the array
		Collections.sort(a);

		// check for even case
		if (n % 2 != 0)
			return a.get(n / 2);

		return (float) ((a.get((n - 1) / 2) + a.get(n / 2)) / 2.0);
	}
	
	abstract static class Foo<T extends Comparable<T>> {

		abstract T getData(InputData inputData);

		List<T> getGenericList(List<InputData> inputDataList) {
			List<T> list = new ArrayList<T>();
			for (InputData inputData : inputDataList) {
				list.add(getData(inputData));
			}
			Collections.sort(list);
			return list;
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
	
	private static void appendMetric(StringBuilder sb, DecimalFormat df, Metric metric, String varName) {
		sb.append(varName).append(" = new Metric(").append((int) metric.value).append(",")
				.append(df.format(100 * metric.cityPercentile)).append(", ")
				.append(df.format(100 * metric.personPercentile)).append(");\n");
	}
	
	abstract static class Bar<T extends Comparable<T>> {

		abstract T getData2(InputData inputData);

		float getPersonPercentile(T value, List<InputData> inputDataList) {
			float totalPeopleWeHaveMoreThan = 0;
			float totalPopulation = 0;
			for (InputData inputData : inputDataList) {
				if (value.compareTo(getData2(inputData)) > 0) {
					totalPeopleWeHaveMoreThan += inputData.population;
				}
				totalPopulation += inputData.population;
			}
			return totalPeopleWeHaveMoreThan / totalPopulation;
		}
	}

	private static float getCityPercentile(int value, List<Float> values) {
		for (int i = 0; i < values.size(); i++) {
			Float val = values.get(i);
			if (value <= val) {
				return ((float) (i + 1)) / ((float) (values.size() - 1));
			}
		}
		throw new RuntimeException("hi");
	}

}
