package DAO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.Book;

public class ProductCatalog {
    private DataSource dataSource;
    private static int id;
    private static String title;
    private static String price;
    private static String category;
    private static String author;

    public ProductCatalog() throws ClassNotFoundException {
        try {
            dataSource = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Book> runQuery(String query) throws SQLException {

        Map<String, Book> rv = new HashMap<String, Book>();

        Connection connection;
        try {

            connection = this.dataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet result = p.executeQuery();
            int counter = 0;

            while (result.next()) {
                id = result.getInt("id");
                title = result.getString("TITLE");
                price = result.getString("PRICE");
                category = result.getString("CATEGORY");
                author = result.getString("AUTHOR");

                Book b1 = new Book(id, title, author, price, category);
                b1.setBid(id);
                String counterString = Integer.toString(counter);

                rv.put(Integer.toString(id), b1);
                counter++;
            }

            //close connection to DB
            result.close();
            p.close();
            connection.close();
            return rv;
        } catch (SQLException e) {
            System.out.println("Error in BookDAO");
            return null;
        }

    }

    public String getProductInfo(String id) throws Exception {
        int intId = Integer.parseInt(id);
        final String query = "select * from book where id =" + intId;
        Map<String, Book> res = runQuery(query);
        if (res.size() == 0) {
            return "<html lang=\"en\"><body><h1> No Book by this product Id!</h1></body></html>";
        }

        Book b1 = res.get(id);
        b1.setBid(intId);
        System.out.println(b1.getTitle());
        return "<h1>" + b1.getTitle() + "</h1>"
                + "<h2>" + b1.getAuthor() + "</h2>"
                + "<h3>" + b1.getPrice() + "</h3>"
                + "<h4>" + b1.getCategory() + "</h4>";
    }
}
