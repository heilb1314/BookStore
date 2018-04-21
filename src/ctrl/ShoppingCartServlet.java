package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.ShoppingCartItemBean;
import model.BookStoreModel;
import model.SessionAttributeManager;
import model.UserModel;

/**
 * Servlet implementation class ShoppingCartServlet
 */
@WebServlet({"/Cart","/Cart/Update","/Cart/Remove","/Cart/GoToPayment"})
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShoppingCartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if(!path.equals("/Cart")) {
			response.sendRedirect("/bookStore/Cart");
			return;
		}
		List<ShoppingCartItemBean> books = new ArrayList<ShoppingCartItemBean>(
        		BookStoreModel.getInstance().getCartModel().getMyCart(request).values());
        request.setAttribute("books", books);
        request.getRequestDispatcher("/ShoppingCart.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		switch (path) {
		case "/Cart/Remove":
			this.handlePostRemoveFromCart(request, response);
			break;
		case "/Cart/Update":
			this.handlePostUpdateCart(request, response);
			break;
		case "/Cart/GoToPayment":
			if(UserModel.isLoggedIn(request)) {
				response.sendRedirect("/bookStore/Payment");
				return;
			} else {
				SessionAttributeManager.setErrorMessage("Please login first!", request);
			}
			break;
		}
		response.sendRedirect("/bookStore/Cart");
	}
	
	private void handlePostRemoveFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Remove a book from cart
        String bid = request.getParameter("bid");
        try {
			BookStoreModel.getInstance().getCartModel().removeFromCart(bid, request);
            SessionAttributeManager.setSuccessMessage("Item successfully removed!", request);
        } catch (Exception e) {
            e.printStackTrace();
            SessionAttributeManager.setErrorMessage(e.getMessage(), request);
        }
	}
	
	private void handlePostUpdateCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// update a book's quantity
        String bid = request.getParameter("bid");
        String quantityStr = request.getParameter("quantity");
        try {
			BookStoreModel.getInstance().getCartModel().updateCartItemQuantity(bid, quantityStr, request);
            SessionAttributeManager.setSuccessMessage("Item successfully updated!", request);
        } catch (Exception e) {
            e.printStackTrace();
            SessionAttributeManager.setErrorMessage(e.getMessage(), request);
        }
	}

}
