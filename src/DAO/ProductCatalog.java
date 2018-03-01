package DAO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.Book;

public class ProductCatalog 
{
	private DataSource dataSource;
	
	public ProductCatalog() throws ClassNotFoundException{
		try{
			dataSource = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		}catch(NamingException e){
			e.printStackTrace();
		}
	}
	
	public Map<String, Book> runQuery(String query) throws SQLException
	{
		
		Map<String, Book> rv = new HashMap<String, Book>();

		Connection connection;
		try {
			
			connection = this.dataSource.getConnection();
			PreparedStatement p = connection.prepareStatement(query);
			ResultSet result = p.executeQuery();
			int counter = 0;
			
			while (result.next()) 
			{
				int id = result.getInt("id");
				String title = result.getString("TITLE") ;
				String price = result.getString("PRICE");
				String category = result.getString("CATEGORY");
				String author = result.getString("AUTHOR");

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
		} 
		catch (SQLException e) 
		{
			System.out.println("Error in BookDAO");
			return null;
		}
		
	}
	
	public String getProductInfo(String id) throws Exception
	{
		int intId = Integer.parseInt(id);
		String query = "select * from book where id =" + intId;
		Map<String, Book> res = runQuery(query);
		if(res.size()  == 0)
		{
			return "<html lang=\"en\"><body><h1> No Book by this product Id!</h1></body></html>";
		}
		
		Book b = res.get(id);
		b.setBid(intId);
		System.out.println(b.getTitle());
		return  "<h1>" + b.getTitle() + "</h1>" 
				+ "<h2>" + b.getAuthor() + "</h2>"
				+ "<h3>" + b.getPrice() + "</h3>"
				+ "<h4>"+ b.getCategory() + "</h4>";		
	}
}
