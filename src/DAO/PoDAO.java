package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.BookReviewBean;
import bean.PoBean;
import bean.ShoppingCartItemBean;
import javafx.util.Pair;

public class PoDAO extends ObjectDAO {

	public PoDAO() throws ClassNotFoundException {
		super();

	}
	
	/**
	 * Get Purchase Order By Id
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public PoBean getPoById(int pid) throws Exception {
		String query = "SELECT * FROM PO WHERE id = ?";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		PoBean po = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, pid);
			r = p.executeQuery();
			if(r.next()) {
				int id = r.getInt("id");
				enums.Status status = enums.Status.getStatus(r.getString("status"));
				int addressId = r.getInt("address");
				int uid = r.getInt("uid");
				AddressDAO addressDAO = new AddressDAO();
				UserDAO userDAO = new UserDAO();
				po = new PoBean(id, userDAO.getUserById(uid), status, addressDAO.getAddressById(addressId));
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
		return po;
	}
	
	/**
	 * Get a list of book reviews of a given book id
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public List<BookReviewBean> getBookReviewById(String bid) throws Exception {
		List<BookReviewBean> reviews = new ArrayList<BookReviewBean>();
		String query = "SELECT poi.rating, poi.review FROM POItem poi INNER JOIN PO p ON poi.id=p.id AND p.status='ORDERED' INNER JOIN book b ON poi.bid=b.bid WHERE poi.bid=? AND poi.rating>0 ORDER BY poi.id DESC";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, bid);
			r = p.executeQuery();
			while(r.next()) {
				BookReviewBean review = new BookReviewBean(r.getInt("rating"), r.getString("review"));
				reviews.add(review);
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
		return reviews;
	}
	
	/**
	 * Get the purchase id of the available purchased item with given user id and book id 
	 * (user can rate a book only when he/she made a successful purchase on that book 
	 * and have never rated on that book for that specific purchase yet)
	 * @param uid
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public int getAvailableRatingPurchaseItemId(int uid, String bid) throws Exception {
		String query = "SELECT poi.id AS pid FROM POItem poi INNER JOIN PO p ON poi.id=p.id WHERE p.uid=? AND p.status='ORDERED' AND poi.bid=? AND poi.rating=0 LIMIT 1";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		int pid = 0;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, uid);
			p.setString(2, bid);
			r = p.executeQuery();
			if(r.next()) {
				pid = r.getInt("pid");
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
		return pid;
	}
	
	/**
	 * process a Purchase Order
	 * @param status
	 * @param addressId
	 * @param uid
	 * @throws Exception
	 */
	public int processPo(int addressId, int uid) throws Exception {
		String query = "INSERT INTO PO (status, address, uid) VALUES (?, ?, ?)";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			p.setString(1, enums.Status.PROCESSED.toString());
			p.setInt(2, addressId);
			p.setInt(3, uid);
			int r = p.executeUpdate();
			if(r==0) throw new Exception("Fail to create address. no rows affected.");
			ResultSet generatedKeys = p.getGeneratedKeys();
			if(!generatedKeys.next()) throw new Exception("Creating address failed, no ID obtained.");
			int id = generatedKeys.getInt(1);
			return id;
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
	 * Add shopping cart items to database as purchase order item
	 * @param items
	 * @param pid
	 * @throws Exception
	 */
	public void addPoItems(List<ShoppingCartItemBean> items, int pid) throws Exception {
		String query = "INSERT INTO POItem (id, bid, price, quantity) VALUES (?,?,?,?)";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			for(ShoppingCartItemBean item : items) {
				p.setInt(1, pid);
				p.setString(2, item.getBook().getBid());
				p.setInt(3, item.getPrice());
				p.setInt(4, item.getQuantity());
				p.addBatch();
			}
			p.executeBatch();
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
	 * Delete a list of Purchase Order Items by purchase order id
	 * @param items
	 * @throws Exception
	 */
	public void removePoItems(int pid) throws Exception {
		String query = "SELECT * FROM PO WHERE id=?";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, pid);
			r = p.executeQuery();
			if(!r.next()) throw new Exception("Purchase Order doesn't exist.");
			r.close();
			p.close();
			query = "DELETE FROM POItem WHERE id = ?";
			p = con.prepareStatement(query);
			p.setInt(1, pid);
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
	
	/**
	 * Deny a Purchase Order
	 * @param pid
	 * @throws Exception
	 */
	public void denyPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=enums.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(enums.Status.DENIED, pid);
	}
	
	/**
	 * Ordered a Purchase Order
	 * @param pid
	 * @throws Exception
	 */
	public void orderPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=enums.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(enums.Status.ORDERED, pid);
	}
	
	/**
	 * Re-process Purchase order
	 * @param pid
	 * @throws Exception
	 */
	public void reprocessPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=enums.Status.DENIED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(enums.Status.PROCESSED, pid);
	}
	
	/**
	 * Rate a Purchase order item ( give it rating and review )
	 * @param pid
	 * @param bid
	 * @param rating
	 * @param review
	 * @throws Exception
	 */
	public void ratePoItem(int pid, String bid, int rating, String review) throws Exception {
		String query = "UPDATE POItem SET rating = ?, review = ? WHERE id = ? and bid = ?";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setInt(1, rating);
			p.setString(2, review);
			p.setInt(3, pid);
			p.setString(4, bid);
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
	 * Get purchases stats (List of User with their total spent per zip code)
	 * @return
	 * @throws Exception
	 */
	public List<Pair<Integer,String>> getPurchaseStats() throws Exception {
		String query = "SELECT p.uid, a.zip, SUM(pi.price) AS total_spent FROM poitem pi INNER JOIN po p ON pi.id=p.id INNER JOIN address a ON p.address=a.id WHERE p.status='ORDERED' GROUP BY p.uid, a.zip";
		Connection con = null;
		PreparedStatement p = null;
		ResultSet r = null;
		List<Pair<Integer,String>> results = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			r = p.executeQuery();
			results = new ArrayList<>();
			while(r.next()) {
				String zip = r.getString("zip");
				int totalSpent = r.getInt("total_spent");
				Pair<Integer,String> data = new Pair<Integer,String>(totalSpent,zip);
				results.add(data);
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
		
		return results;
	}
	
	
	/*********************/
	/** Private Methods **/
	/*********************/
	
	/**
	 * Set a Purchase Order Status
	 * @param status
	 * @param pid
	 * @throws Exception
	 */
	private void setPoStatus(enums.Status status, int pid) throws Exception {
		String query = "UPDATE PO SET status = ? WHERE id = ?";
		Connection con = null;
		PreparedStatement p = null;
		try {
			con = this.ds.getConnection();
			p = con.prepareStatement(query);
			p.setString(1, status.toString());
			p.setInt(2, pid);
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
	

}
