function searchBook(form, path) {
	var text = form.elements.search.value;
	var searchPath = path + "?search=" + text;
	window.location.href = searchPath;
}

function openModal(title, category, price, description) {
	el = document.getElementById("book-detail-div");
	el.style.visibility = "visible";
	document.getElementById("book-detail-title").innerText = title;
	document.getElementById("book-detail-category").innerText = category;
	document.getElementById("book-detail-price").innerText = "$"+parseFloat(price).toFixed(2);
	document.getElementById("book-detail-description").innerText = description;
}

function closeModal() {
	el = document.getElementById("book-detail-div");
	el.style.visibility = "hidden";
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

//function quantityDidChange(priceId, quantity, unitPrice) {
//	document.getElementById(priceId).innerText = "Price: $"+(parseInt(quantity)*parseFloat(unitPrice)).toFixed(2);
//}