package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAO.AddressDAO;
import DAO.BookDAO;
import DAO.PoDAO;
import bean.AddressBean;
import bean.BookBean;
import bean.PoBean;
import bean.ShoppingCartItemBean;
import bean.UserBean;
import bean.UserBean.UserType;

public class BookStoreModel {
	
	private BookDAO bookDAO;
	private PoDAO poDAO;
	private AddressDAO addressDAO;
	private ShoppingCartModel cartModel;
	private UserModel userModel;

	public BookStoreModel() {
		try {
			this.bookDAO = new BookDAO();
			this.poDAO = new PoDAO();
			this.addressDAO = new AddressDAO();
			this.cartModel = new ShoppingCartModel();
			this.userModel = new UserModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserModel getUserModel() {
		return this.userModel;
	}
	
	public ShoppingCartModel getCartModel() {
		return this.cartModel;
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
	
	public PoBean retrievePurchaseOrderById(int id) throws Exception {
		return this.poDAO.getPoById(id);
	}
	
	/**
	 * Add an address
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @return new address id
	 * @throws Exception
	 */
	public int addAddress(String street, String province, String country, String zip, String phone) throws Exception {
		this.validateAddress(street, province, country, zip);
		return addressDAO.addAddress(street, province, country, zip, phone);
	}
	
	/**
	 * Update an existing address
	 * @param id
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @throws Exception
	 */
	public void updateAddress(int id, String street, String province, String country, String zip, String phone) throws Exception {
		this.validateAddress(street, province, country, zip);
		addressDAO.updateAddress(id, street, province, country, zip, phone);
	}
	
	/**
	 * Get an address
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AddressBean getAddress(int id) throws Exception {
		return addressDAO.getAddressById(id);
	}
	
	/**
	 * Process purchase order
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @param bstreet
	 * @param bprovince
	 * @param bcountry
	 * @param bzip
	 * @param firstname
	 * @param lastname
	 * @param cardNumber
	 * @param month
	 * @param year
	 * @param cvc
	 * @param request
	 * @throws Exception
	 */
	public void processPo(String street, String province, String country, String zip, String phone, String bstreet, String bprovince, String bcountry, String bzip, String firstname, String lastname, String cardNumber, String month, String year, String cvc, HttpServletRequest request) throws Exception {
		UserBean user = this.getUserModel().getUser(request);
		if(user==null) throw new Exception("Please log in first!");
		this.validateShoppingCart(request);
		this.validateAddress(street, province, country, zip);
		this.validatePoPayment(bstreet, bprovince, bcountry, bzip, firstname, lastname, cardNumber, month, year, cvc);
		int addressId = this.addAddress(street, province, country, zip, phone);
		int pid = this.poDAO.processPo(addressId, user.getId());
		List<ShoppingCartItemBean> cartItems = new ArrayList<ShoppingCartItemBean>(this.getCartModel().getMyCart(request).values());
		this.poDAO.addPoItems(cartItems, pid);
		this.orderPo(pid, request);
		if(user.getUserType()==UserType.VISITOR) {
			this.getUserModel().updateVisitorToCustomer(request);
		}
	}
	
	/**
	 * Confirm Purchase Order / Deny every 3rd request
	 * @param pid
	 * @param request
	 * @throws Exception
	 */
	private void orderPo(int pid, HttpServletRequest request) throws Exception {
		Integer n = (Integer) request.getSession().getAttribute("numOfPoTry");
		if (n == null) {
			n = 1;
		}
		System.out.println("num of PO Try: " + n);
		request.getSession().setAttribute("numOfPoTry", n+1);
		if (n % 3 == 0) {
			poDAO.denyPo(pid);
			throw new Exception("Credit Card Authorization Failed.");
		} else {
			poDAO.orderPo(pid);
		}
	}
	
	
	/***************/
	/* validations */
	/***************/
	
	private void validateShoppingCart(HttpServletRequest request) throws Exception {
		Map<String,ShoppingCartItemBean> cart = this.getCartModel().getMyCart(request);
		if(cart==null || cart.isEmpty()) throw new Exception("No Item to purchase!");
	}
	
	private void validatePoPayment(String street, String province, String country, String zip, String firstname, String lastname, String cardNumber, String month, String year, String cvc) throws Exception {
		this.validateAddress(street, province, country, zip);
		this.validateCreditCard(firstname, lastname, cardNumber, month, year, cvc);
	}
	
	private void validateAddress(String street, String province, String country, String zip) throws Exception {
		if (street == null || street.isEmpty()) throw new Exception("Street cannot be empty!");
		if (province == null || province.isEmpty()) throw new Exception("Province cannot be empty!");
		if (country == null || country.isEmpty()) throw new Exception("Country cannot be empty!");
		if (zip == null || zip.isEmpty()) throw new Exception("Zip cannot be empty!");
	}
	
	private void validateCreditCard(String firstname, String lastname, String cardNumber, String month, String year, String cvc) throws Exception {
		if (firstname==null || firstname.isEmpty()) 
			throw new Exception("Invalid firstname!");
		if (lastname==null || lastname.isEmpty()) 
			throw new Exception("Invalid lastname!");
		if (cardNumber==null || cardNumber.isEmpty() || cardNumber.length()<12 || cardNumber.length()>19 || !cardNumber.matches("^[0-9]+$"))
			throw new Exception("Invalid Credit Card Number!");
		int m, y;
		try {
			m = Integer.parseInt(month);
			if (m<1 || m>12) throw new Exception("Invalid Month");
		} catch(NumberFormatException e) {
			throw new Exception("Invalid Month!");
		}
		try {
			y = Integer.parseInt(year);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
			if (y<currentYear || (y==currentYear && m<currentMonth)) 
				throw new Exception("Credit Card expired!");
		} catch(NumberFormatException e) {
			throw new Exception("Invalid Month!");
		}
		if (cvc==null || cvc.length()<3 || cvc.length()>4 || !cvc.matches("^[0-9]+$"))
			throw new Exception("Invalid CVV");
	}
	
}
