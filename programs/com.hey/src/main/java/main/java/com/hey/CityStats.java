package main.java.com.hey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
	}

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

	//////////
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
	public List<Data> readData(String stateName) throws Exception {
		List<Data> dataList = new ArrayList<>();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String header = myReader.nextLine(); //
		Map<String, Integer> mapOfNameToIndex = createMapOfNameToIndex(header);
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			Data data = new Data();
			int idx = 0;
			data.cityName = arr[idx++];
			data.stateName = arr[idx++];
			data.population = arr[idx++];
			data.populationDensity = arr[idx++];
			data.metro = arr[idx++];
			data.metroPopulation = arr[idx++];
			data.hottestMonthsHigh = arr[idx++];
			data.coldestHigh = arr[idx++];
			data.hottestMonthMinusColdestMonth = arr[idx++];
			data.numInchesOfRain = arr[idx++];
			data.numDaysOfRain = arr[idx++];
			data.numSunnyDays = arr[idx++];
			data.annualSnowfall = arr[idx++];
			data.avgSummerDewPoint = arr[idx++];
			data.avgAnnualDewPoint = arr[idx++];
			data.avgYearlyWindspeed = arr[idx++];
			data.violentCrime = arr[idx++];
			data.propertyCrime = arr[idx++];
			data.medianAge = arr[idx++];
			data.percentWithAtleastBachelors = arr[idx++];
			data.medianIncome = arr[idx++];
			data.povertyRate = arr[idx++];
			data.avgApartmentRent = arr[idx++];
			data.avgApartmentSize = arr[idx++];
			data.medianHomePrice = arr[idx++];
			data.homeSquareFeet = arr[idx++];
			data.costPerSquareFoot = arr[idx++];
			data.medianHomeAge = arr[idx++];
			data.homeOwnershipRate = arr[idx++];
			data.homeAppreciationLastYear = arr[idx++];
			data.homeAppreciationLastFiveYears = arr[idx++];
			data.homeAppreciationLastTenYears = arr[idx++];
			data.airQuality = arr[idx++];
			data.averageCommuteTime = arr[idx++];
			data.unemploymentRate = arr[idx++];
			data.jobGrowthLastYear = arr[idx++];
			data.populationGrowthSince2010 = arr[idx++];
			data.countyName = arr[idx++];
			data.percentDemocrat = arr[idx++];
			data.percentRepublican = arr[idx++];
			data.percentAsian = arr[idx++];
			data.percentBlack = arr[idx++];
			data.percentWhite = arr[idx++];
			data.percentHispanic = arr[idx++];
			data.foreignBornPercent = arr[idx++];
			data.timeZone = arr[idx++];
			data.feetAboveSeaLevel = arr[idx++];
			data.uvIndex = arr[idx++];
			data.singlePopulation = arr[idx++];
			data.walkScore = arr[idx++];
			data.transitScore = arr[idx++];
			data.bikeScore = arr[idx++];
			data.percentOfIncomeLostToHousingCosts = arr[idx++];
			data.sexOffenderCount = arr[idx++];
			data.hurricanes = arr[idx++];
			data.tornadoes = arr[idx++];
			data.earthQuakes = arr[idx++];
			data.fbiViolentCrimeRate = arr[idx++];
			data.fbiPropertyCrimeRate = arr[idx++];
			data.fipsCode = arr[idx++];
			data.landArea = arr[idx++];
			data.laborForceParticipationRate = arr[idx++];
			if (idx != arr.length) {
				throw new RuntimeException("didn't read all elements!");
			}
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
