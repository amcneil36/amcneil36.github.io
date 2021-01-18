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

function main(){
  let body = document.getElementsByTagName('body')[0];
  var element = document.getElementById('table');
  if (element != null){
    element.parentNode.removeChild(element);	
  }
  let tbl = document.createElement('table');
      tbl.className = "sortable";
  tbl.setAttribute("id", 'table');
  let thead = document.createElement('thead');
  let thr = document.createElement('tr');
  createColumnHeader(thr, 'City');
  createColumnHeader(thr, 'State');
      for (var i = 0; i < arrayOfFields.length; i++){
	  createColumnHeader(thr, arrayOfFields[i]);
  }
  thead.appendChild(thr);
  tbl.appendChild(thead);

  let tbdy = document.createElement('tbody');
  let tr = document.createElement('tr');
  createRows(tbdy);
  tbl.appendChild(tbdy);
  body.appendChild(tbl);
  sorttable.makeSortable(document.getElementById('table'));
}

function createColumnHeader(thr, fieldTitle){
    let th = document.createElement('th');
	th.style.border = '1px solid black';
    th.appendChild(document.createTextNode(fieldTitle));
    thr.appendChild(th);	
}

function createColumn(tr, val){
	      var td = document.createElement('td');
      td.appendChild(document.createTextNode(val));
	  td.style.border = '1px solid black';
      tr.appendChild(td);	
}

function createRows(tbdy){
    arr.forEach((obj) => {
		if (doesObjQualify(obj)){
	  let tr = document.createElement('tr');
      createRow(tr, obj);
	  tbdy.appendChild(tr);   	
		}
    });
    
}

