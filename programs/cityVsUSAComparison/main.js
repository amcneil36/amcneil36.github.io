document.getElementById("addForm").addBtn.onclick = main;

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