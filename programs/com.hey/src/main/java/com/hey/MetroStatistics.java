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
		WeightedAverage percentDemocrat = new WeightedAverage();
		WeightedAverage percentRepublican = new WeightedAverage();
		WeightedAverage percentAsian = new WeightedAverage();
		WeightedAverage percentBlack = new WeightedAverage();
		WeightedAverage percentWhite = new WeightedAverage();
		WeightedAverage percentHispanic = new WeightedAverage();
		WeightedAverage foreignBornPercent = new WeightedAverage();
		WeightedAverage uvIndex = new WeightedAverage();
		WeightedAverage singlePopulation = new WeightedAverage();
		WeightedAverage percentOfIncomeLostToHousingCosts = new WeightedAverage();
		WeightedAverage sexOffenderCount = new WeightedAverage();
		private Map<String, Integer> mapOfStateToPopulation = new HashMap<>();
		
		public String getPrimaryState() {
			Set<String> keys = mapOfStateToPopulation.keySet();
			
			String mostPopulatedState = "";
			int maxPopulation = -1;
			for (String key : keys) {
				int currentPop = mapOfStateToPopulation.get(key);
				if (currentPop > maxPopulation) {
					maxPopulation = currentPop;
					mostPopulatedState = key;
				}
			}
			if (maxPopulation == -1) {
				throw new RuntimeException("didn't find most populated state");
			}
			return mostPopulatedState;
		}
		
		
		public void addDataToMap(CityStats.Data data) {
			String state = data.stateName;
			if (!mapOfStateToPopulation.containsKey(state)) {
				mapOfStateToPopulation.put(state, 0);
			}
			int pop = mapOfStateToPopulation.get(state);
			pop += Integer.valueOf(data.population);
			mapOfStateToPopulation.put(state, pop);
		}

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
		stats.percentDemocrat.addCity(data, data.percentDemocrat);
		stats.percentRepublican.addCity(data, data.percentRepublican);
		stats.percentAsian.addCity(data, data.percentAsian);
		stats.percentBlack.addCity(data, data.percentBlack);
		stats.percentWhite.addCity(data, data.percentWhite);
		stats.percentHispanic.addCity(data, data.percentHispanic);
		stats.foreignBornPercent.addCity(data, data.foreignBornPercent);
		stats.uvIndex.addCity(data, data.uvIndex);
		stats.singlePopulation.addCity(data, data.singlePopulation);
		stats.percentOfIncomeLostToHousingCosts.addCity(data, data.percentOfIncomeLostToHousingCosts);
		stats.sexOffenderCount.addCity(data, data.sexOffenderCount);
		stats.addDataToMap(data);

	}

	static String startSt = "Metro Name,Predominant State,Metro Population,People Per Sq Mi,Hottest month's avg high (F),Coldest month's avg high (F),Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),"
			+ "Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,"
			+ "Median household income,Poverty Rate,Median home price,Median home sqft,"
			+ "Median home cost per sqft,Homeownership Rate,Population growth since 2010,"
			+ "% Democrat,% Republican,% Asian,% Black,% Non-Hispanic White,% Hispanic,Foreign Born %,UV Index,Single Population,% of income spent on housing costs (owners),Number of sex offenders per 10k residents";

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
		sb.appendWAPercent(stat.percentWithAtleastBachelors);
		sb.appendWADollar(stat.medianIncome);
		sb.appendWAPercent(stat.povertyRate);
		sb.appendWADollar(stat.medianHomePrice);
		sb.appendWA(stat.homeSquareFeet);
		sb.appendWADollar(stat.costPerSquareFoot);
		sb.appendWAPercent(stat.homeOwnershipRate);
		sb.appendWAPercent(stat.populationGrowthSince2010);
		sb.appendWAPercent(stat.percentDemocrat);
		sb.appendWAPercent(stat.percentRepublican);
		sb.appendWAPercent(stat.percentAsian);
		sb.appendWAPercent(stat.percentBlack);
		sb.appendWAPercent(stat.percentWhite);
		sb.appendWAPercent(stat.percentHispanic);
		sb.appendWAPercent(stat.foreignBornPercent);
		sb.appendWA(stat.uvIndex);
		sb.appendWAPercent(stat.singlePopulation);
		sb.appendWAPercent(stat.percentOfIncomeLostToHousingCosts);
		sb.appendWA(stat.sexOffenderCount);
	}
	
	public static String getMetroKey(CityStats.Data data) {
		return data.metro + "," + data.metroPopulation;
	}

	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, Stats> mapOfMetroNameToStats = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (isMetroValid(data)) {
				String metroKey = getMetroKey(data);
				if (!mapOfMetroNameToStats.containsKey(metroKey)) {
					Stats stats = new Stats();
					stats.metroName = data.metro;
					stats.metroPopulation = Integer.valueOf(data.metroPopulation);
					mapOfMetroNameToStats.put(metroKey, stats);
				}
				Stats stats = mapOfMetroNameToStats.get(metroKey);
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
			sb.appendWithComma(stat.metroName).appendWithComma(stat.getPrimaryState()).appendWithComma(stat.metroPopulation);
			addToSb(sb, stat);
			sb.appendEnding();
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
	}

	private static boolean isMetroValid(CityStats.Data data) {
		return !data.metro.contains("None");
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

		public String getWeightedAverage() {
			if (totalPopulation == 0) {
				return "N/A";
			}
			return String.valueOf((int)(totalSummedValue / totalPopulation));
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
