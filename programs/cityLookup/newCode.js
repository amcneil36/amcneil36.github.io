var augustHigh = "August high (F)";
var decHigh = "December high (F)";
var annualRainfall = "Annual rainfall (in)";
var annualSnowfall = "Annual snowfall (in)";
var numSunnyDays = "Annual number of sunny days";
var numDaysOfRain = "Annual number of days of precipitation";
var population = "Population";
var populationDensity = "People per sq mi";
var medianIncome = "Median income";
var medianHomePrice = "Median home price";
var medianAge = "Median age";
var violentCrime = "Violent Crime index";
var propertyCrime = "Property Crime index";
var airQuality = "Air Quality Index";
var medianHomeAge = "Median home age";
var homeAppreciationLastYear = "Home Appreciation Last Year";
var homeAppreciationLastFiveYears = "Home Appreciation Last 5 Years";
var homeAppreciationLastTenYears = "Home Appreciation Last 10 Years";
var averageCommuteTime = "Average one-way commute time";
var arrayOfFields = [augustHigh,decHigh, annualRainfall, annualSnowfall, numSunnyDays, numDaysOfRain, population, populationDensity, medianIncome, medianHomePrice, medianAge, violentCrime, propertyCrime, airQuality, medianHomeAge, homeAppreciationLastYear, homeAppreciationLastFiveYears, homeAppreciationLastTenYears, averageCommuteTime];

function createMap(obj){
  let map = new Map();
  map.set(augustHigh, obj.augHi);
  map.set(decHigh, obj.decHi);
  map.set(annualRainfall, obj.numInchesOfRain);
  map.set(annualSnowfall, obj.annualSnowfall);
  map.set(numSunnyDays, obj.numSunnyDays);
  map.set(numDaysOfRain, obj.numDaysOfRain);
  map.set(population, obj.population);
  map.set(populationDensity, obj.populationDensity);
  map.set(medianIncome, obj.medianIncome);
  map.set(medianHomePrice, obj.medianHomePrice);
  map.set(medianAge, obj.medianAge);
  map.set(violentCrime, obj.violentCrime);
  map.set(propertyCrime, obj.propertyCrime);
  map.set(airQuality, obj.airQuality);
  map.set(medianHomeAge, obj.medianHomeAge);
  map.set(homeAppreciationLastYear, obj.homeAppreciationLastYear);
  map.set(homeAppreciationLastFiveYears, obj.homeAppreciationLastFiveYears);
  map.set(homeAppreciationLastTenYears, obj.homeAppreciationLastTenYears);
  map.set(averageCommuteTime, obj.averageCommuteTime);
  return map;
 
}

function createRow(tr, obj){
  createColumn(tr, obj.cityName);
  createColumn(tr, obj.stateName);
  createColumn(tr, obj.augHi);
  createColumn(tr, obj.decHi);
  createColumn(tr, obj.numInchesOfRain);
  createColumn(tr, obj.annualSnowfall);  
  createColumn(tr, obj.numSunnyDays);
  createColumn(tr, obj.numDaysOfRain);
  createColumn(tr, numberWithCommas(obj.population));
  createColumn(tr, numberWithCommas(obj.populationDensity));
  createColumn(tr, formatter.format(obj.medianIncome));
  createColumn(tr, formatter.format(obj.medianHomePrice));
  createColumn(tr, obj.medianAge);
  createColumn(tr, obj.violentCrime);
  createColumn(tr, obj.propertyCrime);
  createColumn(tr, obj.airQuality);
  createColumn(tr, obj.medianHomeAge);
  createColumn(tr, obj.homeAppreciationLastYear + '%');
  createColumn(tr, obj.homeAppreciationLastFiveYears + '%');
  createColumn(tr, obj.homeAppreciationLastTenYears + '%');
  createColumn(tr, obj.averageCommuteTime + ' mins');
  

}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

var formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

class Data {
  constructor(cityName, stateName, augHi, decHi, numInchesOfRain, annualSnowfall, numSunnyDays, numDaysOfRain, population, populationDensity, medianIncome, medianHomePrice, medianAge, violentCrime, propertyCrime, airQuality, medianHomeAge, homeAppreciationLastYear, homeAppreciationLastFiveYears, homeAppreciationLastTenYears, averageCommuteTime) {
    this.cityName = cityName;
	this.stateName = stateName;
    this.augHi = augHi;
	this.decHi = decHi;
	this.numInchesOfRain = numInchesOfRain;
	this.annualSnowfall = annualSnowfall;
	this.numSunnyDays = numSunnyDays;
	this.numDaysOfRain = numDaysOfRain;
	this.population = population;
	this.populationDensity = populationDensity;
	this.medianIncome = medianIncome;
	this.medianHomePrice = medianHomePrice;
	this.medianAge = medianAge;
	this.violentCrime = violentCrime;
	this.propertyCrime = propertyCrime;
	this.airQuality = airQuality;
	this.medianHomeAge = medianHomeAge;
	this.homeAppreciationLastYear = homeAppreciationLastYear;
	this.homeAppreciationLastFiveYears = homeAppreciationLastFiveYears;
	this.homeAppreciationLastTenYears = homeAppreciationLastTenYears;
	this.averageCommuteTime = averageCommuteTime;
  }
}
