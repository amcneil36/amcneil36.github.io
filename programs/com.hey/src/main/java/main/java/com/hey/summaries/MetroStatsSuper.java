package main.java.com.hey.summaries;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;
import main.java.com.hey.summaries.MetroStats.Stats;

public abstract class MetroStatsSuper {
	
	public static String getMetroKey(CityStats.Data data) {
		return data.metro + "," + data.metroPopulation;
	}
	
	private static boolean isMetroValid(CityStats.Data data) {
		return !data.metro.contains("None");
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
			return String.valueOf((int)(totalSummedValue / totalPopulation));
		}
	}
	
	public abstract String getHeader();
	
	public void performStuff() throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfMetroNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (isMetroValid(data)) {
				String metroKey = MetroStatsSuper.getMetroKey(data);
				if (!mapOfMetroNameToStats.containsKey(metroKey)) {
					Stats stats = new Stats();
					stats.metroName = data.metro;
					stats.metroPopulation = Integer.valueOf(data.metroPopulation);
					mapOfMetroNameToStats.put(metroKey, stats);
				}
				Stats stats = mapOfMetroNameToStats.get(metroKey);
				addStuffToStats(stats, data);
			}
		}
		Set<String> keys = mapOfMetroNameToStats.keySet();
		List<Stats> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(mapOfMetroNameToStats.get(key));
		}
		Collections.sort(statsList, Collections.reverseOrder());
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\MetroStats.csv";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(getHeader());
		for (Stats stat : statsList) {
			sb.appendWithComma(stat.metroName).appendWithComma(stat.getPrimaryState()).appendWithComma(stat.metroPopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}
	
	public abstract void addToSb(AndrewStringWriter sb, Stats stat);

	public abstract void addStuffToStats(Stats stats, Data data);

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

	static int compareTo2(Stats2 self, Stats2 arg0) {
		if (self.metroPopulation < arg0.metroPopulation) {
			return -1;
		} else if (self.metroPopulation == arg0.metroPopulation) {
			return 0;
		}
		return 1;
	}
}
