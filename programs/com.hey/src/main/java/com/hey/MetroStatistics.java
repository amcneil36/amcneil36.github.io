package main.java.com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	static class Stats {
		WeightedAverage peoplePerSqMi = new WeightedAverage();

	}

	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfMetroNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (Integer.valueOf(data.metroPopulation) > 999999 && !data.metro.contains("None")) {
				if (!mapOfMetroNameToStats.containsKey(data.metro)) {
					Stats stats = new Stats();
					mapOfMetroNameToStats.put(data.metro, stats);
				}
				Stats stats = mapOfMetroNameToStats.get(data.metro);
				stats.peoplePerSqMi.addCity(data.population, data.populationDensity);
			}
		}
		Set<String> keys = mapOfMetroNameToStats.keySet();
		for (String key : keys) {
			WeightedAverage wa = mapOfMetroNameToStats.get(key).peoplePerSqMi;
			System.out.println(key + " population density: " + wa.getWeightedAverage());
		}

	}

}
