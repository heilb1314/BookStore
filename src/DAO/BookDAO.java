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

    /**
     * Get a book by id
     *
     * @param bid
     * @return
     * @throws Exception
     */
    public BookBean getBookById(String bid) throws Exception {
        String query = "SELECT * FROM Book WHERE bid = ?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {

            p.setString(1, bid);
            ResultSet r = p.executeQuery();
            BookBean book = null;
            if (r.next()) {
                book = this.parseBookBean(r);
            }
            r.close();
            p.close();
            con.close();
            if (book == null)
                throw new Exception("No book with bid: " + bid);
            return book;
        }
    }

    /**
     * Get a list of books by title query
     *
     * @param title
     * @return
     * @throws Exception
     */
    public List<BookBean> getListOfBooksByTitle(String title) throws Exception {
        String query = "SELECT * FROM Book WHERE title LIKE ? ORDER BY title";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {

            p.setString(1, title != null ? "%" + title + "%" : "%");
            ResultSet r = p.executeQuery();
            List<BookBean> rv = this.parseResultSetToList(r);
            r.close();
            p.close();
            con.close();
            return rv;
        }
    }

    /**
     * Get a list of books by category
     *
     * @param category
     * @return
     * @throws Exception
     */
    public List<BookBean> getListOfBooksByCategory(BookBean.Category category) throws Exception {
        String query = "";
        if (category == null) {
            query = "SELECT * FROM Book";
        } else {
            query = "SELECT * FROM Book WHERE category = ?";
        }
        query += " ORDER BY title";
        try (
                Connection con = this.ds.getConnection();
                PreparedStatement p = con.prepareStatement(query)) {
            if (category != null) {
                p.setString(1, category.toString());
            }
            ResultSet r = p.executeQuery();
            List<BookBean> rv = this.parseResultSetToList(r);
            r.close();
            p.close();
            con.close();
            return rv;
        }
    }

    /**
     * Parse book from ResultSet
     *
     * @param r
     * @return
     * @throws Exception
     */
    public BookBean parseBookBean(ResultSet r) throws Exception {
        String bid = r.getString("BID");
        String title = r.getString("TITLE");
        int price = r.getInt("PRICE");
        float rating = r.getFloat("rating");
        BookBean.Category category = BookBean.Category.getCategory(r.getString("CATEGORY"));
        String description = r.getString("DESCRIPTION");
        BookBean book = new BookBean(bid, title, price, category, rating, description);
        return book;
    }

    /**
     * Recalculate and update a book rating
     *
     * @param bid
     * @throws Exception
     */
    public void updateBookRating(String bid) throws Exception {
        BookBean book = this.getBookById(bid);
        if (book == null) throw new Exception("No such book!");
        String query = "UPDATE book b SET b.rating=(SELECT ROUND(AVG(poi.rating),1) FROM POItem poi INNER JOIN PO p ON poi.id=p.id WHERE poi.bid=? AND poi.rating>0 AND p.status='ORDERED') WHERE bid=?";
        try (
                Connection con = this.ds.getConnection();
                PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, bid);
            p.setString(2, bid);
            p.executeUpdate();
            p.close();
            con.close();
        }
    }

    private List<BookBean> parseResultSetToList(ResultSet r) throws Exception {
        List<BookBean> rv = new ArrayList<BookBean>();
        while (r.next()) {
            BookBean book = parseBookBean(r);
            rv.add(book);
        }
        return rv;
    }
}