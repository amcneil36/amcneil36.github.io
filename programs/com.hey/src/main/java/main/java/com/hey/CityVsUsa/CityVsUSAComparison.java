package main.java.com.hey.CityVsUsa;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import main.java.com.hey.CityStats.Data;
import main.java.com.hey.summaries.CreateBigCsv;

public class CityVsUSAComparison {
	
	static class InputData {
		String cityName;
		String stateName;
		int population;
		int populationDensity;
		int augustHigh;
		int decemberHigh;
		int augustHighMinusDecemberHigh;
		int annualInchesOfRain;
		int daysOfRain;
		int sunnyDays;
		int annualSnowfall;
		int averageYearlyHumidity;
		int yearlyWindspeed;
		int violentCrime;
		int propertyCrime;
		int medianAge;
		int metroPopulation;
		float bachelors;
		int medianHouseholdIncome;
		int medianHomePrice;
		int medianHomeAge;
		int homeAppreciation;
		int airQuality;
		float unemploymentRate;
		float populationGrowth;
		float percentDemocrat;
		float percentRepublican;
		float percentAsian;
		float percentBlack;
		float percentWhite;
		float percentHispanic;
		float povertyRate;
		int avgSummerDewPoint;
		int avgApartmentRent;
		int avgApartmentSize;
		int homeSquareFeet;
		int costPerSquareFoot;
		float homeOwnershipRate;
		float foreignBornPercent;
		int feetAboveSeaLevel;
		float uvIndex;
		float singlePopulation;
		int walkScore;
		int transitScore;
		int bikeScore;
		float percentOfIncomeLostToHousingCosts;
		float sexOffenderCount;
		int hurricanes;
		int tornadoes;
		int earthQuakes;
		//TODO1

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	static Foo<Integer> populationFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return  inputData.population;}};	
	static Foo<Integer> populationDensityFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.populationDensity;}};	
	static Foo<Integer> augustHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.augustHigh;}};	
	static Foo<Integer> decemberHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.decemberHigh;}};	
	static Foo<Integer> augustHighMinusDecemberHighFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.augustHighMinusDecemberHigh;}};	
	static Foo<Integer> annualInchesOfRainFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.annualInchesOfRain;}};	
	static Foo<Integer> daysOfRainFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.daysOfRain;}};	
	static Foo<Integer> sunnyDaysFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.sunnyDays;}};	
	static Foo<Integer> annualSnowfallFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.annualSnowfall;}};	
	static Foo<Integer> averageYearlyHumidityFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.averageYearlyHumidity;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};	
	static Foo<Integer> yearlyWindspeedFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.yearlyWindspeed;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};	
	static Foo<Integer> violentCrimeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.violentCrime;}};
	static Foo<Integer> propertyCrimeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.propertyCrime;}};
	static Foo<Integer> medianAgeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.medianAge;}};	
	static Foo<Float> bachelorsFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.bachelors;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Integer> medianHouseholdIncomeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.medianHouseholdIncome;}};
	static Foo<Integer> medianHomePriceFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.medianHomePrice;}};
	static Foo<Integer> medianHomeAgeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.medianHomeAge;}};
	static Foo<Integer> homeAppreciationFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.homeAppreciation;}};
	static Foo<Integer> airQualityFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.airQuality;}};
	static Foo<Float> unemploymentRateFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.unemploymentRate;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> populationGrowthFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.populationGrowth;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentDemocratFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentDemocrat;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentRepublicanFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentRepublican;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentAsianFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentAsian;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentBlackFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentBlack;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentWhiteFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentWhite;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Float> percentHispanicFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentHispanic;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Integer> metroPopulationFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.metroPopulation;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Float> povertyRateFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.povertyRate;} @Override boolean isInvalidValue(Float data) {return data == -100;}};	
	static Foo<Integer> avgSummerDewPointFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.avgSummerDewPoint;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> avgApartmentRentFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.avgApartmentRent;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> avgApartmentSizeFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.avgApartmentSize;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> homeSquareFeetFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.homeSquareFeet;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> costPerSquareFootFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.costPerSquareFoot;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Float> homeOwnershipRateFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.homeOwnershipRate;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Float> foreignBornPercentFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.foreignBornPercent;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Integer> feetAboveSeaLevelFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.feetAboveSeaLevel;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Float> uvIndexFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.uvIndex;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Float> singlePopulationFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.singlePopulation;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Integer> walkScoreFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.walkScore;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> transitScoreFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.transitScore;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> bikeScoreFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.bikeScore;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Float> percentOfIncomeLostToHousingCostsFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.percentOfIncomeLostToHousingCosts;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Float> sexOffenderCountFoo = new Foo<Float>() { @Override Float getData(InputData inputData) { return inputData.sexOffenderCount;} @Override boolean isInvalidValue(Float data) {return data == -100;}};
	static Foo<Integer> hurricanesFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.hurricanes;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> tornadoesFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.tornadoes;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	static Foo<Integer> earthQuakesFoo = new Foo<Integer>() { @Override Integer getData(InputData inputData) { return inputData.earthQuakes;} @Override boolean isInvalidValue(Integer data) {return data == -100;}};
	//TODO2
	
	static class AveragesAndMedians {
		int populationAverage;
		int populationMedian;
		int populationDensityAverage;
		int populationDensityMedian;
		int augustHighAverage;
		int augustHighMedian;
		int decemberHighAverage;
		int decemberHighMedian;
		int augustHighMinusDecemberHighAverage;
		int augustHighMinusDecemberHighMedian;
		int annualInchesOfRainAverage;
		int annualInchesOfRainMedian;
		int daysOfRainAverage;
		int daysOfRainMedian;
		int sunnyDaysAverage;
		int sunnyDaysMedian;
		int annualSnowfallAverage;
		int annualSnowfallMedian;
		int averageYearlyHumidityAverage;
		int averageYearlyHumidityMedian;
		int yearlyWindspeedAverage;
		int yearlyWindspeedMedian;
		int violentCrimeAverage;
		int violentCrimeMedian;
		int propertyCrimeAverage;
		int propertyCrimeMedian;
		int medianAgeAverage;
		int medianAgeMedian;
		float bachelorsAverage;
		float bachelorsMedian;
		int medianHouseholdIncomeAverage;
		int medianHouseholdIncomeMedian;
		int medianHomePriceAverage;
		int medianHomePriceMedian;
		int medianHomeAgeAverage;
		int medianHomeAgeMedian;
		int homeAppreciationAverage;
		int homeAppreciationMedian;
		int airQualityAverage;
		int airQualityMedian;
		float unemploymentRateAverage;
		float unemploymentRateMedian;
		float populationGrowthAverage;
		float populationGrowthMedian;
		float percentDemocratAverage;
		float percentDemocratMedian;
		float percentRepublicanAverage;
		float percentRepublicanMedian;
		float percentAsianAverage;
		float percentAsianMedian;
		float percentBlackAverage;
		float percentBlackMedian;
		float percentWhiteAverage;
		float percentWhiteMedian;
		float percentHispanicAverage;
		float percentHispanicMedian;
		int metroPopulationAverage;
		int metroPopulationMedian;
		float povertyRateAverage;
		float povertyRateMedian;
		int avgSummerDewPointAverage;
		int avgSummerDewPointMedian;
		int avgApartmentRentAverage;
		int avgApartmentRentMedian;
		int avgApartmentSizeAverage;
		int avgApartmentSizeMedian;
		int homeSquareFeetAverage;
		int homeSquareFeetMedian;
		int costPerSquareFootAverage;
		int costPerSquareFootMedian;
		float homeOwnershipRateAverage;
		float homeOwnershipRateMedian;
		float foreignBornPercentAverage;
		float foreignBornPercentMedian;
		int feetAboveSeaLevelAverage;
		int feetAboveSeaLevelMedian;
		float uvIndexAverage;
		float uvIndexMedian;
		float singlePopulationAverage;
		float singlePopulationMedian;
		int walkScoreAverage;
		int walkScoreMedian;
		int transitScoreAverage;
		int transitScoreMedian;
		int bikeScoreAverage;
		int bikeScoreMedian;
		float percentOfIncomeLostToHousingCostsAverage;
		float percentOfIncomeLostToHousingCostsMedian;
		float sexOffenderCountAverage;
		float sexOffenderCountMedian;
		int hurricanesAverage;
		int hurricanesMedian;
		int tornadoesAverage;
		int tornadoesMedian;
		int earthQuakesAverage;
		int earthQuakesMedian;
        //TODO3
	}

	static class OutputData {
		public String key;
		public Metric populationMetric = new Metric();
		public Metric populationDensityMetric = new Metric();
		public Metric augustHighMetric = new Metric();
		public Metric decemberHighMetric = new Metric();
		public Metric augustHighMinusDecemberHighMetric = new Metric();
		public Metric annualInchesOfRainMetric = new Metric();
		public Metric daysOfRainMetric = new Metric();
		public Metric sunnyDaysMetric = new Metric();
		public Metric annualSnowfallMetric = new Metric();
		public Metric averageYearlyHumidityMetric = new Metric();
		public Metric yearlyWindspeedMetric = new Metric();
		public Metric violentCrimeMetric = new Metric();
		public Metric propertyCrimeMetric = new Metric();
		public Metric medianAgeMetric = new Metric();
		public Metric bachelorsMetric = new Metric();
		public Metric medianHouseholdIncomeMetric = new Metric();
		public Metric medianHomePriceMetric = new Metric();
		public Metric medianHomeAgeMetric = new Metric();
		public Metric homeAppreciationMetric = new Metric();
		public Metric airQualityMetric = new Metric();
		public Metric unemploymentRateMetric = new Metric();
		public Metric populationGrowthMetric = new Metric();
		public Metric percentDemocratMetric = new Metric();
		public Metric percentRepublicanMetric = new Metric();
		public Metric percentAsianMetric = new Metric();
		public Metric percentBlackMetric = new Metric();
		public Metric percentWhiteMetric = new Metric();
		public Metric percentHispanicMetric = new Metric();
		public Metric metroPopulationMetric = new Metric();
		public Metric povertyRateMetric = new Metric();
		public Metric avgSummerDewPointMetric = new Metric();
		public Metric avgApartmentRentMetric = new Metric();
		public Metric avgApartmentSizeMetric = new Metric();
		public Metric homeSquareFeetMetric = new Metric();
		public Metric costPerSquareFootMetric = new Metric();
		public Metric homeOwnershipRateMetric = new Metric();
		public Metric foreignBornPercentMetric = new Metric();
		public Metric feetAboveSeaLevelMetric = new Metric();
		public Metric uvIndexMetric = new Metric();
		public Metric singlePopulationMetric = new Metric();
		public Metric walkScoreMetric = new Metric();
		public Metric transitScoreMetric = new Metric();
		public Metric bikeScoreMetric = new Metric();
		public Metric percentOfIncomeLostToHousingCostsMetric = new Metric();
		public Metric sexOffenderCountMetric = new Metric();
		public Metric hurricanesMetric = new Metric();
		public Metric tornadoesMetric = new Metric();
		public Metric earthQuakesMetric = new Metric();
		//TODO4

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	private static List<InputData> readInput() {
		try {

			List<InputData> list = new ArrayList<InputData>();

	        List<Data> dataList = CreateBigCsv.readInput();
	        for (Data data : dataList) {
				InputData inputData = new InputData();
				inputData.cityName = data.cityName.toLowerCase();
				inputData.stateName = data.stateName.toLowerCase();
				inputData.population = Integer.valueOf(data.population);
				inputData.populationDensity = Integer.valueOf(data.populationDensity);
				inputData.augustHigh = Integer.valueOf(data.hottestMonthsHigh);
				inputData.decemberHigh = Integer.valueOf(data.coldestHigh);
				inputData.augustHighMinusDecemberHigh = Integer.valueOf(data.hottestMonthMinusColdestMonth);
				inputData.annualInchesOfRain = Integer.valueOf(data.numInchesOfRain);
				inputData.daysOfRain = Integer.valueOf(data.numDaysOfRain);
				inputData.sunnyDays = Integer.valueOf(data.numSunnyDays);
				inputData.annualSnowfall = Integer.valueOf(data.annualSnowfall);
				inputData.averageYearlyHumidity = getValidInt(data.avgAnnualDewPoint); // swapped
				inputData.yearlyWindspeed = getValidInt(data.avgYearlyWindspeed);
				inputData.violentCrime = Integer.valueOf(data.violentCrime);
				inputData.propertyCrime = Integer.valueOf(data.propertyCrime);
				inputData.medianAge = getValidInt(data.medianAge);
				inputData.bachelors = getValidFloatFromPercent(data.percentWithAtleastBachelors);
				inputData.medianHouseholdIncome = Integer.valueOf(data.medianIncome.replace("$", ""));
				inputData.medianHomePrice = getValidInt(data.medianHomePrice.replace("$", ""));
				inputData.medianHomeAge = Integer.valueOf(data.medianHomeAge);
				inputData.homeAppreciation = getValidIntegerFromPercent(data.homeAppreciationLastTenYears);
				inputData.airQuality = Integer.valueOf(data.airQuality);
				inputData.unemploymentRate = getValidFloatFromPercent(data.unemploymentRate);
				inputData.populationGrowth = getValidFloatFromPercent(data.populationGrowthSince2010);		
				inputData.percentDemocrat = getValidFloatFromPercent(data.percentDemocrat);
				inputData.percentRepublican = getValidFloatFromPercent(data.percentRepublican);		
				inputData.percentAsian = getValidFloatFromPercent(data.percentAsian);		
				inputData.percentBlack = getValidFloatFromPercent(data.percentBlack);		
				inputData.percentWhite = getValidFloatFromPercent(data.percentWhite);		
				inputData.percentHispanic = getValidFloatFromPercent(data.percentHispanic);		
				inputData.metroPopulation = getValidInt(data.metroPopulation);
				inputData.povertyRate = getValidFloatFromPercent(data.povertyRate);
				inputData.avgSummerDewPoint = getValidInt(data.avgSummerDewPoint);
				inputData.avgApartmentRent = getValidInt(data.avgApartmentRent.replace("$", ""));
				inputData.avgApartmentSize = getValidInt(data.avgApartmentSize);
				inputData.homeSquareFeet = getValidInt(data.homeSquareFeet);
				inputData.costPerSquareFoot = getValidInt(data.costPerSquareFoot.replace("$", ""));
				inputData.homeOwnershipRate = getValidFloatFromPercent(data.homeOwnershipRate);
				inputData.foreignBornPercent = getValidFloatFromPercent(data.foreignBornPercent);
				inputData.feetAboveSeaLevel = getValidInt(data.feetAboveSeaLevel);
				inputData.uvIndex = Float.valueOf(data.uvIndex);
				inputData.singlePopulation = getValidFloatFromPercent(data.singlePopulation);
				inputData.walkScore = getValidInt(data.walkScore);
				inputData.transitScore = getValidInt(data.transitScore);
				inputData.bikeScore = getValidInt(data.bikeScore);
				inputData.percentOfIncomeLostToHousingCosts = getValidFloatFromPercent(data.percentOfIncomeLostToHousingCosts);
				inputData.sexOffenderCount = getValidFloat(data.sexOffenderCount);
				inputData.hurricanes = getValidInt(data.hurricanes);
				inputData.tornadoes = getValidInt(data.tornadoes);
				inputData.earthQuakes = getValidInt(data.earthQuakes);
				//TODO5
				list.add(inputData);
	        }
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("issue in readInput");
		}

	}

	private static float getValidFloat(String sexOffenderCount) {
		if (sexOffenderCount.contains("N/A")) {
			return -100;
		}
		else {
			return Float.valueOf(sexOffenderCount);
		}
	}

	private static AveragesAndMedians getAveragesAndMedians(List<InputData> inputDataList) {
		AveragesAndMedians obj = new AveragesAndMedians();
		List<Integer> populations = populationFoo.getGenericList(inputDataList);	
		obj.populationAverage = (int) findMean(populations);
		obj.populationMedian = (int) findMedian(populations);
		List<Integer> populationDensities = populationDensityFoo.getGenericList(inputDataList);
		obj.populationDensityAverage = (int) findMean(populationDensities);
		obj.populationDensityMedian = (int) findMedian(populationDensities);
		List<Integer> augustHighs = augustHighFoo.getGenericList(inputDataList);	
		obj.augustHighAverage = (int) findMean(augustHighs);
		obj.augustHighMedian = (int) findMedian(augustHighs);
		List<Integer> decemberHighs = decemberHighFoo.getGenericList(inputDataList);	
		obj.decemberHighAverage = (int) findMean(decemberHighs);
		obj.decemberHighMedian = (int) findMedian(decemberHighs);
		List<Integer> augHighMinusDecHighs = augustHighMinusDecemberHighFoo.getGenericList(inputDataList);	
		obj.augustHighMinusDecemberHighAverage = (int) findMean(augHighMinusDecHighs);
		obj.augustHighMinusDecemberHighMedian = (int) findMedian(augHighMinusDecHighs);
		List<Integer> inchesOfRainList = annualInchesOfRainFoo.getGenericList(inputDataList);
		obj.annualInchesOfRainAverage = (int) findMean(inchesOfRainList);
		obj.annualInchesOfRainMedian = (int) findMedian(inchesOfRainList);
		
		List<Integer> daysOfRainList = daysOfRainFoo.getGenericList(inputDataList);
		obj.daysOfRainAverage = (int) findMean(daysOfRainList);
		obj.daysOfRainMedian = (int) findMedian(daysOfRainList);
		
		List<Integer> sunnyDaysList = sunnyDaysFoo.getGenericList(inputDataList);
		obj.sunnyDaysAverage = (int) findMean(sunnyDaysList);
		obj.sunnyDaysMedian = (int) findMedian(sunnyDaysList);
		
		List<Integer> annualSnowfallList = annualSnowfallFoo.getGenericList(inputDataList);
		obj.annualSnowfallAverage = (int) findMean(annualSnowfallList);
		obj.annualSnowfallMedian = (int) findMedian(annualSnowfallList);
		
		List<Integer> averageYearlyHumidityList = averageYearlyHumidityFoo.getGenericList(inputDataList);
		obj.averageYearlyHumidityAverage = (int) findMean(averageYearlyHumidityList);
		obj.averageYearlyHumidityMedian = (int) findMedian(averageYearlyHumidityList);
		
		List<Integer> yearlyWindspeedList = yearlyWindspeedFoo.getGenericList(inputDataList);
		obj.yearlyWindspeedAverage = (int) findMean(yearlyWindspeedList);
		obj.yearlyWindspeedMedian = (int) findMedian(yearlyWindspeedList);
		
		List<Integer> violentCrimeList = violentCrimeFoo.getGenericList(inputDataList);
		obj.violentCrimeAverage = (int) findMean(violentCrimeList);
		obj.violentCrimeMedian = (int) findMedian(violentCrimeList);
		
		List<Integer> propertyCrimeList = propertyCrimeFoo.getGenericList(inputDataList);
		obj.propertyCrimeAverage = (int) findMean(propertyCrimeList);
		obj.propertyCrimeMedian = (int) findMedian(propertyCrimeList);
		
		List<Integer> medianAgeList = medianAgeFoo.getGenericList(inputDataList);
		obj.medianAgeAverage = (int) findMean(medianAgeList);
		obj.medianAgeMedian = (int) findMedian(medianAgeList);
		
		List<Float> bachelorsList = bachelorsFoo.getGenericList(inputDataList);
		obj.bachelorsAverage = findMeanFloat(bachelorsList);
		obj.bachelorsMedian = findMedianFloat(bachelorsList);
		
		List<Integer> medianHouseholdIncomeList = medianHouseholdIncomeFoo.getGenericList(inputDataList);
		obj.medianHouseholdIncomeAverage = (int)findMean(medianHouseholdIncomeList);
		obj.medianHouseholdIncomeMedian = (int)findMedian(medianHouseholdIncomeList);
		
		List<Integer> medianHomePriceList = medianHomePriceFoo.getGenericList(inputDataList);
		obj.medianHomePriceAverage = (int)findMean(medianHomePriceList);
		obj.medianHomePriceMedian = (int)findMedian(medianHomePriceList);
		
		List<Integer> medianHomeAgeList = medianHomeAgeFoo.getGenericList(inputDataList);
		obj.medianHomeAgeAverage = (int)findMean(medianHomeAgeList);
		obj.medianHomeAgeMedian = (int)findMedian(medianHomeAgeList);
		
		List<Integer> homeAppreciationList = homeAppreciationFoo.getGenericList(inputDataList);
		obj.homeAppreciationAverage = (int)findMean(homeAppreciationList);
		obj.homeAppreciationMedian = (int)findMedian(homeAppreciationList);
		
		List<Integer> airQualityList = airQualityFoo.getGenericList(inputDataList);
		obj.airQualityAverage = (int)findMean(airQualityList);
		obj.airQualityMedian = (int)findMedian(airQualityList);
		
		List<Float> unemploymentRateList = unemploymentRateFoo.getGenericList(inputDataList);
		obj.unemploymentRateAverage = findMeanFloat(unemploymentRateList);
		obj.unemploymentRateMedian = findMedianFloat(unemploymentRateList);
		
		List<Float> populationGrowthList = populationGrowthFoo.getGenericList(inputDataList);
		obj.populationGrowthAverage = findMeanFloat(populationGrowthList);
		obj.populationGrowthMedian = findMedianFloat(populationGrowthList);
		
		List<Float> percentDemocratList = percentDemocratFoo.getGenericList(inputDataList);
		obj.percentDemocratAverage = findMeanFloat(percentDemocratList);
		obj.percentDemocratMedian = findMedianFloat(percentDemocratList);
		
		List<Float> percentRepublicanList = percentRepublicanFoo.getGenericList(inputDataList);
		obj.percentRepublicanAverage = findMeanFloat(percentRepublicanList);
		obj.percentRepublicanMedian = findMedianFloat(percentRepublicanList);
		
		List<Float> percentAsianList = percentAsianFoo.getGenericList(inputDataList);
		obj.percentAsianAverage = findMeanFloat(percentAsianList);
		obj.percentAsianMedian = findMedianFloat(percentAsianList);
		
		List<Float> percentBlackList = percentBlackFoo.getGenericList(inputDataList);
		obj.percentBlackAverage = findMeanFloat(percentBlackList);
		obj.percentBlackMedian = findMedianFloat(percentBlackList);
		
		List<Float> percentWhiteList = percentWhiteFoo.getGenericList(inputDataList);
		obj.percentWhiteAverage = findMeanFloat(percentWhiteList);
		obj.percentWhiteMedian = findMedianFloat(percentWhiteList);
		
		List<Float> percentHispanicList = percentHispanicFoo.getGenericList(inputDataList);
		obj.percentHispanicAverage = findMeanFloat(percentHispanicList);
		obj.percentHispanicMedian = findMedianFloat(percentHispanicList);
		
		List<Integer> metroPopulationList = metroPopulationFoo.getGenericList(inputDataList);
		obj.metroPopulationAverage = (int)findMean(metroPopulationList);
		obj.metroPopulationMedian = (int)findMedian(metroPopulationList);
		List<Float> povertyRateList = povertyRateFoo.getGenericList(inputDataList);
		obj.povertyRateAverage = findMeanFloat(povertyRateList);
		obj.povertyRateMedian = findMedianFloat(povertyRateList);
		List<Integer> avgSummerDewPointList = avgSummerDewPointFoo.getGenericList(inputDataList);
		obj.avgSummerDewPointAverage = (int) findMean(avgSummerDewPointList);
		obj.avgSummerDewPointMedian = (int) findMedian(avgSummerDewPointList);
		List<Integer> avgApartmentRentList = avgApartmentRentFoo.getGenericList(inputDataList);
		obj.avgApartmentRentAverage = (int) findMean(avgApartmentRentList);
		obj.avgApartmentRentMedian = (int) findMedian(avgApartmentRentList);
		List<Integer> avgApartmentSizeList = avgApartmentSizeFoo.getGenericList(inputDataList);
		obj.avgApartmentSizeAverage = (int) findMean(avgApartmentSizeList);
		obj.avgApartmentSizeMedian = (int) findMedian(avgApartmentSizeList);
		List<Integer> homeSquareFeetList = homeSquareFeetFoo.getGenericList(inputDataList);
		obj.homeSquareFeetAverage = (int) findMean(homeSquareFeetList);
		obj.homeSquareFeetMedian = (int) findMedian(homeSquareFeetList);
		List<Integer> costPerSquareFootList = costPerSquareFootFoo.getGenericList(inputDataList);
		obj.costPerSquareFootAverage = (int) findMean(costPerSquareFootList);
		obj.costPerSquareFootMedian = (int) findMedian(costPerSquareFootList);
		List<Float> homeOwnershipRateList = homeOwnershipRateFoo.getGenericList(inputDataList);
		obj.homeOwnershipRateAverage = findMeanFloat(homeOwnershipRateList);
		obj.homeOwnershipRateMedian = findMedianFloat(homeOwnershipRateList);
		List<Float> foreignBornPercentList = foreignBornPercentFoo.getGenericList(inputDataList);
		obj.foreignBornPercentAverage = findMeanFloat(foreignBornPercentList);
		obj.foreignBornPercentMedian = findMedianFloat(foreignBornPercentList);
		List<Integer> feetAboveSeaLevelList = feetAboveSeaLevelFoo.getGenericList(inputDataList);
		obj.feetAboveSeaLevelAverage = (int) findMean(feetAboveSeaLevelList);
		obj.feetAboveSeaLevelMedian = (int) findMedian(feetAboveSeaLevelList);
		List<Float> uvIndexList = uvIndexFoo.getGenericList(inputDataList);
		obj.uvIndexAverage = findMeanFloat(uvIndexList);
		obj.uvIndexMedian = findMedianFloat(uvIndexList);
		List<Float> singlePopulationList = singlePopulationFoo.getGenericList(inputDataList);
		obj.singlePopulationAverage = findMeanFloat(singlePopulationList);
		obj.singlePopulationMedian = findMedianFloat(singlePopulationList);
		List<Integer> walkScoreList = walkScoreFoo.getGenericList(inputDataList);
		obj.walkScoreAverage = (int) findMean(walkScoreList);
		obj.walkScoreMedian = (int) findMedian(walkScoreList);
		List<Integer> transitScoreList = transitScoreFoo.getGenericList(inputDataList);
		obj.transitScoreAverage = (int) findMean(transitScoreList);
		obj.transitScoreMedian = (int) findMedian(transitScoreList);
		List<Integer> bikeScoreList = bikeScoreFoo.getGenericList(inputDataList);
		obj.bikeScoreAverage = (int) findMean(bikeScoreList);
		obj.bikeScoreMedian = (int) findMedian(bikeScoreList);
		List<Float> percentOfIncomeLostToHousingCostsList = percentOfIncomeLostToHousingCostsFoo.getGenericList(inputDataList);
		obj.percentOfIncomeLostToHousingCostsAverage = findMeanFloat(percentOfIncomeLostToHousingCostsList);
		obj.percentOfIncomeLostToHousingCostsMedian = findMedianFloat(percentOfIncomeLostToHousingCostsList);
		List<Float> sexOffenderCountList = sexOffenderCountFoo.getGenericList(inputDataList);
		obj.sexOffenderCountAverage = findMeanFloat(sexOffenderCountList);
		obj.sexOffenderCountMedian = findMedianFloat(sexOffenderCountList);
		List<Integer> hurricanesList = hurricanesFoo.getGenericList(inputDataList);
		obj.hurricanesAverage = (int) findMean(hurricanesList);
		obj.hurricanesMedian = (int) findMedian(hurricanesList);
		List<Integer> tornadoesList = tornadoesFoo.getGenericList(inputDataList);
		obj.tornadoesAverage = (int) findMean(tornadoesList);
		obj.tornadoesMedian = (int) findMedian(tornadoesList);
		List<Integer> earthQuakesList = earthQuakesFoo.getGenericList(inputDataList);
		obj.earthQuakesAverage = (int) findMean(earthQuakesList);
		obj.earthQuakesMedian = (int) findMedian(earthQuakesList);
		//TODO6
		return obj;
	}

	private static void writeAveragesAndMedians(AveragesAndMedians averagesAndMedians) {
		StringBuilder sb = new StringBuilder();
		sb.append(getString("populationAverage", averagesAndMedians.populationAverage));
		sb.append(getString("populationAverage", averagesAndMedians.populationAverage));
		sb.append(getString("populationMedian", averagesAndMedians.populationMedian));
		sb.append(getString("populationDensityAverage", averagesAndMedians.populationDensityAverage));
		sb.append(getString("populationDensityMedian", averagesAndMedians.populationDensityMedian));
		sb.append(getString("augustHighAverage", averagesAndMedians.augustHighAverage));
		sb.append(getString("augustHighMedian", averagesAndMedians.augustHighMedian));
		sb.append(getString("decemberHighAverage", averagesAndMedians.decemberHighAverage));
		sb.append(getString("decemberHighMedian", averagesAndMedians.decemberHighMedian));
		sb.append(getString("augustHighMinusDecemberHighAverage", averagesAndMedians.augustHighMinusDecemberHighAverage));
		sb.append(getString("augustHighMinusDecemberHighMedian", averagesAndMedians.augustHighMinusDecemberHighMedian));
		sb.append(getString("annualInchesOfRainAverage", averagesAndMedians.annualInchesOfRainAverage));
		sb.append(getString("annualInchesOfRainMedian", averagesAndMedians.annualInchesOfRainMedian));
		sb.append(getString("daysOfRainAverage", averagesAndMedians.daysOfRainAverage));
		sb.append(getString("daysOfRainMedian", averagesAndMedians.daysOfRainMedian));
		sb.append(getString("sunnyDaysAverage", averagesAndMedians.sunnyDaysAverage));
		sb.append(getString("sunnyDaysMedian", averagesAndMedians.sunnyDaysMedian));
		sb.append(getString("annualSnowfallAverage", averagesAndMedians.annualSnowfallAverage));
		sb.append(getString("annualSnowfallMedian", averagesAndMedians.annualSnowfallMedian));	
		sb.append(getString("averageYearlyHumidityAverage", averagesAndMedians.averageYearlyHumidityAverage));
		sb.append(getString("averageYearlyHumidityMedian", averagesAndMedians.averageYearlyHumidityMedian));	
		sb.append(getString("yearlyWindspeedAverage", averagesAndMedians.yearlyWindspeedAverage));
		sb.append(getString("yearlyWindspeedMedian", averagesAndMedians.yearlyWindspeedMedian));
		sb.append(getString("violentCrimeAverage", averagesAndMedians.violentCrimeAverage));
		sb.append(getString("violentCrimeMedian", averagesAndMedians.violentCrimeMedian));
		sb.append(getString("propertyCrimeAverage", averagesAndMedians.propertyCrimeAverage));
		sb.append(getString("propertyCrimeMedian", averagesAndMedians.propertyCrimeMedian));
		sb.append(getString("medianAgeAverage", averagesAndMedians.medianAgeAverage));
		sb.append(getString("medianAgeMedian", averagesAndMedians.medianAgeMedian));
		sb.append(getFloatString("bachelorsAverage", averagesAndMedians.bachelorsAverage));
		sb.append(getFloatString("bachelorsMedian", averagesAndMedians.bachelorsMedian));
		sb.append(getString("medianHouseholdIncomeAverage", averagesAndMedians.medianHouseholdIncomeAverage));
		sb.append(getString("medianHouseholdIncomeMedian", averagesAndMedians.medianHouseholdIncomeMedian));
		sb.append(getString("medianHomePriceAverage", averagesAndMedians.medianHomePriceAverage));
		sb.append(getString("medianHomePriceMedian", averagesAndMedians.medianHomePriceMedian));
		sb.append(getString("medianHomeAgeAverage", averagesAndMedians.medianHomeAgeAverage));
		sb.append(getString("medianHomeAgeMedian", averagesAndMedians.medianHomeAgeMedian));
		sb.append(getString("homeAppreciationAverage", averagesAndMedians.homeAppreciationAverage));
		sb.append(getString("homeAppreciationMedian", averagesAndMedians.homeAppreciationMedian));
		sb.append(getString("airQualityAverage", averagesAndMedians.airQualityAverage));
		sb.append(getString("airQualityMedian", averagesAndMedians.airQualityMedian));
		sb.append(getFloatString("unemploymentRateAverage", averagesAndMedians.unemploymentRateAverage));
		sb.append(getFloatString("unemploymentRateMedian", averagesAndMedians.unemploymentRateMedian));
		sb.append(getFloatString("populationGrowthAverage", averagesAndMedians.populationGrowthAverage));
		sb.append(getFloatString("populationGrowthMedian", averagesAndMedians.populationGrowthMedian));
		sb.append(getFloatString("percentDemocratAverage", averagesAndMedians.percentDemocratAverage));
		sb.append(getFloatString("percentDemocratMedian", averagesAndMedians.percentDemocratMedian));
		sb.append(getFloatString("percentRepublicanAverage", averagesAndMedians.percentRepublicanAverage));
		sb.append(getFloatString("percentRepublicanMedian", averagesAndMedians.percentRepublicanMedian));
		sb.append(getFloatString("percentAsianAverage", averagesAndMedians.percentAsianAverage));
		sb.append(getFloatString("percentAsianMedian", averagesAndMedians.percentAsianMedian));
		sb.append(getFloatString("percentBlackAverage", averagesAndMedians.percentBlackAverage));
		sb.append(getFloatString("percentBlackMedian", averagesAndMedians.percentBlackMedian));
		sb.append(getFloatString("percentWhiteAverage", averagesAndMedians.percentWhiteAverage));
		sb.append(getFloatString("percentWhiteMedian", averagesAndMedians.percentWhiteMedian));
		sb.append(getFloatString("percentHispanicAverage", averagesAndMedians.percentHispanicAverage));
		sb.append(getFloatString("percentHispanicMedian", averagesAndMedians.percentHispanicMedian));
		sb.append(getString("metroPopulationAverage", averagesAndMedians.metroPopulationAverage));
		sb.append(getString("metroPopulationMedian", averagesAndMedians.metroPopulationMedian));
		sb.append(getFloatString("povertyRateAverage", averagesAndMedians.povertyRateAverage));
		sb.append(getFloatString("povertyRateMedian", averagesAndMedians.povertyRateMedian));
		sb.append(getString("avgSummerDewPointAverage", averagesAndMedians.avgSummerDewPointAverage));
		sb.append(getString("avgSummerDewPointMedian", averagesAndMedians.avgSummerDewPointMedian));
		sb.append(getString("avgApartmentRentAverage", averagesAndMedians.avgApartmentRentAverage));
		sb.append(getString("avgApartmentRentMedian", averagesAndMedians.avgApartmentRentMedian));
		sb.append(getString("avgApartmentSizeAverage", averagesAndMedians.avgApartmentSizeAverage));
		sb.append(getString("avgApartmentSizeMedian", averagesAndMedians.avgApartmentSizeMedian));
		sb.append(getString("homeSquareFeetAverage", averagesAndMedians.homeSquareFeetAverage));
		sb.append(getString("homeSquareFeetMedian", averagesAndMedians.homeSquareFeetMedian));
		sb.append(getString("costPerSquareFootAverage", averagesAndMedians.costPerSquareFootAverage));
		sb.append(getString("costPerSquareFootMedian", averagesAndMedians.costPerSquareFootMedian));
		sb.append(getFloatString("homeOwnershipRateAverage", averagesAndMedians.homeOwnershipRateAverage));
		sb.append(getFloatString("homeOwnershipRateMedian", averagesAndMedians.homeOwnershipRateMedian));
		sb.append(getFloatString("foreignBornPercentAverage", averagesAndMedians.foreignBornPercentAverage));
		sb.append(getFloatString("foreignBornPercentMedian", averagesAndMedians.foreignBornPercentMedian));
		sb.append(getString("feetAboveSeaLevelAverage", averagesAndMedians.feetAboveSeaLevelAverage));
		sb.append(getString("feetAboveSeaLevelMedian", averagesAndMedians.feetAboveSeaLevelMedian));
		sb.append(getFloatString("uvIndexAverage", averagesAndMedians.uvIndexAverage));
		sb.append(getFloatString("uvIndexMedian", averagesAndMedians.uvIndexMedian));
		sb.append(getFloatString("singlePopulationAverage", averagesAndMedians.singlePopulationAverage));
		sb.append(getFloatString("singlePopulationMedian", averagesAndMedians.singlePopulationMedian));
		sb.append(getString("walkScoreAverage", averagesAndMedians.walkScoreAverage));
		sb.append(getString("walkScoreMedian", averagesAndMedians.walkScoreMedian));
		sb.append(getString("transitScoreAverage", averagesAndMedians.transitScoreAverage));
		sb.append(getString("transitScoreMedian", averagesAndMedians.transitScoreMedian));
		sb.append(getString("bikeScoreAverage", averagesAndMedians.bikeScoreAverage));
		sb.append(getString("bikeScoreMedian", averagesAndMedians.bikeScoreMedian));
		sb.append(getFloatString("percentOfIncomeLostToHousingCostsAverage", averagesAndMedians.percentOfIncomeLostToHousingCostsAverage));
		sb.append(getFloatString("percentOfIncomeLostToHousingCostsMedian", averagesAndMedians.percentOfIncomeLostToHousingCostsMedian));
		sb.append(getFloatString("sexOffenderCountAverage", averagesAndMedians.sexOffenderCountAverage));
		sb.append(getFloatString("sexOffenderCountMedian", averagesAndMedians.sexOffenderCountMedian));
		sb.append(getString("hurricanesAverage", averagesAndMedians.hurricanesAverage));
		sb.append(getString("hurricanesMedian", averagesAndMedians.hurricanesMedian));
		sb.append(getString("tornadoesAverage", averagesAndMedians.tornadoesAverage));
		sb.append(getString("tornadoesMedian", averagesAndMedians.tornadoesMedian));
		sb.append(getString("earthQuakesAverage", averagesAndMedians.earthQuakesAverage));
		sb.append(getString("earthQuakesMedian", averagesAndMedians.earthQuakesMedian));
		//TODO7
		
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\averagesAndMedians.js", sb);
	}

	private static List<OutputData> generateOutputData(List<InputData> inputDataList) {
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		for (InputData inputData : inputDataList) {
			OutputData outputData = new OutputData();
			outputData.key = inputData.cityName + "," + inputData.stateName;
			outputData.populationMetric = populationFoo.getMetric(inputData.population, inputDataList);
			outputData.populationDensityMetric = populationDensityFoo.getMetric(inputData.populationDensity, inputDataList);
			outputData.augustHighMetric = augustHighFoo.getMetric(inputData.augustHigh, inputDataList);
			outputData.decemberHighMetric = decemberHighFoo.getMetric(inputData.decemberHigh, inputDataList);
			outputData.augustHighMinusDecemberHighMetric = augustHighMinusDecemberHighFoo.getMetric(inputData.augustHighMinusDecemberHigh, inputDataList);
			outputData.annualInchesOfRainMetric = annualInchesOfRainFoo.getMetric(inputData.annualInchesOfRain, inputDataList);
			outputData.daysOfRainMetric = daysOfRainFoo.getMetric(inputData.daysOfRain, inputDataList);
			outputData.sunnyDaysMetric = sunnyDaysFoo.getMetric(inputData.sunnyDays, inputDataList);
			outputData.annualSnowfallMetric = annualSnowfallFoo.getMetric(inputData.annualSnowfall, inputDataList);
			outputData.averageYearlyHumidityMetric = averageYearlyHumidityFoo.getMetric(inputData.averageYearlyHumidity, inputDataList);
			outputData.yearlyWindspeedMetric = yearlyWindspeedFoo.getMetric(inputData.yearlyWindspeed, inputDataList);
			outputData.violentCrimeMetric = violentCrimeFoo.getMetric(inputData.violentCrime, inputDataList);
			outputData.propertyCrimeMetric = propertyCrimeFoo.getMetric(inputData.propertyCrime, inputDataList);
			outputData.medianAgeMetric = medianAgeFoo.getMetric(inputData.medianAge, inputDataList);
			outputData.bachelorsMetric = bachelorsFoo.getMetricFloat(inputData.bachelors, inputDataList);
			outputData.medianHouseholdIncomeMetric = medianHouseholdIncomeFoo.getMetric(inputData.medianHouseholdIncome, inputDataList);
			outputData.medianHomePriceMetric = medianHomePriceFoo.getMetric(inputData.medianHomePrice, inputDataList);	    
			outputData.medianHomeAgeMetric = medianHomeAgeFoo.getMetric(inputData.medianHomeAge, inputDataList);	    
			outputData.homeAppreciationMetric = homeAppreciationFoo.getMetric(inputData.homeAppreciation, inputDataList);	
			outputData.airQualityMetric = airQualityFoo.getMetric(inputData.airQuality, inputDataList);	
			outputData.unemploymentRateMetric = unemploymentRateFoo.getMetricFloat(inputData.unemploymentRate, inputDataList);
			outputData.populationGrowthMetric = populationGrowthFoo.getMetricFloat(inputData.populationGrowth, inputDataList);
			outputData.percentDemocratMetric = percentDemocratFoo.getMetricFloat(inputData.percentDemocrat, inputDataList);
			outputData.percentRepublicanMetric = percentRepublicanFoo.getMetricFloat(inputData.percentRepublican, inputDataList);
			outputData.percentAsianMetric = percentAsianFoo.getMetricFloat(inputData.percentAsian, inputDataList);
			outputData.percentBlackMetric = percentBlackFoo.getMetricFloat(inputData.percentBlack, inputDataList);
			outputData.percentWhiteMetric = percentWhiteFoo.getMetricFloat(inputData.percentWhite, inputDataList);
			outputData.percentHispanicMetric = percentHispanicFoo.getMetricFloat(inputData.percentHispanic, inputDataList);
			outputData.metroPopulationMetric = metroPopulationFoo.getMetric(inputData.metroPopulation, inputDataList);
			outputData.povertyRateMetric = povertyRateFoo.getMetricFloat(inputData.povertyRate, inputDataList);
			outputData.avgSummerDewPointMetric = avgSummerDewPointFoo.getMetric(inputData.avgSummerDewPoint, inputDataList);
			outputData.avgApartmentRentMetric = avgApartmentRentFoo.getMetric(inputData.avgApartmentRent, inputDataList);
			outputData.avgApartmentSizeMetric = avgApartmentSizeFoo.getMetric(inputData.avgApartmentSize, inputDataList);
			outputData.homeSquareFeetMetric = homeSquareFeetFoo.getMetric(inputData.homeSquareFeet, inputDataList);
			outputData.costPerSquareFootMetric = costPerSquareFootFoo.getMetric(inputData.costPerSquareFoot, inputDataList);
			outputData.homeOwnershipRateMetric = homeOwnershipRateFoo.getMetricFloat(inputData.homeOwnershipRate, inputDataList);
			outputData.foreignBornPercentMetric = foreignBornPercentFoo.getMetricFloat(inputData.foreignBornPercent, inputDataList);
			outputData.feetAboveSeaLevelMetric = feetAboveSeaLevelFoo.getMetric(inputData.feetAboveSeaLevel, inputDataList);
			outputData.uvIndexMetric = uvIndexFoo.getMetricFloat(inputData.uvIndex, inputDataList);
			outputData.singlePopulationMetric = singlePopulationFoo.getMetricFloat(inputData.singlePopulation, inputDataList);
			outputData.walkScoreMetric = walkScoreFoo.getMetric(inputData.walkScore, inputDataList);
			outputData.transitScoreMetric = transitScoreFoo.getMetric(inputData.transitScore, inputDataList);
			outputData.bikeScoreMetric = bikeScoreFoo.getMetric(inputData.bikeScore, inputDataList);
			outputData.percentOfIncomeLostToHousingCostsMetric = percentOfIncomeLostToHousingCostsFoo.getMetricFloat(inputData.percentOfIncomeLostToHousingCosts, inputDataList);
			outputData.sexOffenderCountMetric = sexOffenderCountFoo.getMetricFloat(inputData.sexOffenderCount, inputDataList);
			outputData.hurricanesMetric = hurricanesFoo.getMetric(inputData.hurricanes, inputDataList);
			outputData.tornadoesMetric = tornadoesFoo.getMetric(inputData.tornadoes, inputDataList);
			outputData.earthQuakesMetric = earthQuakesFoo.getMetric(inputData.earthQuakes, inputDataList);
			//TODO8
			outputDataList.add(outputData);
		}
		return outputDataList;
	}

	private static void writeOutput(List<OutputData> outputDataList) {
		StringBuilder sb = new StringBuilder();
		sb.append("var populationMetric;\n");
		sb.append("var populationDensityMetric;\n");
		sb.append("var augustHighMetric;\n");
		sb.append("var decemberHighMetric;\n");
		sb.append("var augustHighMinusDecemberHighMetric;\n");
		
		sb.append("var annualInchesOfRainMetric;\n");
		sb.append("var daysOfRainMetric;\n");
		sb.append("var sunnyDaysMetric;\n");
		sb.append("var annualSnowfallMetric;\n");
		sb.append("var averageYearlyHumidityMetric;\n");
		sb.append("var yearlyWindspeedMetric;\n");
		sb.append("var violentCrimeMetric;\n");
		sb.append("var propertyCrimeMetric;\n");
		sb.append("var medianAgeMetric;\n");
		sb.append("var bachelorsMetric;\n");
		sb.append("var medianHouseholdIncomeMetric;\n");
		sb.append("var medianHomePriceMetric;\n");
		sb.append("var medianHomeAgeMetric;\n");
		sb.append("var homeAppreciationMetric;\n");
		sb.append("var airQualityMetric;\n");
		sb.append("var unemploymentRateMetric;\n");
		sb.append("var populationGrowthMetric;\n");
		sb.append("var percentDemocratMetric;\n");
		sb.append("var percentRepublicanMetric;\n");
		sb.append("var percentAsianMetric;\n");
		sb.append("var percentBlackMetric;\n");
		sb.append("var percentWhiteMetric;\n");
		sb.append("var percentHispanicMetric;\n");
		sb.append("var metroPopulationMetric;\n");
		sb.append("var povertyRateMetric;\n");
		sb.append("var avgSummerDewPointMetric;\n");
		sb.append("var avgApartmentRentMetric;\n");
		sb.append("var avgApartmentSizeMetric;\n");
		sb.append("var homeSquareFeetMetric;\n");
		sb.append("var costPerSquareFootMetric;\n");
		sb.append("var homeOwnershipRateMetric;\n");
		sb.append("var foreignBornPercentMetric;\n");
		sb.append("var feetAboveSeaLevelMetric;\n");
		sb.append("var uvIndexMetric;\n");
		sb.append("var singlePopulationMetric;\n");
		sb.append("var walkScoreMetric;\n");
		sb.append("var transitScoreMetric;\n");
		sb.append("var bikeScoreMetric;\n");
		sb.append("var percentOfIncomeLostToHousingCostsMetric;\n");
		sb.append("var sexOffenderCountMetric;\n");
		sb.append("var hurricanesMetric;\n");
		sb.append("var tornadoesMetric;\n");
		sb.append("var earthQuakesMetric;\n");
		//TODO9
		sb.append("var cityData;\n");
		StringBuilder sb1 = new StringBuilder("function populateMap1(map1){\n");
		sb1.append(sb.toString());
		StringBuilder sb2 = new StringBuilder("function populateMap2(map1){\n");
		sb2.append(sb.toString());
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap1.js", sb1);
		writeOutput("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap2.js", sb2);
		sb = new StringBuilder();
		int i = 0;
		for (OutputData outputData : outputDataList) {
			i++;
			appendMetric(sb, outputData.populationMetric, "populationMetric");
			appendMetric(sb, outputData.populationDensityMetric, "populationDensityMetric");
			appendMetric(sb, outputData.augustHighMetric, "augustHighMetric");
			appendMetric(sb, outputData.decemberHighMetric, "decemberHighMetric");
			appendMetric(sb, outputData.augustHighMinusDecemberHighMetric, "augustHighMinusDecemberHighMetric");
			appendMetric(sb, outputData.annualInchesOfRainMetric, "annualInchesOfRainMetric");
			appendMetric(sb, outputData.daysOfRainMetric, "daysOfRainMetric");
			appendMetric(sb, outputData.sunnyDaysMetric, "sunnyDaysMetric");
			appendMetric(sb, outputData.annualSnowfallMetric, "annualSnowfallMetric");
			appendMetric(sb, outputData.averageYearlyHumidityMetric, "averageYearlyHumidityMetric");
			appendMetric(sb, outputData.yearlyWindspeedMetric, "yearlyWindspeedMetric");
			appendMetric(sb, outputData.violentCrimeMetric, "violentCrimeMetric");
			appendMetric(sb, outputData.propertyCrimeMetric, "propertyCrimeMetric");
			appendMetric(sb, outputData.medianAgeMetric, "medianAgeMetric");
			appendFloatMetric(sb, outputData.bachelorsMetric, "bachelorsMetric");
			appendMetric(sb, outputData.medianHouseholdIncomeMetric, "medianHouseholdIncomeMetric");
			appendMetric(sb, outputData.medianHomePriceMetric, "medianHomePriceMetric");
			appendMetric(sb, outputData.medianHomeAgeMetric, "medianHomeAgeMetric");
			appendMetric(sb, outputData.homeAppreciationMetric, "homeAppreciationMetric");
			appendMetric(sb, outputData.airQualityMetric, "airQualityMetric");
			appendFloatMetric(sb, outputData.unemploymentRateMetric, "unemploymentRateMetric");
			appendFloatMetric(sb, outputData.populationGrowthMetric, "populationGrowthMetric");			
			appendFloatMetric(sb, outputData.percentDemocratMetric, "percentDemocratMetric");			
			appendFloatMetric(sb, outputData.percentRepublicanMetric, "percentRepublicanMetric");	
			appendFloatMetric(sb, outputData.percentAsianMetric, "percentAsianMetric");	
			appendFloatMetric(sb, outputData.percentBlackMetric, "percentBlackMetric");	
			appendFloatMetric(sb, outputData.percentWhiteMetric, "percentWhiteMetric");	
			appendFloatMetric(sb, outputData.percentHispanicMetric, "percentHispanicMetric");	
			appendMetric(sb, outputData.metroPopulationMetric, "metroPopulationMetric");
			appendFloatMetric(sb, outputData.povertyRateMetric, "povertyRateMetric");
			appendMetric(sb, outputData.avgSummerDewPointMetric, "avgSummerDewPointMetric");
			appendMetric(sb, outputData.avgApartmentRentMetric, "avgApartmentRentMetric");
			appendMetric(sb, outputData.avgApartmentSizeMetric, "avgApartmentSizeMetric");
			appendMetric(sb, outputData.homeSquareFeetMetric, "homeSquareFeetMetric");
			appendMetric(sb, outputData.costPerSquareFootMetric, "costPerSquareFootMetric");
			appendFloatMetric(sb, outputData.homeOwnershipRateMetric, "homeOwnershipRateMetric");
			appendFloatMetric(sb, outputData.foreignBornPercentMetric, "foreignBornPercentMetric");
			appendMetric(sb, outputData.feetAboveSeaLevelMetric, "feetAboveSeaLevelMetric");
			appendFloatMetric(sb, outputData.uvIndexMetric, "uvIndexMetric");
			appendFloatMetric(sb, outputData.singlePopulationMetric, "singlePopulationMetric");
			appendMetric(sb, outputData.walkScoreMetric, "walkScoreMetric");
			appendMetric(sb, outputData.transitScoreMetric, "transitScoreMetric");
			appendMetric(sb, outputData.bikeScoreMetric, "bikeScoreMetric");
			appendFloatMetric(sb, outputData.percentOfIncomeLostToHousingCostsMetric, "percentOfIncomeLostToHousingCostsMetric");
			appendFloatMetric(sb, outputData.sexOffenderCountMetric, "sexOffenderCountMetric");
			appendMetric(sb, outputData.hurricanesMetric, "hurricanesMetric");
			appendMetric(sb, outputData.tornadoesMetric, "tornadoesMetric");
			appendMetric(sb, outputData.earthQuakesMetric, "earthQuakesMetric");
			//TODO10
			
			sb.append("cityData = new CityData(populationMetric,populationDensityMetric,augustHighMetric,decemberHighMetric,augustHighMinusDecemberHighMetric,annualInchesOfRainMetric,daysOfRainMetric,sunnyDaysMetric,annualSnowfallMetric,averageYearlyHumidityMetric,yearlyWindspeedMetric,violentCrimeMetric,propertyCrimeMetric,medianAgeMetric,bachelorsMetric,medianHouseholdIncomeMetric,medianHomePriceMetric,medianHomeAgeMetric, homeAppreciationMetric, airQualityMetric, unemploymentRateMetric, populationGrowthMetric, percentDemocratMetric, percentRepublicanMetric, percentAsianMetric, percentBlackMetric, percentWhiteMetric, percentHispanicMetric, metroPopulationMetric, povertyRateMetric, avgSummerDewPointMetric, avgApartmentRentMetric, avgApartmentSizeMetric, homeSquareFeetMetric, costPerSquareFootMetric, homeOwnershipRateMetric, foreignBornPercentMetric, feetAboveSeaLevelMetric, uvIndexMetric, singlePopulationMetric, walkScoreMetric, transitScoreMetric, bikeScoreMetric, percentOfIncomeLostToHousingCostsMetric, sexOffenderCountMetric, hurricanesMetric, tornadoesMetric, earthQuakesMetric");
			//TODO11
			sb.append(");\nmap1.set(\"")
					.append(outputData.key).append("\", cityData);\n");
			if (i %4000 == 0 && i > 0 && i < 17000) {
				writeOutputAppend("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap1.js", sb);
				sb = new StringBuilder();
			}
			else if (i %4000 == 0 && i > 0 && i >= 17000) {
				writeOutputAppend("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap2.js", sb);
				sb = new StringBuilder();
			}
		}
		writeOutputAppend("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap2.js", sb);
		
		
		sb = new StringBuilder("}"); // closing the function
		writeOutputAppend("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap1.js", sb);
		writeOutputAppend("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityVsUSAComparison\\populationMap2.js", sb);
	}

	
	static DecimalFormat df = new DecimalFormat("0.0");
	
	static class Metric {
		float value;
		float cityPercentile;
		float personPercentile;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
	
	public static void main(String[] args) {
		List<InputData> inputDataList = readInput();
		AveragesAndMedians averagesAndMedians = getAveragesAndMedians(inputDataList);
		writeAveragesAndMedians(averagesAndMedians);
		List<OutputData> outputDataList = generateOutputData(inputDataList);
		writeOutput(outputDataList);

	}
	
	private static int getValidInt(String string) {
		if (string.contains("N/A")) {
			return -100;
		}
		if (string.contains(".")) {
			float f = Float.valueOf(string);
			return (int) f;
		}
		return Integer.valueOf(string);
	}
	
	private static int getValidIntegerFromPercent(String string) {
		int val = -100;
		if (string.contains("%")) {
			string = string.replace("%", "");
			val = Integer.valueOf(string);
		}
		return val;
	}
	
	private static float getValidFloatFromPercent(String string) {
		float val = -100;
		if (string.contains("%")) {
			string = string.replace("%", "");
			val = Float.valueOf(string);
		}
		return val;
	}

	private static String getString(String varName, int varValue) {
		return "var " + varName + " = " + varValue + ";\n";
	}
	
	private static String getFloatString(String varName, float varValue) {
		return "var " + varName + " = " + varValue + ";\n";
	}
	
	public static <T> float findMean(List<Integer> a) {
		int n = a.size();
		float sum = 0;
		for (int i = 0; i < n; i++) {
			sum += a.get(i);
		}
		return (float) sum / (float) n;
	}
	
	public static <T> float findMeanFloat(List<Float> a) {
		int n = a.size();
		float sum = 0;
		for (int i = 0; i < n; i++)
			sum += a.get(i);
		
		DecimalFormat df2 = new DecimalFormat("0.00");
		float f = (float) sum / (float) n;
		String st = df2.format(f);
		f = Float.valueOf(st);
		return f;
	}

	public static float findMedian(List<Integer> a) {
		int n = a.size();
		// First we sort the array
		Collections.sort(a);

		// check for even case
		if (n % 2 != 0)
			return a.get(n / 2);

		return (float) ((a.get((n - 1) / 2) + a.get(n / 2)) / 2.0);
	}
	
	public static float findMedianFloat(List<Float> a) {
		int n = a.size();
		// First we sort the array
		Collections.sort(a);

		// check for even case
		if (n % 2 != 0)
			return a.get(n / 2);

		return (float) ((a.get((n - 1) / 2) + a.get(n / 2)) / 2.0);
	}
	
	abstract static class Foo<T extends Comparable<T>> {
		
		Map<T, Float> mapOfValueToPersonPercentile = new HashMap<T, Float>();
		Map<T, Float> mapOfValueToCityPercentile = new HashMap<T, Float>();
		
		boolean isInvalidValue(T data) {
			return false;
		}
		
		public Metric getMetricFloat(float value, List<InputData> inputDataList) {
			Metric metric = new Metric();
			metric.value = value;
			if (mapOfValueToPersonPercentile.size() == 0) {
				mapOfValueToPersonPercentile = createPersonPercentileMap2(inputDataList);
				mapOfValueToCityPercentile = createCityPercentileMap2(inputDataList);
			}
			if (mapOfValueToPersonPercentile.containsKey(value)) {
				metric.personPercentile = mapOfValueToPersonPercentile.get(value);
				metric.cityPercentile = mapOfValueToCityPercentile.get(value);
			}
			else {
				metric.personPercentile = -1;
				metric.cityPercentile = -1;
			}
			return metric;
		}
		
		public Metric getMetric(int value, List<InputData> inputDataList) {
			Metric metric = new Metric();
			metric.value = value;
			if (mapOfValueToPersonPercentile.size() == 0) {
				mapOfValueToPersonPercentile = createPersonPercentileMap2(inputDataList);
				mapOfValueToCityPercentile = createCityPercentileMap2(inputDataList);
			}
			if (mapOfValueToPersonPercentile.containsKey(value)) {
				metric.personPercentile = mapOfValueToPersonPercentile.get(value);
				metric.cityPercentile = mapOfValueToCityPercentile.get(value);
			}
			else {
				metric.personPercentile = -1;
				metric.cityPercentile = -1;
			}
			return metric;
		}

		abstract T getData(InputData inputData);

		List<T> getGenericList(List<InputData> inputDataList) {
			List<T> list = new ArrayList<T>();
			for (InputData inputData : inputDataList) {
				T value = getData(inputData);
				if (!isInvalidValue(value)) {
					list.add(value);	
				}
			}
			Collections.sort(list);
			return list;
		}
		
		private Map<T, Float> createPersonPercentileMap2(List<InputData> inputDataList){
			List<Meh<T>> values = new ArrayList<Meh<T>>();
			Map<T, Float> map = new HashMap<T, Float>();
			for (InputData inputData : inputDataList) {
				T value = getData(inputData);
				if (!isInvalidValue(value)) {
					Meh<T> meh = new Meh<T>();
					meh.value = value;
					meh.population = inputData.population;
					values.add(meh);	
				}
			}
			Collections.sort(values);
			float totalPeopleWeHaveMoreThan = 0;
			float totalPopulation = 0;
			for (Meh<T> meh : values) {
				totalPopulation += meh.population;
			}
			for (int i = 0; i < values.size(); i++) {
				T val = values.get(i).value;
				if (!map.containsKey(val)) {
					float percentile = totalPeopleWeHaveMoreThan / totalPopulation;
					map.put(val, percentile);
				}
				totalPeopleWeHaveMoreThan += values.get(i).population;
			}
			return map;
		}
		
		private Map<T, Float> createCityPercentileMap2(List<InputData> inputDataList){
			List<T> values = new ArrayList<T>();
			Map<T, Float> map = new HashMap<T, Float>();
			for (InputData inputData : inputDataList) {
				T value = getData(inputData);
				if (!isInvalidValue(value)) {
					values.add(getData(inputData));	
				}
			}
			Collections.sort(values);
			for (int i = 0; i < values.size(); i++) {
				T val = values.get(i);
				if (!map.containsKey(val)) {
					float percentile = ((float) (i + 1)) / ((float) (values.size() - 1));
					map.put(val, percentile);
				}
			}
			return map;
		}
	}
	
	private static void writeOutput(String filePath, StringBuilder sb, boolean append) {
		try {
			FileWriter myWriter = new FileWriter(filePath, append);
			String st = sb.toString();
			myWriter.write(st);
			myWriter.close();
			System.out.println("wrote to file " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void writeOutput(String filePath, StringBuilder sb) {
		writeOutput(filePath, sb, false);

	}
	
	private static void writeOutputAppend(String filePath, StringBuilder sb) {
		writeOutput(filePath, sb, true);
	}
	
	static class Meh<T extends Comparable<T>> implements Comparable<Meh<T>> {
		T value;
		int population;
		public int compareTo(Meh<T> o1) {
			return value.compareTo(o1.value);
		}

	}
	
	private static void appendFloatMetric(StringBuilder sb, Metric metric, String varName) {
		sb.append(varName).append(" = new Metric(").append(getSafeFloatMetricValue(metric.value)).append(",")
				.append(getPercent(metric.cityPercentile)).append(", ")
				.append(getPercent(metric.personPercentile)).append(");\n");
	}
	
	private static void appendMetric(StringBuilder sb, Metric metric, String varName) {
		sb.append(varName).append(" = new Metric(").append(getSafeMetricValue(metric.value)).append(",")
				.append(getPercent(metric.cityPercentile)).append(", ")
				.append(getPercent(metric.personPercentile)).append(");\n");
	}
	
	private static String getPercent(float f) {
		if (f >= 0.9995 && f < 1) {
			f = 0.999f;
		}
		if (f < 0) {
			String st = "\"N/A\"";
			return st;
		}
		return "\"" + df.format(100 * f) + "%\"";
	}
	
	private static String getSafeFloatMetricValue (float f) {
		if (f < -99) {
			return "\"N/A\"";
		}
		return String.valueOf(f);
	}
	
	private static String getSafeMetricValue (float f) {
		if (f < -99) {
			return "\"N/A\"";
		}
		return String.valueOf((int) f);
	}
	
}
