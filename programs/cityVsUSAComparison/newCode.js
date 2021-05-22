class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric, decemberHighMetric, augustHighMinusDecemberHighMetric, annualInchesOfRainMetric, daysOfRainMetric, sunnyDaysMetric, annualSnowfallMetric, averageYearlyHumidityMetric) {
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
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  insertRow(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric);
	  insertRow(tbdy, "Average August High(F)", augustHighAverage, augustHighMedian, cityData.augustHighMetric);
	  insertRow(tbdy, "Average December High(F)", decemberHighAverage, decemberHighMedian, cityData.decemberHighMetric);
	  insertRow(tbdy, "August High minus December High", augustHighMinusDecemberHighAverage, augustHighMinusDecemberHighMedian, cityData.augustHighMinusDecemberHighMetric);
	  insertRow(tbdy, "Annual rainfall(in)", annualInchesOfRainAverage, annualInchesOfRainMedian, cityData.annualInchesOfRainMetric);
	  insertRow(tbdy, "Annual days of precipitation", daysOfRainAverage, daysOfRainMedian, cityData.daysOfRainMetric);
	  insertRow(tbdy, "Annual days of sunshine", sunnyDaysAverage, sunnyDaysMedian, cityData.sunnyDaysMetric);
	  insertRow(tbdy, "Annual snowfall(in)", annualSnowfallAverage, annualSnowfallMedian, cityData.annualSnowfallMetric);
	  insertRow(tbdy, "Average yearly humidity (%)", averageYearlyHumidityAverage, averageYearlyHumidityMedian, cityData.averageYearlyHumidityMetric);
		  
}