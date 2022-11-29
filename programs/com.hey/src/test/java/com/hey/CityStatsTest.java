package com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;
import main.java.com.hey.city.stats.BachelorsDegree;
import main.java.com.hey.city.stats.NoopCityStats;

public class CityStatsTest {

	@Test
	public void testFoo() {
		CityStats cs = new BachelorsDegree();
		String expected = "City,State,Population,People per sq mi,Metro Name,Metro population,Hottest month's avg high (F),Coldest month's avg high (F),Hottest high minus coldest high,Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,Median household income,Poverty Rate,Avg Apartment Monthly Rent,Avg Apartment sqft,Median home price,Median home sqft,Median home cost per sqft,Median home age,Homeownership Rate,Home appreciation Last Year,Home appreciation Last 5 Years,Home appreciation Last 10 Years,Air quality Index,Average one-way commute time,Unemployment rate,Job growth last year,Population growth since 2010,County,% Democrat,% Republican,% Asian,% Black,% Non-Hispanic White,% Hispanic,Foreign Born %,Timezone,Elevation (ft),UV Index,Single Population,Walk Score,Transit Score,Bike Score,% of income spent on housing costs (owners),Number of sex offenders per 10k residents,Number of hurricanes since 1930,Number of tornadoes per year,Number of earthquakes since 1931,Num Violent Crimes Per 100k residents,Num Property Crimes Per 100k residents,Fips Code,Land Area (sq mi),Labor Force Participation rate";
		Assert.assertEquals(cs.getStartString(), expected);
	}

	@Test
	public void testFoo2() throws Exception {
		CityStats cs = new BachelorsDegree();
		List<Data> list = cs.readData("Florida");
		Data data = list.get(12);
		String expected = "airQuality=68,annualSnowfall=0,averageCommuteTime=33 mins,avgAnnualDewPoint=63.5,avgApartmentRent=$1559,avgApartmentSize=1026,avgSummerDewPoint=73.2,avgYearlyWindspeed=N/A,bikeScore=52,cityName=Apollo Beach,coldestHigh=70,costPerSquareFoot=$219,countyName=Hillsborough County,earthQuakes=0,fbiPropertyCrimeRate=N/A,fbiViolentCrimeRate=N/A,feetAboveSeaLevel=57,fipsCode=FL-01675,foreignBornPercent=9.31%,homeAppreciationLastFiveYears=46%,homeAppreciationLastTenYears=32%,homeAppreciationLastYear=6%,homeOwnershipRate=83.09%,homeSquareFeet=2023,hottestMonthMinusColdestMonth=20,hottestMonthsHigh=90,hurricanes=69,jobGrowthLastYear=2.30%,laborForceParticipationRate=65.32%,landArea=19.67,medianAge=44,medianHomeAge=16,medianHomePrice=$443000,medianIncome=$95980,metro=Tampa-St. Petersburg-Clearwater,metroPopulation=3243963,numDaysOfRain=100,numInchesOfRain=51,numSunnyDays=241,percentAsian=2.05%,percentBlack=12.13%,percentDemocrat=51.00%,percentHispanic=15.31%,percentOfIncomeLostToHousingCosts=19.6%,percentRepublican=44.20%,percentWhite=65.6%,percentWithAtleastBachelors=43.45%,population=23487,populationDensity=1194,populationGrowthSince2010=33.00%,povertyRate=3.88%,propertyCrime=40,sexOffenderCount=8.81,singlePopulation=35.90%,stateName=Florida,timeZone=Eastern Standard Time,tornadoes=6,transitScore=N/A,unemploymentRate=4.2%,uvIndex=6.9,violentCrime=20,walkScore=32]";
		Assert.assertTrue(data.toString().contains(expected));
	}

	@Test
	public void testFoo3() throws Exception {
		CityStats cs = new BachelorsDegree();
		List<Data> list = cs.readData("Florida");
		Data data = list.get(12);
		AndrewStringWriter sb = new AndrewStringWriter();
		String[] headers = cs.getHeaders();
		Map<String, Integer> mapOfNameToIndex = new HashMap<>();
		for (int i = 0; i < headers.length; i++) {
			mapOfNameToIndex.put(headers[i], i);
		}
		cs.appendRowToSb(sb, data, mapOfNameToIndex);
		String actual = sb.getString();
		AndrewStringWriter sb2 = new AndrewStringWriter();
		cs.appendRowToSbOld(sb2, data, mapOfNameToIndex);
		String expected = sb2.getString();
		Assert.assertEquals(actual, expected);
	}

}
