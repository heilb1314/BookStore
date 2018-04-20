function searchBook(form, path) {
	var text = form.elements.search.value;
	var searchPath = path + "?search=" + text;
	window.location.href = searchPath;
}

function openModal(bid, title, category, price, description) {
	el = document.getElementById("book-detail-div");
	el.style.visibility = "visible";
	document.getElementById("book-detail-bid").value = bid;
	document.getElementById("book-detail-title").innerText = title;
	document.getElementById("book-detail-category").innerText = category;
	document.getElementById("book-detail-price").innerText = "$"+parseFloat(price).toFixed(2);
	document.getElementById("book-detail-description").innerText = description;
	fetchReviews(bid);
}

function closeModal() {
	el = document.getElementById("book-detail-div");
	el.style.visibility = "hidden";
	document.getElementById("book-detail-review-table").style.display = "none";
	document.getElementById("book-detail-scrollable-div").scrollTop = 0;
}

function fetchAjaxRequest(method, url, formData, cb) {
	var request = null;
	if (window.XMLHttpRequest) {
		request = new XMLHttpRequest();
	} else {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	request.open(method, url, true);
	request.onreadystatechange = function() {
		if(request.readyState == 4 && request.status == 200) {
			console.log(request);
			var res = JSON.parse(request.responseText);
			cb(res);
		}
	};
	request.send(formData);
}

function fetchReviews(bid) {
	var formData = new FormData();
	formData.append("bid",bid);
	fetchAjaxRequest("POST", "/bookStore/Start/Ajax/Review", formData, function(res){
		if(res.code===1){
			var result = res.result;
			var resultTable = document.getElementById("book-detail-review-tbody");
			// remove previous results
			while (resultTable.firstChild) {
				resultTable.removeChild(resultTable.firstChild);
			}
			result.forEach(function(item) {
				var row = constructRowOfBookReviews(item);
				resultTable.appendChild(row);
			});
			document.getElementById("book-detail-review-table").style.display = "inline";
		} else {
			alert(res.error);
		}
	});
}

function validateShoppingCartItem(form) {
	var bid = form.elements.bid.value;
	var quantity = form.elements.quantity.value;
	quantity = parseInt(quantity);
	if (bid==null || bid=="") {
		alert("Invalid book id.");
		return false;
	}
	else if (isNaN(quantity) || quantity == null || quantity <= 0) {
		alert("Invalid quantity.");
		return false;
	}
	return true;
}

function billingCheckboxDidChange(checkbox) {
	if(checkbox.checked) {
		document.getElementById("billingDiv").style.display = "none";
//		checkbox.value = "on";
	} else {
		document.getElementById("billingDiv").style.display = "block";
//		checkbox.value = "off";
	}
}


function validateRegister(form) {
	var errors = {
			"usernameErr":false,
			"firstnameErr":false,
			"lastnameErr":false,
			"passwordErr":false,
			"verifiedPasswordErr":false
	};
	var errorMessages = [];
	if (form.elements.username.value == "") {
		errors.usernameErr = true;
		errorMessages.push("Username cannot be empty!");
	} else if (form.elements.username.value.length < 4 || form.elements.username.value.length > 20) {
		errors.usernameErr = true;
		errorMessages.push("Username must be between 4 - 20 characters!");
	}
	if (form.elements.firstname.value == "") {
		errors.firstnameErr = true;
		errorMessages.push("Firstname cannot be empty!");
	} else if (form.elements.firstname.value.length < 4 || form.elements.firstname.value.length > 20) {
		errors.firstnameErr = true;
		errorMessages.push("Firstname must be between 4 - 20 characters!");
	}
	if (form.elements.lastname.value == "") {
		errors.lastnameErr = true;
		errorMessages.push("Lastname cannot be empty!");
	} else if (form.elements.lastname.value.length < 4 || form.elements.lastname.value.length > 20) {
		errors.lastnameErr = true;
		errorMessages.push("Lastname must be between 4 - 20 characters!");
	}
	if (form.elements.password.value == "") {
		errors.passwordErr = true;
		errorMessages.push("Password cannot be empty!");
	} else if (form.elements.password.value.length < 6 || form.elements.password.value.length > 16) {
		errors.passwordErr = true;
		errorMessages.push("Password must be between 6 - 16 characters!");
	} else if (form.elements.verifiedPassword.value == "") {
		errors.verifiedPasswordErr = true;
		errorMessages.push("Verified Password cannot be empty!");
	} else if (form.elements.verifiedPassword.value !== form.elements.password.value) {
		errors.verifiedPasswordErr = true;
		errorMessages.push("Passwords did not match!");
	}
	if (errors.passwordErr || errors.verifiedPasswordErr) {
		form.elements.password.value = "";
		form.elements.verifiedPassword.value = "";
	}
	
	return renderErrorMessages(errors, errorMessages);
}

function validatePurchase(form) {
	var errors = {
			"streetErr":false,
			"provinceErr":false,
			"countryErr":false,
			"zipErr":false,
			"bstreetErr":false,
			"bprovinceErr":false,
			"bcountryErr":false,
			"bzipErr":false,
			"firstnameErr":false,
			"lastnameErr":false,
			"cardnumberErr":false,
			"dateErr":false,
			"cvcErr":false
		};
	var errorMessages = [];
	if (form.elements.street.value == "") {
		errors.streetErr = true;
		errorMessages.push("Shipping street cannot be empty!");
	}
	if (form.elements.province.value == "") {
		errors.provinceErr = true;
		errorMessages.push("Shipping province cannot be empty!");
	}
	if (form.elements.country.value == "") {
		errors.countryErr = true;
		errorMessages.push("Shipping country cannot be empty!");
	}
	if (form.elements.zip.value == "") {
		errors.zipErr = true;
		errorMessages.push("Shipping zip code cannot be empty!");
	}
	if (!form.elements.sameAddress.checked) {
		if (form.elements.bstreet.value == "") {
			errors.bstreetErr = true;
			errorMessages.push("Billing street cannot be empty!");
		}
		if (form.elements.bprovince.value == "") {
			errors.bprovinceErr = true;
			errorMessages.push("Billing province cannot be empty!");
		}
		if (form.elements.bcountry.value == "") {
			errors.bcountryErr = true;
			errorMessages.push("Billing country cannot be empty!");
		}
		if (form.elements.bzip.value == "") {
			errors.bzipErr = true;
			errorMessages.push("Billing Zip code cannot be empty!");
		}
	}
	if (form.elements.firstname.value == "") {
		errors.firstnameErr = true;
		errorMessages.push("First name cannot be empty!");
	}
	if (form.elements.lastname.value == "") {
		errors.lastnameErr = true;
		errorMessages.push("Last name cannot be empty!");
	}
	if (form.elements.cardnumber.value == "") {
		errors.cardnumberErr = true;
		errorMessages.push("Card number cannot be empty!");
	} else if (form.elements.cardnumber.value.length<12 || form.elements.cardnumber.value.length>18 || form.elements.cardnumber.value.match("^[0-9]+$") == null) {
		errors.cardnumberErr = true;
		errorMessages.push("Invalid Card number!");
	}
	if (form.elements.cvc.value == "") {
		errors.cvcErr = true;
		errorMessages.push("CVC cannot be empty!");
	} else if (form.elements.cvc.value.length < 3 || form.elements.cvc.value.length > 4 || form.elements.cvc.value.match("^[0-9]+$") == null) {
		errors.cvcErr = true;
		errorMessages.push("Invalid CVC/CVV. CVC/CVV must be 3-4 digits.");
	}
	var m = parseInt(form.elements.month.value);
	var y = parseInt(form.elements.year.value);
	if (isNaN(m) || m==null || m<1 || m>12 || isNaN(y) || y==null) {
		errors.dateErr = true;
		errorMessages.push("Invalid Expiration date!");
	} else {
		var date = new Date();
		var currentMonth = date.getMonth();
		var currentYear = date.getFullYear();
		if (y<currentYear || (y==currentYear && m<currentMonth)){
			errors.dateErr = true;
			errorMessages.push("Credit Card Expired!");
		}
	}	
	
	return renderErrorMessages(errors, errorMessages);
}

function renderErrorMessages(errors, errorMessages) {
	var errorStr = "";
	if(errorMessages.length>0){
		var erorrLabel = document.getElementById("errorLabel");
		errorMessages.forEach(message=>{
			errorStr += message+"\n";
		});
	}
	for (var key in errors) {
		console.log(key+"   "+document.getElementById(key));
		document.getElementById(key).style.display = (errors[key]===true ? "inline" : "none");
	}
	document.getElementById("errorLabel").innerText = errorStr;
	
	return errorMessages.length===0;
}

function validateRating(form) {
	var rating = form.elements.rating.value;
	var review = form.elements.review.value;
	if(rating==null || rating=="") {
		alert("Please select a rating!"); 
		return false;
	} else if (typeof rating === "string" && rating.match("^[1-5]$") == null) {
		alert("Invalid rating! Must be 1 - 5!");
		return false;
	} else if (review==null || review=="") {
		alert("Please write a review!");
		return false;
	} else if (review.length>255) {
		alert("Maximum of review is 255 characters!");
		return false;
	}
	return true;
}

function fetchTop10Purchases(){
	var request = null;
	if (window.XMLHttpRequest) {
		request = new XMLHttpRequest();
	} else {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	request.open("POST", "/bookStore/Start/Ajax/Analytics", true);
	request.onreadystatechange = function() {
		if(request.readyState == 4 && request.status == 200) {
			var res = JSON.parse(request.responseText);
			if(res.code===1){
				var result = res.result;
				var resultTable = document.getElementById("popularBooksTableBody");
				// remove previous results
				while (resultTable.firstChild) {
					resultTable.removeChild(resultTable.firstChild);
				}
				result.forEach(function(item){
					var row = constructRowOfPopularBooks(item);
					resultTable.appendChild(row);
				});
				document.getElementById("popularBooksTable").style.display = "inline";					
			}else{
				alert(res.error);
			}
		}
		
	};
	request.send(new FormData());
}

function constructRowOfPopularBooks(item){
	var keys = ["bid","title","category"]; 
	var row = document.createElement("tr");
	keys.forEach(function(key){
	    var col = document.createElement("td");
	    col.textContent = item.book[key];
	    row.appendChild(col);
	});
	var price = document.createElement("td");
	price.textContent = "$" + parseInt(item.book.price).toFixed(2);
	var countTd = document.createElement("td");
	countTd.textContent = item.count;
	row.appendChild(price);
	row.appendChild(countTd);
	return row;
}

function constructRowOfBookReviews(item){
	var keys = ["rating","review"]; 
	var row = document.createElement("tr");
	keys.forEach(function(key){
	    var col = document.createElement("td");
	    col.textContent = item[key];
	    row.appendChild(col);
	});
	return row;
}