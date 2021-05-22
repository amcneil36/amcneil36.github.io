var arrayOfTableHeaders = ["Metric Name", "Metric value", "USA Avg", "USA Median", "Higher than which % of other cities?", "Higher than which % of other people's city?"];
var BORDER = '1px solid black';

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
  createColumn(tr, metric.cityPercentile);
  createColumn(tr, metric.personPercentile);
}

function insertRow(tbdy, metricName, average, median, metric){
	  let tr = document.createElement('tr');
      createRow(tr, metricName, average, median, metric);
	  tbdy.appendChild(tr);
}