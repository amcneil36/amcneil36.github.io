class CityData {
  constructor(populationMetric) {
    this.populationMetric = populationMetric;
  }
}
	

class Metric {
  constructor(value, cityPercentile, personPercentile) {
    this.value = value;
	this.cityPercentile = cityPercentile;
	this.personPercentile = personPercentile;
  }
}