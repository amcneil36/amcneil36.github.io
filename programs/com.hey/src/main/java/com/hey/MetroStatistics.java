package main.java.com.hey;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats.AndrewStringWriter;

public class MetroStatistics {

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
		

	}

	static void addStuffToStats(Stats stats, CityStats.Data data) {
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

	}

	static String startSt = "Metro Name,Metro Population,People Per Sq Mi,Hottest month's avg high (F),Coldest month's avg high (F),Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),"
			+ "Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,"
			+ "Median household income,Poverty Rate,Median home price,Median home sqft,"+
			"Median home cost per sqft,Homeownership Rate (%),Population growth since 2010,% Democrat,% Republican,% Asian,% Black,% White,% Hispanic,Foreign Born %";

	static void addToSb(AndrewStringWriter sb, Stats stat) {
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
		sb.appendWA(stat.percentWithAtleastBachelors);
		sb.appendWA(stat.medianIncome);
		sb.appendWA(stat.povertyRate);
		sb.appendWA(stat.medianHomePrice);
		sb.appendWA(stat.homeSquareFeet);
		sb.appendWA(stat.costPerSquareFoot);
		sb.appendWA(stat.homeOwnershipRate);
		sb.appendWA(stat.populationGrowthSince2010);
	}

	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfMetroNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (Integer.valueOf(data.metroPopulation) > 999999 && !data.metro.contains("None")) {
				if (!mapOfMetroNameToStats.containsKey(data.metro)) {
					Stats stats = new Stats();
					stats.metroName = data.metro;
					stats.metroPopulation = Integer.valueOf(data.metroPopulation);
					mapOfMetroNameToStats.put(data.metro, stats);
				}
				Stats stats = mapOfMetroNameToStats.get(data.metro);
				addStuffToStats(stats, data);
			}
		}
		Set<String> keys = mapOfMetroNameToStats.keySet();
		List<Stats> statsList = new ArrayList<>();
		for (String key : keys) {
			statsList.add(mapOfMetroNameToStats.get(key));
		}
		Collections.sort(statsList, Collections.reverseOrder());
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\MetroSummary.csv";
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(startSt);
		for (Stats stat : statsList) {
			sb.appendWithComma(stat.metroName).appendWithComma(stat.metroPopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}

	static class WeightedAverage {
		private double totalSummedValue = 0;
		private double totalPopulation = 0;

		public void addCity(CityStats.Data data, String value) {
			if (value.contains("N/A")) {
				return;
			}
			double val = Double.valueOf(value.replace("%", "").replace("$", ""));
			int pop = Integer.valueOf(data.population);
			totalSummedValue += val * pop;
			totalPopulation += pop;
		}

		public int getWeightedAverage() {
			return (int) (totalSummedValue / totalPopulation);
		}
	}

	static class Stats2 implements Comparable<Stats2> {
		public String metroName = "";
		public int metroPopulation = 0;

		@Override
		public int compareTo(Stats2 arg0) {
			return compareTo2(this, arg0);
		}

	}

	static int compareTo2(Stats2 self, Stats2 arg0) {
		if (self.metroPopulation < arg0.metroPopulation) {
			return -1;
		} else if (self.metroPopulation == arg0.metroPopulation) {
			return 0;
		}
		return 1;
	}

}
