document.getElementById("addForm").addBtn.onclick = main;

function capitalizeFirstLetter(string) {
  const words = string.split(" ");
  for (let i = 0; i < words.length; i++) {
    words[i] = words[i][0].toUpperCase() + words[i].substr(1);
  }
  words.join(" ");
  return words;
}

function main(){
	document.getElementById("numResults").innerHTML = "";
   var elt = document.getElementById("addForm");
   var cityName = elt.cityName.value;
   var stateName = elt.stateName.value;
   var key = cityName.toLowerCase() + "," + stateName.toLowerCase();
   cityName = capitalizeFirstLetter(cityName);
   stateName = capitalizeFirstLetter(stateName);
   var element = document.getElementById('table');
   if (element != null){
    element.parentNode.removeChild(element);	
   }
   if (!myMap.has(key)){
	   document.getElementById("numResults").innerHTML = "<center>City and state combination \"" + cityName + ", " + stateName + "\" was not found.<center>";
	   return;
   }
   document.getElementById("numResults").innerHTML = "<center>Displaying results for " + cityName + ", " + stateName + ".<center>";
  let body = document.getElementsByTagName('body')[0];
  let tbl = document.createElement('table');
  tbl.className = "center";
  tbl.setAttribute("id", 'table');
  let thead = document.createElement('thead');
  let thr = document.createElement('tr');
  for (var i = 0; i < arrayOfTableHeaders.length; i++){
	  createColumnHeader(thr, arrayOfTableHeaders[i], arrayOfHeaderHelpText[i]);
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