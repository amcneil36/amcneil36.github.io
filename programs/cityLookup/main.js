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
  var selectList = document.createElement("select");
  selectList.setAttribute('id', 'fieldName' + idx);
  myDiv.appendChild(selectList);

  for (var i = 0; i < arrayOfFields.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", arrayOfFields[i]);
    option.text = arrayOfFields[i];
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
	let map = createMap(obj);
    fieldName = map.get(fieldId);
	if (fieldName == undefined){
	 alert("something went wrong");	
	 	 throw new Error("fill in all input boxes");
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
  for (var i = 0; i < arrayOfFields.length; i++){
    insertColumnHeader(tr, arrayOfFields[i]);
  }
				
  for (var i = 0; i < arr.length; i++){
    if (doesObjQualify(arr[i])){
      tr = tbl.insertRow();
      insertColumnBodies(tr, arr[i]);		
	}
  }
  body.appendChild(tbl);
}