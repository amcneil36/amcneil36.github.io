package main.java.com.hey.useful.not.needed.to.modify.much;

import static main.java.com.hey.CityStats.METRO_NAME;
import static main.java.com.hey.CityStats.METRO_POP;

import java.util.Collections;
import java.util.List;

import main.java.com.hey.GenericStats;
import main.java.com.hey.CityStats.Data;

public class MetroStats extends GenericStats {

	public static final String PREDOMINANT_STATE = "Predominant State";
	public static final String PREDOMINANT_TIMEZONE = "Predominant Timezone";

	public static void main(String[] args) throws Exception {
		MetroStats metroStats = new MetroStats();
		metroStats.createCsvWithDataSummary();
	}

	@Override
	public String[] getInitialHeaders() {
		return new String[] { METRO_NAME, PREDOMINANT_STATE, METRO_POP };
	}

	@Override
	public String getFilePath() {
		return "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\MetroStats.csv";
	}

	@Override
	public void sortStatsList(List<Stats> statsList) {
		Collections.sort(statsList, Collections.reverseOrder());
	}

	@Override
	public void doExtraStatsProcessing(Stats stats, Data data) {
		stats.addDataToMapOfStateToPopulation(data);
		stats.addDataToMapOfTimeZoneToPopulation(data);
	}

	@Override
	public void doInitialStatsProcessing(Stats stats, Data data) {
		stats.metroName = data.metro;
		stats.metroPopulation = Integer.valueOf(data.metroPopulation);
	}

	@Override
	public String getMapKey(Data data) {
		return data.metro + "," + data.metroPopulation;
	}

	@Override
	public boolean isDataValid(Data data) {
		return !data.metro.contains("None");
	}

}
