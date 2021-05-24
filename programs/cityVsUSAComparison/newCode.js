class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric, decemberHighMetric, augustHighMinusDecemberHighMetric, annualInchesOfRainMetric, daysOfRainMetric, sunnyDaysMetric, annualSnowfallMetric, averageYearlyHumidityMetric, yearlyWindspeedMetric, violentCrimeMetric, propertyCrimeMetric, medianAgeMetric, bachelorsMetric, medianHouseholdIncomeMetric, medianHomePriceMetric, medianHomeAgeMetric, homeAppreciationMetric, airQualityMetric, unemploymentRateMetric, populationGrowthMetric, percentDemocratMetric, percentRepublicanMetric, percentAsianMetric, percentBlackMetric, percentWhiteMetric, percentHispanicMetric) {
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
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric, "hey!");
	  insertRow(tbdy, "Average August High(F)", augustHighAverage, augustHighMedian, cityData.augustHighMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Average December High(F)", decemberHighAverage, decemberHighMedian, cityData.decemberHighMetric, "hey!");
	  insertRow(tbdy, "August High minus December High", augustHighMinusDecemberHighAverage, augustHighMinusDecemberHighMedian, cityData.augustHighMinusDecemberHighMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Annual rainfall(in)", annualInchesOfRainAverage, annualInchesOfRainMedian, cityData.annualInchesOfRainMetric, "hey!");
	  insertRow(tbdy, "Annual days of precipitation", daysOfRainAverage, daysOfRainMedian, cityData.daysOfRainMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Annual days of sunshine", sunnyDaysAverage, sunnyDaysMedian, cityData.sunnyDaysMetric, "hey!");
	  insertRow(tbdy, "Annual snowfall(in)", annualSnowfallAverage, annualSnowfallMedian, cityData.annualSnowfallMetric);
	  insertRowWithGrayBackground(tbdy, "Average yearly humidity (%)", averageYearlyHumidityAverage, averageYearlyHumidityMedian, cityData.averageYearlyHumidityMetric, "hey!");
	  insertRow(tbdy, "Average yearly windspeed (mph)", yearlyWindspeedAverage, yearlyWindspeedMedian, cityData.yearlyWindspeedMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Violent crime index", violentCrimeAverage, violentCrimeMedian, cityData.violentCrimeMetric, "hey!");
	  insertRow(tbdy, "Property crime index", propertyCrimeAverage, propertyCrimeMedian, cityData.propertyCrimeMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Median age", medianAgeAverage, medianAgeMedian, cityData.medianAgeMetric, "hey!");
	  insertRow(tbdy, "% with at least a Bachelor's degree", bachelorsAverage, bachelorsMedian, cityData.bachelorsMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Median Household Income ($)", medianHouseholdIncomeAverage, medianHouseholdIncomeMedian, cityData.medianHouseholdIncomeMetric, "hey!");
	  insertRow(tbdy, "Median Home Price ($)", medianHomePriceAverage, medianHomePriceMedian, cityData.medianHomePriceMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Median Home Age (yrs)", medianHomeAgeAverage, medianAgeMedian, cityData.medianHomeAgeMetric, "hey!");
	  insertRow(tbdy, "Home appreciation last 10 yrs (%)", homeAppreciationAverage, homeAppreciationMedian, cityData.homeAppreciationMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Air quality index", airQualityAverage, airQualityMedian, cityData.airQualityMetric, "hey!");
	  insertRow(tbdy, "Unemployment Rate (%)", unemploymentRateAverage, unemploymentRateMedian, cityData.unemploymentRateMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Population growth last 10 yrs (%)", populationGrowthAverage, populationGrowthMedian, cityData.populationGrowthMetric, "hey!");
	  insertRow(tbdy, "Percent Democrat (%)", percentDemocratAverage, percentDemocratMedian, cityData.percentDemocratMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Percent Republican (%)", percentRepublicanAverage, percentRepublicanMedian, cityData.percentRepublicanMetric, "hey!");
	  insertRow(tbdy, "Percent Asian (%)", percentAsianAverage, percentAsianMedian, cityData.percentAsianMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Percent Black (%)", percentBlackAverage, percentBlackMedian, cityData.percentBlackMetric, "hey!");
	  insertRow(tbdy, "Percent White (%)", percentWhiteAverage, percentWhiteMedian, cityData.percentWhiteMetric, "hey!");
	  insertRowWithGrayBackground(tbdy, "Percent Hispanic (%)", percentHispanicAverage, percentHispanicMedian, cityData.percentHispanicMetric, "hey!");
	  
}