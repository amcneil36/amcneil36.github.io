package com.hey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CityVsUSAComparison {
	
	static class InputData {
		String cityName;
		String stateName;
		int population;
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	static class OutputData{
		String name;
		int population;
		float avg;
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}

	public static void main(String[] args) {
		List<InputData> inputDataList = readInput();
		List<OutputData> outputDataList = generateOutputData(inputDataList);
		writeOutput(outputDataList);

	}
	
	private static void writeOutput(List<OutputData> outputDataList) {
		for (OutputData outputData : outputDataList) {
			System.out.println(outputData.toString());
		}
		
	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		for (InputData inputData : inputDataList) {
			OutputData outputData = generateOutputDataForCity(inputData, inputDataList);
			outputDataList.add(outputData);
		}
		return outputDataList;
	}

	private static OutputData generateOutputDataForCity(InputData inputData, List<InputData> inputDataList) {
		OutputData outputData = new OutputData();
		outputData.name = inputData.cityName.toLowerCase() + "," + inputData.stateName.toLowerCase();
		outputData.population = inputData.population;
		outputData.avg = getPopulationAverage(inputData.population, inputDataList);
		return outputData;
	}

	private static float getPopulationAverage(int population, List<InputData> inputDataList) {
		// TODO Auto-generated method stub
		return 0;
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
					break;
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
