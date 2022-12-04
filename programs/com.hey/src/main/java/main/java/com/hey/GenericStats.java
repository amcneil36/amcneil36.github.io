package main.java.com.hey;

import static main.java.com.hey.CityStats.*;
import static main.java.com.hey.CityStats.AIR_QUALITY_IDX;
import static main.java.com.hey.CityStats.ANNUAL_DEW_POINT;
import static main.java.com.hey.CityStats.ANNUAL_HUMIDITY;
import static main.java.com.hey.CityStats.ANNUAL_RAINFALL;
import static main.java.com.hey.CityStats.ANNUAL_SNOWFALL;
import static main.java.com.hey.CityStats.ANNUAL_SUNSHINE;
import static main.java.com.hey.CityStats.ANNUAL_THUNDERSTORMS;
import static main.java.com.hey.CityStats.ASIAN;
import static main.java.com.hey.CityStats.BACHELORS;
import static main.java.com.hey.CityStats.BLACK;
import static main.java.com.hey.CityStats.COLDEST_MONTH;
import static main.java.com.hey.CityStats.COLDEST_MONTH_AVG_LOW;
import static main.java.com.hey.CityStats.COST_PER_SQFT;
import static main.java.com.hey.CityStats.DEMOCRAT;
import static main.java.com.hey.CityStats.FOREIGN_BORN;
import static main.java.com.hey.CityStats.HISPANIC;
import static main.java.com.hey.CityStats.HOMEOWNERSHIP_RATE;
import static main.java.com.hey.CityStats.HOME_AGE;
import static main.java.com.hey.CityStats.HOME_PRICE;
import static main.java.com.hey.CityStats.HOME_SQFT;
import static main.java.com.hey.CityStats.HOTTEST_MINUS_COLDEST;
import static main.java.com.hey.CityStats.HOTTEST_MONTH;
import static main.java.com.hey.CityStats.HOTTEST_MONTH_AVG_LOW;
import static main.java.com.hey.CityStats.INCOME;
import static main.java.com.hey.CityStats.LABOR_FORCE;
import static main.java.com.hey.CityStats.LATITUDE;
import static main.java.com.hey.CityStats.LONGITUDE;
import static main.java.com.hey.CityStats.POPULATION_DENSITY;
import static main.java.com.hey.CityStats.POVERTY_RATE;
import static main.java.com.hey.CityStats.PROPERTY_CRIMES_FBI;
import static main.java.com.hey.CityStats.RENT;
import static main.java.com.hey.CityStats.REPUBLICAN;
import static main.java.com.hey.CityStats.SINGLE_POPULATION;
import static main.java.com.hey.CityStats.SUMMER_DEW_POINT;
import static main.java.com.hey.CityStats.SUMMER_HUMIDITY;
import static main.java.com.hey.CityStats.SUMMER_RAINFALL;
import static main.java.com.hey.CityStats.SUMMER_SUNSHINE;
import static main.java.com.hey.CityStats.TIME_ZONE;
import static main.java.com.hey.CityStats.UNEMPLOYMENT_RATE;
import static main.java.com.hey.CityStats.UV_INDEX;
import static main.java.com.hey.CityStats.VIOLENT_CRIMES_FBI;
import static main.java.com.hey.CityStats.WHITE;
import static main.java.com.hey.CityStats.WIND_SPEED;
import static main.java.com.hey.CityStats.WINTER_RAINFALL;
import static main.java.com.hey.CityStats.WINTER_SUNSHINE;

import java.util.Map;

import main.java.com.hey.useful.not.needed.to.modify.much.GenericStatsSuper;

// update this file to get data written to State/Metro/County stats
public abstract class GenericStats extends GenericStatsSuper {

	public static class Stats extends Stats2 {
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
		WeightedAverage percentWithAtleastBachelors = new WeightedAveragePercent();
		WeightedAverage medianIncome = new WeightedAverageDollar();
		WeightedAverage povertyRate = new WeightedAveragePercent();
		WeightedAverage medianHomePrice = new WeightedAverageDollar();
		WeightedAverage homeSquareFeet = new WeightedAverage();
		WeightedAverage costPerSquareFoot = new WeightedAverageDollar();
		WeightedAverage homeOwnershipRate = new WeightedAveragePercent();
		WeightedAverage populationGrowthSince2010 = new WeightedAveragePercent();
		WeightedAverage percentDemocrat = new WeightedAveragePercent();
		WeightedAverage percentRepublican = new WeightedAveragePercent();
		WeightedAverage percentAsian = new WeightedAveragePercent();
		WeightedAverage percentBlack = new WeightedAveragePercent();
		WeightedAverage percentWhite = new WeightedAveragePercent();
		WeightedAverage percentHispanic = new WeightedAveragePercent();
		WeightedAverage foreignBornPercent = new WeightedAveragePercent();
		WeightedAverage uvIndex = new WeightedAverage();
		WeightedAverage singlePopulation = new WeightedAveragePercent();
		WeightedAverage percentOfIncomeLostToHousingCosts = new WeightedAveragePercent();
		WeightedAverage sexOffenderCount = new WeightedAverage();
		WeightedAverage fbiViolentCrimeData = new WeightedAverage();
		WeightedAverage fbiPropertyCrimeData = new WeightedAverage();
		WeightedAverage laborForceParticipationRate = new WeightedAveragePercent();
		WeightedAverage rent = new WeightedAverageDollar();
		WeightedAverage unemploymentRate = new WeightedAveragePercent();
		WeightedAverage hottestMinusColdest = new WeightedAverage();
		WeightedAverage airQuality = new WeightedAverage();
		WeightedAverage homeAge = new WeightedAverage();
		WeightedAverage latitude = new WeightedAverageDouble();
		WeightedAverage longitude = new WeightedAverageDouble();
		WeightedAverage annualSunshinePercent = new WeightedAveragePercent();
		WeightedAverage summerSunshinePercent = new WeightedAveragePercent();
		WeightedAverage winterSunshinePercent = new WeightedAveragePercent();
		WeightedAverage annualHumidityPercent = new WeightedAveragePercent();
		WeightedAverage summerHumidityPercent = new WeightedAveragePercent();
		WeightedAverage thunderStorms = new WeightedAverage();
		WeightedAverage annualDewPoint = new WeightedAverage();
		WeightedAverage summerDewPoint = new WeightedAverage();
		WeightedAverage hottestMonthAvgLow = new WeightedAverage();
		WeightedAverage coldestMonthAvgLow = new WeightedAverage();
		WeightedAverage summerRainfall = new WeightedAverageRoundTwo();
		WeightedAverage winterRainfall = new WeightedAverageRoundTwo();
		WeightedAverage percentOfDaysWithRain = new WeightedAveragePercent();
		WeightedAverage percentOfSummerDaysWithRain = new WeightedAveragePercent();
		WeightedAverage percentOfWinterDaysWithRain = new WeightedAveragePercent();
		WeightedAverage numberOfDaysOfSnowPerYear = new WeightedAverage();
		WeightedAverage hottestMonthsHeatIndexHigh = new WeightedAverage();
	}

	@Override
	public void addStuffToStats(Stats stats, CityStats.Data data) {
		stats.peoplePerSqMi.addCity(data, data.populationDensity);
		stats.hottestMonthsHigh.addCity(data, data.hottestMonthsHigh);
		stats.coldestHigh.addCity(data, data.coldestHigh);
		stats.numInchesOfRain.addCity(data, data.numInchesOfRain);
		stats.annualSnowfall.addCity(data, data.annualSnowfall);
		stats.avgYearlyWindspeed.addCity(data, data.avgYearlyWindspeed);
		stats.medianAge.addCity(data, data.medianAge);
		stats.percentWithAtleastBachelors.addCity(data, data.percentWithAtleastBachelors);
		stats.medianIncome.addCity(data, data.medianIncome);
		stats.povertyRate.addCity(data, data.povertyRate);
		stats.medianHomePrice.addCity(data, data.medianHomePrice);
		stats.homeSquareFeet.addCity(data, data.homeSquareFeet);
		stats.costPerSquareFoot.addCity(data, data.costPerSquareFoot);
		stats.homeOwnershipRate.addCity(data, data.homeOwnershipRate);
		stats.percentDemocrat.addCity(data, data.percentDemocrat);
		stats.percentRepublican.addCity(data, data.percentRepublican);
		stats.percentAsian.addCity(data, data.percentAsian);
		stats.percentBlack.addCity(data, data.percentBlack);
		stats.percentWhite.addCity(data, data.percentWhite);
		stats.percentHispanic.addCity(data, data.percentHispanic);
		stats.foreignBornPercent.addCity(data, data.foreignBornPercent);
		stats.uvIndex.addCity(data, data.uvIndex);
		stats.singlePopulation.addCity(data, data.singlePopulation);
		stats.fbiViolentCrimeData.addCity(data, data.fbiViolentCrimeRate);
		stats.fbiPropertyCrimeData.addCity(data, data.fbiPropertyCrimeRate);
		stats.laborForceParticipationRate.addCity(data, data.laborForceParticipationRate);
		stats.rent.addCity(data, data.avgApartmentRent);
		stats.unemploymentRate.addCity(data, data.unemploymentRate);
		stats.hottestMinusColdest.addCity(data, data.hottestMonthMinusColdestMonth);
		stats.airQuality.addCity(data, data.airQuality);
		stats.homeAge.addCity(data, data.medianHomeAge);
		stats.latitude.addCity(data, data.latitude);
		stats.longitude.addCity(data, data.longitude);
		stats.annualSunshinePercent.addCity(data, data.annualSunshinePercent);
		stats.summerSunshinePercent.addCity(data, data.summerSunshinePercent);
		stats.winterSunshinePercent.addCity(data, data.winterSunshinePercent);
		stats.annualHumidityPercent.addCity(data, data.annualHumidityPercent);
		stats.summerHumidityPercent.addCity(data, data.summerHumidityPercent);
		stats.thunderStorms.addCity(data, data.numberOfThunderstormsPerYear);
		stats.annualDewPoint.addCity(data, data.annualDewPoint);
		stats.summerDewPoint.addCity(data, data.summerDewPoint);
		stats.hottestMonthAvgLow.addCity(data, data.hottestMonthAvgLow);
		stats.coldestMonthAvgLow.addCity(data, data.coldestMonthAvgLow);
		stats.summerRainfall.addCity(data, data.numInchesOfRainPerSummer);
		stats.winterRainfall.addCity(data, data.numInchesOfRainPerWinter);
		stats.percentOfDaysWithRain.addCity(data, data.percentOfDaysWithRain);
		stats.percentOfSummerDaysWithRain.addCity(data, data.percentOfSummerDaysWithRain);
		stats.percentOfWinterDaysWithRain.addCity(data, data.percentOfWinterDaysWithRain);
		stats.numberOfDaysOfSnowPerYear.addCity(data, data.daysOfSnowPerYear);
		stats.hottestMonthsHeatIndexHigh.addCity(data, data.hottestMonthsHeatIndexHigh);

	}

	@Override
	public void extractDataToArray(Stats stat, Map<String, Integer> mapOfNameToIdx, Object[] arr) {
		arr[mapOfNameToIdx.get(POPULATION_DENSITY)] = stat.peoplePerSqMi;
		arr[mapOfNameToIdx.get(HOTTEST_MONTH)] = stat.hottestMonthsHigh;
		arr[mapOfNameToIdx.get(COLDEST_MONTH)] = stat.coldestHigh;
		arr[mapOfNameToIdx.get(ANNUAL_RAINFALL)] = stat.numInchesOfRain;
		arr[mapOfNameToIdx.get(ANNUAL_SNOWFALL)] = stat.annualSnowfall;
		arr[mapOfNameToIdx.get(WIND_SPEED)] = stat.avgYearlyWindspeed;
		arr[mapOfNameToIdx.get(AGE)] = stat.medianAge;
		arr[mapOfNameToIdx.get(BACHELORS)] = stat.percentWithAtleastBachelors;
		arr[mapOfNameToIdx.get(INCOME)] = stat.medianIncome;
		arr[mapOfNameToIdx.get(POVERTY_RATE)] = stat.povertyRate;
		arr[mapOfNameToIdx.get(HOME_PRICE)] = stat.medianHomePrice;
		arr[mapOfNameToIdx.get(HOME_SQFT)] = stat.homeSquareFeet;
		arr[mapOfNameToIdx.get(COST_PER_SQFT)] = stat.costPerSquareFoot;
		arr[mapOfNameToIdx.get(HOMEOWNERSHIP_RATE)] = stat.homeOwnershipRate;
		arr[mapOfNameToIdx.get(DEMOCRAT)] = stat.percentDemocrat;
		arr[mapOfNameToIdx.get(REPUBLICAN)] = stat.percentRepublican;
		arr[mapOfNameToIdx.get(ASIAN)] = stat.percentAsian;
		arr[mapOfNameToIdx.get(BLACK)] = stat.percentBlack;
		arr[mapOfNameToIdx.get(WHITE)] = stat.percentWhite;
		arr[mapOfNameToIdx.get(HISPANIC)] = stat.percentHispanic;
		arr[mapOfNameToIdx.get(FOREIGN_BORN)] = stat.foreignBornPercent;
		arr[mapOfNameToIdx.get(UV_INDEX)] = stat.uvIndex;
		arr[mapOfNameToIdx.get(SINGLE_POPULATION)] = stat.singlePopulation;
		arr[mapOfNameToIdx.get(TIME_ZONE)] = stat.getPrimaryTimeZone();
		arr[mapOfNameToIdx.get(VIOLENT_CRIMES_FBI)] = stat.fbiViolentCrimeData;
		arr[mapOfNameToIdx.get(PROPERTY_CRIMES_FBI)] = stat.fbiPropertyCrimeData;
		arr[mapOfNameToIdx.get(LABOR_FORCE)] = stat.laborForceParticipationRate;
		arr[mapOfNameToIdx.get(RENT)] = stat.rent;
		arr[mapOfNameToIdx.get(UNEMPLOYMENT_RATE)] = stat.unemploymentRate;
		arr[mapOfNameToIdx.get(HOTTEST_MINUS_COLDEST)] = stat.hottestMinusColdest;
		arr[mapOfNameToIdx.get(AIR_QUALITY_IDX)] = stat.airQuality;
		arr[mapOfNameToIdx.get(HOME_AGE)] = stat.homeAge;
		arr[mapOfNameToIdx.get(LATITUDE)] = stat.latitude;
		arr[mapOfNameToIdx.get(LONGITUDE)] = stat.longitude;
		arr[mapOfNameToIdx.get(ANNUAL_SUNSHINE)] = stat.annualSunshinePercent;
		arr[mapOfNameToIdx.get(SUMMER_SUNSHINE)] = stat.summerSunshinePercent;
		arr[mapOfNameToIdx.get(WINTER_SUNSHINE)] = stat.winterSunshinePercent;
		arr[mapOfNameToIdx.get(ANNUAL_HUMIDITY)] = stat.annualHumidityPercent;
		arr[mapOfNameToIdx.get(SUMMER_HUMIDITY)] = stat.summerHumidityPercent;
		arr[mapOfNameToIdx.get(ANNUAL_THUNDERSTORMS)] = stat.thunderStorms;
		arr[mapOfNameToIdx.get(ANNUAL_DEW_POINT)] = stat.annualDewPoint;
		arr[mapOfNameToIdx.get(SUMMER_DEW_POINT)] = stat.summerDewPoint;
		arr[mapOfNameToIdx.get(HOTTEST_MONTH_AVG_LOW)] = stat.hottestMonthAvgLow;
		arr[mapOfNameToIdx.get(COLDEST_MONTH_AVG_LOW)] = stat.coldestMonthAvgLow;
		arr[mapOfNameToIdx.get(SUMMER_RAINFALL)] = stat.summerRainfall;
		arr[mapOfNameToIdx.get(WINTER_RAINFALL)] = stat.winterRainfall;
		arr[mapOfNameToIdx.get(PERCENT_OF_DAYS_WITH_RAIN)] = stat.percentOfDaysWithRain;
		arr[mapOfNameToIdx.get(PERCENT_OF_SUMMER_DAYS_WITH_RAIN)] = stat.percentOfSummerDaysWithRain;
		arr[mapOfNameToIdx.get(PERCENT_OF_WINTER_DAYS_WITH_RAIN)] = stat.percentOfWinterDaysWithRain;
		arr[mapOfNameToIdx.get(DAYS_OF_SNOW_PER_YEAR)] = stat.numberOfDaysOfSnowPerYear;
		arr[mapOfNameToIdx.get(HOTTEST_MONTHS_HEAT_INDEX_HIGH)] = stat.hottestMonthsHeatIndexHigh;
	}

	@Override
	public String[] getHeader() {
		return new String[] { POPULATION_DENSITY, INCOME, BACHELORS, AGE, HOME_PRICE, HOME_SQFT, COST_PER_SQFT,
				HOME_AGE, HOMEOWNERSHIP_RATE, RENT, SINGLE_POPULATION, VIOLENT_CRIMES_FBI, PROPERTY_CRIMES_FBI,
				POVERTY_RATE, UNEMPLOYMENT_RATE, LABOR_FORCE, DEMOCRAT, REPUBLICAN, ASIAN, BLACK, WHITE, HISPANIC,
				FOREIGN_BORN, TIME_ZONE, HOTTEST_MONTH, COLDEST_MONTH, HOTTEST_MINUS_COLDEST, ANNUAL_RAINFALL,
				ANNUAL_SNOWFALL, WIND_SPEED, AIR_QUALITY_IDX, UV_INDEX, LATITUDE, LONGITUDE, ANNUAL_SUNSHINE,
				SUMMER_SUNSHINE, WINTER_SUNSHINE, ANNUAL_HUMIDITY, SUMMER_HUMIDITY, ANNUAL_THUNDERSTORMS,
				ANNUAL_DEW_POINT, SUMMER_DEW_POINT, HOTTEST_MONTH_AVG_LOW, COLDEST_MONTH_AVG_LOW, SUMMER_RAINFALL,
				WINTER_RAINFALL, PERCENT_OF_DAYS_WITH_RAIN, PERCENT_OF_SUMMER_DAYS_WITH_RAIN,
				PERCENT_OF_WINTER_DAYS_WITH_RAIN, DAYS_OF_SNOW_PER_YEAR, HOTTEST_MONTHS_HEAT_INDEX_HIGH };
	}

}