package main.java.com.hey;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.com.hey.MetroStatistics.WeightedAverage;

public abstract class CityStats {

	public static String startSt = "City,State,Population,People per sq mi,Metro Name,Metro population,Hottest month's avg high (F),Coldest month's avg high (F),Hottest high minus coldest high,Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,Median household income,Poverty Rate,"
			+ "Avg Apartment Monthly Rent,Avg Apartment sqft,"
			+ "Median home price,Median home sqft,Median home cost per sqft,Median home age,"
			+ "Homeownership Rate,Home appreciation Last Year,Home appreciation Last 5 Years,Home appreciation Last 10 Years,Air quality Index,Average one-way commute time,Unemployment rate,Job growth last year,"
			+ "Population growth since 2010,County,% Democrat,% Republican,% Asian,% Black,% White,% Hispanic,Foreign Born %,Timezone,Elevation (ft),"
			+ "UV Index,Single Population,Walk Score,Transit Score,Bike Score,% of income spent on housing costs (owners),Number of sex offenders per 10k residents,Number of hurricanes since 1930,Number of tornadoes per year,Number of earthquakes since 1931";

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
	}

	public static class AndrewStringWriter {
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

		AndrewStringWriter appendWithComma(int st) {
			sb.append(st).append(",");
			return this;
		}

		AndrewStringWriter appendWAPercent(WeightedAverage wa) {
			sb.append(wa.getWeightedAverage());
			sb.append("%,");
			return this;
		}

		AndrewStringWriter appendWADollar(WeightedAverage wa) {
			sb.append("$");
			return appendWA(wa);
		}

		AndrewStringWriter appendWA(WeightedAverage wa) {
			return appendWithComma(wa.getWeightedAverage());
		}

		AndrewStringWriter appendLastItem(String st) {
			sb.append(st);
			sb.append("\n");
			return this;
		}

		AndrewStringWriter appendLastItem(int st) {
			sb.append(st);
			sb.append("\n");
			return this;
		}

		AndrewStringWriter appendEnding() {
			sb.setLength(sb.length() - 1);
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
			try {
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
			appendRowToSb(sb, data);
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		if (isLastWrite) {
			System.out.println("wrote to file " + filePath);
		}
	}

	public static void appendRowToSb(AndrewStringWriter sb, Data data) {
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
				.appendWithComma(data.hurricanes).appendWithComma(data.tornadoes).appendLastItem(data.earthQuakes);
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
		runStateAsync("District of Columbia");
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
			runCleanup();
		}
	}

	protected void runCleanup() {
		
	};

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
