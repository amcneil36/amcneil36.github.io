var arrayOfTableHeaders = ["Metric Name", "Metric value", "USA Avg", "USA Median", "Higher than which % of other cities?", "Higher than which % of other people's city?"];
var metricNameHelpText = "metric name!";
var metricValueHelpText = "metric value!";
var usaAvgHelpText = "usa avg!";
var usaMedianHelpText = "usa median!";
var cityPercentileHelpText = "city percentile";
var percentPercentileHelpText = "person persentile";
var arrayOfHeaderHelpText = [metricNameHelpText, metricValueHelpText, usaAvgHelpText, usaMedianHelpText, cityPercentileHelpText, percentPercentileHelpText];
var BORDER = '1px solid black';


function createColumnHeader(thr, fieldTitle, text){
    let th = document.createElement('th');
	th.style.border = BORDER;
	th.style.backgroundColor = "#f2f2f2";
	th.innerHTML = fieldTitle + "<div class='tooltip'>&#x1F6C8;<span class='tooltiptext'>" + text + "</span></div>";
    thr.appendChild(th);	
}

function createColumn(tr, val){
	  var td = document.createElement('td');
      td.appendChild(document.createTextNode(val));
	  td.style.border = BORDER;
      tr.appendChild(td);	
}

function createColumnOne(tr, val, text){
	  var td = document.createElement('td');
	  td.style.border = BORDER;
	  td.innerHTML = val + " <div class='tooltip'>&#x1F6C8;<span class='tooltiptext'>" + text + "</span></div>";
      tr.appendChild(td);	
}

function createRow(tr, metricName, average, median, metric, text){
  createColumnOne(tr, metricName, text);
  createColumn(tr, metric.value);
  createColumn(tr, average);
  createColumn(tr, median);
  createColumn(tr, metric.cityPercentile);
  createColumn(tr, metric.personPercentile);
}

function insertRow(tbdy, metricName, average, median, metric, text){
	  let tr = document.createElement('tr');
      createRow(tr, metricName, average, median, metric, text);
	  tbdy.appendChild(tr);
}

function insertRowWithGrayBackground(tbdy, metricName, average, median, metric, text){
	  let tr = document.createElement('tr');
      createRow(tr, metricName, average, median, metric, text);
	  tr.style.backgroundColor = "#f2f2f2";
	  tbdy.appendChild(tr);
}