package main.java.com.hey.summaries;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;
import main.java.com.hey.summaries.MetroStats.Stats;

public class StateStats extends StateStatsSuper {
	
	@Override
	public void addStuffToStats(StateStatsObj stats, CityStats.Data data) {
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
		stats.fbiViolentCrimeData.addCity(data, data.fbiViolentCrimeRate);
		stats.fbiPropertyCrimeData.addCity(data, data.fbiPropertyCrimeRate);
		stats.laborForceParticipationRate.addCity(data, data.laborForceParticipationRate);
		stats.addDataToMapOfTimeZoneToPopulation(data);
		int cityPop = Integer.valueOf(data.population);
		stats.statePopulation += cityPop;

	}
	
	static String startSt = "State Name,Population,People Per Sq Mi,Hottest month's avg high (F),Coldest month's avg high (F),Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),"
			+ "Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,"
			+ "Median household income,Poverty Rate,Median home price,Median home sqft,"
			+ "Median home cost per sqft,Homeownership Rate,Population growth since 2010,"
			+ "% Democrat,% Republican,% Asian,% Black,% Non-Hispanic White,% Hispanic,Foreign Born %,UV Index,Single Population,% of income spent on housing costs (owners),Number of sex offenders per 10k residents,Predominant Timezone,Num Violent Crimes Per 100k residents,Num Property Crimes Per 100k residents,Labor Force Participation rate";

	@Override
	public String getStartStr() {
		return startSt;
	}

	@Override
	public void addToSb(AndrewStringWriter sb, StateStatsObj stat){
		sb.appendWA(stat.peoplePerSqMi);
		sb.appendWA(stat.hottestMonthsHigh);
		sb.appendWA(stat.coldestHigh);
		sb.appendWA(stat.numInchesOfRain);
		sb.appendWA(stat.numDaysOfRain);
		sb.appendWA(stat.numSunnyDays);
		sb.appendWA(stat.annualSnowfall);
		sb.appendWA(stat.avgSummerDewPoint);
		sb.appendWA(stat.avgAnnualDewPoint);
		sb.appendWA(stat.avgYearlyWindspeed);
		sb.appendWA(stat.violentCrime);
		sb.appendWA(stat.propertyCrime);
		sb.appendWA(stat.medianAge);
		sb.appendWAPercent(stat.percentWithAtleastBachelors);
		sb.appendWADollar(stat.medianIncome);
		sb.appendWAPercent(stat.povertyRate);
		sb.appendWADollar(stat.medianHomePrice);
		sb.appendWA(stat.homeSquareFeet);
		sb.appendWADollar(stat.costPerSquareFoot);
		sb.appendWAPercent(stat.homeOwnershipRate);
		sb.appendWAPercent(stat.populationGrowthSince2010);
		sb.appendWAPercent(stat.percentDemocrat);
		sb.appendWAPercent(stat.percentRepublican);
		sb.appendWAPercent(stat.percentAsian);
		sb.appendWAPercent(stat.percentBlack);
		sb.appendWAPercent(stat.percentWhite);
		sb.appendWAPercent(stat.percentHispanic);
		sb.appendWAPercent(stat.foreignBornPercent);
		sb.appendWA(stat.uvIndex);
		sb.appendWAPercent(stat.singlePopulation);
		sb.appendWAPercent(stat.percentOfIncomeLostToHousingCosts);
		sb.appendWA(stat.sexOffenderCount);
		sb.appendWithComma(stat.getPrimaryTimeZone());
		sb.appendWA(stat.fbiViolentCrimeData);
		sb.appendWA(stat.fbiPropertyCrimeData);
		sb.appendWAPercent(stat.laborForceParticipationRate);
	}
	
	public static void main(String[] args) throws Exception {
		StateStats ss = new StateStats();
		ss.performStuff();
	}
}
