package bean;

import java.util.Set;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class Po {
	
	public static enum Status {
		ORDERED, PROCESSED, DENIED
	}
	
	private int id;
	private String lname;
	private String fname;
	private Po.Status status;
	private Address address;
	private Set<PoItem> poItems = new HashSet<PoItem>(0);
	

	public Po(int id, String lname, String fname, Status status, Address address) {
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
	public Po.Status getStatus() {
		return status;
	}
	public void setStatus(Po.Status status) {
		this.status = status;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Set<PoItem> getPoItems() {
		return poItems;
	}

	public void setPoItems(Set<PoItem> items) {
		this.poItems = items;
	}
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for(PoItem p : this.getPoItems()) {
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
		Address address = new Address(1,"1st Ave","Ontario","Canada","M1M 3G5","647-128-1832");
		Po po = new Po(1,"Gates", "Bill", Po.Status.PROCESSED, address);
		String json = po.toJsonObjectBuilder().build().toString();
		System.out.println(json);
	}

}
