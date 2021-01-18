//alert (arr[0].siteName); // alerts `#fff`

function main(){
	var outputString = "";
	var fieldId = document.getElementById("fieldName").value;
	var fieldName = null;
	
		var value = parseFloat(document.getElementById("value").value);
	
	var symbol = document.getElementById("symbols").value;
	
	for (var i = 0; i < arr.length; i++){
		 	if (fieldId == "augustHi"){
	   fieldName = arr[i].augHi;
	}
	else{
	   fieldName = arr[i].decHi;
	}
	
	var flag = false;
	if (symbol == "greaterThan"){
		flag = fieldName > value;
	}
	else if (symbol == "greaterThanOrEqualTo"){
	  flag = fieldName >= value;	
	}
	else if (symbol == "lessThan"){
	  flag = fieldName < value;	
	}
	else if (symbol == "lessThanOrEqualTo"){
	  flag = fieldName <= value;	
	}
	else if (symbol == "equal"){
	  flag = fieldName == value;	
	}
    if (flag){
		outputString += arr[i].siteName + "<br>";
	}
	}
	document.getElementById("output").innerHTML  = outputString;
	//alert(document.getElementById("fieldName").value);
}