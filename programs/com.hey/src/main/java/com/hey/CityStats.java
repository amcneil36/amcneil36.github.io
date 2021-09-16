package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.hey.GenericDataReadAndWriter.Data;

public class CityStats {

	private static String startSt = "City,State,County,Population,People per sq mi,Hottest month's avg high (F),Coldest month's avg high (F),Hottest high minus coldest high,Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Average August humidity,Average December humidity,Average yearly humidity,Average yearly windspeed (mph),Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,Median household income,Median rent,Median home price,Median home square feet,Median home cost per sq ft,Median home age,Home appreciation Last Year,Home appreciation Last 5 Years,Home appreciation Last 10 Years,Air quality Index,Average one-way commute time,Unemployment rate,Job growth last year,Population growth since 2010,% Democrat,% Republican,% Asian,% Black,% White,% Hispanic,Metro,Timezone,Elevation (ft),UV Index,Home Ownership Rate,Single Population,Walk Score,Transit Score,Bike Score";

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
		public String avgAugustHumidity = "N/A";
		public String avgDecemberHumidity = "N/A";
		public String avgHumidity = "N/A";
		public String percentDemocrat = "N/A";
		public String percentRepublican = "N/A";
		public String unemploymentRate = "N/A";
		public String jobGrowthLastYear = "N/A";
		public String populationGrowthSince2010 = "N/A";
		public String percentAsian = "N/A";
		public String percentBlack = "N/A";
		public String percentWhite = "N/A";
		public String percentHispanic = "N/A";
		public String medianRent = "N/A";
		public String percentWithAtleastBachelors = "N/A";
		public String metro = "N/A";
		public String homeSquareFeet = "N/A";
		public String costPerSquareFoot = "N/A";
		public String timeZone = "N/A";
		public String feetAboveSeaLevel = "N/A";
		public String uvIndex = " \"N/A\"";
		public String homeOwnershipRate = " \"N/A\"";
		public String singlePopulation = " \"N/A\"";
		public String walkScore = " \"N/A\"";
		public String transitScore = " \"N/A\"";
		public String bikeScore = " \"N/A\"";

	}

	private static class AndrewStringWriter {
		StringBuilder sb = new StringBuilder();

		AndrewStringWriter appendWithComma(String st) {
			sb.append(st).append(",");
			return this;
		}

		AndrewStringWriter append(String st) {
			sb.append(st);
			return this;
		}

		AndrewStringWriter appendLastItem(String st) {
			sb.append(st);
			sb.append("\n");
			return this;
		}

		String getString() {
			return sb.toString();
		}
	}

	public static void main(String[] args) throws Exception {

		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\cityStats.csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String line = myReader.nextLine();
		List<Data> dataList = new ArrayList<>();
		Map<String, List<Data>> mapOfStateToData = new HashMap<>();
		while (myReader.hasNextLine()) {
			line = myReader.nextLine();
			String[] arr = line.split(",");
			if (arr.length != 49) {
				System.out.println(arr.length);
			}
			Data data = new Data();
			data.cityName = arr[0];
			data.stateName = arr[1];
			data.hottestMonthsHigh = arr[2];
			data.coldestHigh = arr[3];
			data.numInchesOfRain = arr[4];
			data.annualSnowfall = arr[5];
			data.numSunnyDays = arr[6];
			data.numDaysOfRain = arr[7];
			data.population = arr[8];
			data.populationDensity = arr[9];
			data.medianIncome = arr[10];
			data.medianHomePrice = arr[11];
			data.medianAge = arr[12];
			data.violentCrime = arr[13];
			data.propertyCrime = arr[14];
			data.airQuality = arr[15];
			data.medianHomeAge = arr[16];
			data.homeAppreciationLastYear = arr[17];
			data.homeAppreciationLastFiveYears = arr[18];
			data.homeAppreciationLastTenYears = arr[19];
			data.averageCommuteTime = arr[20];
			data.countyName = arr[21];
			data.hottestMonthMinusColdestMonth = arr[22];
			data.avgYearlyWindspeed = arr[23];
			data.avgAugustHumidity = arr[24];
			data.avgDecemberHumidity = arr[25];
			data.avgHumidity = arr[26];
			data.percentDemocrat = arr[27];
			data.percentRepublican = arr[28];
			data.unemploymentRate = arr[29];
			data.jobGrowthLastYear = arr[30];
			data.populationGrowthSince2010 = arr[31];
			data.percentAsian = arr[32];
			data.percentBlack = arr[33];
			data.percentWhite = arr[34];
			data.percentHispanic = arr[35];
			data.medianRent = arr[36];
			data.percentWithAtleastBachelors = arr[37];
			data.metro = arr[38];
			data.homeSquareFeet = arr[39];
			data.costPerSquareFoot = arr[40];
			data.timeZone = arr[41];
			data.feetAboveSeaLevel = arr[42];
			data.uvIndex = arr[43];
			data.homeOwnershipRate = arr[44];
			data.singlePopulation = arr[45];
			data.walkScore = arr[46];
			data.transitScore = arr[47];
			data.bikeScore = arr[48];
			dataList.add(data);
			List<Data> listOfData;
			if (mapOfStateToData.containsKey(data.stateName)) {
				listOfData = mapOfStateToData.get(data.stateName);
			} else {
				listOfData = new ArrayList<>();
			}
			listOfData.add(data);
			mapOfStateToData.put(data.stateName, listOfData);
		}

		myReader.close();
		Set<String> keys = mapOfStateToData.keySet();
		for (String key : keys) {
			String filePath3 = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + key + ".csv";
			FileWriter myWriter = new FileWriter(filePath3);
			AndrewStringWriter sb = new AndrewStringWriter();
			List<Data> listOfData = mapOfStateToData.get(key);
			sb.appendLastItem(startSt);
			for (Data data : listOfData) {
				sb.appendWithComma(data.cityName).appendWithComma(data.stateName)
						.appendWithComma(data.hottestMonthsHigh).appendWithComma(data.coldestHigh)
						.appendWithComma(data.numInchesOfRain).appendWithComma(data.annualSnowfall)
						.appendWithComma(data.numSunnyDays).appendWithComma(data.numDaysOfRain)
						.appendWithComma(data.population).appendWithComma(data.populationDensity)
						.appendWithComma(data.medianIncome).appendWithComma(data.medianHomePrice)
						.appendWithComma(data.medianAge).appendWithComma(data.violentCrime)
						.appendWithComma(data.propertyCrime).appendWithComma(data.airQuality)
						.appendWithComma(data.medianHomeAge).appendWithComma(data.homeAppreciationLastYear)
						.appendWithComma(data.homeAppreciationLastFiveYears)
						.appendWithComma(data.homeAppreciationLastTenYears).appendWithComma(data.averageCommuteTime)
						.appendWithComma(data.countyName).appendWithComma(data.hottestMonthMinusColdestMonth)
						.appendWithComma(data.avgYearlyWindspeed).appendWithComma(data.avgAugustHumidity)
						.appendWithComma(data.avgDecemberHumidity).appendWithComma(data.avgHumidity)
						.appendWithComma(data.percentDemocrat).appendWithComma(data.percentRepublican)
						.appendWithComma(data.unemploymentRate).appendWithComma(data.jobGrowthLastYear)
						.appendWithComma(data.populationGrowthSince2010).appendWithComma(data.percentAsian)
						.appendWithComma(data.percentBlack).appendWithComma(data.percentWhite)
						.appendWithComma(data.percentHispanic).appendWithComma(data.medianRent)
						.appendWithComma(data.percentWithAtleastBachelors).appendWithComma(data.metro)
						.appendWithComma(data.homeSquareFeet).appendWithComma(data.costPerSquareFoot)
						.appendWithComma(data.timeZone).appendWithComma(data.feetAboveSeaLevel)
						.appendWithComma(data.uvIndex).appendWithComma(data.homeOwnershipRate)
						.appendWithComma(data.singlePopulation).appendWithComma(data.walkScore)
						.appendWithComma(data.transitScore).appendLastItem(data.bikeScore);
			}
			String st = sb.getString();
			myWriter.write(st);
			myWriter.close();
		}

	}

}
