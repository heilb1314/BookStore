package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.ObjectDAO;
import bean.BookBean;

public class BookDAO extends ObjectDAO {

	public BookDAO() throws ClassNotFoundException {
		super();
		
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
			String bid = r.getString("BID");
			String title = r.getString("TITLE");
			int price = r.getInt("PRICE");
			BookBean.Category category = BookBean.Category.getCategory(r.getString("CATEGORY"));
			BookBean book = new BookBean(bid, title, price, category);
			rv.add(book);
		}
		return rv;
	}
	
}