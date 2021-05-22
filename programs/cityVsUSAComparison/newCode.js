class CityData {
  constructor(populationMetric, populationDensityMetric, augustHighMetric) {
    this.populationMetric = populationMetric;
	this.populationDensityMetric = populationDensityMetric;
	this.augustHighMetric = augustHighMetric;
  }
}

function createRows(tbdy, cityData){

      insertRow(tbdy, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  insertRow(tbdy, "People per sq mi", populationDensityAverage, populationDensityMedian, cityData.populationDensityMetric);
}