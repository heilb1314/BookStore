package bean;

import java.util.Set;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class PoBean {
	
	public static enum Status {
		ORDERED, PROCESSED, DENIED
	}
	
	private int id;
	private String lname;
	private String fname;
	private PoBean.Status status;
	private AddressBean address;
	private Set<PoItemBean> poItems = new HashSet<PoItemBean>(0);
	

	public PoBean(int id, String lname, String fname, Status status, AddressBean address) {
		super();
		this.setId(id);
		this.setLname(lname);
		this.setFname(fname);
		this.setStatus(status);
		this.setAddress(address);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public PoBean.Status getStatus() {
		return status;
	}
	public void setStatus(PoBean.Status status) {
		this.status = status;
	}
	public AddressBean getAddress() {
		return address;
	}
	public void setAddress(AddressBean address) {
		this.address = address;
	}
	
	public Set<PoItemBean> getPoItems() {
		return poItems;
	}

	public void setPoItems(Set<PoItemBean> items) {
		this.poItems = items;
	}
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for(PoItemBean p : this.getPoItems()) {
			jab.add(p.toJsonObjectBuilder());
		}	
		return Json.createObjectBuilder()
				.add("id", this.getId())
				.add("lname", this.getLname())
				.add("fname", this.getFname())
				.add("status", this.getStatus().toString())
				.add("address", this.getAddress().toJsonObjectBuilder())
				.add("poitems", jab);
	}
	
	@Override
	public String toString() {
		return this.toJsonObjectBuilder().build().toString();
	}
	
	
	public static void main(String[] args) {
		AddressBean address = new AddressBean(1,"1st Ave","Ontario","Canada","M1M 3G5","647-128-1832");
		PoBean po = new PoBean(1,"Gates", "Bill", PoBean.Status.PROCESSED, address);
		String json = po.toJsonObjectBuilder().build().toString();
		System.out.println(json);
	}

}
