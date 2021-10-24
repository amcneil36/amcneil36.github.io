package main.java.com.hey;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats.AndrewStringWriter;

public class MetroStatistics {

	static class WeightedAverage {
		private double totalSummedValue = 0;
		private double totalPopulation = 0;

		public void addCity(String population, String value) {
			if (value.contains("N/A")) {
				return;
			}
			double val = Double.valueOf(value);
			int pop = Integer.valueOf(population);
			totalSummedValue += val * pop;
			totalPopulation += pop;
		}

		public int getWeightedAverage() {
			return (int) (totalSummedValue / totalPopulation);
		}
	}

	static class Stats implements Comparable<Stats>{
		String metroName = "";
		int metroPopulation = 0;
		WeightedAverage peoplePerSqMi = new WeightedAverage();
		@Override
		public int compareTo(Stats arg0) {
			if (metroPopulation < arg0.metroPopulation) {
				return -1;
			}
			else if (metroPopulation == arg0.metroPopulation) {
				return 0;
			}
			return 1;
		}

	}

	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfMetroNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (Integer.valueOf(data.metroPopulation) > 999999 && !data.metro.contains("None")) {
				if (!mapOfMetroNameToStats.containsKey(data.metro)) {
					Stats stats = new Stats();
					stats.metroName = data.metro;
					stats.metroPopulation = Integer.valueOf(data.metroPopulation);
					mapOfMetroNameToStats.put(data.metro, stats);
				}
				Stats stats = mapOfMetroNameToStats.get(data.metro);
				stats.peoplePerSqMi.addCity(data.population, data.populationDensity);
			}
		}
		Set<String> keys = mapOfMetroNameToStats.keySet();
		List<Stats> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(mapOfMetroNameToStats.get(key));
		}
		Collections.sort(statsList, Collections.reverseOrder());
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\MetroSummary.csv";
		String startSt = "Metro Name,Metro Population,People Per Sq Mi";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(startSt);
		for (Stats stat : statsList) {
			sb.appendWithComma(stat.metroName).appendWithComma(stat.metroPopulation).appendLastItem(stat.peoplePerSqMi.getWeightedAverage());
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}

}
