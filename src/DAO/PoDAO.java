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
import bean.ShoppingCartItemBean;

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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, pid);
		ResultSet r = p.executeQuery();
		PoBean po = null;
		if(r.next()) {
			int id = r.getInt("id");
			PoBean.Status status = PoBean.Status.getStatus(r.getString("status"));
			int addressId = r.getInt("address");
			int uid = r.getInt("uid");
			AddressDAO addressDAO = new AddressDAO();
			UserDAO userDAO = new UserDAO();
			po = new PoBean(id, userDAO.getUserById(uid), status, addressDAO.getAddressById(addressId));
		}
		return po;
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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		p.setString(1, PoBean.Status.PROCESSED.toString());
		p.setInt(2, addressId);
		p.setInt(3, uid);
		int r = p.executeUpdate();
		if(r == 0) {
			p.close();
			con.close();
			throw new Exception("Fail to create address. no rows affected.");
		} else {
			try (ResultSet generatedKeys = p.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            		int id = generatedKeys.getInt(1);
	            		p.close();
	            		con.close();
	            		return id;
	            }
	            else {
		            	p.close();
		        		con.close();
	                throw new Exception("Creating address failed, no ID obtained.");
	            }
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
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		for(ShoppingCartItemBean item : items) {
			p.setInt(1, pid);
			p.setString(2, item.getBook().getBid());
			p.setInt(3, item.getPrice());
			p.setInt(4, item.getQuantity());
			p.addBatch();
		}
		p.executeBatch();
		p.close();
		con.close();
	}
	
	/**
	 * Delete a list of Purchase Order Items by purchase order id
	 * @param items
	 * @throws Exception
	 */
	public void removePoItems(int pid) throws Exception {
		String query = "SELECT * FROM PO WHERE id=?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, pid);
		ResultSet r = p.executeQuery();
		if(!r.next()) throw new Exception("Purchase Order doesn't exist.");
		r.close();
		p.close();
		query = "DELETE FROM POItem WHERE id = ?";
		p = con.prepareStatement(query);
		p.setInt(1, pid);
		p.executeUpdate();
		p.close();
		con.close();
	}
	
	/**
	 * Deny a Purchase Order
	 * @param pid
	 * @throws Exception
	 */
	public void denyPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=PoBean.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(PoBean.Status.DENIED, pid);
	}
	
	/**
	 * Ordered a Purchase Order
	 * @param pid
	 * @throws Exception
	 */
	public void orderPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=PoBean.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(PoBean.Status.ORDERED, pid);
	}
	
	/**
	 * Re-process Purchase order
	 * @param pid
	 * @throws Exception
	 */
	public void reprocessPo(int pid) throws Exception {
		PoBean po = this.getPoById(pid);
		if(po==null) throw new Exception("Purchase Order doesn't exist!");
		if(po.getStatus()!=PoBean.Status.DENIED) throw new Exception("Invalid Purchase Order Status.");
		this.setPoStatus(PoBean.Status.PROCESSED, pid);
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
	 * Set a Purchase Order Status
	 * @param status
	 * @param pid
	 * @throws Exception
	 */
	private void setPoStatus(PoBean.Status status, int pid) throws Exception {
		String query = "UPDATE PO SET status = ? WHERE id = ?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, status.toString());
		p.setInt(2, pid);
		p.executeUpdate();
		p.close();
		con.close();
	}
	

}
