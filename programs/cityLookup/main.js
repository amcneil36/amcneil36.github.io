//alert (arr[0].siteName); // alerts `#fff`

var idx = 0;
addRow();
function addRow(){

createFirstElement();
createSecondElement();
createThirdElement();
var myDiv = document.getElementById("extraRows");
if (idx != 0){
  var button = document.createElement('button');
  button.innerHTML = 'Remove row';
  button.setAttribute('id', 'button' + idx);
  var tempIdx = idx;
  button.onclick = function(){
    removeElement(tempIdx);
  };
  // where do we want to have the button to appear?
  // you can append it to another element just by doing something like
  // document.getElementById('foobutton').appendChild(button);
  myDiv.appendChild(button);	
}
var f = document.createElement('br');
f.setAttribute("id", 'br'+idx);
myDiv.appendChild(f);
idx++;
}

function createThirdElement(){
	var input = document.createElement("input");
input.setAttribute('type', 'text');
input.setAttribute('size', '1');
input.setAttribute('id', 'value' + idx);
  var myDiv = document.getElementById("extraRows");
    myDiv.appendChild(input);
}

function createSecondElement(){
  var myDiv = document.getElementById("extraRows");
  var array = [">", ">=", "<", "<=", "="];
  var selectList = document.createElement("select");
  selectList.setAttribute("id", "symbols" + idx);
  myDiv.appendChild(selectList);

  for (var i = 0; i < array.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", array[i]);
    option.text = array[i];
    selectList.appendChild(option);
  }	
}

function createFirstElement(){
  var myDiv = document.getElementById("extraRows");
  var array = ["August high (F)","December high (F)", "Annual rainfall (in)"];
  var selectList = document.createElement("select");
  selectList.setAttribute('id', 'fieldName' + idx);
  myDiv.appendChild(selectList);

  for (var i = 0; i < array.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", array[i]);
    option.text = array[i];
    selectList.appendChild(option);
  }	
}

function doesObjQualify(obj){
	var state = document.getElementById("State").value;
	if (state != "any"){
	 if (obj.stateName != state){
       return false;
     }	   
	}
	for (var i = 0; i < idx; i++){
		var fieldId2 = document.getElementById("fieldName" + i);
		// element may not exist because it may have been deleted by that remove row button
        if (fieldId2 == null){
		 continue;	
		}
		var fieldId = fieldId2.value;
	var fieldName = null;
	
    var value = parseFloat(document.getElementById("value" + i).value);
	if (isNaN(value)){
	 alert("fill in all input boxes");
	 throw new Error("fill in all input boxes");
     return;	 
	}
	var symbol = document.getElementById("symbols" + i).value;
	if (fieldId == "August high (F)"){
	    fieldName = obj.augHi;
	}
	else if (fieldId == "December high (F)"){
	    fieldName = obj.decHi;
	}
	else if (fieldId == "Annual rainfall (in)"){
	    fieldName = obj.numInchesOfRain;	
	}
	else{
		alert("something went wrong");
		return false;
	}
	var flag = false;
	if (symbol == ">"){
		flag = fieldName > value;
	}
	else if (symbol == ">="){
	  flag = fieldName >= value;	
	}
	else if (symbol == "<"){
	  flag = fieldName < value;	
	}
	else if (symbol == "<="){
	  flag = fieldName <= value;	
	}
	else if (symbol == "="){
	  flag = fieldName == value;	
	}
    if (flag == false){
     return false;		
	}
    }
	return true;
}


function removeElement(index){
document.getElementById("value"+index).remove();
document.getElementById("fieldName"+index).remove();
document.getElementById("symbols"+index).remove();	
document.getElementById("button"+index).remove();	
document.getElementById("br"+index).remove();	
}

function insertColumnHeader(tr, name){
                var td = tr.insertCell();
				var bold = document.createElement('strong');
                bold.appendChild(document.createTextNode(name));
				td.appendChild(bold);
                td.style.border = '1px solid black';	
				td.style.whiteSpace = 'nowrap';
}

function insertColumnBody(tr, val){
                var td = tr.insertCell();
                td.appendChild(document.createTextNode(val));
                td.style.border = '1px solid black';	
}

function main(){

	    var element = document.getElementById('table');
		if (element != null){
		    element.parentNode.removeChild(element);	
		}
    var body = document.body,
        tbl  = document.createElement('table');
    tbl.style.width  = '100px';
    tbl.style.border = '1px solid black';
	    tbl.setAttribute("id", 'table');

        var tr = tbl.insertRow();

        insertColumnHeader(tr, 'City');
        insertColumnHeader(tr, 'State');
        insertColumnHeader(tr, 'August high (F)');
        insertColumnHeader(tr, 'December high (F)');
        insertColumnHeader(tr, 'Annual rainfall (in)');
				
	for (var i = 0; i < arr.length; i++){
      if (doesObjQualify(arr[i])){
	    tr = tbl.insertRow();
		insertColumnBody(tr, arr[i].cityName);
		insertColumnBody(tr, arr[i].stateName);
		insertColumnBody(tr, arr[i].augHi);
		insertColumnBody(tr, arr[i].decHi);
		insertColumnBody(tr, arr[i].numInchesOfRain);				
	  }
	}
    body.appendChild(tbl);
}