package main.java.com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.MetroStats.Stats;
import main.java.com.hey.MetroStats.Stats2;

public class CountyStats {

	static class CountyStatsObj extends MetroStats.Stats {
		public String countyName = "";
		public int countyPopulation = 0;
	}

	public static String getCountyKey(CityStats.Data data) {
		return data.countyName + "," + data.stateName + "," + data.percentDemocrat;
	}

	static void addStuffToStats(CountyStatsObj stats, CityStats.Data data) {
		stats.peoplePerSqMi.addCity(data, data.populationDensity);
		stats.hottestMonthsHigh.addCity(data, data.hottestMonthsHigh);
		stats.coldestHigh.addCity(data, data.coldestHigh);
		stats.numInchesOfRain.addCity(data, data.numInchesOfRain);
		stats.annualSnowfall.addCity(data, data.annualSnowfall);
		stats.numSunnyDays.addCity(data, data.numSunnyDays);
		stats.numDaysOfRain.addCity(data, data.numDaysOfRain);
		stats.avgSummerDewPoint.addCity(data, data.avgSummerDewPoint);
		stats.avgAnnualDewPoint.addCity(data, data.avgAnnualDewPoint);
		stats.avgYearlyWindspeed.addCity(data, data.avgYearlyWindspeed);
		stats.violentCrime.addCity(data, data.violentCrime);
		stats.propertyCrime.addCity(data, data.propertyCrime);
		stats.medianAge.addCity(data, data.medianAge);
		stats.percentWithAtleastBachelors.addCity(data, data.percentWithAtleastBachelors);
		stats.medianIncome.addCity(data, data.medianIncome);
		stats.povertyRate.addCity(data, data.povertyRate);
		stats.medianHomePrice.addCity(data, data.medianHomePrice);
		stats.homeSquareFeet.addCity(data, data.homeSquareFeet);
		stats.costPerSquareFoot.addCity(data, data.costPerSquareFoot);
		stats.homeOwnershipRate.addCity(data, data.homeOwnershipRate);
		stats.populationGrowthSince2010.addCity(data, data.populationGrowthSince2010);
		stats.percentDemocrat.addCity(data, data.percentDemocrat);
		stats.percentRepublican.addCity(data, data.percentRepublican);
		stats.percentAsian.addCity(data, data.percentAsian);
		stats.percentBlack.addCity(data, data.percentBlack);
		stats.percentWhite.addCity(data, data.percentWhite);
		stats.percentHispanic.addCity(data, data.percentHispanic);
		stats.foreignBornPercent.addCity(data, data.foreignBornPercent);
		stats.uvIndex.addCity(data, data.uvIndex);
		stats.singlePopulation.addCity(data, data.singlePopulation);
		stats.percentOfIncomeLostToHousingCosts.addCity(data, data.percentOfIncomeLostToHousingCosts);
		stats.sexOffenderCount.addCity(data, data.sexOffenderCount);
		stats.addDataToMapOfTimeZoneToPopulation(data);
		stats.countyPopulation += Integer.valueOf(data.population);

	}

	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, CountyStatsObj> mapOfCountyNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			String countyKey = getCountyKey(data);
			if (!mapOfCountyNameToStats.containsKey(countyKey)) {
				CountyStatsObj stats = new CountyStatsObj();
				stats.countyName = data.countyName;
				stats.countyPopulation = Integer.valueOf(data.population);
				mapOfCountyNameToStats.put(countyKey, stats);
			}
			CountyStatsObj stats = mapOfCountyNameToStats.get(countyKey);
			addStuffToStats(stats, data);
		}
	}
}
