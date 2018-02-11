package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbManager {
	
	private Connection conn = null;
	
	public Connection getConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				return conn;
			} else {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_store", "root", "");
				return conn;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet getBooks() {
		try {
			Connection conn = this.getConnection();
			String sql = "SELECT * FROM book";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet r = ps.executeQuery();
			return r;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
