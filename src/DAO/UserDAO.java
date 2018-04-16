package DAO;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.UserBean;

public class UserDAO extends ObjectDAO {

	public UserDAO() throws Exception {
		super();
	}
	
	/**
	 * Update user type
	 * @param id
	 * @param userType
	 * @throws Exception
	 */
	public void updateUserType(int id, enums.UserType userType) throws Exception {
		String query = "UPDATE user SET user_type=? WHERE id=?";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, userType.toString());
			p.setInt(2, id);
			p.executeUpdate();
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
	 * Get User by id
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public UserBean getUserById(int uid) throws Exception {
		UserBean user = null;
		String query = "SELECT * FROM user WHERE id = ?";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, uid);
			r = p.executeQuery();
			if(r.next()) {
				int id = r.getInt("id");
				String username = r.getString("username");
				String firstname = r.getString("fname");
				String lastname = r.getString("lname");
				enums.UserType userType = enums.UserType.getUserType(r.getString("user_type").toLowerCase());
				user = new UserBean(id,username,firstname,lastname,userType);
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
		
		return user;
	}
	
	
	/**
	 * Log in with username and password.
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public UserBean login(String username, String password) throws Exception {		
		this.validateUsername(username);
		this.validatePassword(password);
		
		UserBean user = null;
		String dbPassword = null;
		
		String query = "SELECT * FROM User WHERE username=?";
		
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, username);
			r = p.executeQuery();
			if(r.next()) {
				int id = r.getInt("id");
				String firstname = r.getString("fname");
				String lastname = r.getString("lname");
				dbPassword = r.getString("password");
				enums.UserType userType = enums.UserType.getUserType(r.getString("user_type").toLowerCase());
				user = new UserBean(id,username,firstname,lastname,userType);
			}
			if(user == null) throw new Exception("Username doesn't exist.");
	        String hashedPassword = UserDAO.hash(password);
			if(hashedPassword.equals(dbPassword)) {
				return user;
			} else {
				throw new Exception("Password is not correct!");
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
	}
	
	/**
	 * Sign up a new user. Note: user must log out first.
	 * 
	 * @param username
	 * @param password
	 * @param fname
	 * @param lname
	 * @param userType
	 * @param request
	 * @throws Exception
	 */
	public void signup(String username, String password, String fname, String lname, String userType) throws Exception {
		
		// validations
		this.validateUsername(username);
		this.validatePassword(password);
		this.validateFirstname(fname);
		this.validateLastname(lname);
		this.validateUserType(userType);
		
		String query = "SELECT * FROM User WHERE username=?";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, username);
			r = p.executeQuery();
			if(r.next())
				throw new Exception("Username is already taken.");
			String hashedPassword = UserDAO.hash(password);
			p.close();
			// insert user
			query = "INSERT INTO User (username, password, lname, fname, user_type) VALUES (?,?,?,?,?)";
			p = con.prepareStatement(query);
			p.setString(1, username);
			p.setString(2, hashedPassword);
			p.setString(3, lname);
			p.setString(4, fname);
			p.setString(5, userType.toString());
			p.executeUpdate();
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
	}
	
	
	/***************************/
	/******* Validations *******/
	/***************************/
	
	private void validatePassword(String password) throws Exception {
		if (password == null || password.length() < 6)
			throw new Exception ("Invalid Password");
	}
	
	
	private void validateUsername(String n) throws Exception {
		this.validateName(n, "Username");
	}
	
	private void validateFirstname(String n) throws Exception {
		this.validateName(n, "Firstname");
	}
	
	private void validateLastname(String n) throws Exception {
		this.validateName(n, "Lastname");
	}
	
	private void validateUserType(String type) throws Exception {
		if(enums.UserType.getUserType(type.toLowerCase())==null) throw new Exception("Invalid user type.");
	}
	
	private void validateName(String name, String label) throws Exception {
		if(name==null || name.length()<4) throw new Exception ("Invalid "+label);
	}

	static private String hash(String password) throws Exception {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
        byte byteData[] = md.digest();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
        		sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
	
}
