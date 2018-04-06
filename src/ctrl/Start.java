package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.BookBean;
import bean.ShoppingCartItemBean;
import bean.UserBean;
import model.BookStoreModel;

/**
 * Servlet implementation class Start
 */
@WebServlet({ "/Start", "/Start/*" })
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Start() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    this.getModel();
	  }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String path = request.getRequestURI().substring(request.getContextPath().length());
		System.out.println(path);
		
		if(path.equals("/Start")) {
			this.handleGetHomePageRequest(request, response);
		} else if (path.equals("/Start/Cart")) {
			this.handleGetShoppingCartPageRequest(request,response);
		} else if (path.equals("/Start/Register")) {
			this.handleGetRegisterPageRequest(request, response);
		} else if (path.equals("/Start/Login")) {
			this.handleGetLoginPageRequest(request, response);
		} else if (path.equals("/Start/Logout")) {
			this.getModel().logout(request);
			this.handleGetHomePageRequest(request, response);
		}
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		String submit = request.getParameter("submit");
		// debug
		System.out.println(String.format("Path=%s  submit=%s", path,submit));
		
		if (path.equals("/Start") && submit != null) {
			// Home page POST
			if(submit.equals("Add To Cart")) {
				String bid = request.getParameter("bid");
				String quantityStr = request.getParameter("quantity");
				// debug
				System.out.println("Adding bid="+bid+"&quantity="+quantityStr+" to cart...");
				try {
					this.addToCart(bid, quantityStr, request);
				} catch(Exception e) {
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				this.handleGetHomePageRequest(request, response);
				return;
			}
		} else if (path.equals("/Start/Cart") && submit != null) {
			// Shopping Cart page POST
			if (submit.equals("Remove")) {
				// Remove a book from cart
				String bid = request.getParameter("bid");
				// debug
				System.out.println("Removing "+bid+" from cart...");
				try {
					this.removeFromCart(bid, request);
				} catch(Exception e) {
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				this.handleGetShoppingCartPageRequest(request,response);
				return;
			} else if (submit.equals("Update")) {
				// update a book's quantity
				String bid = request.getParameter("bid");
				String quantityStr = request.getParameter("quantity");
				// debug
				System.out.println("Updating bid="+bid+"&quantity="+quantityStr+" ...");
				try {
					this.updateCartItemQuantity(bid, quantityStr, request);
				} catch(Exception e) {
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				this.handleGetShoppingCartPageRequest(request,response);
				return;
			} else if (submit.equals("Go To Payment")) {
				// go to payment page
				
				request.getRequestDispatcher("/Payment.jspx").forward(request, response);
				return;
			}
		} else if (path.equals("/Start/Register") && submit != null) {
			String username = request.getParameter("username");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");
			String password = request.getParameter("password");
			String verifiedPassword = request.getParameter("verifiedPassword");
			try {
				this.getModel().registerUser(username, firstname, lastname, password, verifiedPassword, request);
				request.setAttribute("successMessage", "User successfully registered!");
			} catch(Exception e) {
				e.printStackTrace();
				// setup error message and store form info
				request.setAttribute("errorMessage", e.getMessage());
				request.setAttribute("username", username);
				request.setAttribute("firstname", firstname);
				request.setAttribute("lastname", lastname);
			}
			this.handleGetRegisterPageRequest(request,response);
			return;
			
		} else if (path.equals("/Start/Login") && submit != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			try {
				this.getModel().loginUser(username, password, request);
				request.setAttribute("successMessage", "User successfully logged in!");
			} catch(Exception e) {
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
				request.setAttribute("username", username);
			}
			this.handleGetLoginPageRequest(request, response);
			return;
		} else if (path.equals("/Start/Payment") && submit != null) {
			if (submit.equals("Confirm Order")) {
				// TODO: confirm order
			}
		}
		
		doGet(request, response);
	}

	/**
	 * Get Book store model
	 * 
	 * @return
	 */
	private BookStoreModel getModel() {
		BookStoreModel model = (BookStoreModel) this.getServletContext().getAttribute("model");
		if (model == null) {
			model = new BookStoreModel();
			this.getServletContext().setAttribute("model", model);
		}
		return model;
	}
	
	/**
	 * Get Session User
	 * @param request
	 * @return
	 */
	private UserBean getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			UserBean user = (UserBean) session.getAttribute("user");
			return user;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get my cart
	 * @return
	 */
	private Map<String, ShoppingCartItemBean> getMyCart(HttpServletRequest request) {
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
	private void updateCart(Map<String, ShoppingCartItemBean> cart, HttpServletRequest request) {
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
	private void addToCart(String bid, String quantityStr, HttpServletRequest request) throws Exception {
		ShoppingCartItemBean item = this.getModel().retrieveShoppingCartItem(bid, quantityStr);
		Map<String, ShoppingCartItemBean> cart = this.getMyCart(request);
		if(cart.containsKey(bid)) {
			cart.get(bid).setQuantity(cart.get(bid).getQuantity()+item.getQuantity());
		} else {
			cart.put(bid, item);
			this.updateCart(cart, request);
		}
	}
	
	/**
	 * Remove selected book from my cart
	 * @param bid
	 * @throws Exception
	 */
	private void removeFromCart(String bid, HttpServletRequest request) throws Exception {
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
	private void updateCartItemQuantity(String bid, String quantityStr, HttpServletRequest request) throws Exception {
		ShoppingCartItemBean item = this.getModel().retrieveShoppingCartItem(bid, quantityStr);
		Map<String, ShoppingCartItemBean> cart = this.getMyCart(request);
		if(cart.containsKey(bid)) {
			cart.get(bid).setQuantity(item.getQuantity());
			this.updateCart(cart, request);
		} else {
			throw new Exception("Book is not in Cart.");
		}
	}
	
	public void handleGetHomePageRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bookQueryCategory = request.getParameter("category");
		String bookQueryTitle = request.getParameter("search");
		List<BookBean> books = null;
		try {
			if (bookQueryTitle != null) {
				books = this.getModel().retrieveBooksByTitle(bookQueryTitle);
			} else {
				BookBean.Category c = BookBean.Category.getCategory(bookQueryCategory);
				books = this.getModel().retrieveBooksByCategory(c);
				System.out.println(books.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("books", books);

		request.getRequestDispatcher("/Home.jspx").forward(request, response);
	}
	
	public void handleGetShoppingCartPageRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<ShoppingCartItemBean> books = new ArrayList<ShoppingCartItemBean>(this.getMyCart(request).values());
		request.setAttribute("books", books);
		request.getRequestDispatcher("/ShoppingCart.jspx").forward(request, response);
	}
	
	private void handleGetRegisterPageRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/Register.jspx").forward(request, response);
	}
	
	private void handleGetLoginPageRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/Login.jspx").forward(request, response);
	}
	
}



//// Object to XML
// JAXBContext jaxbContext = JAXBContext.newInstance(BookListWrapper.class); //
// instantiate a context
// Marshaller marshaller = jaxbContext.createMarshaller(); // create a
// marshaller
// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
// StringWriter sw = new StringWriter(); // standard IO
// sw.write("\n");
// model.BookListWrapper wrapper = new model.BookListWrapper(c, books);
// marshaller.marshal(wrapper, new StreamResult(sw));

// System.out.println(sw.toString()); // for debugging
