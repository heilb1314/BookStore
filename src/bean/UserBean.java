package bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="user")
@XmlType(propOrder={"id","username","firstname","lastname","userType"})
public class UserBean {
	
	@XmlType
	@XmlEnum(String.class)
	public static enum UserType {
		@XmlEnumValue("Visitor") VISITOR,
		@XmlEnumValue("Customer") CUSTOMER,
		@XmlEnumValue("Partner") PARTNER,
		@XmlEnumValue("Administrator") ADMIN;
	}
	
	private int id;
	private String username;
	private String firstname;
	private String lastname;
	private UserType userType;
	
	public UserBean(int id, String username, String fname, String lname, UserType type) {
		this.setId(id);
		this.setUsername(username);
		this.setFirstname(fname);
		this.setLastname(lname);
		this.setUserType(type);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
