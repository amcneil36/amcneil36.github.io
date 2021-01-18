var augustHigh = "August high (F)";
var decHigh = "December high (F)";
var annualRainfall = "Annual rainfall (in)";
var arrayOfFields = [augustHigh,decHigh, annualRainfall];

function createMap(obj){
  let map = new Map();
  map.set(augustHigh, obj.augHi);
  map.set(decHigh, obj.decHi);
  map.set(annualRainfall, obj.numInchesOfRain);
  return map;
}

function insertColumnBodies(tr, obj){
  insertColumnBody(tr, obj.cityName);
  insertColumnBody(tr, obj.stateName);
  insertColumnBody(tr, obj.augHi);
  insertColumnBody(tr, obj.decHi);
  insertColumnBody(tr, obj.numInchesOfRain);	
}

class Data {
  constructor(cityName, stateName, augHi, decHi, numInchesOfRain) {
    this.cityName = cityName;
	this.stateName = stateName;
    this.augHi = augHi;
	this.decHi = decHi;
	this.numInchesOfRain = numInchesOfRain;
  }
}
var arr = [];
arr.push(new Data("Aberdeen", "Washington", 68, 47, 76));
arr.push(new Data("Aberdeen Gardens", "Washington", 68, 47, 76));
arr.push(new Data("Acme", "Washington", 73, 45, 39));
arr.push(new Data("Addy", "Washington", 86, 32, 21));
arr.push(new Data("Ahtanum", "Washington", 87, 36, 9));
arr.push(new Data("Abbott", "Texas", 97, 59, 38));
arr.push(new Data("Abernathy", "Texas", 90, 53, 21));
arr.push(new Data("Abilene", "Texas", 95, 57, 26));
arr.push(new Data("Abram", "Texas", 99, 72, 21));
arr.push(new Data("Ackerly", "Texas", 93, 57, 19));

