package model;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAO.BookDAO;
import DAO.PoDAO;
import DAO.UserDAO;
import bean.BookBean;
import bean.PoBean;
import bean.PoItemBean;
import bean.ShoppingCartItemBean;
import bean.UserBean;
import bean.UserBean.UserType;

public class BookStoreModel {
	
	private BookDAO bookDAO;
	private PoDAO poDAO;
	private UserDAO userDAO;

	public BookStoreModel() {
		try {
			this.bookDAO = new BookDAO();
			this.poDAO = new PoDAO();
			this.userDAO = new UserDAO();
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
	
	/**
	 * Register a user
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param password
	 * @param verifiedPassword
	 * @param request
	 * @throws Exception
	 */
	public void registerUser(String username, String firstname, String lastname, String password, String verifiedPassword, HttpServletRequest request) throws Exception {
		if(this.loggedIn(request)) throw new Exception("Please Log out first.");
		if(!password.equals(verifiedPassword)) throw new Exception("Passwords are not matched.");
		System.out.println(String.format("username=%s, firstname=%s, lastname=%s, password=%s, verifiedPassword=%s", username, firstname, lastname, password, verifiedPassword));
		this.userDAO.signup(username, password, firstname, lastname, UserType.VISITOR.toString());
	}
	
	/**
	 * Login a user
	 * @param username
	 * @param password
	 * @param request
	 * @throws Exception
	 */
	public void loginUser(String username, String password, HttpServletRequest request) throws Exception {
		if(this.loggedIn(request)) throw new Exception("Please Log out first.");
		UserBean user = this.userDAO.login(username, password);
		request.getSession().setAttribute("user", user);
	}
	
	/**
	 * Check if user with given username is logged in or not.
	 * @param username
	 * @param request
	 * @return
	 */
	public boolean loggedIn(HttpServletRequest request) {
		try {
			UserBean user = (UserBean) request.getSession().getAttribute("user");
			if(user != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Logout
	 * @param request
	 */
	public void logout(HttpServletRequest request) {
		if(this.loggedIn(request)) {
			request.getSession().setAttribute("user", null);
		}
	}

}
