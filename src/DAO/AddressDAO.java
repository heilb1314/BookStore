package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		AddressBean address = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, id);
			r = p.executeQuery();
			if(r.next()) {
				int aid = r.getInt("id");
				String street = r.getString("street");
				String province = r.getString("province");
				String country = r.getString("country");
				String zip = r.getString("zip");
				String phone = r.getString("phone");
				address = new AddressBean(aid, street, province, country, zip, phone);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(r!=null) r.close();
				if(p!=null) p.close();
				if(con!=null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
		
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			p.setString(1, street);
			p.setString(2, province);
			p.setString(3, country);
			p.setString(4, zip);
			if(phone!=null) {
				p.setString(5, phone);			
			}
			int r = p.executeUpdate();
			if(r == 0) {
				throw new Exception("Fail to create address. no rows affected.");
			} else {
				try (ResultSet generatedKeys = p.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		            		int id = generatedKeys.getInt(1);
		            		return id;
		            }
		            else {
		                throw new Exception("Creating address failed, no ID obtained.");
		            }
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(p!=null) p.close();
				if(con!=null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
		Connection con = null;
		PreparedStatement p = null;
		try {
			AddressBean address = this.getAddressById(id);
			if(address == null) throw new Exception("Address Id does not exist!");
			String query = "UPDATE address SET street=?, province=?, country=?, zip=?";
			if(phone!=null) {
				query += ", phone=?";
			}
			query += " WHERE id=?";
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
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
		} catch(Exception e) {
			throw e;
		} finally {
			try {
				if(p!=null) p.close();
				if(con!=null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
