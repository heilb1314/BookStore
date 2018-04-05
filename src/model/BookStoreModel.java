package model;

import java.util.List;
import java.util.Map;

import DAO.BookDAO;
import DAO.PoDAO;
import bean.BookBean;
import bean.PoBean;
import bean.PoItemBean;
import bean.ShoppingCartItemBean;

public class BookStoreModel {
	
	private BookDAO bookDAO;
	private PoDAO poDAO;

	public BookStoreModel() {
		try {
			this.bookDAO = new BookDAO();
			this.poDAO = new PoDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BookBean retrieveBookById(String bid) throws Exception {
		return this.bookDAO.getBookById(bid);
	}
	
	public List<BookBean> retrieveBooksByCategory(BookBean.Category category) throws Exception {
		return this.bookDAO.getListOfBooksByCategory(category);
	}
	
	public List<BookBean> retrieveBooksByTitle(String title) throws Exception {
		return this.bookDAO.getListOfBooksByTitle(title);
	}
	
	public Map<Integer, PoBean> retrievePurchaseOrders() throws Exception {
		return this.poDAO.getListOfPos();
	}
	
	public PoBean retrievePurchaseOrderById(int id) throws Exception {
		return this.poDAO.getPoById(id);
	}
	
	/**
	 * Retrieve a shopping cart item with given book id and quantity
	 * @param bid
	 * @param quantityStr
	 * @return
	 * @throws Exception
	 */
	public ShoppingCartItemBean retrieveShoppingCartItem(String bid, String quantityStr) throws Exception {
		int quantity;
		System.out.println(quantityStr);
		try {
			quantity = Integer.parseInt(quantityStr);
			if(quantity<=0) throw new Exception("Quantity must be larger than 0.");
		} catch (Exception e) {
			throw new Exception("Invalid quantity!");
		}
		BookBean book = this.retrieveBookById(bid);
		ShoppingCartItemBean item = new ShoppingCartItemBean(book, quantity);
		return item;
	}

}
