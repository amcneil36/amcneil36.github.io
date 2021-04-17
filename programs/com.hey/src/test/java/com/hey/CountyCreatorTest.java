package com.hey;

import org.junit.Test;

import com.hey.ZipCode.DataObject2;

import org.junit.*;

public class CountyCreatorTest {

	@Test
	public void testZipWithGoodData() {
		DataObject2 actual = ZipCode.populateDataObject("georgia", "<a href=\"../zip-code/georgia/avondale_estates/30002\"><u>30002 (Avondale Estates)</u></a>");
		DataObject2 expected = new DataObject2();
		expected.augHi = "87";
		expected.augHiMinusDecHi = 33;
		expected.cityName = "avondale_estates";
		expected.countyName = "DeKalb County";
		expected.decHi = "54";
		expected.medianAge = "42.5";
		expected.medianHomePrice = "391400";
		expected.medianIncome = "47297";
		expected.numDaysOfRain = "109";
		expected.numInchesOfRain = "53";
		expected.numInchesOfSnow = "2";
		expected.numSunnyDays = "217";
		expected.percentAsian = "4.2";
		expected.percentBlack = "33.8";
		expected.percentHispanic="3.8";
		expected.percentWhite = "55.3";
		expected.population = "5669";
		expected.populationDensity = "3352";
		expected.propertyCrime = "42.1";
		expected.stateName = "georgia";
		expected.violentCrime = "35.6";
		expected.zipCode = "30002";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testZipWithWeirdData() {
		DataObject2 actual = ZipCode.populateDataObject("georgia", "<a href=\"../zip-code/georgia/carrollton/30118\"><u>30118 (Carrollton)</u></a>");
		DataObject2 expected = new DataObject2();
		expected.augHi = "88";
		expected.augHiMinusDecHi = 33;
		expected.cityName = "carrollton";
		expected.countyName = "Carroll County";
		expected.decHi = "55";
		expected.medianAge = "19.6";
		expected.medianHomePrice = "151800";
		expected.medianIncome = "0";
		expected.numDaysOfRain = "106.4";
		expected.numInchesOfRain = "52.0";
		expected.numInchesOfSnow = "0.9";
		expected.numSunnyDays = "217";
		expected.percentAsian = "1.1";
		expected.percentBlack = "66.1";
		expected.percentHispanic="5.6";
		expected.percentWhite = "26.4";
		expected.population = "2285";
		expected.populationDensity = "6109.6";
		expected.propertyCrime = "N/A";
		expected.stateName = "georgia";
		expected.violentCrime = "N/A";
		expected.zipCode = "30118";
		Assert.assertEquals(expected, actual);
	}
}
