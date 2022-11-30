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

public abstract class StateStatsSuper {
	public static class StateStatsObj extends MetroStats.Stats{
		public int statePopulation = 0;
		public String stateName = "";
	}
	
	public void performStuff()throws Exception  {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, StateStatsObj> mapOfStateNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			String state = data.stateName;
			if (!mapOfStateNameToStats.containsKey(state)) {
				StateStatsObj stats = new StateStatsObj();
				stats.stateName = data.stateName;
				mapOfStateNameToStats.put(state, stats);
			}
			StateStatsObj stats = mapOfStateNameToStats.get(state);
			addStuffToStats(stats, data);
		}
		
		Set<String> keys = mapOfStateNameToStats.keySet();
		List<StateStatsObj> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(mapOfStateNameToStats.get(key));
		}
		Collections.sort(statsList, (a,b)->a.stateName.compareTo(b.stateName));
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\StateStats\\StateStats.csv";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(getStartStr());
		for (StateStatsObj stat : statsList) {
			sb.appendWithComma(stat.stateName).appendWithComma(stat.statePopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);		
	}

	public abstract void addToSb(AndrewStringWriter sb, StateStatsObj stat);

	public abstract String getStartStr();

	public abstract void addStuffToStats(StateStatsObj stats, Data data);
}
