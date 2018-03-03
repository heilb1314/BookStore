package bean;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class PoItem {
	
	private PoItemId pk = new PoItemId();
	private int price;
	
	public PoItem(int price) {
		this(price,null,null);
	}
	
	public PoItem(int price, Po po, Book book) {
		super();
		this.setPrice(price);
		this.getPk().setPo(po);
		this.getPk().setBook(book);
	}
	
	public PoItemId getPk() {
		return pk;
	}

	public void setPk(PoItemId pk) {
		this.pk = pk;
	}

	public Po getPo() {
		return this.getPk().getPo();
	}
	public void setPo(Po po) {
		this.getPk().setPo(po);
	}
	public Book getBook() {
		return this.getPk().getBook();
	}
	public void setBook(Book book) {
		this.getPk().setBook(book);
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		return Json.createObjectBuilder()
				.add("id", this.getPk().getPo().getId())
				.add("bid", this.getPk().getBook().getBid())
				.add("price", this.getPrice());
	}
	
	@Override
	public String toString() {
		return this.toJsonObjectBuilder().build().toString();
	}
	
	
	public static void main(String[] args) {
		Address address = new Address(1,"1st Ave","Ontario","Canada","M1M 3G5","647-128-1832");
		Po po = new Po(1,"Gates", "Bill", Po.Status.PROCESSED, address);
		Book book = new Book("sample bid","sample title",10,Book.Category.Fiction);
		PoItem poItem = new PoItem(50, po, book);
		String json = poItem.toJsonObjectBuilder().build().toString();
		System.out.println(json);
	}
	

}
