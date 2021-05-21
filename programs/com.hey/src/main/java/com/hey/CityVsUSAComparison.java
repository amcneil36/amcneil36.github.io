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
	}

	static class InputData {
		String cityName;
		String stateName;
		int population;

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

	private static void printAveragesAndMedians(AveragesAndMedians averagesAndMedians) {
		System.out.println("var populationAverage = " + averagesAndMedians.populationAverage + ";");
		System.out.println("var populationMedian = " + averagesAndMedians.populationMedian + ";");
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
		return obj;
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
		
			String outputTitle = "CityVsUSAComparison_";
			outputTitle += System.currentTimeMillis() + ".txt";
			FileWriter myWriter = new FileWriter(outputTitle);
			myWriter.write(sb.toString());
			myWriter.close();
			System.out.println("wrote to file " + outputTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeOutput(List<OutputData> outputDataList) {
		StringBuilder sb = new StringBuilder();
		sb.append("var myMap = new Map();\n");
		sb.append("var populationMetric;\n");
		sb.append("var cityData;\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for (OutputData outputData : outputDataList) {
			//System.out.println(outputData.populationMetric.cityPercentile);
			//System.out.println(outputData.populationMetric.value);
			sb.append("populationMetric = new Metric(").append((int)outputData.populationMetric.value).append(",")
					.append(df.format(100*outputData.populationMetric.cityPercentile))
					.append(", ")
					.append(df.format(100*outputData.populationMetric.personPercentile)).append(");\n")
					.append("cityData = new CityData(populationMetric);\n").append("myMap.set(\"")
					.append(outputData.key).append("\", cityData);\n");
		}
		writeOutput(sb);

	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		float totalPopulation = getTotalPopulation(inputDataList);
		List<Float> populations = createSortedPopulationsList(inputDataList);
		for (InputData inputData : inputDataList) {
			OutputData outputData = new OutputData();
			outputData.populationMetric.value = inputData.population;
			outputData.populationMetric.cityPercentile = getCityPercentile(inputData.population, populations);
			outputData.populationMetric.personPercentile = getPersonPercentile(inputData.population, populations,
					totalPopulation);
			outputData.key = inputData.cityName + "," + inputData.stateName;
			outputDataList.add(outputData);
		}
		return outputDataList;
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
				return ((float)(i + 1)) / ((float)(populations.size()-1));
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
				list.add(inputData);
				if (idx > 10) {
					//break;
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
