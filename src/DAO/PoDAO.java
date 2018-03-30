package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import bean.AddressBean;
import bean.BookBean;
import bean.PoBean;
import bean.PoItemBean;

public class PoDAO extends ObjectDAO {

	public PoDAO() throws ClassNotFoundException {
		super();

	}
	
	public Map<Integer, PoBean> getListOfPos() throws Exception {
		return this.getListOfPos(null);
	}
	
	public PoBean getPoById(int id) throws Exception {
		return this.getListOfPos("po.id="+id).get(id);
	}

	/**
	 * Get list of Purchase Orders
	 * @param q additional conditional SQL statement
	 * @return a map of purchase orders, key is its id
	 * @throws Exception
	 */
	private Map<Integer, PoBean> getListOfPos(String q) throws Exception {
		String query = "SELECT po.*, a.street, a.province, a.country, a.zip, a.phone, poi.price, b.bid, b.title, b.price as bprice, b.category FROM po INNER JOIN address a ON po.address=a.id INNER JOIN POItem poi ON po.id = poi.id INNER JOIN book b ON poi.bid=b.bid";
		if(q!=null) {
			query += " WHERE " + q;
		}
		query += " ORDER BY po.id";
		Map<Integer, PoBean> rv = new HashMap<Integer, PoBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		
		while (r.next()) {
			// PO id
			int id = r.getInt("id");
			
			// POItem fields
			int price = r.getInt("price");
			
			// Book fields
			String bid = r.getString("bid");
			String title = r.getString("title");
			int bPrice = r.getInt("bprice");
			BookBean.Category category = BookBean.Category.valueOf(r.getString("category").toUpperCase());
			
			PoBean po;
			
			if(rv.containsKey(id)) {
				po = rv.get(id);
			} else {
				// PO fields
				String lastname = r.getString("lname");
				String firstname = r.getString("fname");
				PoBean.Status poStatus = PoBean.Status.valueOf(r.getString("status").toUpperCase());
				// Address fields
				int aid = r.getInt("address");
				String street = r.getString("street");
				String province = r.getString("province");
				String country = r.getString("country");
				String zip = r.getString("zip");
				String phone = r.getString("phone");
				AddressBean addr = new AddressBean(aid, street, province, country, zip, phone);
				po = new PoBean(id, lastname, firstname, poStatus, addr);
				rv.put(id, po);
			}
			
			// create and add new PoItem to PO
			BookBean book = new BookBean(bid, title, bPrice, category,"lalalal");
			PoItemBean poItem = new PoItemBean(price, po, book);
			po.getPoItems().add(poItem);
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}

}
