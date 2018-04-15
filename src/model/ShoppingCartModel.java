package model;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import DAO.BookDAO;
import bean.BookBean;
import bean.ShoppingCartItemBean;

public class ShoppingCartModel {

	public ShoppingCartModel() {
		// TODO Auto-generated constructor stub
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
		BookDAO bookDAO = new BookDAO();
		BookBean book = bookDAO.getBookById(bid);
		ShoppingCartItemBean item = new ShoppingCartItemBean(book, quantity);
		return item;
	}
	
	/**
	 * Get my cart
	 * @return
	 */
	public Map<String, ShoppingCartItemBean> getMyCart(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Map<String, ShoppingCartItemBean> cart = (Map<String, ShoppingCartItemBean>) session.getAttribute("cart");
		if (cart == null) {
			cart = new HashMap<String, ShoppingCartItemBean>();
			session.setAttribute("cart", cart);
		}
		return cart;
	}
	
	/**
	 * Update shopping cart and recalculate the total price
	 * @param cart
	 */
	public void updateCart(Map<String, ShoppingCartItemBean> cart, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("cart", cart);
		double price = 0.0;
		for(ShoppingCartItemBean item : cart.values()) {
			price += item.getPrice();
		}
		session.setAttribute("cartPrice", price);
	}
	
	/**
	 * Add book with selected quantity to my cart. quantity will accumulate if book already in the cart.
	 * @param bid
	 * @param quantityStr
	 * @throws Exception
	 */
	public void addToCart(String bid, String quantityStr, HttpServletRequest request) throws Exception {
		ShoppingCartItemBean item = this.retrieveShoppingCartItem(bid, quantityStr);
		Map<String, ShoppingCartItemBean> cart = this.getMyCart(request);
		if(cart.containsKey(bid)) {
			cart.get(bid).setQuantity(cart.get(bid).getQuantity()+item.getQuantity());
		} else {
			cart.put(bid, item);
		}
		this.updateCart(cart, request);
	}
	
	/**
	 * Remove selected book from my cart
	 * @param bid
	 * @throws Exception
	 */
	public void removeFromCart(String bid, HttpServletRequest request) throws Exception {
		Map<String, ShoppingCartItemBean> cart = this.getMyCart(request);
		if(cart.containsKey(bid)) {
			cart.remove(bid);
			this.updateCart(cart, request);
		} else {
			throw new Exception("Book is not in Cart.");
		}
	}
	
	/**
	 * Update one shopping cart book quantity, quantity must be greater than 1.
	 * @param bid
	 * @param quantityStr
	 * @throws Exception
	 */
	public void updateCartItemQuantity(String bid, String quantityStr, HttpServletRequest request) throws Exception {
		ShoppingCartItemBean item = this.retrieveShoppingCartItem(bid, quantityStr);
		Map<String, ShoppingCartItemBean> cart = this.getMyCart(request);
		if(cart.containsKey(bid)) {
			cart.get(bid).setQuantity(item.getQuantity());
			this.updateCart(cart, request);
		} else {
			throw new Exception("Book is not in Cart.");
		}
	}
}
