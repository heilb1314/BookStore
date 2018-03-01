package bean;

public class Book {
	
	private int bid; 
	private String title; 
	private String price; 
	private String category; 
	private String author; 
	
	public Book(int bid, String title, String price, String category, String author)
	{
		this.bid = bid;
		this.title = title;
		this.price = price;
		this.category = category;
		this.author = author; 
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
