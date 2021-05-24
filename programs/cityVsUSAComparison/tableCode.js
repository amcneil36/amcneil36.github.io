var arrayOfTableHeaders = ["Metric Name", "Metric value", "USA Avg", "USA Median", "Higher than which % of other cities?", "Higher than which % of other people's city?"];
var BORDER = '1px solid black';


function createColumnHeader(thr, fieldTitle){
    let th = document.createElement('th');
	th.style.border = BORDER;
	th.style.backgroundColor = "#f2f2f2";
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
  createColumn(tr, metric.cityPercentile);
  createColumn(tr, metric.personPercentile);
}

function insertRow(tbdy, metricName, average, median, metric){
	  let tr = document.createElement('tr');
      createRow(tr, metricName, average, median, metric);
	  tbdy.appendChild(tr);
}

function insertRowWithGrayBackground(tbdy, metricName, average, median, metric){
	  let tr = document.createElement('tr');
      createRow(tr, metricName, average, median, metric);
	  tr.style.backgroundColor = "#f2f2f2";
	  tbdy.appendChild(tr);
}