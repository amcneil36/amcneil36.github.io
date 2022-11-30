package main.java.com.hey.summaries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;

public abstract class GenericStatsSuper {
	public static class Stats2 implements Comparable<Stats2> {
		public String metroName = "";
		public int metroPopulation = 0;
		public Map<String, Integer> mapOfStateToPopulation = new HashMap<>();
		public Map<String, Integer> mapOfTimeZoneToPopulation = new HashMap<>();

		@Override
		public int compareTo(Stats2 arg0) {
			return compareTo2(this, arg0);
		}

		public String getPrimaryState() {
			Set<String> keys = mapOfStateToPopulation.keySet();

			String mostPopulatedState = "";
			int maxPopulation = -1;
			for (String key : keys) {
				int currentPop = mapOfStateToPopulation.get(key);
				if (currentPop > maxPopulation) {
					maxPopulation = currentPop;
					mostPopulatedState = key;
				}
			}
			if (maxPopulation == -1) {
				throw new RuntimeException("didn't find most populated state");
			}
			return mostPopulatedState;
		}

		public String getPrimaryTimeZone() {
			Set<String> keys = mapOfTimeZoneToPopulation.keySet();

			String mostPopulatedTimeZone = "";
			int maxPopulation = -1;
			for (String key : keys) {
				int currentPop = mapOfTimeZoneToPopulation.get(key);
				if (currentPop > maxPopulation) {
					maxPopulation = currentPop;
					mostPopulatedTimeZone = key;
				}
			}
			if (maxPopulation == -1) {
				throw new RuntimeException("didn't find most populated time zone");
			}
			return mostPopulatedTimeZone;
		}

		public void addDataToMapOfStateToPopulation(CityStats.Data data) {
			String state = data.stateName;
			if (!mapOfStateToPopulation.containsKey(state)) {
				mapOfStateToPopulation.put(state, 0);
			}
			int pop = mapOfStateToPopulation.get(state);
			pop += Integer.valueOf(data.population);
			mapOfStateToPopulation.put(state, pop);
		}

		public void addDataToMapOfTimeZoneToPopulation(CityStats.Data data) {
			String timeZone = data.timeZone;
			if (!mapOfTimeZoneToPopulation.containsKey(timeZone)) {
				mapOfTimeZoneToPopulation.put(timeZone, 0);
			}
			int pop = mapOfTimeZoneToPopulation.get(timeZone);
			pop += Integer.valueOf(data.population);
			mapOfTimeZoneToPopulation.put(timeZone, pop);
		}

	}

	public static class WeightedAverage {
		private double totalSummedValue = 0;
		private double totalPopulation = 0;

		public void addCity(CityStats.Data data, String value) {
			if (value.contains("N/A")) {
				return;
			}
			double val = Double.valueOf(value.replace("%", "").replace("$", ""));
			int pop = Integer.valueOf(data.population);
			totalSummedValue += val * pop;
			totalPopulation += pop;
		}

		public String getWeightedAverage() {
			if (totalPopulation == 0) {
				return "N/A";
			}
			return String.valueOf((int) (totalSummedValue / totalPopulation));
		}

		@Override
		public String toString() {
			return getWeightedAverage();
		}
	}

	public static class WeightedAveragePercent extends WeightedAverage {
		@Override
		public String toString() {
			return getWeightedAverage() + "%";
		}
	}

	public static class WeightedAverageDollar extends WeightedAverage {
		@Override
		public String toString() {
			String wa = getWeightedAverage();
			if (!wa.equals("N/A")) {
				wa = "$" + wa;
			}
			return wa;
		}
	}

	static int compareTo2(Stats2 self, Stats2 arg0) {
		if (self.metroPopulation < arg0.metroPopulation) {
			return -1;
		} else if (self.metroPopulation == arg0.metroPopulation) {
			return 0;
		}
		return 1;
	}
}
