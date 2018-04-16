package bean;

import java.util.Set;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="purchaseOrder")
@XmlType(propOrder={"id","lname","fname","status","address","items"})
@XmlAccessorType(XmlAccessType.FIELD)
public class PoBean {
	
	@XmlAttribute
	private int id;
	@XmlAttribute
	private UserBean user;
	@XmlAttribute(name="status")
	private enums.Status status;
	@XmlElement
	private AddressBean address;
	@XmlElementWrapper(name="items")
    @XmlElement(name="purchaseOrderItem")
	private Set<PoItemBean> poItems = new HashSet<PoItemBean>(0);
	

	public PoBean(int id, UserBean user, enums.Status status, AddressBean address) {
		super();
		this.setId(id);
		this.setUser(user);
		this.setStatus(status);
		this.setAddress(address);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public enums.Status getStatus() {
		return status;
	}
	public void setStatus(enums.Status status) {
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

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

}
