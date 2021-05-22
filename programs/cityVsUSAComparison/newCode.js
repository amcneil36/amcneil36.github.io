class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric, decemberHighMetric, augustHighMinusDecemberHighMetric) {
    this.populationMetric = populationMetric;
	this.populationDensityMetric = populationDensityMetric;
	this.augustHighMetric = augustHighMetric;
	this.decemberHighMetric = decemberHighMetric;
	this.augustHighMinusDecemberHighMetric = augustHighMinusDecemberHighMetric;
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  insertRow(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric);
	  insertRow(tbdy, "Average August High(F)", augustHighAverage, augustHighMedian, cityData.augustHighMetric);
	  insertRow(tbdy, "Average December High(F)", decemberHighAverage, decemberHighMedian, cityData.decemberHighMetric);
	  insertRow(tbdy, "August High minus December High", augustHighMinusDecemberHighAverage, augustHighMinusDecemberHighMedian, cityData.augustHighMinusDecemberHighMetric);
}