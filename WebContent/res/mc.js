

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
