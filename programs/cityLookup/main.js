//alert (arr[0].siteName); // alerts `#fff`

var idx = 0;
addRow();
function addRow(){

createFirstElement();
createSecondElement();
createThirdElement();
var myDiv = document.getElementById("extraRows");
var f = document.createElement('br');
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
  var array = ["August high","December high"];
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
	for (var i = 0; i < idx; i++){
		var fieldId = document.getElementById("fieldName" + i).value;
	var fieldName = null;
	
    var value = parseFloat(document.getElementById("value" + i).value);
	var symbol = document.getElementById("symbols" + i).value;
	if (fieldId == "August high"){
	    fieldName = obj.augHi;
	}
	else{
	    fieldName = obj.decHi;
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

                var td = tr.insertCell();
				var bold = document.createElement('strong');
                bold.appendChild(document.createTextNode('City'));
				td.appendChild(bold);
                td.style.border = '1px solid black';
				
		        var td = tr.insertCell();
				bold = document.createElement('strong');
                bold.appendChild(document.createTextNode('August high'));
                td.appendChild(bold);
				td.style.border = '1px solid black';
				td.style.whiteSpace = 'nowrap';
				
								                var td = tr.insertCell();
				bold = document.createElement('strong');
                bold.appendChild(document.createTextNode('December high'));
                td.appendChild(bold);
				td.style.border = '1px solid black';
	td.style.whiteSpace = 'nowrap';
	for (var i = 0; i < arr.length; i++){
      if (doesObjQualify(arr[i])){
		          tr = tbl.insertRow();
                var td = tr.insertCell();
                td.appendChild(document.createTextNode(arr[i].siteName));
                td.style.border = '1px solid black';
				
				                var td = tr.insertCell();
                td.appendChild(document.createTextNode(arr[i].augHi));
                td.style.border = '1px solid black';
				
								                var td = tr.insertCell();
                td.appendChild(document.createTextNode(arr[i].decHi));
                td.style.border = '1px solid black';
	  }
	}
    body.appendChild(tbl);
}