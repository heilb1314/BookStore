package ctrl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.VisitEventBean;
import javafx.util.Pair;
import model.BookStoreModel;
import model.SessionAttributeManager;
import model.UserModel;

/**
 * Servlet implementation class AnalyticsServlet
 */
@WebServlet({"/Analytics","/Analytics/Statistics","/Analytics/MonthlyReport"})
public class AnalyticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnalyticsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if(!path.equals("/Analytics")) {
			response.sendRedirect("/bookStore/Analytics");
			return;
		}
		try {
            if (!UserModel.getOrSetUser(request).isAdmin())
                throw new Exception("Permission Denied!");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
        }
        request.getRequestDispatcher("/AnalyticsPage.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		switch (path) {
		case "/Analytics/Statistics":
			this.handlePostStatistics(request, response);
			break;
		case "/Analytics/MonthlyReport":
			this.handlePostMonthlyReport(request, response);
			break;
		}
		response.sendRedirect("/bookStore/Analytics");
	}
	
	private void handlePostStatistics(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
            List<Pair<Integer, String>> results = BookStoreModel.getInstance().retrieveUserPurchaseStatistics(request);
            request.getSession().setAttribute("stats", results);
        } catch (Exception e) {
            e.printStackTrace();
            SessionAttributeManager.setErrorMessage(e.getMessage(), request);
        }
	}
	
	private void handlePostMonthlyReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String month = request.getParameter("month");
			List<VisitEventBean> results = BookStoreModel.getInstance().retrieveMonthlyPurchaseStatistics(month, request);
			request.getSession().setAttribute("monthlyStats", results);
		} catch (Exception e) {
			e.printStackTrace();
			SessionAttributeManager.setErrorMessage(e.getMessage(), request);
		}
	}
	
	

}
