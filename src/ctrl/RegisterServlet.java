package ctrl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.UserBean;
import model.BookStoreModel;
import model.SessionAttributeManager;
import model.UserModel;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/Register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/Register.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String username = request.getParameter("username");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String password = request.getParameter("password");
        String verifiedPassword = request.getParameter("verifiedPassword");
        try {
	    		if(UserModel.isLoggedIn(request)) throw new Exception("Already logged in");
	    		UserBean user = BookStoreModel.getInstance().getUserModel().registerCustomerUser(username, firstname,
                        lastname, password, verifiedPassword,
                        request);
            UserModel.setUser(request, user);
	        SessionAttributeManager.setSuccessMessage("User successfully registered!", request);
	    } catch (Exception e) {
	        e.printStackTrace();
	        // setup error message and store form info
	        SessionAttributeManager.setErrorMessage(e.getMessage(), request);
	        SessionAttributeManager.addCarryForwardAttribute("username", username, request);
	        SessionAttributeManager.addCarryForwardAttribute("firstname", firstname, request);
	        SessionAttributeManager.addCarryForwardAttribute("lastname", lastname, request);
	    }
	    response.sendRedirect("/bookStore/Register");
	}

}
