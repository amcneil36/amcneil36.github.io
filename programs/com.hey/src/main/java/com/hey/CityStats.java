package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class CityStats {

	private static String startSt = "City,State,County,Population,People per sq mi,Hottest month's avg high (F),Coldest month's avg high (F),Hottest high minus coldest high,Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,Median household income,Median rent,Median home price,Median home square feet,Median home cost per sq ft,Median home age,Home appreciation Last Year,Home appreciation Last 5 Years,Home appreciation Last 10 Years,Air quality Index,Average one-way commute time,Unemployment rate,Job growth last year,Population growth since 2010,% Democrat,% Republican,% Asian,% Black,% White,% Hispanic,Metro,Timezone,Elevation (ft),UV Index,Home Ownership Rate,Single Population,Walk Score,Transit Score,Bike Score,Poverty Rate, % of income spent on housing costs (owners),Foreign Born %,Ratio of all residents to sex offenders";

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
	}

	private static class AndrewStringWriter {
		StringBuilder sb = new StringBuilder();

		AndrewStringWriter appendDollar(String st) {
			if (!st.contains("$") && !st.contains("N/A")) {
				sb.append("$").append(st).append(",");
				return this;
			}
			sb.append(st).append(",");
			return this;
		}

		AndrewStringWriter appendWithComma(String st) {
			sb.append(st).append(",");
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

	public static List<Data> readData(String stateName) throws Exception {
		List<Data> dataList = new ArrayList<>();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine(); // skipHeader
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			Data data = new Data();
			data.cityName = arr[0];
			data.stateName = arr[1];
			data.countyName = arr[2];
			data.population = arr[3];
			data.populationDensity = arr[4];
			data.hottestMonthsHigh = arr[5];
			data.coldestHigh = arr[6];
			data.hottestMonthMinusColdestMonth = arr[7];
			data.numInchesOfRain = arr[8];
			data.numDaysOfRain = arr[9];
			data.numSunnyDays = arr[10];
			data.annualSnowfall = arr[11];
			data.avgSummerDewPoint = arr[12];
			data.avgAnnualDewPoint = arr[13];
			data.avgYearlyWindspeed = arr[14];
			data.violentCrime = arr[15];
			data.propertyCrime = arr[16];
			data.medianAge = arr[17];
			data.percentWithAtleastBachelors = arr[18];
			data.medianIncome = arr[19];
			data.medianRent = arr[20];
			data.medianHomePrice = arr[21];
			data.homeSquareFeet = arr[22];
			data.costPerSquareFoot = arr[23];
			data.medianHomeAge = arr[24];
			data.homeAppreciationLastYear = arr[25];
			data.homeAppreciationLastFiveYears = arr[26];
			data.homeAppreciationLastTenYears = arr[27];
			data.airQuality = arr[28];
			data.averageCommuteTime = arr[29];
			data.unemploymentRate = arr[30];
			data.jobGrowthLastYear = arr[31];
			data.populationGrowthSince2010 = arr[32];
			data.percentDemocrat = arr[33];
			data.percentRepublican = arr[34];
			data.percentAsian = arr[35];
			data.percentBlack = arr[36];
			data.percentWhite = arr[37];
			data.percentHispanic = arr[38];
			data.metro = arr[39];
			data.timeZone = arr[40];
			data.feetAboveSeaLevel = arr[41];
			try {
				data.uvIndex = arr[42];
				data.homeOwnershipRate = arr[43];
				data.singlePopulation = arr[44];
				data.walkScore = arr[45];
				data.transitScore = arr[46];
				data.bikeScore = arr[47];
				data.povertyRate = arr[48];
				data.percentOfIncomeLostToHousingCosts = arr[49];
				data.foreignBornPercent = arr[50];
				data.sexOffenderCount = arr[51];
			} catch (Exception ex) {

			}
			dataList.add(data);
		}
		myReader.close();
		return dataList;
	}

	public static void writeDataToPath(List<Data> dataList, String filePath, boolean isLastWrite) throws Exception {
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(startSt);
		for (Data data : dataList) {
			sb.appendWithComma(data.cityName).appendWithComma(data.stateName).appendWithComma(data.countyName)
					.appendWithComma(data.population).appendWithComma(data.populationDensity)
					.appendWithComma(data.hottestMonthsHigh).appendWithComma(data.coldestHigh)
					.appendWithComma(data.hottestMonthMinusColdestMonth).appendWithComma(data.numInchesOfRain)
					.appendWithComma(data.numDaysOfRain).appendWithComma(data.numSunnyDays)
					.appendWithComma(data.annualSnowfall).appendWithComma(data.avgSummerDewPoint)
					.appendWithComma(data.avgAnnualDewPoint).appendWithComma(data.avgYearlyWindspeed)
					.appendWithComma(data.violentCrime).appendWithComma(data.propertyCrime)
					.appendWithComma(data.medianAge).appendWithComma(data.percentWithAtleastBachelors)
					.appendDollar(data.medianIncome).appendDollar(data.medianRent).appendDollar(data.medianHomePrice)
					.appendWithComma(data.homeSquareFeet).appendDollar(data.costPerSquareFoot)
					.appendWithComma(data.medianHomeAge).appendWithComma(data.homeAppreciationLastYear)
					.appendWithComma(data.homeAppreciationLastFiveYears)
					.appendWithComma(data.homeAppreciationLastTenYears).appendWithComma(data.airQuality)
					.appendWithComma(data.averageCommuteTime).appendWithComma(data.unemploymentRate)
					.appendWithComma(data.jobGrowthLastYear).appendWithComma(data.populationGrowthSince2010)
					.appendWithComma(data.percentDemocrat).appendWithComma(data.percentRepublican)
					.appendWithComma(data.percentAsian).appendWithComma(data.percentBlack)
					.appendWithComma(data.percentWhite).appendWithComma(data.percentHispanic)
					.appendWithComma(data.metro).appendWithComma(data.timeZone).appendWithComma(data.feetAboveSeaLevel)
					.appendWithComma(data.uvIndex).appendWithComma(data.homeOwnershipRate)
					.appendWithComma(data.singlePopulation).appendWithComma(data.walkScore)
					.appendWithComma(data.transitScore).appendWithComma(data.bikeScore)
					.appendWithComma(data.povertyRate).appendWithComma(data.percentOfIncomeLostToHousingCosts)
					.appendWithComma(data.foreignBornPercent).appendLastItem(data.sexOffenderCount);
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		if (isLastWrite) {
			System.out.println("wrote to file " + filePath);
		}
	}

	public static void writeData(List<Data> dataList, String stateName, boolean isLastWrite) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		writeDataToPath(dataList, filePath, isLastWrite);
	}

	protected abstract void updateData(Data data, String stateName) throws Exception;

	public void processAllStates() throws Exception {
		runStateAsync("Alabama");
		runStateAsync("Alaska");
		runStateAsync("Arizona");
		runStateAsync("Arkansas");
		runStateAsync("California");
		runStateAsync("Colorado");
		runStateAsync("Connecticut");
		runStateAsync("Delaware");
		runStateAsync("Florida");
		runStateAsync("Georgia");
		runStateAsync("Hawaii");
		runStateAsync("Idaho");
		runStateAsync("Illinois");
		runStateAsync("Indiana");
		runStateAsync("Iowa");
		runStateAsync("Kansas");
		runStateAsync("Kentucky");
		runStateAsync("Louisiana");
		runStateAsync("Maine");
		runStateAsync("Maryland");
		runStateAsync("Massachusetts");
		runStateAsync("Michigan");
		runStateAsync("Minnesota");
		runStateAsync("Mississippi");
		runStateAsync("Missouri");
		runStateAsync("Montana");
		runStateAsync("Nebraska");
		runStateAsync("Nevada");
		runStateAsync("New Hampshire");
		runStateAsync("New Jersey");
		runStateAsync("New Mexico");
		runStateAsync("New York");
		runStateAsync("North Carolina");
		runStateAsync("North Dakota");
		runStateAsync("Ohio");
		runStateAsync("Oklahoma");
		runStateAsync("Oregon");
		runStateAsync("Pennsylvania");
		runStateAsync("Rhode Island");
		runStateAsync("South Carolina");
		runStateAsync("South Dakota");
		runStateAsync("Tennessee");
		runStateAsync("Texas");
		runStateAsync("Utah");
		runStateAsync("Vermont");
		runStateAsync("Virginia");
		runStateAsync("Washington");
		runStateAsync("West Virginia");
		runStateAsync("Wisconsin");
		runStateAsync("Wyoming");
	}

	private void runStateAsync(String stateName) throws Exception {
		new RunnableDemo52(stateName, this).start();
	}

	public void processState(String stateName) throws Exception {
		List<Data> dataList = readData(stateName);
		try {
			UpdatePrinter updatePrinter = new UpdatePrinter(dataList.size(), stateName);
			int idx = 0;
			for (Data data : dataList) {
				updateData(data, stateName);
				updatePrinter.printUpdateIfNeeded();
				idx++;
				if (idx % 30 == 0) {
					writeData(dataList, stateName, false);
				}
			}
		} finally {
			writeData(dataList, stateName, true);
		}
	}

	static class RunnableDemo52 implements Runnable {
		private Thread t;
		private String stateName;
		private CityStats g;

		RunnableDemo52(String stateName, CityStats g) {
			this.stateName = stateName;
			this.g = g;
		}

		public void run() {
			try {
				g.processState(stateName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void start() {
			if (t == null) {
				t = new Thread(this);
				t.start();
			}
		}

	}

}
