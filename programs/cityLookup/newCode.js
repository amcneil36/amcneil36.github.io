var city = "City";
var state = "State";
var hottestMonthsHigh = "Hottest month's avg high (F)";
var coldestMonthsHigh = "Coldest month's avg high (F)";
var annualRainfall = "Annual rainfall (in)";
var annualSnowfall = "Annual snowfall (in)";
var numSunnyDays = "Annual days of sunshine";
var numDaysOfRain = "Annual days of precipitation";
var population = "Population";
var populationDensity = "People per sq mi";
var medianIncome = "Median household income";
var medianHomePrice = "Median home price";
var medianAge = "Median age";
var violentCrime = "Violent crime index";
var propertyCrime = "Property crime index";
var airQuality = "Air quality Index";
var medianHomeAge = "Median home age";
var homeAppreciationLastYear = "Home appreciation Last Year";
var homeAppreciationLastFiveYears = "Home appreciation Last 5 Years";
var homeAppreciationLastTenYears = "Home appreciation Last 10 Years";
var averageCommuteTime = "Average one-way commute time";
var countyName = "County";
var hottestMonthMinusColdestMonth = "Hottest high minus coldest high";
var avgYearlyWindspeed = "Average yearly windspeed (mph)";
var avgAugustHumidity = "Average August humidity";
var avgDecemberHumidity = "Average December humidity";
var avgHumidity = "Average yearly humidity";
var metro = "Metro";
var homeSquareFeet = "Median home square feet";
var costPerSquareFoot = "Median home cost per sq ft";

var percentDemocrat = "% Democrat";
var percentRepublican = "% Republican";
var unemploymentRate = "Unemployment rate";
var jobGrowthLastYear = "Job growth last year";
var populationGrowthSince2010 = "Population growth since 2010";
var percentAsian = "% Asian";
var percentBlack = "% Black";
var percentWhite = "% White";
var percentHispanic = "% Hispanic";
var medianRent = "Median rent";
var percentWithAtleastBachelors = "% with at least Bachelor's degree";
var timeZone = "Timezone";
var feetAboveSeaLevel = "Elevation (ft)";
var uvIndex = "UV Index";
var arrayOfFields = [city, state,countyName,population,populationDensity,hottestMonthsHigh,coldestMonthsHigh,hottestMonthMinusColdestMonth,annualRainfall,numDaysOfRain,numSunnyDays,annualSnowfall,avgAugustHumidity, avgDecemberHumidity, avgHumidity, avgYearlyWindspeed,violentCrime,propertyCrime,medianAge,percentWithAtleastBachelors,medianIncome,medianRent,medianHomePrice,homeSquareFeet,costPerSquareFoot,medianHomeAge,homeAppreciationLastYear,homeAppreciationLastFiveYears,homeAppreciationLastTenYears,airQuality,
averageCommuteTime, unemploymentRate, jobGrowthLastYear, populationGrowthSince2010, percentDemocrat, percentRepublican, percentAsian, percentBlack, percentWhite,percentHispanic, metro, timeZone, feetAboveSeaLevel, uvIndex];
function createMap(obj){
  let map = new Map();
  map.set(city, obj.cityName);
  map.set(state, obj.stateName);
  map.set(countyName, obj.countyName);
  map.set(population, obj.population);
  map.set(populationDensity, obj.populationDensity);
  map.set(hottestMonthsHigh, obj.hottestMonthsHigh);
  map.set(coldestMonthsHigh, obj.coldestHigh);
  map.set(hottestMonthMinusColdestMonth, obj.hottestMonthMinusColdestMonth);
  map.set(annualRainfall, obj.numInchesOfRain);
  map.set(numDaysOfRain, obj.numDaysOfRain);
  map.set(numSunnyDays, obj.numSunnyDays);
  map.set(annualSnowfall, obj.annualSnowfall);
  map.set(violentCrime, obj.violentCrime);
  map.set(propertyCrime, obj.propertyCrime);
  map.set(medianAge, obj.medianAge);
  map.set(medianIncome, obj.medianIncome);
  map.set(medianHomePrice, obj.medianHomePrice);
  map.set(homeSquareFeet, obj.homeSquareFeet);
  map.set(costPerSquareFoot, obj.costPerSquareFoot);
  map.set(medianHomeAge, obj.medianHomeAge);
  map.set(homeAppreciationLastYear, obj.homeAppreciationLastYear);
  map.set(homeAppreciationLastFiveYears, obj.homeAppreciationLastFiveYears);
  map.set(homeAppreciationLastTenYears, obj.homeAppreciationLastTenYears);
  map.set(airQuality, obj.airQuality);
  map.set(averageCommuteTime, obj.averageCommuteTime);
  map.set(avgYearlyWindspeed, obj.avgYearlyWindspeed);
  map.set(avgAugustHumidity, obj.avgAugustHumidity);
  map.set(avgDecemberHumidity, obj.avgDecemberHumidity);
  map.set(avgHumidity, obj.avgHumidity);
  map.set(medianRent, obj.medianRent);
  map.set(percentDemocrat, obj.percentDemocrat);
  map.set(percentRepublican, obj.percentRepublican);
  map.set(unemploymentRate, obj.unemploymentRate);
  map.set(jobGrowthLastYear, obj.jobGrowthLastYear);
  map.set(populationGrowthSince2010,obj.populationGrowthSince2010);
  map.set(percentAsian,obj.percentAsian);
  map.set(percentBlack,obj.percentBlack);
  map.set(percentWhite,obj.percentWhite);
  map.set(percentHispanic,obj.percentHispanic);
  map.set(percentWithAtleastBachelors,obj.percentWithAtleastBachelors);
  map.set(metro,obj.metro);
  map.set(timeZone, obj.timeZone);
  map.set(feetAboveSeaLevel, obj.feetAboveSeaLevel);
  map.set(uvIndex, obj.uvIndex);
  return map; 
}

function createRow(tr, obj){
  createColumn(tr, obj.cityName);
  createColumn(tr, obj.stateName);
  createColumn(tr, obj.countyName);
  createColumn(tr, numberWithCommas(obj.population));
  createColumn(tr, numberWithCommas(obj.populationDensity));
  createColumn(tr, obj.hottestMonthsHigh);
  createColumn(tr, obj.coldestHigh);
  createColumn(tr, obj.hottestMonthMinusColdestMonth);
  createColumn(tr, obj.numInchesOfRain);
  createColumn(tr, obj.numDaysOfRain);
  createColumn(tr, obj.numSunnyDays);
  createColumn(tr, obj.annualSnowfall);
  createColumn(tr, formatPercent(obj.avgAugustHumidity));
  createColumn(tr, formatPercent(obj.avgDecemberHumidity));
  createColumn(tr, formatPercent(obj.avgHumidity));
  createColumn(tr, obj.avgYearlyWindspeed);  
  createColumn(tr, obj.violentCrime);
  createColumn(tr, obj.propertyCrime);
  createColumn(tr, obj.medianAge);
  createColumn(tr, formatPercent(obj.percentWithAtleastBachelors));
  createColumn(tr, formatCurrency(obj.medianIncome));
  createColumn(tr, formatCurrency(obj.medianRent));
  createColumn(tr, formatCurrency(obj.medianHomePrice));
  createColumn(tr, obj.homeSquareFeet);
  createColumn(tr, formatCurrency(obj.costPerSquareFoot));
  createColumn(tr, obj.medianHomeAge);
  createColumn(tr, obj.homeAppreciationLastYear + '%');
  createColumn(tr, obj.homeAppreciationLastFiveYears + '%');
  createColumn(tr, obj.homeAppreciationLastTenYears + '%');
  createColumn(tr, obj.airQuality);
  createColumn(tr, obj.averageCommuteTime + ' mins');
  createColumn(tr, formatPercent(obj.unemploymentRate));
  createColumn(tr, formatPercent(obj.jobGrowthLastYear));
  createColumn(tr, formatPercent(obj.populationGrowthSince2010));
  createColumn(tr, formatPercent(obj.percentDemocrat));
  createColumn(tr, formatPercent(obj.percentRepublican));
  createColumn(tr, formatPercent(obj.percentAsian));
  createColumn(tr, formatPercent(obj.percentBlack));
  createColumn(tr, formatPercent(obj.percentWhite));
  createColumn(tr, formatPercent(obj.percentHispanic));
  createColumn(tr, obj.metro);
  createColumn(tr, obj.timeZone);
  createColumn(tr, obj.feetAboveSeaLevel);
  createColumn(tr, obj.uvIndex);
}

function formatCurrency(inp){
 if (inp == "N/A" || isNaN(inp)){
  return "N/A";
 }
return formatter.format(inp); 
}

function formatPercent(val){
 if (val == "N/A"){
  return val;
 }
  return val + "%";	
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

var formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

class Data {
  constructor(cityName, stateName, hottestMonthsHigh, coldestHigh, numInchesOfRain, annualSnowfall, numSunnyDays, numDaysOfRain, population, populationDensity, medianIncome, medianHomePrice, medianAge, violentCrime, propertyCrime, airQuality, medianHomeAge, homeAppreciationLastYear, homeAppreciationLastFiveYears, homeAppreciationLastTenYears, averageCommuteTime, countyName, hottestMonthMinusColdestMonth, avgYearlyWindspeed, avgAugustHumidity, avgDecemberHumidity, avgHumidity, percentDemocrat, percentRepublican, unemploymentRate, jobGrowthLastYear, populationGrowthSince2010, percentAsian, percentBlack, percentWhite, percentHispanic, medianRent, percentWithAtleastBachelors, metro, 
  homeSquareFeet, costPerSquareFoot, timeZone, feetAboveSeaLevel, uvIndex) {
    this.cityName = cityName;
	this.stateName = stateName;
    this.hottestMonthsHigh = hottestMonthsHigh;
	this.coldestHigh = coldestHigh;
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
	this.countyName = countyName;
	this.hottestMonthMinusColdestMonth = hottestMonthMinusColdestMonth;
	this.avgYearlyWindspeed = avgYearlyWindspeed;
	this.avgAugustHumidity = avgAugustHumidity;
	this.avgDecemberHumidity = avgDecemberHumidity;
	this.avgHumidity = avgHumidity;
	this.percentDemocrat = percentDemocrat;
	this.percentRepublican = percentRepublican;
	this.unemploymentRate = unemploymentRate;
	this.jobGrowthLastYear = jobGrowthLastYear;
	this.populationGrowthSince2010 = populationGrowthSince2010;
	this.percentAsian = percentAsian;
	this.percentBlack = percentBlack;
	this.percentWhite = percentWhite;
	this.percentHispanic = percentHispanic;
	this.medianRent = medianRent;
	this.percentWithAtleastBachelors = percentWithAtleastBachelors;
	this.metro = metro;
	this.homeSquareFeet = homeSquareFeet;
	this.costPerSquareFoot = costPerSquareFoot;
	this.feetAboveSeaLevel = feetAboveSeaLevel;
	this.timeZone = timeZone;
	this.uvIndex = uvIndex;
  }
}
