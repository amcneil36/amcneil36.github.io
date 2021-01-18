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

function createRow(tr, obj){
  createColumn(tr, obj.cityName);
  createColumn(tr, obj.stateName);
  createColumn(tr, obj.augHi);
  createColumn(tr, obj.decHi);
  createColumn(tr, obj.numInchesOfRain);	
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
