package main.java.com.hey.summaries;

import static main.java.com.hey.CityStats.POPULATION;
import static main.java.com.hey.CityStats.STATE;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;

public class StateStats extends GenericStats {

	public void addToSb(AndrewStringWriter sb, Stats stat) {

		Map<String, Integer> mapOfNameToIdx = new HashMap<>();
		String[] headers = getHeader();
		Object[] arr = new Object[headers.length];
		for (int i = 0; i < headers.length; i++) {
			mapOfNameToIdx.put(headers[i], i);
		}
		extractDataToArray(stat, mapOfNameToIdx, arr);

		for (Object obj : arr) {
			sb.appendWithComma(obj.toString());
		}
	}

	public void performStuff() throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfStateNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			String state = data.stateName;
			if (!mapOfStateNameToStats.containsKey(state)) {
				Stats stats = new Stats();
				stats.stateName = data.stateName;
				mapOfStateNameToStats.put(state, stats);
			}
			Stats stats = mapOfStateNameToStats.get(state);
			addStuffToStats(stats, data);
			stats.addDataToMapOfTimeZoneToPopulation(data);
			int cityPop = Integer.valueOf(data.population);
			stats.statePopulation += cityPop;
		}

		Set<String> keys = mapOfStateNameToStats.keySet();
		List<Stats> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(mapOfStateNameToStats.get(key));
		}
		Collections.sort(statsList, (a, b) -> a.stateName.compareTo(b.stateName));
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\StateStats\\StateStats.csv";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		StringBuilder mySb = new StringBuilder();
		String[] firstHeaders = { STATE, POPULATION };
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
			sb.appendWithComma(stat.stateName).appendWithComma(stat.statePopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}

	public static void main(String[] args) throws Exception {
		StateStats metroStats = new StateStats();
		metroStats.performStuff();
	}

}
