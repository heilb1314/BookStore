package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import bean.BookBean;

public class BookDAO extends ObjectDAO {

	public BookDAO() throws ClassNotFoundException {
		super();
		
	}
	
	public Map<String, BookBean> getListOfBooksByCategory(BookBean.Category category) throws Exception {
		String query = "select * from book where category = ? order by title";
		Map<String, BookBean> rv = new HashMap<String, BookBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, category.toString());
		ResultSet r = p.executeQuery();
		while (r.next()){
			String bid = r.getString("BID");
			String title = r.getString("TITLE");
			int price = r.getInt("PRICE");
			BookBean book = new BookBean(bid, title, price, category);
			rv.put(bid, book);
		}
		r.close();
		p.close();
		con.close();
		return rv;		
	}
	
}
