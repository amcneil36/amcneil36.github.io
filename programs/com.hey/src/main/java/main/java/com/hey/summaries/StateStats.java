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
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;

public class StateStats extends GenericStats {

	public static void main(String[] args) throws Exception {
		StateStats metroStats = new StateStats();
		metroStats.performStuff();
	}

	@Override
	public String[] getInitialHeaders() {
		return new String[]{ STATE, POPULATION };
	}

	@Override
	public String getFilePath() {
		return "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\StateStats\\StateStats.csv";
	}

	@Override
	public void sortStatsList(List<Stats> statsList) {
		Collections.sort(statsList, (a, b) -> a.stateName.compareTo(b.stateName));
		
	}

	@Override
	public void doExtraStatsProcessing(Stats stats, Data data) {
		stats.addDataToMapOfTimeZoneToPopulation(data);
		int cityPop = Integer.valueOf(data.population);
		stats.statePopulation += cityPop;
	}

	@Override
	public void doInitialStatsProcessing(Stats stats, Data data) {
		stats.stateName = data.stateName;
	}

	@Override
	public String getMapKey(Data data) {
		return data.stateName;
	}

	@Override
	public boolean isDataValid(Data data) {
		return true;
	}

}
