package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DAO.ObjectDAO;
import bean.BookBean;

public class BookDAO extends ObjectDAO {

	public BookDAO() throws ClassNotFoundException {
		super();
		
	}
	
	public BookBean getBookById(String bid) throws Exception {
		String query = "SELECT * FROM book WHERE bid = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, bid);
		ResultSet r = p.executeQuery();
		BookBean book = null;
		if (r.next()) {
			book = this.parseBookBean(r);
		}
		r.close();
		p.close();
		con.close();
		if(book==null)
			throw new Exception("No book with bid: " + bid);
		return book;
	}
	
	public List<BookBean> getListOfBooksByTitle(String title) throws Exception {
		String query = "SELECT * FROM book WHERE title LIKE ? ORDER BY title";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, title!=null?"%"+title+"%":"%");
		ResultSet r = p.executeQuery();
		List<BookBean> rv = this.parseResultSetToList(r);
		r.close();
		p.close();
		con.close();
		return rv;
	}

	public List<BookBean> getListOfBooksByCategory(BookBean.Category category) throws Exception {
		String query = "";
		if(category==null) {
			query = "SELECT * FROM book";
		} else {
			query = "SELECT * FROM book WHERE category = ?";
		}
		query += " ORDER BY title";
		
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		if(category!=null) {
			p.setString(1, category.toString());			
		}
		ResultSet r = p.executeQuery();
		List<BookBean> rv = this.parseResultSetToList(r);
		r.close();
		p.close();
		con.close();
		return rv;		
	}
	
	
	private List<BookBean> parseResultSetToList(ResultSet r) throws Exception {
		List<BookBean> rv = new ArrayList<BookBean>();
		while (r.next()){
			BookBean book = parseBookBean(r);
			rv.add(book);
		}
		return rv;
	}
	
	private BookBean parseBookBean(ResultSet r) throws Exception {
		String bid = r.getString("BID");
		String title = r.getString("TITLE");
		int price = r.getInt("PRICE");
		int rating = r.getInt("rating");
		BookBean.Category category = BookBean.Category.getCategory(r.getString("CATEGORY"));
		String description = r.getString("DESCRIPTION");
		BookBean book = new BookBean(bid, title, price, category, rating, description);
		return book;
	}
	
}