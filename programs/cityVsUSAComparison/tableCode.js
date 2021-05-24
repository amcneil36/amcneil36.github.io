var arrayOfTableHeaders = ["Metric Name!!", "Metric value", "USA Avg", "USA Median", "Higher than which % of other cities?", "Higher than which % of other people's city?"];
var metricNameHelpText = "The name of the metric that the row's data is going to correspond to";
var metricValueHelpText = "The value of the metric for the city that was entered";
var usaAvgHelpText = "The average calculated from all USA cities";
var usaMedianHelpText = "The median calculated from all USA cities";
var cityPercentileHelpText = "The % of USA cities that your city's metric value is higher than";
var percentPercentileHelpText = "The % of Americans that are living in a city that your city's metric value is higher than";
var arrayOfHeaderHelpText = [metricNameHelpText, metricValueHelpText, usaAvgHelpText, usaMedianHelpText, cityPercentileHelpText, percentPercentileHelpText];
var BORDER = '1px solid black';


function createColumnHeader(thr, fieldTitle, text){
    let th = document.createElement('th');
	th.style.border = BORDER;
	th.style.backgroundColor = "#f2f2f2";
	th.innerHTML = fieldTitle + "<div class='tooltip'>&#x1f6c8;<span class='tooltiptext'>" + text + "</span></div>";
    thr.appendChild(th);	
}

function createColumn(tr, val){
	  var td = document.createElement('td');
      td.appendChild(document.createTextNode(numberWithCommas(val)));
	  td.style.border = BORDER;
      tr.appendChild(td);	
}

function createColumnWithInfoButton(tr, val, text){
	  var td = document.createElement('td');
	  td.style.border = BORDER;
	  td.innerHTML = val + " <div class='tooltip'>heyyyyyy;<span class='tooltiptext'>" + text + "</span></div>";
      tr.appendChild(td);	
}

function createRowWithInfoButton(tr, metricName, average, median, metric, text){
  createColumnWithInfoButton(tr, metricName, text);
  createColumn(tr, metric.value);
  createColumn(tr, average);
  createColumn(tr, median);
  createColumn(tr, metric.cityPercentile);
  createColumn(tr, metric.personPercentile);
}

function createRow(tr, metricName, average, median, metric){
  createColumn(tr, metricName);
  createColumn(tr, metric.value);
  createColumn(tr, average);
  createColumn(tr, median);
  createColumn(tr, metric.cityPercentile);
  createColumn(tr, metric.personPercentile);
}

function insertRowWithInfoButton(tbdy, metricName, average, median, metric, text){
	  let tr = document.createElement('tr');
      createRowWithInfoButton(tr, metricName, average, median, metric, text);
	  tbdy.appendChild(tr);
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

function insertRowWithGrayBackgroundWithInfoButton(tbdy, metricName, average, median, metric, text){
	  let tr = document.createElement('tr');
      createRowWithInfoButton(tr, metricName, average, median, metric, text);
	  tr.style.backgroundColor = "#f2f2f2";
	  tbdy.appendChild(tr);
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}