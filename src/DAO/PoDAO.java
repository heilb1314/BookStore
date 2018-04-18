package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import bean.*;
import javafx.util.Pair;
import listener.NewPurchase;

public class PoDAO extends ObjectDAO {

    public PoDAO() throws ClassNotFoundException {
        super();

    }

    /**
     * Get Purchase Order By Id
     *
     * @param pid
     * @return
     * @throws Exception
     */
    public PoBean getPoById(int pid) throws Exception {
        String query = "SELECT * FROM PO WHERE id = ?";
        try (
                Connection con = this.ds.getConnection();
                PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, pid);
            ResultSet r = p.executeQuery();
            PoBean po = null;
            if (r.next()) {
                int id = r.getInt("id");
                PoBean.Status status = PoBean.Status.getStatus(r.getString("status"));
                int addressId = r.getInt("address");
                int uid = r.getInt("uid");
                AddressDAO addressDAO = new AddressDAO();
                UserDAO userDAO = new UserDAO();
                po = new PoBean(id, userDAO.getUserById(uid), status, addressDAO.getAddressById(addressId));
            }
            r.close();
            p.close();
            con.close();
            return po;
        }
    }

    /**
     * Get a list of book reviews of a given book id
     *
     * @param bid
     * @return
     * @throws Exception
     */
    public List<BookReviewBean> getBookReviewsById(String bid) throws Exception {
        List<BookReviewBean> reviews = new ArrayList<BookReviewBean>();
        String query = "SELECT poi.rating, poi.review FROM POItem poi INNER JOIN PO p ON poi.id=p.id AND p.status='ORDERED' INNER JOIN book b ON poi.bid=b.bid WHERE poi.bid=? AND poi.rating>0 ORDER BY poi.id DESC";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, bid);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                BookReviewBean review = new BookReviewBean(r.getInt("rating"), r.getString("review"));
                reviews.add(review);
            }
            return reviews;
        }
    }

    /**
     * Get the purchase id of the available purchased item with given user id and book id
     * (user can rate a book only when he/she made a successful purchase on that book
     * and have never rated on that book for that specific purchase yet)
     *
     * @param uid
     * @param bid
     * @return
     * @throws Exception
     */
    public int getAvailableRatingPurchaseItemId(int uid, String bid) throws Exception {
        String query = "SELECT poi.id AS pid FROM POItem poi INNER JOIN PO p ON poi.id=p.id WHERE p.uid=? AND p.status='ORDERED' AND poi.bid=? AND poi.rating=0 LIMIT 1";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, uid);
            p.setString(2, bid);
            ResultSet r = p.executeQuery();
            int pid = 0;
            if (r.next()) {
                pid = r.getInt("pid");
            }
            r.close();
            p.close();
            con.close();
            return pid;
        }
    }

    /**
     * process a Purchase Order
     *
     * @param addressId
     * @param uid
     * @throws Exception
     */
    public int processPo(int addressId, int uid) throws Exception {
        String query = "INSERT INTO PO (status, address, uid) VALUES (?, ?, ?)";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            p.setString(1, PoBean.Status.PROCESSED.toString());
            p.setInt(2, addressId);
            p.setInt(3, uid);
            int r = p.executeUpdate();
            try {
                if (r == 0) throw new Exception("Fail to create address. no rows affected.");
                ResultSet generatedKeys = p.getGeneratedKeys();
                if (!generatedKeys.next()) throw new Exception("Creating address failed, no ID obtained.");
                int id = generatedKeys.getInt(1);
                p.close();
                con.close();
                return id;
            } catch (Exception e) {
                p.close();
                con.close();
                throw e;
            }
        }
    }

    /**
     * Add shopping cart items to database as purchase order item
     *
     * @param items
     * @param pid
     * @throws Exception
     */
    public void addPoItems(List<ShoppingCartItemBean> items, int pid) throws Exception {
        String query = "INSERT INTO POItem (id, bid, price, quantity) VALUES (?,?,?,?)";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            for (ShoppingCartItemBean item : items) {
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
    }

    /**
     * Delete a list of Purchase Order Items by purchase order id
     *
     * @param pid
     * @throws Exception
     */
    public void removePoItems(int pid) throws Exception {
        String query = "SELECT * FROM PO WHERE id=?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query);
             PreparedStatement p2 = con.prepareStatement("DELETE FROM POItem WHERE id = ?")) {
            p.setInt(1, pid);
            ResultSet r = p.executeQuery();
            if (!r.next()) throw new Exception("Purchase Order doesn't exist.");
            p2.setInt(1, pid);
            p2.executeUpdate();
        }
    }

    /**
     * Deny a Purchase Order
     *
     * @param pid
     * @throws Exception
     */
    public void denyPo(int pid) throws Exception {
        PoBean po = this.getPoById(pid);
        if (po == null) throw new Exception("Purchase Order doesn't exist!");
        if (po.getStatus() != PoBean.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
        this.setPoStatus(PoBean.Status.DENIED, pid);
    }

    /**
     * Ordered a Purchase Order
     *
     * @param pid
     * @throws Exception
     */
    public void orderPo(int pid) throws Exception {
        PoBean po = this.getPoById(pid);
        if (po == null) throw new Exception("Purchase Order doesn't exist!");
        if (po.getStatus() != PoBean.Status.PROCESSED) throw new Exception("Invalid Purchase Order Status.");
        this.setPoStatus(PoBean.Status.ORDERED, pid);
    }

    /**
     * Re-process Purchase order
     *
     * @param pid
     * @throws Exception
     */
    public void reprocessPo(int pid) throws Exception {
        PoBean po = this.getPoById(pid);
        if (po == null) throw new Exception("Purchase Order doesn't exist!");
        if (po.getStatus() != PoBean.Status.DENIED) throw new Exception("Invalid Purchase Order Status.");
        this.setPoStatus(PoBean.Status.PROCESSED, pid);
    }

    /**
     * Rate a Purchase order item ( give it rating and review )
     *
     * @param pid
     * @param bid
     * @param rating
     * @param review
     * @throws Exception
     */
    public void ratePoItem(int pid, String bid, int rating, String review) throws Exception {
        String query = "UPDATE POItem SET rating = ?, review = ? WHERE id = ? AND bid = ?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, rating);
            p.setString(2, review);
            p.setInt(3, pid);
            p.setString(4, bid);
            p.executeUpdate();
        }
    }

    /**
     * Get purchases stats (List of User with their total spent per zip code)
     *
     * @return
     * @throws Exception
     */
    public List<Pair<Integer, String>> getPurchaseStats() throws Exception {
        String query = "SELECT p.uid, a.zip, SUM(pi.price) AS total_spent FROM POItem pi INNER JOIN PO p ON pi.id=p.id INNER JOIN Address a ON p.address=a.id WHERE p.status='ORDERED' GROUP BY p.uid, a.zip";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query);
             ResultSet r = p.executeQuery()) {
            List<Pair<Integer, String>> results = new LinkedList<>();
            while (r.next()) {
                String uid = r.getString("uid");
                String zip = r.getString("zip");
                int totalSpent = r.getInt("total_spent");
                Pair<Integer, String> data = new Pair<Integer, String>(totalSpent, zip);
                results.add(data);
            }
            r.close();
            return results;
        }
    }
    /*********************/
    /** Private Methods **/
    /*********************/

    /**
     * Set a Purchase Order Status
     *
     * @param status
     * @param pid
     * @throws Exception
     */
    private void setPoStatus(PoBean.Status status, int pid) throws Exception {
        String query = "UPDATE PO SET status = ? WHERE id = ?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, status.toString());
            p.setInt(2, pid);
            p.executeUpdate();
        }
    }

    public AnonPOBean getOrdersByBookId(String bookId) throws Exception {
        String query =
                "SELECT price, quantity, rating, review, addr.province, addr.zip " +
                        "FROM POItem pitem INNER JOIN PO p on pitem.id = p.id " +
                        "INNER JOIN Address addr on p.address=addr.id WHERE pitem.bid = ?";
        try (Connection conn = this.ds.getConnection();
             PreparedStatement p = conn.prepareStatement(query)){
            p.setString(1, bookId);
            List<AnonPOBean.AnonPOItem> out = new LinkedList<>();
            try(ResultSet r = p.executeQuery()){
                while(r.next()) {
                    out.add(new AnonPOBean.AnonPOItem(r.getInt(1),
                            r.getInt(2),
                            r.getInt(3),
                            r.getString(4),
                            r.getString(5),
                            r.getString(6)
                    ));
                }

            }
            return new AnonPOBean(bookId, out);
        }
    }
}
