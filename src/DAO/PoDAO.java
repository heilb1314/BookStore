package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.AddressBean;
import bean.BookBean;
import bean.PoBean;
import bean.PoItemBean;

public class PoDAO extends ObjectDAO {

	public PoDAO() throws ClassNotFoundException {
		super();

	}
	
	/**
	 * Get All purchase orders
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, PoBean> getListOfPos() throws Exception {
		return this.getListOfPos(null,null,null);
	}
	
	/**
	 * Get One purchase order with purchase order id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PoBean getPoById(int id) throws Exception {
		PoBean po =  this.getListOfPos(null,null,"po.id="+id).get(id);
		if (po==null) throw new Exception("No Purchase Order has found with id: "+id);
		return po;
	}
	
	/**
	 * Get All purchase orders of given user id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, PoBean> getListOfPosByUserId(int id) throws Exception {
		return this.getListOfPos("u.id as user_id, u.username, u.fname, u.lname, u.user_type", "INNER JOIN user u ON po.uid=u.id", "u.id="+id);
	}
	
	
	/**
	 * Add a Purchase Order
	 * @param status
	 * @param addressId
	 * @param uid
	 * @throws Exception
	 */
	public void addPo(PoBean.Status status, int addressId, int uid, List<PoItemBean> items) throws Exception {
		if(items.size()==0) throw new Exception("Purchase Order Must Contain at least one item.");
		this.addPoItems(items);
		String query = "INSERT INTO PO (status, address, uid) VALUES (?, ?, ?)";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, status.toString());
		p.setInt(2, addressId);
		p.setInt(3, uid);
		p.executeUpdate();
		p.close();
		con.close();
	}
	
	/**
	 * Deny a Purchase Order
	 * @param po
	 * @throws Exception
	 */
	public void denyPo(PoBean po) throws Exception {
		this.setPoStatus(PoBean.Status.DENIED, po);
	}
	
	/**
	 * Ordered a Purchase Order
	 * @param po
	 * @throws Exception
	 */
	public void orderPo(PoBean po) throws Exception {
		this.setPoStatus(PoBean.Status.ORDERED, po);
	}
	
	
	/**
	 * Delete a list of Purchase Order Items
	 * @param items
	 * @throws Exception
	 */
	public void removePoItems(List<PoItemBean> items) throws Exception {
		String query = "DELETE FROM POItem WHERE id = ? and bid = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		for(PoItemBean item : items) {
			p.setInt(1, item.getPo().getId());
			p.setString(2, item.getBook().getBid());
			p.addBatch();
		}
		p.executeBatch();
		p.close();
		con.close();
	}
	
	
	/**
	 * Rate a Purchase order item ( give it rating and review )
	 * @param item
	 * @param rating
	 * @param review
	 * @throws Exception
	 */
	public void ratePoItem(PoItemBean item, int rating, String review) throws Exception {
		String query = "UPDATE POItem SET rating = ?, review = ? WHERE id = ? and bid = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, rating);
		p.setString(2, review);
		p.setInt(3, item.getPo().getId());
		p.setString(4, item.getBook().getBid());
		p.executeUpdate();
		p.close();
		con.close();
	}
	
	/*********************/
	/** Private Methods **/
	/*********************/
	
	
	/**
	 * Insert a list of POItems into database
	 * @param items
	 * @throws Exception
	 */
	private void addPoItems(List<PoItemBean> items) throws Exception {
		String query = "INSERT INTO POItem (bid, price, quantity) VALUES (?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		for(PoItemBean item : items) {
			p.setString(1, item.getBook().getBid());
			p.setInt(2, item.getPrice());
			p.setInt(3, item.getQuantity());
			p.addBatch();
		}
		p.executeBatch();
		p.close();
		con.close();
	}
	
	/**
	 * Set a Purchase Order Status
	 * @param status
	 * @param po
	 * @throws Exception
	 */
	private void setPoStatus(PoBean.Status status, PoBean po) throws Exception {
		String query = "UPDATE PO SET status = ? WHERE id = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, status.toString());
		p.setInt(2, po.getId());
		p.executeUpdate();
		p.close();
		con.close();
	}
	
	
	/**
	 * Get list of Purchase Orders
	 * @param additionalSelectSql
	 * @param additionalFromSql
	 * @param additionalWhereSql
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, PoBean> getListOfPos(String additionalSelectSql, String additionalFromSql, String additionalWhereSql) throws Exception {
		String select = "SELECT po.*, a.street, a.province, a.country, a.zip, a.phone, poi.price, poi.quantity, poi.rating as poi_rating, poi.review, b.bid, b.title, b.price as bprice, b.category, b.description, b.rating as book_rating";
		String from = "FROM po INNER JOIN address a ON po.address=a.id INNER JOIN POItem poi ON po.id = poi.id INNER JOIN book b ON poi.bid=b.bid";
		
		if(additionalSelectSql != null) {
			select += "," + additionalSelectSql;
		}
		
		if(additionalFromSql != null) {
			from += " " + additionalFromSql;
		}
		
		String query = select + " " + from;
		if(additionalWhereSql!=null) {
			query = " WHERE " + additionalWhereSql;
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
			int quantity = r.getInt("quantity");
			int poiRating = r.getInt("poi_rating");
			String poiReview = r.getString("review");
			
			// Book fields
			String bid = r.getString("bid");
			String title = r.getString("title");
			int bPrice = r.getInt("bprice");
			int bRating = r.getInt("book_rating");
			String description = r.getString("description");
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
			BookBean book = new BookBean(bid, title, bPrice, category, bRating, description);
			PoItemBean poItem = new PoItemBean(price, quantity, po, book, poiRating, poiReview);
			po.getPoItems().add(poItem);
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}

}
