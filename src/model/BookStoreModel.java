package model;

import java.util.List;
import java.util.Map;

import DAO.BookDAO;
import DAO.PoDAO;
import bean.BookBean;
import bean.PoBean;

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

}
