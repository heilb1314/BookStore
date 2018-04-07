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
			this.getModel().getUserModel().logout(request);
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
					this.getModel().getCartModel().addToCart(bid, quantityStr, request);
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
					this.getModel().getCartModel().removeFromCart(bid, request);
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
					this.getModel().getCartModel().updateCartItemQuantity(bid, quantityStr, request);
				} catch(Exception e) {
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				this.handleGetShoppingCartPageRequest(request,response);
				return;
			}
		} else if (path.equals("/Start/Register") && submit != null) {
			String username = request.getParameter("username");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");
			String password = request.getParameter("password");
			String verifiedPassword = request.getParameter("verifiedPassword");
			try {
				this.getModel().getUserModel().registerUser(username, firstname, lastname, password, verifiedPassword, request);
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
				this.getModel().getUserModel().loginUser(username, password, request);
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
				// process payment
				String street = request.getParameter("street");
				String province = request.getParameter("province");
				String country = request.getParameter("country");
				String zip = request.getParameter("zip");
				String phone = request.getParameter("phone");
				String sameAddress = request.getParameter("sameAddress");
				String bstreet = request.getParameter("bstreet");
				String bprovince = request.getParameter("bprovince");
				String bcountry = request.getParameter("bcountry");
				String bzip = request.getParameter("bzip");
				System.out.println("sameAddress="+sameAddress);
				if(sameAddress!=null) {
					bstreet = street;
					bprovince = province;
					bcountry = country;
					bzip = zip;
				}
				String firstname = request.getParameter("firstname");
				String lastname = request.getParameter("lastname");
				String cardNumber = request.getParameter("cardnumber");
				String month = request.getParameter("month");
				String year = request.getParameter("year");
				String cvc = request.getParameter("cvc");
				// debug
				System.out.println("Shipping Info:");
				System.out.println(String.format("street=%s  province=%s  country=%s  zip=%s  phone=%s  sameAddress=%s", street,province,country,zip,phone,sameAddress));
				System.out.println("Billing Info:");
				System.out.println(String.format("street=%s  province=%s  country=%s  zip=%s", bstreet,bprovince,bcountry,bzip));
				System.out.println("Credit Card Info:");
				System.out.println(String.format("firstname=%s  lastname=%s  cardnumber=%s  month=%s  year=%s  cvc/cvv=%s", firstname,lastname,cardNumber,month,year,cvc));
				
				try {
					this.getModel().processPo(street, province, country, zip, phone, bstreet, bprovince, bcountry, bzip, firstname, lastname, cardNumber, month, year, cvc, request);
					request.setAttribute("poSuccessMessage", "Order Successfully Completed!");
					System.out.println("Order Successfully Completed!");
					this.getModel().getCartModel().updateCart(new HashMap<String, ShoppingCartItemBean>(), request);
				} catch (Exception e) {
					e.printStackTrace();
					this.savePurchaseFormInfo(street, province, country, zip, phone, sameAddress, bstreet, bprovince, bcountry, bzip, firstname, lastname, request);
					request.setAttribute("poErrorMessage", e.getMessage());
				}
				request.getRequestDispatcher("/Payment.jspx").forward(request, response);
				return;
			} else if (submit.equals("Go To Payment")) {
				// go to payment page
				request.getRequestDispatcher("/Payment.jspx").forward(request, response);
				return;
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
	 * set request attributes for shipping billing info and first last name for purchase form
	 * @param street
	 * @param province
	 * @param country
	 * @param zip
	 * @param phone
	 * @param sameAddress
	 * @param bstreet
	 * @param bprovince
	 * @param bcountry
	 * @param bzip
	 * @param firstname
	 * @param lastname
	 * @param request
	 */
	private void savePurchaseFormInfo(String street, String province, String country, String zip, String phone, String sameAddress, String bstreet, String bprovince, String bcountry, String bzip, String firstname, String lastname, HttpServletRequest request) {
		request.setAttribute("street", street);
		request.setAttribute("province", province);
		request.setAttribute("country", country);
		request.setAttribute("zip", zip);
		request.setAttribute("phone", phone);
		request.setAttribute("sameAddress", sameAddress);
		request.setAttribute("bstreet", bstreet);
		request.setAttribute("bprovince", bprovince);
		request.setAttribute("bcountry", bcountry);
		request.setAttribute("bzip", bzip);
		request.setAttribute("firstname", firstname);
		request.setAttribute("lastname", lastname);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("books", books);

		request.getRequestDispatcher("/Home.jspx").forward(request, response);
	}
	
	public void handleGetShoppingCartPageRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<ShoppingCartItemBean> books = new ArrayList<ShoppingCartItemBean>(this.getModel().getCartModel().getMyCart(request).values());
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
