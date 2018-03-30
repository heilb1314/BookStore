package bean;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="purchaseOrderItem")
@XmlType(propOrder={"price","book"})
@XmlAccessorType(XmlAccessType.FIELD)
public class PoItemBean {
	
	private PoItemIdBean pk = new PoItemIdBean();
	@XmlAttribute
	private int price;
	
	public PoItemBean(int price) {
		this(price,null,null);
	}
	
	public PoItemBean(int price, PoBean po, BookBean book) {
		super();
		this.setPrice(price);
		this.getPk().setPo(po);
		this.getPk().setBook(book);
	}
	
	public PoItemIdBean getPk() {
		return pk;
	}

	public void setPk(PoItemIdBean pk) {
		this.pk = pk;
	}
	
	public PoBean getPo() {
		return this.getPk().getPo();
	}
	public void setPo(PoBean po) {
		this.getPk().setPo(po);
	}
	@XmlElement(name="book")
	public BookBean getBook() {
		return this.getPk().getBook();
	}
	public void setBook(BookBean book) {
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
		AddressBean address = new AddressBean(1,"1st Ave","Ontario","Canada","M1M 3G5","647-128-1832");
		PoBean po = new PoBean(1,"Gates", "Bill", PoBean.Status.PROCESSED, address);
		BookBean book = new BookBean("sample bid","sample title",10,BookBean.Category.FICTION,"lalalala");
		PoItemBean poItem = new PoItemBean(50, po, book);
		String json = poItem.toJsonObjectBuilder().build().toString();
		System.out.println(json);
	}
	

}
