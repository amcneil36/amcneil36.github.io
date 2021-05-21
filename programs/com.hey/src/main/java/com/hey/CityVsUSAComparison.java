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

import com.hey.ZipCode.AndrewStringBuilder;
import com.hey.ZipCode.DataObject2;

public class CityVsUSAComparison {

	static class AveragesAndMedians {
		int populationAverage;
		int populationMedian;
		int populationDensityAverage;
		int populationDensityMedian;
	}

	static class InputData {
		String cityName;
		String stateName;
		int population;
		int populationDensity;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}

	static class Metric {
		float value;
		float cityPercentile;
		float personPercentile;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}

	static class OutputData {
		public String key;
		public Metric populationMetric = new Metric();
		public Metric populationDensityMetric = new Metric();

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}

	public static void main(String[] args) {
		List<InputData> inputDataList = readInput();
//		 AveragesAndMedians averagesAndMedians = getAveragesAndMedians(inputDataList);
//		 printAveragesAndMedians(averagesAndMedians);
		List<OutputData> outputDataList = generateOutputData(inputDataList);
		writeOutput(outputDataList);

	}

	private static void printString(String varName, int varValue) {
		System.out.println("var " + varName + " = " + varValue + ";");
	}

	private static void printAveragesAndMedians(AveragesAndMedians averagesAndMedians) {
		printString("populationAverage", averagesAndMedians.populationAverage);
		printString("populationMedian", averagesAndMedians.populationMedian);
		printString("populationDensityAverage", averagesAndMedians.populationDensityAverage);
		printString("populationDensityMedian", averagesAndMedians.populationDensityMedian);
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

	private static AveragesAndMedians getAveragesAndMedians(List<InputData> inputDataList) {
		AveragesAndMedians obj = new AveragesAndMedians();
		List<Float> populations = createSortedPopulationsList(inputDataList);
		obj.populationAverage = (int) findMean(populations);
		obj.populationMedian = (int) findMedian(populations);

		List<Float> populationsDensities = createSortedPopulationDensitiesList(inputDataList);
		obj.populationDensityAverage = (int) findMean(populationsDensities);
		obj.populationDensityMedian = (int) findMedian(populationsDensities);
		return obj;
	}

	private static List<Float> createSortedPopulationDensitiesList(List<InputData> inputDataList) {
		List<Float> populationsDensities = new ArrayList<Float>();
		for (InputData inputData : inputDataList) {
			populationsDensities.add((float) inputData.populationDensity);
		}
		Collections.sort(populationsDensities);
		return populationsDensities;
	}

	private static List<Float> createSortedPopulationsList(List<InputData> inputDataList) {
		List<Float> populations = new ArrayList<Float>();
		for (InputData inputData : inputDataList) {
			populations.add((float) inputData.population);
		}
		Collections.sort(populations);
		return populations;
	}

	private static void writeOutput(StringBuilder sb) {
		try {

			String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\map.js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(sb.toString());
			myWriter.close();
			System.out.println("wrote to file " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeOutput(List<OutputData> outputDataList) {
		StringBuilder sb = new StringBuilder();
		sb.append("var myMap = new Map();\n");
		sb.append("var populationMetric;\n");
		sb.append("var populationDensityMetric;\n");
		sb.append("var cityData;\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for (OutputData outputData : outputDataList) {
			appendMetric(sb, df, outputData.populationMetric, "populationMetric");
			appendMetric(sb, df, outputData.populationDensityMetric, "populationDensityMetric");
			sb.append("cityData = new CityData(populationMetric,populationDensityMetric);\n").append("myMap.set(\"").append(outputData.key)
					.append("\", cityData);\n");
		}
		writeOutput(sb);

	}

	private static void appendMetric(StringBuilder sb, DecimalFormat df, Metric metric, String varName) {
		sb.append(varName).append(" = new Metric(").append((int) metric.value).append(",")
				.append(df.format(100 * metric.cityPercentile)).append(", ")
				.append(df.format(100 * metric.personPercentile)).append(");\n");
	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		float totalPopulation = getTotalPopulation(inputDataList);
		List<Float> populations = createSortedPopulationsList(inputDataList);
		List<Float> populationDensities = createSortedPopulationDensitiesList(inputDataList);
		for (InputData inputData : inputDataList) {
			OutputData outputData = new OutputData();
			outputData.key = inputData.cityName + "," + inputData.stateName;
			outputData.populationMetric.value = inputData.population;
			outputData.populationMetric.cityPercentile = getCityPercentile(inputData.population, populations);
			outputData.populationMetric.personPercentile = getPersonPercentile(inputData.population, populations,
					totalPopulation);

			outputData.populationDensityMetric.value = inputData.populationDensity;
			outputData.populationDensityMetric.cityPercentile = getCityPercentile(inputData.populationDensity,
					populationDensities);
			outputData.populationDensityMetric.personPercentile = getPopulationDensityPersonPercentile(inputData.populationDensity,
					inputDataList); // wrong

			outputDataList.add(outputData);
		}
		return outputDataList;
	}
	
	private static float getPopulationDensityPersonPercentile(int populationDensity, List<InputData> inputDataList) {
		float totalPeopleWeHaveMoreThan = 0;
		float totalPopulation = 0;
		for (InputData inputData : inputDataList) {
			if (populationDensity > inputData.populationDensity) {
				totalPeopleWeHaveMoreThan += inputData.population;
			}
			totalPopulation += inputData.population;
		}
		return totalPeopleWeHaveMoreThan/totalPopulation;
	}

	private static float getPersonPercentile(int population, List<Float> populations, float totalPopulation) {
		float totalPeopleWeHaveMoreThan = 0;
		for (int i = 0; i < populations.size(); i++) {
			Float val = populations.get(i);
			if (population <= val) {
				return totalPeopleWeHaveMoreThan / totalPopulation;
			}
			totalPeopleWeHaveMoreThan += val;
		}
		throw new RuntimeException("hi");
	}

	private static float getTotalPopulation(List<InputData> inputDataList) {
		float totalPopulation = 0;
		for (InputData inputData : inputDataList) {
			totalPopulation += inputData.population;
		}
		return totalPopulation;
	}

	private static float getCityPercentile(int population, List<Float> populations) {
		for (int i = 0; i < populations.size(); i++) {
			Float val = populations.get(i);
			if (population <= val) {
				return ((float) (i + 1)) / ((float) (populations.size() - 1));
			}
		}
		throw new RuntimeException("hi");
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

}
