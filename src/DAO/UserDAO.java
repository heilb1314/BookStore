package DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.security.SecureRandom;

import bean.AddressBean;
import bean.UserBean;
import bean.UserBean.UserType;

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
	public void updateUserType(int id, UserType userType) throws Exception {
		String query = "UPDATE user SET user_type=? WHERE id=?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, userType.toString());
		p.setInt(2, id);
		p.executeUpdate();
		p.close();
		con.close();
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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, uid);
		ResultSet r = p.executeQuery();
		if(r.next()) {
			int id = r.getInt("id");
			String username = r.getString("username");
			String firstname = r.getString("fname");
			String lastname = r.getString("lname");
			UserType userType = UserType.getUserType(r.getString("user_type").toLowerCase());
			user = new UserBean(id,username,firstname,lastname,userType);
		}
		r.close();
		p.close();
		con.close();
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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, username);
		ResultSet r = p.executeQuery();
		if(r.next()) {
			int id = r.getInt("id");
			String firstname = r.getString("fname");
			String lastname = r.getString("lname");
			dbPassword = r.getString("password");
			UserType userType = UserType.getUserType(r.getString("user_type").toLowerCase());
			user = new UserBean(id,username,firstname,lastname,userType);
		}
		r.close();
		p.close();
		con.close();
		
		if(user == null) throw new Exception("Username doesn't exist.");
		
        String hashedPassword = this.hash(password);
		System.out.println("pass: "+hashedPassword);
		System.out.println("saved pass: "+dbPassword);
		
		if(hashedPassword.equals(dbPassword)) {
			return user;
		} else {
			throw new Exception("Password is not correct!");
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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, username);
		ResultSet r = p.executeQuery();
		if(r.next()) {
			// username already existed
			r.close();
			p.close();
			con.close();
			throw new Exception("Username is already taken.");
		}
		if(!r.isClosed()) {
			r.close();
		}
		p.close();
		// hash password
		
		
		String hashedPassword = this.hash(password);
		
		// insert user
		query = "INSERT INTO User (username, password, lname, fname, user_type) VALUES (?,?,?,?,?)";
		p = con.prepareStatement(query);
		p.setString(1, username);
		p.setString(2, hashedPassword);
		p.setString(3, lname);
		p.setString(4, fname);
		p.setString(5, userType.toString());
		p.executeUpdate();
		p.close();
		con.close();
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
		if(UserBean.UserType.getUserType(type.toLowerCase())==null) throw new Exception("Invalid user type.");
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
