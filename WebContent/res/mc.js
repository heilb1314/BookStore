/**
 * 
 */

function setSelectedBookCategory(c) {
	
}

function getFormData(){
	var data = {
			namePrefix:document.getElementById("namePrefix").value,
			creditTaken:document.getElementById("creditTaken").value,
			submit:document.getElementById("submit").value,
	};
	return data;
}

function validate() {
	var data = getFormData();
	console.log(data);
	if (data.namePrefix==null || data.namePrefix.trim().length==0) {
		alert("Name Prefix invalid!");
		document.getElementById("namePrefixErr").style.display = "inline";
		return false;
	} else if (data.namePrefix.trim().match("^[a-zA-Z]+$") == null) {
		alert("Name Prefix must be all letters!");
		document.getElementById("namePrefixErr").style.display = "inline";
		return false;
	}
	document.getElementById("namePrefixErr").style.display = "none";
	
	if (isNaN(data.creditTaken) || data.creditTaken <= 0 || data.creditTaken % 1 != 0) {
		alert("Minimum Credit Taken invalid. Must be positive integer!");
		document.getElementById("creditTakenErr").style.display = "inline";
		return false;
	}
	document.getElementById("creditTakenErr").style.display = "none";
	return true;
}

function doStudentQuery(url){
	if(validate()){
		var data = getFormData();
		var formData = new FormData();
		Object.keys(data).forEach(function(i){
			formData.append(i,data[i]);
		});
		var request = null;
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
		request.open('POST', url, true);
		request.onreadystatechange = function() {
			if(request.readyState == 4 && request.status == 200) {
				var res = JSON.parse(request.responseText);
				if(res.code===1){
					var result = res.result;
					console.log(result);
					var resultTable = document.getElementById("resultBody");
					// remove previous results
					while (resultTable.firstChild) {
						resultTable.removeChild(resultTable.firstChild);
					}
					result.forEach(function(item){
						var row = document.createElement("tr");
						for (var key in item) {
						    // skip loop if the property is from prototype
						    if (!item.hasOwnProperty(key)) continue;
						    var p = document.createElement("p");
						    p.textContent = item[key];
						    var col = document.createElement("td");
						    col.appendChild(p);
						    row.appendChild(col);
						}
						resultTable.appendChild(row);
					});
					document.getElementById("resultTable").style.display = "inline";					
				}else{
					alert(res.error);
				}
			}
			
		};
		request.send(formData);
	}
}
