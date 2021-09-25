class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric, decemberHighMetric, augustHighMinusDecemberHighMetric, annualInchesOfRainMetric, daysOfRainMetric, sunnyDaysMetric, annualSnowfallMetric, averageYearlyHumidityMetric, yearlyWindspeedMetric, violentCrimeMetric, propertyCrimeMetric, medianAgeMetric, bachelorsMetric, medianHouseholdIncomeMetric, medianHomePriceMetric, medianHomeAgeMetric, homeAppreciationMetric, airQualityMetric, unemploymentRateMetric, populationGrowthMetric, percentDemocratMetric, percentRepublicanMetric, percentAsianMetric, percentBlackMetric, percentWhiteMetric, percentHispanicMetric, metroPopulationMetric, povertyRateMetric, avgSummerDewPointMetric, 
  avgApartmentRentMetric, avgApartmentSizeMetric, homeSquareFeetMetric, costPerSquareFootMetric, homeOwnershipRateMetric, foreignBornPercentMetric, feetAboveSeaLevelMetric, 
  uvIndexMetric, singlePopulationMetric, walkScoreMetric, transitScoreMetric, bikeScoreMetric, percentOfIncomeLostToHousingCostsMetric, sexOffenderCountMetric, hurricanesMetric,
  tornadoesMetric, earthQuakesMetric) {
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
	this.avgApartmentRentMetric = avgApartmentRentMetric;
	this.avgApartmentSizeMetric = avgApartmentSizeMetric;
	this.homeSquareFeetMetric = homeSquareFeetMetric;
	this.costPerSquareFootMetric = costPerSquareFootMetric;
	this.homeOwnershipRateMetric = homeOwnershipRateMetric;
	this.foreignBornPercentMetric = foreignBornPercentMetric;
	this.feetAboveSeaLevelMetric = feetAboveSeaLevelMetric;
	this.uvIndexMetric = uvIndexMetric;
	this.singlePopulationMetric = singlePopulationMetric;
	this.walkScoreMetric = walkScoreMetric;
	this.transitScoreMetric = transitScoreMetric;
	this.bikeScoreMetric = bikeScoreMetric;
	this.percentOfIncomeLostToHousingCostsMetric = percentOfIncomeLostToHousingCostsMetric;
	this.sexOffenderCountMetric = sexOffenderCountMetric;
	this.hurricanesMetric = hurricanesMetric;
	this.tornadoesMetric = tornadoesMetric;
	this.earthQuakesMetric = earthQuakesMetric;
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  insertRowWithGrayBackground(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric);
	  insertRow(tbdy, "Metro population", metroPopulationAverage, metroPopulationMedian, cityData.metroPopulationMetric);
	  insertRowWithGrayBackground(tbdy, "Hottest month's avg high(F)", augustHighAverage, augustHighMedian, cityData.augustHighMetric);
	  insertRow(tbdy, "Coldest month's avg high(F)", decemberHighAverage, decemberHighMedian, cityData.decemberHighMetric);
	  insertRowWithGrayBackground(tbdy, "Hottest high minus coldest high", augustHighMinusDecemberHighAverage, augustHighMinusDecemberHighMedian, cityData.augustHighMinusDecemberHighMetric);
	  insertRow(tbdy, "Annual rainfall(in)", annualInchesOfRainAverage, annualInchesOfRainMedian, cityData.annualInchesOfRainMetric);
	  insertRowWithGrayBackground(tbdy, "Annual days of precipitation", daysOfRainAverage, daysOfRainMedian, cityData.daysOfRainMetric);
	  insertRow(tbdy, "Annual days of sunshine", sunnyDaysAverage, sunnyDaysMedian, cityData.sunnyDaysMetric);
	  insertRowWithGrayBackground(tbdy, "Annual snowfall(in)", annualSnowfallAverage, annualSnowfallMedian, cityData.annualSnowfallMetric);
	  insertRow(tbdy, "Avg Summer Dew Point", avgSummerDewPointAverage, avgSummerDewPointMedian, cityData.avgSummerDewPointMetric);
	  insertRowWithGrayBackground(tbdy, "Avg Annual Dew Point", averageYearlyHumidityAverage, averageYearlyHumidityMedian, cityData.averageYearlyHumidityMetric);
	  insertRow(tbdy, "Average yearly windspeed (mph)", yearlyWindspeedAverage, yearlyWindspeedMedian, cityData.yearlyWindspeedMetric);
	  insertRowWithGrayBackgroundWithInfoButton(tbdy, "Violent crime index", violentCrimeAverage, violentCrimeMedian, cityData.violentCrimeMetric, "Crime is ranked from 1 (low) to 100 (high)");
	  insertRowWithInfoButton(tbdy, "Property crime index", propertyCrimeAverage, propertyCrimeMedian, cityData.propertyCrimeMetric, "Crime is ranked from 1 (low) to 100 (high)");
	  insertRowWithGrayBackground(tbdy, "Median age", medianAgeAverage, medianAgeMedian, cityData.medianAgeMetric);
	  insertRow(tbdy, "% with at least a Bachelor's degree", bachelorsAverage, bachelorsMedian, cityData.bachelorsMetric);
	  insertRowWithGrayBackground(tbdy, "Median Household Income ($)", medianHouseholdIncomeAverage, medianHouseholdIncomeMedian, cityData.medianHouseholdIncomeMetric);
	  insertRow(tbdy, "Poverty Rate (%)", povertyRateAverage, povertyRateMedian, cityData.povertyRateMetric);
	  insertRowWithGrayBackground(tbdy, "Average Apartment Monthly Rent ($)", avgApartmentRentAverage, avgApartmentRentMedian, cityData.avgApartmentRentMetric);
	  insertRow(tbdy, "Average apartment sqft", avgApartmentSizeAverage, avgApartmentSizeMedian, cityData.avgApartmentSizeMetric);
	  insertRowWithGrayBackground(tbdy, "Median Home Price ($)", medianHomePriceAverage, medianHomePriceMedian, cityData.medianHomePriceMetric);
	  insertRow(tbdy, "Median home sqft", homeSquareFeetAverage, homeSquareFeetMedian, cityData.homeSquareFeetMetric);
	  insertRowWithGrayBackground(tbdy, "Median home cost per sqft ($)", costPerSquareFootAverage, costPerSquareFootMedian, cityData.costPerSquareFootMetric);
	  insertRow(tbdy, "Median Home Age (yrs)", medianHomeAgeAverage, medianAgeMedian, cityData.medianHomeAgeMetric);
	  insertRowWithGrayBackground(tbdy, "Homeownership Rate (%)", homeOwnershipRateAverage, homeOwnershipRateMedian, cityData.homeOwnershipRateMetric);
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
	  insertRow(tbdy, "Foreign Born %", foreignBornPercentAverage, foreignBornPercentMedian, cityData.foreignBornPercentMetric);
	  insertRowWithGrayBackground(tbdy, "Elevation (ft)", feetAboveSeaLevelAverage, feetAboveSeaLevelMedian, cityData.feetAboveSeaLevelMetric);
	  insertRow(tbdy, "UV Index", uvIndexAverage, uvIndexMedian, cityData.uvIndexMetric);
	  insertRowWithGrayBackground(tbdy, "Single Population (%)", singlePopulationAverage, singlePopulationMedian, cityData.singlePopulationMetric);
	  insertRow(tbdy, "Walk score", walkScoreAverage, walkScoreMedian, cityData.walkScoreMetric);
	  insertRowWithGrayBackground(tbdy, "Transit Score", transitScoreAverage, transitScoreMedian, cityData.transitScoreMetric);
	  insertRow(tbdy, "Bike score", bikeScoreAverage, bikeScoreMedian, cityData.bikeScoreMetric);
	  insertRowWithGrayBackground(tbdy, "% of income spent on housing costs (owners)", percentOfIncomeLostToHousingCostsAverage, percentOfIncomeLostToHousingCostsMedian, cityData.percentOfIncomeLostToHousingCostsMetric);
	  insertRow(tbdy, "Number of sex offenders per 10k residents", sexOffenderCountAverage, sexOffenderCountMedian, cityData.sexOffenderCountMetric);
	  insertRowWithGrayBackground(tbdy, "Number of hurricanes since 1930", hurricanesAverage, hurricanesMedian, cityData.hurricanesMetric);
	  insertRow(tbdy, "Number of tornadoes per year", tornadoesAverage, tornadoesMedian, cityData.tornadoesMetric);
	  insertRowWithGrayBackground(tbdy, "Number of earthquakes since 1931", earthQuakesAverage, earthQuakesMedian, cityData.earthQuakesMetric);
}