package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DAO.ObjectDAO;
import bean.BookBean;

public class BookDAO extends ObjectDAO {

	public BookDAO() throws ClassNotFoundException {
		super();
		
	}
	
	/**
	 * Get a book by id
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public BookBean getBookById(String bid) throws Exception {
		String query = "SELECT * FROM book WHERE bid = ?";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		BookBean book = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, bid);
			r = p.executeQuery();
			if (r.next()) {
				book = this.parseBookBean(r);
			}
			if(book==null)
				throw new Exception("No book with bid: " + bid);
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
		return book;
	}
	
	/**
	 * Get a list of books by title query
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public List<BookBean> getListOfBooksByTitle(String title) throws Exception {
		String query = "SELECT * FROM book WHERE title LIKE ? ORDER BY title";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		List<BookBean> rv = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, title!=null?"%"+title+"%":"%");
			r = p.executeQuery();
			rv = this.parseResultSetToList(r);
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
		return rv;
	}

	/**
	 * Get a list of books by category
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public List<BookBean> getListOfBooksByCategory(enums.Category category) throws Exception {
		String query = "";
		if(category==null) {
			query = "SELECT * FROM book";
		} else {
			query = "SELECT * FROM book WHERE category = ?";
		}
		query += " ORDER BY title";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		List<BookBean> rv = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			if(category!=null) {
				p.setString(1, category.toString());			
			}
			r = p.executeQuery();
			rv = this.parseResultSetToList(r);
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
		return rv;		
	}
	
	/**
	 * Parse book from ResultSet
	 * @param r
	 * @return
	 * @throws Exception
	 */
	public BookBean parseBookBean(ResultSet r) throws Exception {
		String bid = r.getString("BID");
		String title = r.getString("TITLE");
		int price = r.getInt("PRICE");
		float rating = r.getFloat("rating");
		enums.Category category = enums.Category.getCategory(r.getString("CATEGORY"));
		String description = r.getString("DESCRIPTION");
		BookBean book = new BookBean(bid, title, price, category, rating, description);
		return book;
	}
	
	/**
	 * Recalculate and update a book rating
	 * @param bid
	 * @throws Exception
	 */
	public void updateBookRating(String bid) throws Exception {
		BookBean book = this.getBookById(bid);
		if(book==null) throw new Exception("No such book!");
		String query = "UPDATE book b SET b.rating=(SELECT ROUND(AVG(poi.rating),1) FROM POItem poi INNER JOIN PO p ON poi.id=p.id WHERE poi.bid=? AND poi.rating>0 AND p.status='ORDERED') WHERE bid=?";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, bid);
			p.setString(2, bid);
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
	
	private List<BookBean> parseResultSetToList(ResultSet r) throws Exception {
		List<BookBean> rv = new ArrayList<BookBean>();
		while (r.next()){
			BookBean book = parseBookBean(r);
			rv.add(book);
		}
		return rv;
	}
	
	
	
}