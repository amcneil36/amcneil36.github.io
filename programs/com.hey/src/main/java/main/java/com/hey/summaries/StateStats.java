package main.java.com.hey.summaries;

import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.summaries.MetroStats.Stats;

public class StateStats extends StateStatsSuper {
	
	@Override
	public void addStuffToStats(Stats stats, CityStats.Data data) {
		new MetroStats().addStuffToStats(stats, data);

	}
	
	public static void main(String[] args) throws Exception {
		StateStats ss = new StateStats();
		ss.performStuff();
	}

	@Override
	public String[] getHeader() {
		return new MetroStats().getHeader();
	}

	@Override
	public void extractDataToArray(Stats stat, Map<String, Integer> mapOfNameToIdx, Object[] arr) {
		new MetroStats().extractDataToArray(stat, mapOfNameToIdx, arr);
	}
}
