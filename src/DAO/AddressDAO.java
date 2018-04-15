package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.AddressBean;

public class AddressDAO extends ObjectDAO {

	public AddressDAO() throws Exception {
		super();
	}
	
	/**
	 * Get an address by id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AddressBean getAddressById(int id) throws Exception {
		String query = "SELECT * FROM address WHERE id = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, id);
		ResultSet r = p.executeQuery();
		AddressBean address = null;
		if(r.next()) {
			int aid = r.getInt("id");
			String street = r.getString("street");
			String province = r.getString("province");
			String country = r.getString("country");
			String zip = r.getString("zip");
			String phone = r.getString("phone");
			address = new AddressBean(aid, street, province, country, zip, phone);
		}
		r.close();
		p.close();
		con.close();
		return address;
	}

	/**
	 * Add a new address
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @return created address id
	 * @throws Exception
	 */
	public int addAddress(String street, String province, String country, String zip, String phone) throws Exception {
		String query = "INSERT INTO address (street, province, country, zip";
		if(phone!=null) {
			query += ", phone) VALUES (?,?,?,?,?)";
		} else {
			query += ") VALUES (?,?,?,?)";
		}
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		p.setString(1, street);
		p.setString(2, province);
		p.setString(3, country);
		p.setString(4, zip);
		if(phone!=null) {
			p.setString(5, phone);			
		}
		int r = p.executeUpdate();
		if(r == 0) {
			p.close();
			con.close();
			throw new Exception("Fail to create address. no rows affected.");
		} else {
			try (ResultSet generatedKeys = p.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            		int id = generatedKeys.getInt(1);
	            		p.close();
	            		con.close();
	            		return id;
	            }
	            else {
		            	p.close();
		        		con.close();
	                throw new Exception("Creating address failed, no ID obtained.");
	            }
	        }
		}
		
	}
	
	/**
	 * Update Address
	 * @param id
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @throws Exception
	 */
	public void updateAddress(int id, String street, String province, String country, String zip, String phone) throws Exception {
		AddressBean address = this.getAddressById(id);
		if(address == null) throw new Exception("Address Id does not exist!");
		String query = "UPDATE address SET street=?, province=?, country=?, zip=?";
		if(phone!=null) {
			query += ", phone=?";
		}
		query += " WHERE id=?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, street);
		p.setString(2, province);
		p.setString(3, country);
		p.setString(4, zip);
		if(phone!=null) {
			p.setString(5, phone);
			p.setInt(6, id);
		} else {
			p.setInt(5, id);
		}
		p.executeUpdate();
		p.close();
		con.close();
	}
	
	
	
}
