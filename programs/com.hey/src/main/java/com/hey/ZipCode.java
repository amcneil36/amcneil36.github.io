package com.hey;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.jsoup.nodes.Element;

import com.hey.SperlingReader.DataObject;

/*
 * maybe make a set of zip codes that failed and maybe include debug info on it? maybe try catch in our main for loop and populate the failing zip codes in the catch?
 * 
 * completed zip code 30117. 74 of 735 zip codes have been completed for georgia. Time remaining: 1hr 11min
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
pattern not found: 13 (Bind Data) Conversion from string " " to type 'Double' is not valid. 
	Zip 30118 (Carrollton, GA) People
 Enhanced Cost of Living CalculatorNow includes childcare, taxes, health, housing for home owners vs renters, insurance costs and more when you upgrade to premium. SEE MY OPTIONS No Thanks Home (current) Cost of Living | Find Your Best Place Quiz | City Compare | Menu Cost of Living Find Your Best Place Quiz City Compare Crime Compare Climate Top Lists Rankings Schools Find a Place DataEngine News Try Premium Log InSign Up Rankings Zip 30118 (Carrollton, GA) 0 Reviews | Review This Place Download This Place Close United States / Georgia / Atlanta-Sandy Springs-Roswell Metro Area / Carroll County / Carrollton / Carrollton (zip 30118) CATEGORIES Overview Real Estate Cost of Living Job Market Crime Climate Weather Education Stats Economy Health Religion People Stats Politics & Voting Housing Stats Commute Time Rankings ReviewsAtlanta Gifts People in Zip 30118 (Carrollton, GA) ESTIMATED TOTAL POPULATION BY AGE PEOPLE Carrollton, Georgia United States Population 2,285 321,004,407 Female Population 60.7% 50.8% Male Population 39.3% 49.2% Median Age 19.6 37.8 Population - 2010 2,373 308,745,538 Population - 2000 54 285,036,114 Population - 1990 42 251,960,433 Pop. 2000 to Now 4,131.5% 12.6% Pop. 1990 to Now 5,340.5% 27.4% Population Density 6,109.6 90.9 Land Area 0.4 3,531,905.4 Water Area 0.0 264,836.8 RACE White 26.4% 61.5% Black 66.1% 12.3% Asian 1.1% 5.3% Native American 0.0% 0.7% Hawaiian, Pacific Islander 0.0% 0.2% Other Race 0.0% 0.2% Two or More Races 0.9% 2.3% Hispanic 5.6% 17.6% MARRIAGE & FAMILY Married Population 0.9% 50.2% Currently Married 0.9% 48.2% Married but Separated 0.0% 2.0% Single Population 99.1% 49.8% Never Married 99.1% 33.1% Divorced 0.0% 10.9% Widowed 0.0% 5.8% Household Size 0.00 2.63 Households 0 118,825,921 Family Households 0 78,298,703 Married couple, w/children 0.31128 PEOPLE STATS MAP Population Population Density (per sq. mile) Median Age (US Avg: 37.4) Percent Female (US Avg: 50.8%) Population Change (since 2000) Select Map Options Population Population Density Median Age Percent Female Pop Change Reset Map View More Data > Reviews for Carrollton 0 Reviews Start Your Review of Carrollton View More Data > COMPARE COST OF LIVING Compare Zip 30118 (Carrollton, GA) to any other place in the USA. MAPS OF ZIP 30118 (CARROLLTON, GA) ZIP CODES IN ZIP 30118 (CARROLLTON, GA) Carrollton (zip 30118) PREMIUM MEMBERSHIP Members receive 10 FREE city profile downloads a month, unlimited access to our detailed cost of living calculator and analysis, unlimited access to our DataEngine, and more.Try Now Join BestPlaces © Sperling's Best Places. All rights reserved. Info on Best Place to Live, Cost of Living, Schools, Crime Rates, Climate, House Prices, and more.... About Best Places Meet the Team Press Contact Log In Sign Up Data Help Privacy Policy Terms of Use Returns & Refunds Keep this site free by: - Making a gift to support our research - Buy the BestPlaces Board Game Follow Us Facebook Twitter YouTube Instagram Get BestPlaces App Open the app and start learning about the neighborhood you are standing in... BestPlaces Mobile App Best Places Tools Seach Best Places to Live Take the Best Places Quiz Compare Cities Side-by-Side Compare Cost of Living Compare Crime Rates Compare Climates Best Places Ratings Create Best Places List Compare Schools New York, NY Chicago, IL Los Angeles, CA Las Vegas, NV Houston, TX Miami, FL Boston, MA Washington, D.C. Dallas, TX Atlanta, GA Best Places to Live Best Places for Hygee Best Places for Summer Best Places to Retire Best Places for Foodies Best Places to Feel Safe Best Places for Military Retirement Charlottesville, VA Pittsburgh, PA Minneapolis, MN Ames, IA Sarasota, FL Noblesville, IN Oregon City, OR Portland, OR Bozeman, MT Carlsbad, CA Walla Walla, WA San Antonio, TX Boulder, CO
completed zip code 30118. 75 of 735 zip codes have been completed for georgia. Time remaining: 1hr 11min
 */
public class ZipCode {

	public static class DataObject2 {
		String zipCode;
		String cityName;
		String stateName;
		public String countyName;
		String population;
		String populationDensity;
		String augHi; //
		String decHi; //
		public int augHiMinusDecHi;
		String violentCrime = "N/A";
		String propertyCrime = "N/A";
		String medianAge;
		String medianIncome;
		String medianHomePrice;
		String percentAsian = "N/A";
		String percentBlack = "N/A";
		String percentWhite = "N/A";
		String percentHispanic = "N/A";

		String numInchesOfRain = "-1";
		String numInchesOfSnow = "-1";
		String numSunnyDays = "-1";
		String numDaysOfRain = "-1";

		private String getEndingUrl() {
			return this.stateName + "/" + this.cityName + "/" + this.zipCode;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}

	// keytool -import -alias example4 -keystore "C:\Program Files
	// (x86)\Java\jre1.8.0_281\lib\security\cacerts" -file myCer3.cer

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String stateName = "georgia";
		String stateAbbreviation = "ga";
		Element el = SperlingReader
				.RetrieveHtmlcodeFromPage("https://www.bestplaces.net/find/zip.aspx?st=" + stateAbbreviation);
		String[] data = el.getElementsByAttribute("href").toString().split("\n");
		List<DataObject2> list = new ArrayList<DataObject2>();
		int numCompletedZipCodes = 0;
		List<String> data2 = new ArrayList<String>();
		for (String st : data) {
			if (st.contains("<a href=\"../zip-code/")) {
				data2.add(st);
			}
		}
		data = null;
		int numZipCodes = data2.size();
		System.out.println("num zip codes in " + stateName + ": " + numZipCodes);
		long startTime = System.currentTimeMillis();
		for (String st : data2) {
			if (numCompletedZipCodes > 5) {
				break;
			}
			DataObject2 obj = populateDataObject(stateName, st);
			list.add(obj);
			numCompletedZipCodes++;
			long secondsSinceStart = (System.currentTimeMillis() - startTime) / 1000;
			int numRemainingZipCodes = numZipCodes - numCompletedZipCodes;
			long minRemaining = (numRemainingZipCodes * secondsSinceStart / numCompletedZipCodes) / 60;
			System.out.println("completed zip code " + obj.zipCode + ". " + numCompletedZipCodes + " of " + numZipCodes
					+ " zip codes have been completed for " + stateName + ". Time remaining: "
					+ SperlingReader.minToString((int) minRemaining));

		}
		writeOutput(list);

	}

	public static DataObject2 populateDataObject(String stateName, String st) {
		st = st.substring(("<a href=\"../zip-code/" + stateName + "/").length(), st.indexOf("<u>") - 2);
		String[] temp = st.split("/");
		String city = temp[0];
		String zip = temp[1];
		DataObject2 obj = new DataObject2();
		obj.zipCode = zip;
		obj.cityName = city;
		obj.stateName = stateName;
		addTemperatureDataToObj2(obj);
		addPeopleDataToObj2(obj);
		addOverviewDataToObj2(obj);
		addCrimeDataToObj2(obj);
		addClimateDataToObj(obj);
		return obj;
	}

	private static void addClimateDataToObj(DataObject2 obj) {
		String url = "https://www.bestplaces.net/climate/zip-code/" + obj.getEndingUrl();
		String text3 = SperlingReader.ReadTextFromPage(url);
		if (text3.contains("sunny days per")) {
			obj.numInchesOfRain = SperlingReader.getNumbersBeforeText(text3, "inches of rain");
			obj.numInchesOfSnow = SperlingReader.getNumbersBeforeText(text3, "inches of snow");
			obj.numSunnyDays = SperlingReader.getNumbersBeforeText(text3, "sunny days per");
			obj.numDaysOfRain = SperlingReader.getNumbersBeforeText(text3, "days per year. Precipitation");
		} else {
			obj.numInchesOfRain = SperlingReader.getNextNumberAfterText(text3, "Rainfall");
			obj.numInchesOfSnow = SperlingReader.getNextNumberAfterText(text3, "Snowfall");
			obj.numSunnyDays = SperlingReader.getNextNumberAfterText(text3, "Sunny");
			obj.numDaysOfRain = SperlingReader.getNextNumberAfterText(text3, "Precipitation");
		}
	}

	static class AndrewStringBuilder {
		private StringBuilder sb = new StringBuilder();

		private String getString() {
			return sb.toString();
		}

		private AndrewStringBuilder appendComma(String st) {
			sb.append(st).append(",");
			return this;
		}

		private AndrewStringBuilder appendNewLine(String st) {
			sb.append(st).append("\n");
			return this;
		}
	}

	private static void writeOutput(List<DataObject2> list) {
		try {
			AndrewStringBuilder sb = new AndrewStringBuilder();
			sb.appendNewLine(
					"zipCode,cityName,stateName,countyName,population,populationDensity,augHi,decHi,augHiMinusDecHi,numInchesOfRain,numDaysOfRain,numSunnyDays,numInchesOfSnow,numviolentCrime,propertyCrime,medianAge,medianIncome,medianHomePrice,%Asian,%Black,%White,%Hispanic");
			for (DataObject2 obj : list) {
				addRowDataToStringBuilder(sb, obj);
			}
			String outputTitle = list.get(0).stateName + "_";
			outputTitle += System.currentTimeMillis() + ".csv";
			FileWriter myWriter = new FileWriter(outputTitle);
			myWriter.write(sb.getString());
			myWriter.close();
			System.out.println("wrote to file " + outputTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addRowDataToStringBuilder(AndrewStringBuilder sb, DataObject2 obj) {
		sb.appendComma(obj.zipCode).appendComma(obj.cityName).appendComma(obj.stateName).appendComma(obj.countyName)
				.appendComma(obj.population).appendComma(obj.populationDensity).appendComma(obj.augHi)
				.appendComma(obj.decHi).appendComma(String.valueOf(obj.augHiMinusDecHi)).appendComma(obj.numInchesOfRain)
				.appendComma(obj.numDaysOfRain).appendComma(obj.numSunnyDays).appendComma(obj.numInchesOfSnow)
				.appendComma(obj.violentCrime).appendComma(obj.propertyCrime).appendComma(obj.medianAge)
				.appendComma(obj.medianIncome).appendComma(obj.medianHomePrice).appendComma(obj.percentAsian)
				.appendComma(obj.percentBlack).appendComma(obj.percentWhite).appendNewLine(obj.percentHispanic);
	}

	private static void addTemperatureDataToObj2(DataObject2 obj) {
		String text2 = SperlingReader
				.ReadTextFromPage("https://www.bestplaces.net/weather/zip-code/" + obj.getEndingUrl());

		int startIdx3 = text2.indexOf("August ", text2.indexOf("(°F)")) + "August ".length();
		int endIdx3 = text2.indexOf("°", startIdx3);
		String augHi = text2.substring(startIdx3, endIdx3);
		obj.augHi = augHi;

		int startIdx2 = text2.indexOf("December ", text2.indexOf("(°F)")) + "December ".length();
		int endIdx2 = text2.indexOf("°", startIdx2);
		String decHi = text2.substring(startIdx2, endIdx2);
		obj.decHi = decHi;
		obj.augHiMinusDecHi = Integer.parseInt(obj.augHi) - Integer.parseInt(obj.decHi);
	}

	private static void addPeopleDataToObj2(DataObject2 obj) {
		String url = "https://www.bestplaces.net/people/zip-code/" + obj.getEndingUrl();
		String text3 = SperlingReader.ReadTextFromPage(url);
		if (text3.contains("claim Hispanic")) {
			obj.population = SperlingReader.getNumbersBeforeText(text3, " There are ");
			obj.populationDensity = SperlingReader.getNumbersBeforeText(text3, "people per square mile");
			obj.percentHispanic = Reader
					.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " claim Hispanic"));
			obj.percentWhite = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are white"));
			obj.percentBlack = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are black"));
			obj.percentAsian = Reader.returnValidValue(SperlingReader.getNumbersBeforeText(text3, " are asian"));
		} else {
			obj.population = SperlingReader.getNextNumberAfterText(text3, "Population");
			obj.populationDensity = SperlingReader.getNextNumberAfterText(text3, "Population Density");
			obj.percentHispanic = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Hispanic"));
			obj.percentWhite = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "White"));
			obj.percentBlack = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Black"));
			obj.percentAsian = Reader.returnValidValue(SperlingReader.getNextNumberAfterText(text3, "Asian"));
		}
	}

	private static void addOverviewDataToObj2(DataObject2 obj) {
		String text3 = SperlingReader.ReadTextFromPage("https://www.bestplaces.net/zip-code/" + obj.getEndingUrl());
		String medianIncome = SperlingReader.getNextNumberAfterText(text3, "Median Income");
		obj.medianIncome = medianIncome;
		String medianHomePrice = SperlingReader.getNextNumberAfterText(text3, "Home Price");
		obj.medianHomePrice = medianHomePrice;
		String medianAge = SperlingReader.getNextNumberAfterText(text3, "Median Age");
		obj.medianAge = medianAge;
		obj.countyName = text3.substring(text3.indexOf("County: ") + "County: ".length(),
				text3.indexOf(" Metro Area: "));
	}

	private static void addCrimeDataToObj2(DataObject2 obj) {
		String text3 = SperlingReader
				.ReadTextFromPage("https://www.bestplaces.net/crime/zip-code/" + obj.getEndingUrl());
		if (text3.contains("violent crime is")) {
			String violentCrime = SperlingReader.getNextNumberAfterText(text3, "violent crime is");
			obj.violentCrime = violentCrime;
			String propertyCrime = SperlingReader.getNextNumberAfterText(text3, "property crime is");
			obj.propertyCrime = propertyCrime;
		}
	}
}
