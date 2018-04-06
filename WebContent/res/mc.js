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

//function quantityDidChange(priceId, quantity, unitPrice) {
//	document.getElementById(priceId).innerText = "Price: $"+(parseInt(quantity)*parseFloat(unitPrice)).toFixed(2);
//}