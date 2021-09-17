package main.java.com.hey.old;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.com.hey.UpdatePrinter;

public abstract class GenericDataReadAndWriter {

	private static final String startSt = "arr.push(new Data(";

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

	public static List<Data> readData(String stateName) throws Exception {
		List<Data> dataList = new ArrayList<>();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			if (!line.contains(startSt)) {
				continue;
			}
			line = line.substring(startSt.length());
			line = line.substring(0, line.length() - 3);
			String[] arr = line.split(",");
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
			try {
				data.uvIndex = arr[43];
				data.homeOwnershipRate = arr[44];
				data.singlePopulation = arr[45];
				data.walkScore = arr[46];
				data.transitScore = arr[47];
				data.bikeScore = arr[48];
			} catch (Exception ex) {

			}
			dataList.add(data);
		}
		myReader.close();
		return dataList;
	}

	private static void writeData(List<Data> dataList, String stateName, boolean isLastWrite) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		for (Data data : dataList) {
			sb.append(startSt).appendWithComma(data.cityName).appendWithComma(data.stateName)
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
		if (isLastWrite) {
			System.out.println("wrote to file " + filePath);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	protected abstract void updateData(Data data, String stateName) throws Exception;

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
			sb.append("));");
			sb.append("\n");
			return this;
		}

		String getString() {
			return sb.toString();
		}
	}

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
		new RunnableDemo51(stateName, this).start();
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

	static class RunnableDemo51 implements Runnable {
		private Thread t;
		private String stateName;
		private GenericDataReadAndWriter g;

		RunnableDemo51(String stateName, GenericDataReadAndWriter g) {
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
