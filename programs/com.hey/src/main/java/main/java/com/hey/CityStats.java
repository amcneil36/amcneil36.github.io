package main.java.com.hey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public abstract class CityStats extends CityStatsSuper {

	public static class Data {
		public String cityName = "N/A";
		public String stateName = "N/A";
		public String hottestMonthsHigh = "N/A";
		public String coldestHigh = "N/A";
		public String numInchesOfRain = "N/A";
		public String annualSnowfall = "N/A";
		public String numSunnyDays = "N/A";
		public String numDaysOfRain = "N/A";
		public String population = "N/A";
		public String populationDensity = "N/A";
		public String medianIncome = "N/A";
		public String medianHomePrice = "N/A";
		public String medianAge = "N/A";
		public String violentCrime = "N/A";
		public String propertyCrime = "N/A";
		public String airQuality = "N/A";
		public String medianHomeAge = "N/A";
		public String homeAppreciationLastYear = "N/A";
		public String homeAppreciationLastFiveYears = "N/A";
		public String homeAppreciationLastTenYears = "N/A";
		public String averageCommuteTime = "N/A";
		public String countyName = "N/A";
		public String hottestMonthMinusColdestMonth = "N/A";
		public String avgYearlyWindspeed = "N/A";
		public String percentDemocrat = "N/A";
		public String percentRepublican = "N/A";
		public String unemploymentRate = "N/A";
		public String jobGrowthLastYear = "N/A";
		public String populationGrowthSince2010 = "N/A";
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
		public String walkScore = "N/A";
		public String transitScore = "N/A";
		public String bikeScore = "N/A";
		public String povertyRate = "N/A";
		public String percentOfIncomeLostToHousingCosts = "N/A";
		public String foreignBornPercent = "N/A";
		public String avgSummerDewPoint = "N/A";
		public String avgAnnualDewPoint = "N/A";
		public String sexOffenderCount = "N/A";
		public String hurricanes = "N/A";
		public String tornadoes = "N/A";
		public String earthQuakes = "N/A";
		public String avgApartmentRent = "N/A";
		public String avgApartmentSize = "N/A";
		public String metroPopulation = "N/A";
		public String fbiViolentCrimeRate = "N/A";
		public String fbiPropertyCrimeRate = "N/A";
		public String fipsCode = "N/A";
		public String landArea = "N/A";
		public String laborForceParticipationRate = "N/A";
		
		@Override
		 public String toString() {
		     return ReflectionToStringBuilder.toString(this);
		 }
	}

	private static final String CITY = "City";
	private static final String STATE = "State";
	private static final String POPULATION = "Population";
	private static final String POPULATION_DENSITY = "People per sq mi";
	private static final String METRO_NAME = "Metro Name";
	private static final String METRO_POP = "Metro population";
	private static final String HOTTEST_MONTH = "Hottest month's avg high (F)";
	private static final String COLDEST_MONTH = "Coldest month's avg high (F)";
	private static final String HOTTEST_MINUS_COLDEST = "Hottest high minus coldest high";
	private static final String ANNUAL_RAINFALL = "Annual rainfall (in)";
	private static final String ANNUAL_DAYS_OF_PRECIPITATION = "Annual days of precipitation";
	private static final String ANNUAL_DAYS_OF_SUNSHINE = "Annual days of sunshine";
	private static final String ANNUAL_SNOWFALL = "Annual snowfall (in)";
	private static final String AVERAGE_SUMMER_DEW_POINT = "Avg Summer Dew Point";
	private static final String DEW_POINT = "Avg Annual Dew Point";
	private static final String WIND_SPEED = "Average yearly windspeed (mph)";
	private static final String VIOLENT_CRIME_INDEX = "Violent crime index";
	private static final String PROPERTY_CRIME_INDEX = "Property crime index";
	private static final String AGE = "Median age";
	private static final String BACHELORS = "% with at least Bachelor's degree";
	private static final String INCOME = "Median household income";
	private static final String POVERTY_RATE = "Poverty Rate";
	private static final String RENT = "Avg Apartment Monthly Rent";
	private static final String SQFT = "Avg Apartment sqft";
	private static final String HOME_PRICE = "Median home price";
	private static final String HOME_SQFT = "Median home sqft";
	private static final String COST_PER_SQFT = "Median home cost per sqft";
	private static final String HOME_AGE = "Median home age";
	private static final String HOMEOWNERSHIP_RATE = "Homeownership Rate";
	private static final String HOME_APPRECIATION = "Home appreciation Last Year";
	private static final String HOME_APPRECIAITON_LAST_5_YEARS = "Home appreciation Last 5 Years";
	private static final String HOME_APPRECIATION_LAST_10_YEARS = "Home appreciation Last 10 Years";
	private static final String AIR_QUALITY_IDX = "Air quality Index";
	private static final String COMMUTE = "Average one-way commute time";
	private static final String UNEMPLOYMENT_RATE = "Unemployment rate";
	private static final String JOB_GROWTH = "Job growth last year";
	private static final String POPULATION_GROWTH = "Population growth since 2010";
	private static final String COUNTY = "County";
	private static final String DEMOCRAT = "% Democrat";
	private static final String REPUBLICAN = "% Republican";
	private static final String ASIAN = "% Asian";
	private static final String BLACK = "% Black";
	private static final String WHITE = "% Non-Hispanic White";
	private static final String HISPANIC = "% Hispanic";
	private static final String FOREIGN_BORN = "Foreign Born %";
	private static final String TIME_ZONE = "Timezone";
	private static final String ELEVATION = "Elevation (ft)";
	private static final String UV_INDEX = "UV Index";
	private static final String SINGLE_POPULATION = "Single Population";
	private static final String WALK_SCORE = "Walk Score";
	private static final String TRANSIT_SCORE = "Transit Score";
	private static final String BIKE_SCORE = "Bike Score";
	private static final String INCOME_SPENT = "% of income spent on housing costs (owners)";
	private static final String SEX_OFFENDERS = "Number of sex offenders per 10k residents";
	private static final String HURRICANES = "Number of hurricanes since 1930";
	private static final String TORNADOES = "Number of tornadoes per year";
	private static final String EARTHQUAKES = "Number of earthquakes since 1931";
	private static final String VIOLENT_CRIMES_FBI = "Num Violent Crimes Per 100k residents";
	private static final String PROPERTY_CRIMES_FBI = "Num Property Crimes Per 100k residents";
	private static final String FIPS_CODE = "Fips Code";
	private static final String LAND_AREA = "Land Area (sq mi)";
	private static final String LABOR_FORCE = "Labor Force Participation rate";
	
	@Override
	public String getStartString() {
		return new AndrewStringWriter().appendWithComma(CITY).appendWithComma(STATE).appendWithComma(POPULATION)
				.appendWithComma(POPULATION_DENSITY).appendWithComma(METRO_NAME).appendWithComma(METRO_POP)
				.appendWithComma(HOTTEST_MONTH).appendWithComma(COLDEST_MONTH).appendWithComma(HOTTEST_MINUS_COLDEST).appendWithComma(ANNUAL_RAINFALL)
				.appendWithComma(ANNUAL_DAYS_OF_PRECIPITATION).appendWithComma(ANNUAL_DAYS_OF_SUNSHINE)
				.appendWithComma(ANNUAL_SNOWFALL).appendWithComma(AVERAGE_SUMMER_DEW_POINT).appendWithComma(DEW_POINT)
				.appendWithComma(WIND_SPEED).appendWithComma(VIOLENT_CRIME_INDEX).appendWithComma(PROPERTY_CRIME_INDEX)
				.appendWithComma(AGE).appendWithComma(BACHELORS).appendWithComma(INCOME).appendWithComma(POVERTY_RATE)
				.appendWithComma(RENT).appendWithComma(SQFT).appendWithComma(HOME_PRICE).appendWithComma(HOME_SQFT)
				.appendWithComma(COST_PER_SQFT).appendWithComma(HOME_AGE).appendWithComma(HOMEOWNERSHIP_RATE)
				.appendWithComma(HOME_APPRECIATION).appendWithComma(HOME_APPRECIAITON_LAST_5_YEARS)
				.appendWithComma(HOME_APPRECIATION_LAST_10_YEARS).appendWithComma(AIR_QUALITY_IDX)
				.appendWithComma(COMMUTE).appendWithComma(UNEMPLOYMENT_RATE).appendWithComma(JOB_GROWTH)
				.appendWithComma(POPULATION_GROWTH).appendWithComma(COUNTY).appendWithComma(DEMOCRAT)
				.appendWithComma(REPUBLICAN).appendWithComma(ASIAN).appendWithComma(BLACK).appendWithComma(WHITE)
				.appendWithComma(HISPANIC).appendWithComma(FOREIGN_BORN).appendWithComma(TIME_ZONE)
				.appendWithComma(ELEVATION).appendWithComma(UV_INDEX).appendWithComma(SINGLE_POPULATION)
				.appendWithComma(WALK_SCORE).appendWithComma(TRANSIT_SCORE).appendWithComma(BIKE_SCORE)
				.appendWithComma(INCOME_SPENT).appendWithComma(SEX_OFFENDERS).appendWithComma(HURRICANES)
				.appendWithComma(TORNADOES).appendWithComma(EARTHQUAKES).appendWithComma(VIOLENT_CRIMES_FBI)
				.appendWithComma(PROPERTY_CRIMES_FBI).appendWithComma(FIPS_CODE).appendWithComma(LAND_AREA)
				.appendWithComma(LABOR_FORCE).removeLastElement().getString();
	}

	@Override
	public List<Data> readData(String stateName) throws Exception {
		List<Data> dataList = new ArrayList<>();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String header = myReader.nextLine(); //
		Map<String, Integer> mapOfNameToIndex = createMapOfNameToIndex(header);
		System.out.println(mapOfNameToIndex);
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			Data data = new Data();
			data.cityName = arr[mapOfNameToIndex.get(CITY)];
			data.stateName = arr[mapOfNameToIndex.get(STATE)];
			data.population = arr[mapOfNameToIndex.get(POPULATION)];
			data.populationDensity = arr[mapOfNameToIndex.get(POPULATION_DENSITY)];
			data.metro = arr[mapOfNameToIndex.get(METRO_NAME)];
			data.metroPopulation = arr[mapOfNameToIndex.get(METRO_POP)];
			data.hottestMonthsHigh = arr[mapOfNameToIndex.get(HOTTEST_MONTH)];
			data.coldestHigh = arr[mapOfNameToIndex.get(COLDEST_MONTH)];
			data.hottestMonthMinusColdestMonth = arr[mapOfNameToIndex.get(HOTTEST_MINUS_COLDEST)];
			data.numInchesOfRain = arr[mapOfNameToIndex.get(ANNUAL_RAINFALL)];
			data.numDaysOfRain = arr[mapOfNameToIndex.get(ANNUAL_DAYS_OF_PRECIPITATION)];
			data.numSunnyDays = arr[mapOfNameToIndex.get(ANNUAL_DAYS_OF_SUNSHINE)];
			data.annualSnowfall = arr[mapOfNameToIndex.get(ANNUAL_SNOWFALL)];
			data.avgSummerDewPoint = arr[mapOfNameToIndex.get(AVERAGE_SUMMER_DEW_POINT)];
			data.avgAnnualDewPoint = arr[mapOfNameToIndex.get(DEW_POINT)];
			data.avgYearlyWindspeed = arr[mapOfNameToIndex.get(WIND_SPEED)];
			data.violentCrime = arr[mapOfNameToIndex.get(VIOLENT_CRIME_INDEX)];
			data.propertyCrime = arr[mapOfNameToIndex.get(PROPERTY_CRIME_INDEX)];
			data.medianAge = arr[mapOfNameToIndex.get(AGE)];
			data.percentWithAtleastBachelors = arr[mapOfNameToIndex.get(BACHELORS)];
			data.medianIncome = arr[mapOfNameToIndex.get(INCOME)];
			data.povertyRate = arr[mapOfNameToIndex.get(POVERTY_RATE)];
			data.avgApartmentRent = arr[mapOfNameToIndex.get(RENT)];
			data.avgApartmentSize = arr[mapOfNameToIndex.get(SQFT)];
			data.medianHomePrice = arr[mapOfNameToIndex.get(HOME_PRICE)];
			data.homeSquareFeet = arr[mapOfNameToIndex.get(HOME_SQFT)];
			data.costPerSquareFoot = arr[mapOfNameToIndex.get(COST_PER_SQFT)];
			data.medianHomeAge = arr[mapOfNameToIndex.get(HOME_AGE)];
			data.homeOwnershipRate = arr[mapOfNameToIndex.get(HOMEOWNERSHIP_RATE)];
			data.homeAppreciationLastYear = arr[mapOfNameToIndex.get(HOME_APPRECIATION)];
			data.homeAppreciationLastFiveYears = arr[mapOfNameToIndex.get(HOME_APPRECIAITON_LAST_5_YEARS)];
			data.homeAppreciationLastTenYears = arr[mapOfNameToIndex.get(HOME_APPRECIATION_LAST_10_YEARS)];
			data.airQuality = arr[mapOfNameToIndex.get(AIR_QUALITY_IDX)];
			data.averageCommuteTime = arr[mapOfNameToIndex.get(COMMUTE)];
			data.unemploymentRate = arr[mapOfNameToIndex.get(UNEMPLOYMENT_RATE)];
			data.jobGrowthLastYear = arr[mapOfNameToIndex.get(JOB_GROWTH)];
			data.populationGrowthSince2010 = arr[mapOfNameToIndex.get(POPULATION_GROWTH)];
			data.countyName = arr[mapOfNameToIndex.get(COUNTY)];
			data.percentDemocrat = arr[mapOfNameToIndex.get(DEMOCRAT)];
			data.percentRepublican = arr[mapOfNameToIndex.get(REPUBLICAN)];
			data.percentAsian = arr[mapOfNameToIndex.get(ASIAN)];
			data.percentBlack = arr[mapOfNameToIndex.get(BLACK)];
			data.percentWhite = arr[mapOfNameToIndex.get(WHITE)];
			data.percentHispanic = arr[mapOfNameToIndex.get(HISPANIC)];
			data.foreignBornPercent = arr[mapOfNameToIndex.get(FOREIGN_BORN)];
			data.timeZone = arr[mapOfNameToIndex.get(TIME_ZONE)];
			data.feetAboveSeaLevel = arr[mapOfNameToIndex.get(ELEVATION)];
			data.uvIndex = arr[mapOfNameToIndex.get(UV_INDEX)];
			data.singlePopulation = arr[mapOfNameToIndex.get(SINGLE_POPULATION)];
			data.walkScore = arr[mapOfNameToIndex.get(WALK_SCORE)];
			data.transitScore = arr[mapOfNameToIndex.get(TRANSIT_SCORE)];
			data.bikeScore = arr[mapOfNameToIndex.get(BIKE_SCORE)];
			data.percentOfIncomeLostToHousingCosts = arr[mapOfNameToIndex.get(INCOME_SPENT)];
			data.sexOffenderCount = arr[mapOfNameToIndex.get(SEX_OFFENDERS)];
			data.hurricanes = arr[mapOfNameToIndex.get(HURRICANES)];
			data.tornadoes = arr[mapOfNameToIndex.get(TORNADOES)];
			data.earthQuakes = arr[mapOfNameToIndex.get(EARTHQUAKES)];
			data.fbiViolentCrimeRate = arr[mapOfNameToIndex.get(VIOLENT_CRIMES_FBI)];
			data.fbiPropertyCrimeRate = arr[mapOfNameToIndex.get(PROPERTY_CRIMES_FBI)];
			data.fipsCode = arr[mapOfNameToIndex.get(FIPS_CODE)];
			data.landArea = arr[mapOfNameToIndex.get(LAND_AREA)];
			data.laborForceParticipationRate = arr[mapOfNameToIndex.get(LABOR_FORCE)];
			dataList.add(data);
		}
		myReader.close();
		return dataList;
	}

	private Map<String, Integer> createMapOfNameToIndex(String header) {
		Map<String, Integer> map = new HashMap<>();
		String[] arr = header.split(",");
		for (int i = 0; i < arr.length; i++) {
			map.put(arr[i], i);
		}
		return map;
	}

	public void appendRowToSb(AndrewStringWriter sb, Data data) {
		sb.appendWithComma(data.cityName).appendWithComma(data.stateName).appendWithComma(data.population)
				.appendWithComma(data.populationDensity).appendWithComma(data.metro)
				.appendWithComma(data.metroPopulation).appendWithComma(data.hottestMonthsHigh)
				.appendWithComma(data.coldestHigh).appendWithComma(data.hottestMonthMinusColdestMonth)
				.appendWithComma(data.numInchesOfRain).appendWithComma(data.numDaysOfRain)
				.appendWithComma(data.numSunnyDays).appendWithComma(data.annualSnowfall)
				.appendWithComma(data.avgSummerDewPoint).appendWithComma(data.avgAnnualDewPoint)
				.appendWithComma(data.avgYearlyWindspeed).appendWithComma(data.violentCrime)
				.appendWithComma(data.propertyCrime).appendWithComma(data.medianAge)
				.appendWithComma(data.percentWithAtleastBachelors).appendDollar(data.medianIncome)
				.appendWithComma(data.povertyRate).appendWithComma(data.avgApartmentRent)
				.appendWithComma(data.avgApartmentSize).appendDollar(data.medianHomePrice)
				.appendWithComma(data.homeSquareFeet).appendDollar(data.costPerSquareFoot)
				.appendWithComma(data.medianHomeAge).appendWithComma(data.homeOwnershipRate)
				.appendWithComma(data.homeAppreciationLastYear).appendWithComma(data.homeAppreciationLastFiveYears)
				.appendWithComma(data.homeAppreciationLastTenYears).appendWithComma(data.airQuality)
				.appendWithComma(data.averageCommuteTime).appendWithComma(data.unemploymentRate)
				.appendWithComma(data.jobGrowthLastYear).appendWithComma(data.populationGrowthSince2010)
				.appendWithComma(data.countyName).appendWithComma(data.percentDemocrat)
				.appendWithComma(data.percentRepublican).appendWithComma(data.percentAsian)
				.appendWithComma(data.percentBlack).appendWithComma(data.percentWhite)
				.appendWithComma(data.percentHispanic).appendWithComma(data.foreignBornPercent)
				.appendWithComma(data.timeZone).appendWithComma(data.feetAboveSeaLevel).appendWithComma(data.uvIndex)
				.appendWithComma(data.singlePopulation).appendWithComma(data.walkScore)
				.appendWithComma(data.transitScore).appendWithComma(data.bikeScore)
				.appendWithComma(data.percentOfIncomeLostToHousingCosts).appendWithComma(data.sexOffenderCount)
				.appendWithComma(data.hurricanes).appendWithComma(data.tornadoes).appendWithComma(data.earthQuakes)
				.appendWithComma(data.fbiViolentCrimeRate).appendWithComma(data.fbiPropertyCrimeRate)
				.appendWithComma(data.fipsCode).appendWithComma(data.landArea)
				.appendLastItem(data.laborForceParticipationRate);
	}

}
