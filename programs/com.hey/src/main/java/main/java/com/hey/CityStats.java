package main.java.com.hey;

import java.util.Map;

import main.java.com.hey.useful.not.needed.to.modify.much.CityStatsSuper;
import main.java.com.hey.useful.not.needed.to.modify.much.Pojo;

// update this file to get new columns read/written in CityStats.csv
public abstract class CityStats extends CityStatsSuper {

	public static class Data extends Pojo {
		public String cityName = "N/A";
		public String stateName = "N/A";
		public String hottestMonthsHigh = "N/A";
		public String coldestHigh = "N/A";
		public String numInchesOfRain = "N/A";
		public String annualSnowfall = "N/A";
		public String population = "N/A";
		public String populationDensity = "N/A";
		public String medianIncome = "N/A";
		public String medianHomePrice = "N/A";
		public String medianAge = "N/A";
		public String airQuality = "N/A";
		public String medianHomeAge = "N/A";
		public String countyName = "N/A";
		public String hottestMonthMinusColdestMonth = "N/A";
		public String avgYearlyWindspeed = "N/A";
		public String percentDemocrat = "N/A";
		public String percentRepublican = "N/A";
		public String unemploymentRate = "N/A";
		public String percentAsian = "N/A";
		public String percentBlack = "N/A";
		public String percentWhite = "N/A";
		public String percentHispanic = "N/A";
		public String percentWithAtleastBachelors = "N/A";
		public String metro = "N/A";
		public String homeSquareFeet = "N/A";
		public String costPerSquareFoot = "N/A";
		public String timeZone = "N/A";
		public String feetAboveSeaLevel = "N/A";
		public String uvIndex = "N/A";
		public String homeOwnershipRate = "N/A";
		public String singlePopulation = "N/A";
		public String povertyRate = "N/A";
		public String foreignBornPercent = "N/A";
		public String avgApartmentRent = "N/A";
		public String metroPopulation = "N/A";
		public String fbiViolentCrimeRate = "N/A";
		public String fbiPropertyCrimeRate = "N/A";
		public String fipsCode = "N/A";
		public String landArea = "N/A";
		public String laborForceParticipationRate = "N/A";
		public String latitude = "N/A";
		public String longitude = "N/A";
		public String annualSunshinePercent = "N/A";
		public String summerSunshinePercent = "N/A";
		public String winterSunshinePercent = "N/A";
		public String annualHumidityPercent = "N/A";
		public String summerHumidityPercent = "N/A";
		public String numberOfThunderstormsPerYear = "N/A";
		public String annualDewPoint = "N/A";
		public String summerDewPoint = "N/A";
		public String hottestMonthAvgLow = "N/A";
		public String coldestMonthAvgLow = "N/A";
		public String numInchesOfRainPerSummer = "N/A";
		public String numInchesOfRainPerWinter = "N/A";
		public String percentOfDaysWithRain = "N/A";
		public String percentOfSummerDaysWithRain = "N/A";
		public String percentOfWinterDaysWithRain = "N/A";
		public String daysOfSnowPerYear = "N/A";
		public String hottestMonthsHeatIndexHigh = "N/A";
	}

	public static final String CITY = "City";
	public static final String STATE = "State";
	public static final String POPULATION = "Population";
	public static final String POPULATION_DENSITY = "People per sq mi";
	public static final String METRO_NAME = "Metro Name";
	public static final String METRO_POP = "Metro population";
	public static final String HOTTEST_MONTH = "Hottest month's avg high (F)";
	public static final String COLDEST_MONTH = "Coldest month's avg high (F)";
	public static final String HOTTEST_MINUS_COLDEST = "Hottest high minus coldest high";
	public static final String ANNUAL_RAINFALL = "Annual rainfall (in)";
	public static final String ANNUAL_SNOWFALL = "Annual snowfall (in)";
	public static final String WIND_SPEED = "Average yearly windspeed (mph)";
	public static final String AGE = "Median age";
	public static final String BACHELORS = "% with at least Bachelor's degree";
	public static final String INCOME = "Median household income";
	public static final String POVERTY_RATE = "Poverty Rate";
	public static final String RENT = "Avg Apartment Monthly Rent";
	public static final String HOME_PRICE = "Median home price";
	public static final String HOME_SQFT = "Median home sqft";
	public static final String COST_PER_SQFT = "Median home cost per sqft";
	public static final String HOME_AGE = "Median home age";
	public static final String HOMEOWNERSHIP_RATE = "Homeownership Rate";
	public static final String AIR_QUALITY_IDX = "Air quality Index";
	public static final String UNEMPLOYMENT_RATE = "Unemployment rate";
	public static final String COUNTY = "County";
	public static final String DEMOCRAT = "% Democrat";
	public static final String REPUBLICAN = "% Republican";
	public static final String ASIAN = "% Asian";
	public static final String BLACK = "% Black";
	public static final String WHITE = "% Non-Hispanic White";
	public static final String HISPANIC = "% Hispanic";
	public static final String FOREIGN_BORN = "Foreign Born %";
	public static final String TIME_ZONE = "Timezone";
	public static final String ELEVATION = "Elevation (ft)";
	public static final String UV_INDEX = "UV Index";
	public static final String SINGLE_POPULATION = "Single Population";
	public static final String VIOLENT_CRIMES_FBI = "Num Violent Crimes Per 100k residents";
	public static final String PROPERTY_CRIMES_FBI = "Num Property Crimes Per 100k residents";
	public static final String FIPS_CODE = "Fips Code";
	public static final String LAND_AREA = "Land Area (sq mi)";
	public static final String LABOR_FORCE = "Labor Force Participation rate";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String ANNUAL_SUNSHINE = "Annual Sunshine - Percentage of Possible";
	public static final String SUMMER_SUNSHINE = "Summer Sunshine - Percentage of Possible";
	public static final String WINTER_SUNSHINE = "Winter Sunshine - Percentage of Possible";
	public static final String ANNUAL_HUMIDITY = "Annual Relative Humidity (afternoon)";
	public static final String SUMMER_HUMIDITY = "Summer Relative Humidity (afternoon)";
	public static final String ANNUAL_THUNDERSTORMS = "Number of days with thunder per year";
	public static final String ANNUAL_DEW_POINT = "Annual dew point (F)";
	public static final String SUMMER_DEW_POINT = "Average summer dew point (F)";
	public static final String HOTTEST_MONTH_AVG_LOW = "Hottest month's avg low (F)";
	public static final String COLDEST_MONTH_AVG_LOW = "Coldest month's avg low (F)";
	public static final String SUMMER_RAINFALL = "Summer rainfall (in)";
	public static final String WINTER_RAINFALL = "Winter rainfall (in)";
	public static final String PERCENT_OF_DAYS_WITH_RAIN = "Percent of days that include precipitation";
	public static final String PERCENT_OF_SUMMER_DAYS_WITH_RAIN = "Percent of Summer days that include precipitation";
	public static final String PERCENT_OF_WINTER_DAYS_WITH_RAIN = "Percent of Winter days that include precipitation";
	public static final String DAYS_OF_SNOW_PER_YEAR = "Days of snow per year";
	public static final String HOTTEST_MONTHS_HEAT_INDEX_HIGH = "Hottest month's avg heat index high (F)";

	public String[] getOutputHeaders() {
		return new String[] { CITY, STATE, POPULATION, POPULATION_DENSITY, METRO_NAME, METRO_POP, INCOME, BACHELORS,
				AGE, HOME_PRICE, HOME_SQFT, COST_PER_SQFT, HOME_AGE, HOMEOWNERSHIP_RATE, RENT, SINGLE_POPULATION,
				VIOLENT_CRIMES_FBI, PROPERTY_CRIMES_FBI, POVERTY_RATE, UNEMPLOYMENT_RATE, LABOR_FORCE, COUNTY, DEMOCRAT,
				REPUBLICAN, ASIAN, BLACK, WHITE, HISPANIC, FOREIGN_BORN, TIME_ZONE, HOTTEST_MONTH, COLDEST_MONTH,
				HOTTEST_MINUS_COLDEST, HOTTEST_MONTH_AVG_LOW, COLDEST_MONTH_AVG_LOW, ANNUAL_HUMIDITY, SUMMER_HUMIDITY,
				ANNUAL_DEW_POINT, SUMMER_DEW_POINT, HOTTEST_MONTHS_HEAT_INDEX_HIGH, ANNUAL_SUNSHINE, SUMMER_SUNSHINE,
				WINTER_SUNSHINE, ANNUAL_RAINFALL, SUMMER_RAINFALL, WINTER_RAINFALL, PERCENT_OF_DAYS_WITH_RAIN,
				PERCENT_OF_SUMMER_DAYS_WITH_RAIN, PERCENT_OF_WINTER_DAYS_WITH_RAIN, ANNUAL_SNOWFALL,
				DAYS_OF_SNOW_PER_YEAR, WIND_SPEED, ANNUAL_THUNDERSTORMS, AIR_QUALITY_IDX, ELEVATION, UV_INDEX,
				LAND_AREA, FIPS_CODE, LATITUDE, LONGITUDE,

		};
	}

	@Override
	public void readDataFromMap(Map<String, Integer> mapOfNameToIndex, String[] arr, Data data) {
		data.cityName = read(arr, mapOfNameToIndex, CITY);
		data.stateName = read(arr, mapOfNameToIndex, STATE);
		data.population = read(arr, mapOfNameToIndex, POPULATION);
		data.populationDensity = read(arr, mapOfNameToIndex, POPULATION_DENSITY);
		data.metro = read(arr, mapOfNameToIndex, METRO_NAME);
		data.metroPopulation = read(arr, mapOfNameToIndex, METRO_POP);
		data.hottestMonthsHigh = read(arr, mapOfNameToIndex, HOTTEST_MONTH);
		data.coldestHigh = read(arr, mapOfNameToIndex, COLDEST_MONTH);
		data.hottestMonthMinusColdestMonth = read(arr, mapOfNameToIndex, HOTTEST_MINUS_COLDEST);
		data.numInchesOfRain = read(arr, mapOfNameToIndex, ANNUAL_RAINFALL);
		data.annualSnowfall = read(arr, mapOfNameToIndex, ANNUAL_SNOWFALL);
		data.avgYearlyWindspeed = read(arr, mapOfNameToIndex, WIND_SPEED);
		data.medianAge = read(arr, mapOfNameToIndex, AGE);
		data.percentWithAtleastBachelors = read(arr, mapOfNameToIndex, BACHELORS);
		data.medianIncome = read(arr, mapOfNameToIndex, INCOME);
		data.povertyRate = read(arr, mapOfNameToIndex, POVERTY_RATE);
		data.avgApartmentRent = read(arr, mapOfNameToIndex, RENT);
		data.medianHomePrice = read(arr, mapOfNameToIndex, HOME_PRICE);
		data.homeSquareFeet = read(arr, mapOfNameToIndex, HOME_SQFT);
		data.costPerSquareFoot = read(arr, mapOfNameToIndex, COST_PER_SQFT);
		data.medianHomeAge = read(arr, mapOfNameToIndex, HOME_AGE);
		data.homeOwnershipRate = read(arr, mapOfNameToIndex, HOMEOWNERSHIP_RATE);
		data.airQuality = read(arr, mapOfNameToIndex, AIR_QUALITY_IDX);
		data.unemploymentRate = read(arr, mapOfNameToIndex, UNEMPLOYMENT_RATE);
		data.countyName = read(arr, mapOfNameToIndex, COUNTY);
		data.percentDemocrat = read(arr, mapOfNameToIndex, DEMOCRAT);
		data.percentRepublican = read(arr, mapOfNameToIndex, REPUBLICAN);
		data.percentAsian = read(arr, mapOfNameToIndex, ASIAN);
		data.percentBlack = read(arr, mapOfNameToIndex, BLACK);
		data.percentWhite = read(arr, mapOfNameToIndex, WHITE);
		data.percentHispanic = read(arr, mapOfNameToIndex, HISPANIC);
		data.foreignBornPercent = read(arr, mapOfNameToIndex, FOREIGN_BORN);
		data.timeZone = read(arr, mapOfNameToIndex, TIME_ZONE);
		data.feetAboveSeaLevel = read(arr, mapOfNameToIndex, ELEVATION);
		data.uvIndex = read(arr, mapOfNameToIndex, UV_INDEX);
		data.singlePopulation = read(arr, mapOfNameToIndex, SINGLE_POPULATION);
		data.fbiViolentCrimeRate = read(arr, mapOfNameToIndex, VIOLENT_CRIMES_FBI);
		data.fbiPropertyCrimeRate = read(arr, mapOfNameToIndex, PROPERTY_CRIMES_FBI);
		data.fipsCode = read(arr, mapOfNameToIndex, FIPS_CODE);
		data.landArea = read(arr, mapOfNameToIndex, LAND_AREA);
		data.laborForceParticipationRate = read(arr, mapOfNameToIndex, LABOR_FORCE);
		data.latitude = read(arr, mapOfNameToIndex, LATITUDE);
		data.longitude = read(arr, mapOfNameToIndex, LONGITUDE);
		data.annualSunshinePercent = read(arr, mapOfNameToIndex, ANNUAL_SUNSHINE);
		data.summerSunshinePercent = read(arr, mapOfNameToIndex, SUMMER_SUNSHINE);
		data.winterSunshinePercent = read(arr, mapOfNameToIndex, WINTER_SUNSHINE);
		data.annualHumidityPercent = read(arr, mapOfNameToIndex, ANNUAL_HUMIDITY);
		data.summerHumidityPercent = read(arr, mapOfNameToIndex, SUMMER_HUMIDITY);
		data.numberOfThunderstormsPerYear = read(arr, mapOfNameToIndex, ANNUAL_THUNDERSTORMS);
		data.annualDewPoint = read(arr, mapOfNameToIndex, ANNUAL_DEW_POINT);
		data.summerDewPoint = read(arr, mapOfNameToIndex, SUMMER_DEW_POINT);
		data.hottestMonthAvgLow = read(arr, mapOfNameToIndex, HOTTEST_MONTH_AVG_LOW);
		data.coldestMonthAvgLow = read(arr, mapOfNameToIndex, COLDEST_MONTH_AVG_LOW);
		data.numInchesOfRainPerSummer = read(arr, mapOfNameToIndex, SUMMER_RAINFALL);
		data.numInchesOfRainPerWinter = read(arr, mapOfNameToIndex, WINTER_RAINFALL);
		data.percentOfDaysWithRain = read(arr, mapOfNameToIndex, PERCENT_OF_DAYS_WITH_RAIN);
		data.percentOfSummerDaysWithRain = read(arr, mapOfNameToIndex, PERCENT_OF_SUMMER_DAYS_WITH_RAIN);
		data.percentOfWinterDaysWithRain = read(arr, mapOfNameToIndex, PERCENT_OF_WINTER_DAYS_WITH_RAIN);
		data.daysOfSnowPerYear = read(arr, mapOfNameToIndex, DAYS_OF_SNOW_PER_YEAR);
		data.hottestMonthsHeatIndexHigh = read(arr, mapOfNameToIndex, HOTTEST_MONTHS_HEAT_INDEX_HIGH);
	}

	@Override
	public void writeDataToArray(Data data, Map<String, Integer> mapOfNameToIndex, String[] arr) {
		arr[mapOfNameToIndex.get(CITY)] = data.cityName;
		arr[mapOfNameToIndex.get(STATE)] = data.stateName;
		arr[mapOfNameToIndex.get(POPULATION)] = data.population;
		arr[mapOfNameToIndex.get(POPULATION_DENSITY)] = data.populationDensity;
		arr[mapOfNameToIndex.get(METRO_NAME)] = data.metro;
		arr[mapOfNameToIndex.get(METRO_POP)] = data.metroPopulation;
		arr[mapOfNameToIndex.get(HOTTEST_MONTH)] = data.hottestMonthsHigh;
		arr[mapOfNameToIndex.get(COLDEST_MONTH)] = data.coldestHigh;
		arr[mapOfNameToIndex.get(HOTTEST_MINUS_COLDEST)] = data.hottestMonthMinusColdestMonth;
		arr[mapOfNameToIndex.get(ANNUAL_RAINFALL)] = data.numInchesOfRain;
		arr[mapOfNameToIndex.get(ANNUAL_SNOWFALL)] = data.annualSnowfall;
		arr[mapOfNameToIndex.get(WIND_SPEED)] = data.avgYearlyWindspeed;
		arr[mapOfNameToIndex.get(AGE)] = data.medianAge;
		arr[mapOfNameToIndex.get(BACHELORS)] = data.percentWithAtleastBachelors;
		arr[mapOfNameToIndex.get(INCOME)] = data.medianIncome;
		arr[mapOfNameToIndex.get(POVERTY_RATE)] = data.povertyRate;
		arr[mapOfNameToIndex.get(RENT)] = data.avgApartmentRent;
		arr[mapOfNameToIndex.get(HOME_PRICE)] = data.medianHomePrice;
		arr[mapOfNameToIndex.get(HOME_SQFT)] = data.homeSquareFeet;
		arr[mapOfNameToIndex.get(COST_PER_SQFT)] = data.costPerSquareFoot;
		arr[mapOfNameToIndex.get(HOME_AGE)] = data.medianHomeAge;
		arr[mapOfNameToIndex.get(HOMEOWNERSHIP_RATE)] = data.homeOwnershipRate;
		arr[mapOfNameToIndex.get(AIR_QUALITY_IDX)] = data.airQuality;
		arr[mapOfNameToIndex.get(UNEMPLOYMENT_RATE)] = data.unemploymentRate;
		arr[mapOfNameToIndex.get(COUNTY)] = data.countyName;
		arr[mapOfNameToIndex.get(DEMOCRAT)] = data.percentDemocrat;
		arr[mapOfNameToIndex.get(REPUBLICAN)] = data.percentRepublican;
		arr[mapOfNameToIndex.get(ASIAN)] = data.percentAsian;
		arr[mapOfNameToIndex.get(BLACK)] = data.percentBlack;
		arr[mapOfNameToIndex.get(WHITE)] = data.percentWhite;
		arr[mapOfNameToIndex.get(HISPANIC)] = data.percentHispanic;
		arr[mapOfNameToIndex.get(FOREIGN_BORN)] = data.foreignBornPercent;
		arr[mapOfNameToIndex.get(TIME_ZONE)] = data.timeZone;
		arr[mapOfNameToIndex.get(ELEVATION)] = data.feetAboveSeaLevel;
		arr[mapOfNameToIndex.get(UV_INDEX)] = data.uvIndex;
		arr[mapOfNameToIndex.get(SINGLE_POPULATION)] = data.singlePopulation;
		arr[mapOfNameToIndex.get(VIOLENT_CRIMES_FBI)] = data.fbiViolentCrimeRate;
		arr[mapOfNameToIndex.get(PROPERTY_CRIMES_FBI)] = data.fbiPropertyCrimeRate;
		arr[mapOfNameToIndex.get(FIPS_CODE)] = data.fipsCode;
		arr[mapOfNameToIndex.get(LAND_AREA)] = data.landArea;
		arr[mapOfNameToIndex.get(LABOR_FORCE)] = data.laborForceParticipationRate;
		arr[mapOfNameToIndex.get(LATITUDE)] = data.latitude;
		arr[mapOfNameToIndex.get(LONGITUDE)] = data.longitude;
		arr[mapOfNameToIndex.get(ANNUAL_SUNSHINE)] = data.annualSunshinePercent;
		arr[mapOfNameToIndex.get(SUMMER_SUNSHINE)] = data.summerSunshinePercent;
		arr[mapOfNameToIndex.get(WINTER_SUNSHINE)] = data.winterSunshinePercent;
		arr[mapOfNameToIndex.get(ANNUAL_HUMIDITY)] = data.annualHumidityPercent;
		arr[mapOfNameToIndex.get(SUMMER_HUMIDITY)] = data.summerHumidityPercent;
		arr[mapOfNameToIndex.get(ANNUAL_THUNDERSTORMS)] = data.numberOfThunderstormsPerYear;
		arr[mapOfNameToIndex.get(ANNUAL_DEW_POINT)] = data.annualDewPoint;
		arr[mapOfNameToIndex.get(SUMMER_DEW_POINT)] = data.summerDewPoint;
		arr[mapOfNameToIndex.get(HOTTEST_MONTH_AVG_LOW)] = data.hottestMonthAvgLow;
		arr[mapOfNameToIndex.get(COLDEST_MONTH_AVG_LOW)] = data.coldestMonthAvgLow;
		arr[mapOfNameToIndex.get(SUMMER_RAINFALL)] = data.numInchesOfRainPerSummer;
		arr[mapOfNameToIndex.get(WINTER_RAINFALL)] = data.numInchesOfRainPerWinter;
		arr[mapOfNameToIndex.get(PERCENT_OF_DAYS_WITH_RAIN)] = data.percentOfDaysWithRain;
		arr[mapOfNameToIndex.get(PERCENT_OF_SUMMER_DAYS_WITH_RAIN)] = data.percentOfSummerDaysWithRain;
		arr[mapOfNameToIndex.get(PERCENT_OF_WINTER_DAYS_WITH_RAIN)] = data.percentOfWinterDaysWithRain;
		arr[mapOfNameToIndex.get(DAYS_OF_SNOW_PER_YEAR)] = data.daysOfSnowPerYear;
		arr[mapOfNameToIndex.get(HOTTEST_MONTHS_HEAT_INDEX_HIGH)] = data.hottestMonthsHeatIndexHigh;
	}

}
