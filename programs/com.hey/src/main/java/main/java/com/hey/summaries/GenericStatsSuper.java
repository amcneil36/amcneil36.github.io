package main.java.com.hey.summaries;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;
import main.java.com.hey.summaries.GenericStats.Stats;

public abstract class GenericStatsSuper {
	public static class Stats2 implements Comparable<Stats2> {
		public String metroName = "";
		public int metroPopulation = 0;
		public int statePopulation = 0;
		public String stateName = "";
		public String countyName = "";
		public int countyPopulation = 0;
		public String mostPopulatedCityName = "";
		public int mostPopulatedCityPop = 0;
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
	///////////////////////

	private static final String UNPROCESSED_NODE = "unprocessed node here 23894720";

	public void validateAllFieldsWereWrittenTo(Object[] arr) {
		for (Object st : arr) {
			if (st.equals(UNPROCESSED_NODE)) {
				throw new RuntimeException("Not all fields were written too!");
			}
		}

	}

	public void addToSb(AndrewStringWriter sb, Stats stat) {

		Map<String, Integer> mapOfNameToIdx = new HashMap<>();
		String[] headers = getHeader();
		Object[] arr = new Object[headers.length];
		for (int i = 0; i < headers.length; i++) {
			mapOfNameToIdx.put(headers[i], i);
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i] = UNPROCESSED_NODE;
		}
		extractDataToArray(stat, mapOfNameToIdx, arr);
		validateAllFieldsWereWrittenTo(arr);

		for (Object obj : arr) {
			sb.appendWithComma(obj.toString());
		}
	}

	public void createCsvWithDataSummary() throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> map = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (isDataValid(data)) {
				String key = getMapKey(data);
				if (!map.containsKey(key)) {
					Stats stats = new Stats();
					doInitialStatsProcessing(stats, data);
					map.put(key, stats);
				}
				Stats stats = map.get(key);
				addStuffToStats(stats, data);
				doExtraStatsProcessing(stats, data);
			}
		}
		Set<String> keys = map.keySet();
		List<Stats> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(map.get(key));
		}
		sortStatsList(statsList);
		String filePath = getFilePath();
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		StringBuilder mySb = new StringBuilder();
		String[] firstHeaders = getInitialHeaders();
		for (String str : firstHeaders) {
			mySb.append(str).append(",");
		}
		String[] remainingHeaders = getHeader();
		for (String str : remainingHeaders) {
			mySb.append(str).append(",");
		}
		mySb.deleteCharAt(mySb.length() - 1);
		sb.appendLastItem(mySb.toString());
		for (Stats stat : statsList) {
			sb.appendWithComma(stat.metroName).appendWithComma(stat.getPrimaryState())
					.appendWithComma(stat.metroPopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}

	public abstract String[] getHeader();

	public abstract String[] getInitialHeaders();

	public abstract String getFilePath();

	public abstract void sortStatsList(List<Stats> statsList);

	public abstract void doExtraStatsProcessing(Stats stats, Data data);

	public abstract void addStuffToStats(Stats stats, Data data);

	public abstract void doInitialStatsProcessing(Stats stats, Data data);

	public abstract String getMapKey(Data data);

	public abstract boolean isDataValid(Data data);

	public abstract void extractDataToArray(Stats stat, Map<String, Integer> mapOfNameToIdx, Object[] arr);

}
