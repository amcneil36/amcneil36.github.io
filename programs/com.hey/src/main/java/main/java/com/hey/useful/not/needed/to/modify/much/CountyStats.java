package main.java.com.hey.useful.not.needed.to.modify.much;

import static main.java.com.hey.CityStats.COUNTY;
import static main.java.com.hey.CityStats.STATE;

import java.util.Collections;
import java.util.List;

import main.java.com.hey.GenericStats;
import main.java.com.hey.CityStats.Data;

public class CountyStats extends GenericStats {

	private static final String PREDOMINANT_CITY = "Predominant City";
	private static final String COUNTY_POPULATION = "County Population";

	public static void main(String[] args) throws Exception {
		CountyStats cs = new CountyStats();
		cs.createCsvWithDataSummary();
	}

	@Override
	public String[] getInitialHeaders() {
		return new String[] { COUNTY, COUNTY_POPULATION, PREDOMINANT_CITY, STATE };
	}
	
	@Override
	public String[] getStartingElements(Stats stat) {
		return new String[] {stat.countyName, String.valueOf(stat.countyPopulation), stat.mostPopulatedCityName, stat.stateName};
	}

	@Override
	public String getFilePath() {
		return "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CountyStats\\CountyStats.csv";
	}

	@Override
	public void sortStatsList(List<Stats> statsList) {
		Collections.sort(statsList, (a, b) -> b.countyPopulation - a.countyPopulation);
	}

	@Override
	public void doExtraStatsProcessing(Stats stats, Data data) {
		stats.addDataToMapOfTimeZoneToPopulation(data);
		int cityPop = Integer.valueOf(data.population);
		stats.countyPopulation += cityPop;
		if (cityPop > stats.mostPopulatedCityPop) {
			stats.mostPopulatedCityPop = cityPop;
			stats.mostPopulatedCityName = data.cityName;
		}
	}

	@Override
	public void doInitialStatsProcessing(Stats stats, Data data) {
		stats.countyName = data.countyName;
		stats.stateName = data.stateName;
	}

	@Override
	public String getMapKey(Data data) {
		return data.countyName + "," + data.stateName + "," + data.percentDemocrat;
	}

	@Override
	public boolean isDataValid(Data data) {
		return true;
	}
}
