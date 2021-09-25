class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric, decemberHighMetric, augustHighMinusDecemberHighMetric, annualInchesOfRainMetric, daysOfRainMetric, sunnyDaysMetric, annualSnowfallMetric, averageYearlyHumidityMetric, yearlyWindspeedMetric, violentCrimeMetric, propertyCrimeMetric, medianAgeMetric, bachelorsMetric, medianHouseholdIncomeMetric, medianHomePriceMetric, medianHomeAgeMetric, homeAppreciationMetric, airQualityMetric, unemploymentRateMetric, populationGrowthMetric, percentDemocratMetric, percentRepublicanMetric, percentAsianMetric, percentBlackMetric, percentWhiteMetric, percentHispanicMetric, metroPopulationMetric, povertyRateMetric, avgSummerDewPointMetric) {
    this.populationMetric = populationMetric;
	this.populationDensityMetric = populationDensityMetric;
	this.augustHighMetric = augustHighMetric;
	this.decemberHighMetric = decemberHighMetric;
	this.augustHighMinusDecemberHighMetric = augustHighMinusDecemberHighMetric;
	this.annualInchesOfRainMetric = annualInchesOfRainMetric;
	this.daysOfRainMetric = daysOfRainMetric;
	this.sunnyDaysMetric = sunnyDaysMetric;
	this.annualSnowfallMetric = annualSnowfallMetric;
	this.averageYearlyHumidityMetric = averageYearlyHumidityMetric;
	this.yearlyWindspeedMetric = yearlyWindspeedMetric;
	this.violentCrimeMetric = violentCrimeMetric;
	this.propertyCrimeMetric = propertyCrimeMetric;
	this.medianAgeMetric = medianAgeMetric;
	this.bachelorsMetric = bachelorsMetric;
	this.medianHouseholdIncomeMetric = medianHouseholdIncomeMetric;
	this.medianHomePriceMetric = medianHomePriceMetric;
	this.medianHomeAgeMetric = medianHomeAgeMetric;
	this.homeAppreciationMetric = homeAppreciationMetric;
	this.airQualityMetric = airQualityMetric;
	this.unemploymentRateMetric = unemploymentRateMetric;
	this.populationGrowthMetric = populationGrowthMetric;
	this.percentDemocratMetric = percentDemocratMetric;
	this.percentRepublicanMetric = percentRepublicanMetric;
	this.percentAsianMetric = percentAsianMetric;
	this.percentBlackMetric = percentBlackMetric;
	this.percentWhiteMetric = percentWhiteMetric;
	this.percentHispanicMetric = percentHispanicMetric;
	this.metroPopulationMetric = metroPopulationMetric;
	this.povertyRateMetric = povertyRateMetric;
	this.avgSummerDewPointMetric = avgSummerDewPointMetric;
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  insertRowWithGrayBackground(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric);
	  insertRow(tbdy, "Hottest month's avg high(F)", augustHighAverage, augustHighMedian, cityData.augustHighMetric);
	  insertRowWithGrayBackground(tbdy, "Coldest month's avg high(F)", decemberHighAverage, decemberHighMedian, cityData.decemberHighMetric);
	  insertRow(tbdy, "Hottest high minus coldest high", augustHighMinusDecemberHighAverage, augustHighMinusDecemberHighMedian, cityData.augustHighMinusDecemberHighMetric);
	  insertRowWithGrayBackground(tbdy, "Annual rainfall(in)", annualInchesOfRainAverage, annualInchesOfRainMedian, cityData.annualInchesOfRainMetric);
	  insertRow(tbdy, "Annual days of precipitation", daysOfRainAverage, daysOfRainMedian, cityData.daysOfRainMetric);
	  insertRowWithGrayBackground(tbdy, "Annual days of sunshine", sunnyDaysAverage, sunnyDaysMedian, cityData.sunnyDaysMetric);
	  insertRow(tbdy, "Annual snowfall(in)", annualSnowfallAverage, annualSnowfallMedian, cityData.annualSnowfallMetric);
	  insertRowWithGrayBackground(tbdy, "Average annual dew point", averageYearlyHumidityAverage, averageYearlyHumidityMedian, cityData.averageYearlyHumidityMetric);
	  insertRow(tbdy, "Average yearly windspeed (mph)", yearlyWindspeedAverage, yearlyWindspeedMedian, cityData.yearlyWindspeedMetric);
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Violent crime index", violentCrimeAverage, violentCrimeMedian, cityData.violentCrimeMetric, "Crime is ranked from 1 (low) to 100 (high)");
	  insertRowWithInfoButton(tbdy, "Property crime index", propertyCrimeAverage, propertyCrimeMedian, cityData.propertyCrimeMetric, "Crime is ranked from 1 (low) to 100 (high)");
	  insertRowWithGrayBackground(tbdy, "Median age", medianAgeAverage, medianAgeMedian, cityData.medianAgeMetric);
	  insertRow(tbdy, "% with at least a Bachelor's degree", bachelorsAverage, bachelorsMedian, cityData.bachelorsMetric);
	  insertRowWithGrayBackground(tbdy, "Median Household Income ($)", medianHouseholdIncomeAverage, medianHouseholdIncomeMedian, cityData.medianHouseholdIncomeMetric);
	  insertRow(tbdy, "Median Home Price ($)", medianHomePriceAverage, medianHomePriceMedian, cityData.medianHomePriceMetric);
	  insertRowWithGrayBackground(tbdy, "Median Home Age (yrs)", medianHomeAgeAverage, medianAgeMedian, cityData.medianHomeAgeMetric);
	  insertRow(tbdy, "Home appreciation last 10 yrs (%)", homeAppreciationAverage, homeAppreciationMedian, cityData.homeAppreciationMetric);
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Air quality index", airQualityAverage, airQualityMedian, cityData.airQualityMetric, "The air quality ranked from 1 (low) to 100 (high)");
	  insertRow(tbdy, "Unemployment Rate (%)", unemploymentRateAverage, unemploymentRateMedian, cityData.unemploymentRateMetric);
	  insertRowWithGrayBackground(tbdy, "Population growth last 10 yrs (%)", populationGrowthAverage, populationGrowthMedian, cityData.populationGrowthMetric);
	  insertRowWithInfoButton(tbdy, "Percent Democrat (%)", percentDemocratAverage, percentDemocratMedian, cityData.percentDemocratMetric, "% of residents who are Democrat. Tracked on the county level");
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Percent Republican (%)", percentRepublicanAverage, percentRepublicanMedian, cityData.percentRepublicanMetric, "% of residents who are Republican. Tracked on the county level");
	  insertRow(tbdy, "Percent Asian (%)", percentAsianAverage, percentAsianMedian, cityData.percentAsianMetric);
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Percent Black (%)", percentBlackAverage, percentBlackMedian, cityData.percentBlackMetric, "Includes hispanic black and non-hispanic black residents");
	  insertRowWithInfoButton(tbdy, "Percent White (%)", percentWhiteAverage, percentWhiteMedian, cityData.percentWhiteMetric, "Includes hispanic white and non-hispanic white residents");
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Percent Hispanic (%)", percentHispanicAverage, percentHispanicMedian, cityData.percentHispanicMetric, "% of residents who claim hispanic, regardless of race");
	  insertRow(tbdy, "Metro Population", metroPopulationAverage, metroPopulationMedian, cityData.metroPopulationMetric);
	  insertRowWithGrayBackground(tbdy, "Poverty Rate (%)", povertyRateAverage, povertyRateMedian, cityData.povertyRateMetric);
	  insertRow(tbdy, "Average summer dew point", avgSummerDewPointAverage, avgSummerDewPointMedian, cityData.avgSummerDewPointMetric);
}