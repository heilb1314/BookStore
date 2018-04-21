package ctrl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.BookBean;
import model.BookStoreModel;
import model.SessionAttributeManager;

/**
 * Servlet implementation class HomeServlet
 */
@WebServlet({ "/Home", "/Home/AddToCart", "/Home/SubmitReview" })
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HomeServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if(!path.equals("/Home")) {
			response.sendRedirect("/bookStore/Home");
			return;
		}
		this.handleGetHomePath(path, request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		this.handlePostHomePath(path, request, response);
	}
	
	/**
	 * Handle Get Home request
	 * @param path
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleGetHomePath(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bookQueryCategory = request.getParameter("category");
		String bookQueryTitle = request.getParameter("search");
		List<BookBean> books = null;
		try {
			if (bookQueryTitle != null) {
				books = BookStoreModel.getInstance().retrieveBooksByTitle(bookQueryTitle);
			} else {
				BookBean.Category c = BookBean.Category.getCategory(bookQueryCategory);
				books = BookStoreModel.getInstance().retrieveBooksByCategory(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("books", books);
		request.getRequestDispatcher("/Home.jspx").forward(request, response);
	}

	/**
	 * Handle all Home Post request
	 *
	 * @param route
	 * @param submit
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handlePostHomePath(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Home page POST
		switch (path) {
		case "/Home/AddToCart":
			this.handlePostAddToCart(request, response);
			break;
		case "/Home/SubmitReview":
			this.handlePostBookReview(request, response);
			break;
		}
		response.sendRedirect("/bookStore/Home");
	}
	
	/**
	 * Handle Post add to cart request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handlePostAddToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bid = request.getParameter("bid");
		String quantityStr = request.getParameter("quantity");
		try {
			BookStoreModel.getInstance().getCartModel().addToCart(bid, quantityStr, request);
		} catch (Exception e) {
			e.printStackTrace();
			SessionAttributeManager.setErrorMessage(e.getMessage(), request);
		}
	}

	/**
	 * Handle Post book review request
	 *
	 * @param route
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handlePostBookReview(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bid = request.getParameter("bid");
		String rating = request.getParameter("rating");
		String review = request.getParameter("review");
		try {
			BookStoreModel.getInstance().rateBook(bid, rating, review, request);
			SessionAttributeManager.setSuccessMessage("Review successfully submitted!", request);
		} catch (Exception e) {
			SessionAttributeManager.setErrorMessage(e.getMessage(), request);
		}
	}
}
