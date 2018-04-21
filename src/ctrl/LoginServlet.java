package ctrl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BookStoreModel;
import model.SessionAttributeManager;
import model.UserModel;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/Login.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
	    		if (UserModel.isLoggedIn(request)) throw new Exception("Already logged in");
	        BookStoreModel.getInstance().getUserModel().loginUser(username, password, request);
	        SessionAttributeManager.setSuccessMessage("Welcome back " + username, request);
	        response.sendRedirect("/bookStore/Home");
	    } catch (Exception e) {
	        e.printStackTrace();
	        SessionAttributeManager.setErrorMessage(e.getMessage(), request);
	        SessionAttributeManager.addCarryForwardAttribute("username", username, request);
	        response.sendRedirect("/bookStore/Login");
	    }
	}

}
