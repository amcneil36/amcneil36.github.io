package main.java.com.hey.summaries;

import main.java.com.hey.CityStats;
import static main.java.com.hey.CityStats.*;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;

public class MetroStats extends MetroStatsSuper {

	static class Stats extends Stats2 {
		WeightedAverage peoplePerSqMi = new WeightedAverage();
		WeightedAverage hottestMonthsHigh = new WeightedAverage();
		WeightedAverage coldestHigh = new WeightedAverage();
		WeightedAverage numInchesOfRain = new WeightedAverage();
		WeightedAverage annualSnowfall = new WeightedAverage();
		WeightedAverage numSunnyDays = new WeightedAverage();
		WeightedAverage numDaysOfRain = new WeightedAverage();
		WeightedAverage avgSummerDewPoint = new WeightedAverage();
		WeightedAverage avgAnnualDewPoint = new WeightedAverage();
		WeightedAverage avgYearlyWindspeed = new WeightedAverage();
		WeightedAverage violentCrime = new WeightedAverage();
		WeightedAverage propertyCrime = new WeightedAverage();
		WeightedAverage medianAge = new WeightedAverage();
		WeightedAverage percentWithAtleastBachelors = new WeightedAverage();
		WeightedAverage medianIncome = new WeightedAverage();
		WeightedAverage povertyRate = new WeightedAverage();
		WeightedAverage medianHomePrice = new WeightedAverage();
		WeightedAverage homeSquareFeet = new WeightedAverage();
		WeightedAverage costPerSquareFoot = new WeightedAverage();
		WeightedAverage homeOwnershipRate = new WeightedAverage();
		WeightedAverage populationGrowthSince2010 = new WeightedAverage();
		WeightedAverage percentDemocrat = new WeightedAverage();
		WeightedAverage percentRepublican = new WeightedAverage();
		WeightedAverage percentAsian = new WeightedAverage();
		WeightedAverage percentBlack = new WeightedAverage();
		WeightedAverage percentWhite = new WeightedAverage();
		WeightedAverage percentHispanic = new WeightedAverage();
		WeightedAverage foreignBornPercent = new WeightedAverage();
		WeightedAverage uvIndex = new WeightedAverage();
		WeightedAverage singlePopulation = new WeightedAverage();
		WeightedAverage percentOfIncomeLostToHousingCosts = new WeightedAverage();
		WeightedAverage sexOffenderCount = new WeightedAverage();
		WeightedAverage fbiViolentCrimeData = new WeightedAverage();
		WeightedAverage fbiPropertyCrimeData = new WeightedAverage();
		WeightedAverage laborForceParticipationRate = new WeightedAverage();
	}

	// this can stay as is
	@Override
	public void addStuffToStats(Stats stats, CityStats.Data data) {
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
		stats.addDataToMapOfStateToPopulation(data);
		stats.addDataToMapOfTimeZoneToPopulation(data);

	}

	// change this
	// make a test that reads CityStats.csv and then calls into addStuffToStats with a random
	// CityStats.data object and then calls into this. then use code that makes a map and populates an array from the map
	// similar to CityStats::extractDataToArray
	@Override
	public void addToSb(AndrewStringWriter sb, Stats stat) {
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
		MetroStats metroStats = new MetroStats();
		metroStats.performStuff();
	}

	@Override
	public String[] getHeader() {
		return new String[] { METRO_NAME, PREDOMINANT_STATE, METRO_POP, POPULATION_DENSITY, HOTTEST_MONTH,
				COLDEST_MONTH, ANNUAL_RAINFALL, ANNUAL_DAYS_OF_PRECIPITATION, ANNUAL_DAYS_OF_SUNSHINE, ANNUAL_SNOWFALL,
				AVERAGE_SUMMER_DEW_POINT, DEW_POINT, WIND_SPEED, VIOLENT_CRIME_INDEX, PROPERTY_CRIME_INDEX, AGE,
				BACHELORS, INCOME, POVERTY_RATE, HOME_PRICE, HOME_SQFT, COST_PER_SQFT, HOMEOWNERSHIP_RATE,
				POPULATION_GROWTH, DEMOCRAT, REPUBLICAN, ASIAN, BLACK, WHITE, HISPANIC, FOREIGN_BORN, UV_INDEX,
				SINGLE_POPULATION, INCOME_SPENT, SEX_OFFENDERS, PREDOMINANT_TIMEZONE, VIOLENT_CRIMES_FBI,
				PROPERTY_CRIMES_FBI, LABOR_FORCE };
	}

}
