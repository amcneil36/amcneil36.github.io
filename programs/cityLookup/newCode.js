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
