package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.BookReviewBean;
import listener.NewPurchase;
import model.BookStoreModel;
import model.BookStoreUtil;
import model.UserModel;

/**
 * Servlet implementation class AjaxServlet
 */
@WebServlet({"/Ajax","/Ajax/Analytics","/Ajax/Review"})
@MultipartConfig
public class AjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AjaxServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		JsonObject json = null;
        try {
            System.out.println("handling Ajax request!");
            if (path.equals("/Ajax/Analytics")) {
                // get popular books list for ajax request
                if (!UserModel.getOrSetUser(request).isAdmin())
                    throw new Exception("Permission Denied!");
                @SuppressWarnings("unchecked")
                List<NewPurchase.Book> popularBooks = (List<NewPurchase.Book>) request.getServletContext()
                        .getAttribute("popularBooks");
                if (popularBooks == null) {
                    popularBooks = new ArrayList<NewPurchase.Book>();
                    request.getServletContext().setAttribute("popularBooks", popularBooks);
                }
                json = BookStoreUtil.constructAjaxResponse(popularBooks);
            } else if (path.equals("/Ajax/Review")) {
                // get book's reviews for ajax request
                String bid = request.getParameter("bid");
                System.out.println("receive book review fetch request bid=" + bid);
                if (bid == null || bid.isEmpty())
                    throw new Exception("Must provide Book id.");
				List<BookReviewBean> reviews = BookStoreModel.getInstance().retrieveBookReviewsByBookId(bid);
                System.out.println("review: " + reviews);
                json = BookStoreUtil.constructAjaxResponse(reviews);
                System.out.println(json.toString());
            } else {
            		throw new Exception("Unknown Operation!");
            }
        } catch (Exception e) {
            json = BookStoreUtil.constructAjaxErrorResponse(e.getMessage());
        }
        if (json != null) {
            System.out.println(json.toString());
            response.getWriter().write(json.toString());
            return;
        }
	}

}
