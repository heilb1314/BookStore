package bean;

import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class Book {
	
	public static enum Category {
		Science,Fiction,Engineering
	}

	private String bid;
	private String title;
	private int price;
	private Book.Category category;
	private Set<PoItem> poItems = new HashSet<PoItem>(0);
	
	public Book(String bid, String title, int price, Category category) {
		super();
		this.bid = bid;
		this.title = title;
		this.price = price;
		this.category = category;
	}


	public String getBid() {
		return bid;
	}


	public void setBid(String bid) {
		this.bid = bid;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}


	public Book.Category getCategory() {
		return category;
	}


	public void setCategory(Book.Category category) {
		this.category = category;
	}
	
	public Set<PoItem> getPoItems() {
		return poItems;
	}

	public void setPoItems(Set<PoItem> poItems) {
		this.poItems = poItems;
	}
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for(PoItem p : this.getPoItems()) {
			jab.add(p.toJsonObjectBuilder());
		}
		
		return Json.createObjectBuilder()
				.add("bid", this.getBid())
				.add("title", this.getTitle())
				.add("price", this.getPrice())
				.add("category", this.getCategory().toString())
				.add("poitems", jab);
	}
	
	@Override
	public String toString() {
		return this.toJsonObjectBuilder().build().toString();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((poItems == null) ? 0 : poItems.hashCode());
		result = prime * result + price;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		if (category != other.category)
			return false;
		if (poItems == null) {
			if (other.poItems != null)
				return false;
		} else if (!poItems.equals(other.poItems))
			return false;
		if (price != other.price)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}


	public static void main(String[] args) {
		Book book = new Book("sample bid","sample title",10,Book.Category.Fiction);
		String json = book.toJsonObjectBuilder().build().toString();
		System.out.println(json);
	}


}
