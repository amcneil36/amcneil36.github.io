class CityData {
  constructor(populationMetric, populationDensityMetric) {
    this.populationMetric = populationMetric;
	this.populationDensityMetric = populationDensityMetric;
  }
}
	

class Metric {
  constructor(value, cityPercentile, personPercentile) {
    this.value = value;
	this.cityPercentile = cityPercentile;
	this.personPercentile = personPercentile;
  }
}