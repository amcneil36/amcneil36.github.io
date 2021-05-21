document.getElementById("addForm").addBtn.onclick = main;

var arrayOfTableHeaders = ["Metric Name", "Metric value", "USA Avg", "USA Median", "Higher than which % of cities?", "Higher than which % of people's city?"];
var BORDER = '1px solid black';
var populationAverage = 23244;
var populationMedian = 432;

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

let myMap = new Map();
var populationMetric;
var cityData;
populationMetric = new Metric(800000, 54, 65);
cityData = new CityData(populationMetric);
myMap.set("seattle,washington", cityData);
populationMetric = new Metric(1400000, 99, 97);
cityData = new CityData(populationMetric);
myMap.set("san diego,california", cityData);

function tableCreate(){
    var body = document.body,
        tbl  = document.createElement('table');
    tbl.style.width  = '100px';
    tbl.style.border = '1px solid black';

    for(var i = 0; i < 3; i++){
        var tr = tbl.insertRow();
        for(var j = 0; j < 2; j++){
            if(i == 2 && j == 1){
                break;
            } else {
                var td = tr.insertCell();
                td.appendChild(document.createTextNode('Cell'));
                td.style.border = '1px solid black';
                if(i == 1 && j == 1){
                    td.setAttribute('rowSpan', '2');
                }
            }
        }
    }
    body.appendChild(tbl);
}

function createColumnHeader(thr, fieldTitle){
    let th = document.createElement('th');
	th.style.border = BORDER;
    th.appendChild(document.createTextNode(fieldTitle));
    thr.appendChild(th);	
}

function createColumn(tr, val){
	      var td = document.createElement('td');
      td.appendChild(document.createTextNode(val));
	  td.style.border = BORDER;
      tr.appendChild(td);	
}

function createRow(tr, metricName, average, median, metric){
  createColumn(tr, metricName);
  createColumn(tr, metric.value);
  createColumn(tr, average);
  createColumn(tr, median);
  createColumn(tr, metric.cityPercentile + "%");
  createColumn(tr, metric.personPercentile + "%");
}

function createRows(tbdy, cityData){

	  let tr = document.createElement('tr');
      createRow(tr, "Population", populationAverage, populationMedian, cityData.populationMetric);
	  tbdy.appendChild(tr);
}

function main(){
   var elt = document.getElementById("addForm");
   var cityName = elt.cityName.value;
   var stateName = elt.stateName.value;
   var key = cityName.toLowerCase() + "," + stateName.toLowerCase();
   var element = document.getElementById('table');
   if (element != null){
    element.parentNode.removeChild(element);	
   }
   if (!myMap.has(key)){
	   alert ("City and State combination not found.");
	   return;
   }
  let body = document.getElementsByTagName('body')[0];
  let tbl = document.createElement('table');
  tbl.className = "table-container";
  tbl.setAttribute("id", 'table');
  let thead = document.createElement('thead');
  let thr = document.createElement('tr');
  for (var i = 0; i < arrayOfTableHeaders.length; i++){
	  createColumnHeader(thr, arrayOfTableHeaders[i]);
  }
  thead.appendChild(thr);
  tbl.appendChild(thead);

  let tbdy = document.createElement('tbody');
  let tr = document.createElement('tr');
  var cityData = myMap.get(key);
  createRows(tbdy, cityData);
  tbl.appendChild(tbdy);
  body.appendChild(tbl);
}