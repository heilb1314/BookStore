package ctrl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.VisitEventBean;
import model.BookStoreModel;
import model.SessionAttributeManager;
import model.UserModel;

/**
 * Servlet implementation class PaymentServlet
 */
@WebServlet("/Payment")
public class PaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!UserModel.isLoggedIn(request)) {
            request.setAttribute("errorMessage", "Please Login first!");
        }
        request.getRequestDispatcher("/Payment.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
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
        if (sameAddress != null) {
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

        try {
            BookStoreModel model = BookStoreModel.getInstance();
            model.processPo(street, province, country, zip, phone, bstreet, bprovince, bcountry, bzip,
                    firstname, lastname, cardNumber, month, year, cvc, request);
            SessionAttributeManager.setSuccessMessage("Order Successfully Completed!", request);
            List<VisitEventBean> visitEvents = model.retrieveVisitEventsFromShoppingCart(request);
            model.addVisitEvents(visitEvents);
            SessionAttributeManager.addCarryForwardAttribute("newPurchase", visitEvents, request);
            model.getCartModel().emptyCart(request);
        } catch (Exception e) {
            e.printStackTrace();
            this.savePurchaseFormInfo(street, province, country, zip, phone, sameAddress, bstreet, bprovince,
                    bcountry, bzip, firstname, lastname, request);
            SessionAttributeManager.setErrorMessage(e.getMessage(), request);
        }
        
        response.sendRedirect("/bookStore/Payment");
	}
	
    /**
     * set request attributes for shipping billing info and first last name for
     * purchase form
     *
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
    private void savePurchaseFormInfo(String street, String province, String country, String zip, String phone,
                                      String sameAddress, String bstreet, String bprovince, String bcountry, String bzip, String firstname,
                                      String lastname, HttpServletRequest request) {
    		SessionAttributeManager.addCarryForwardAttribute("street", street, request);
        SessionAttributeManager.addCarryForwardAttribute("province", province, request);
        SessionAttributeManager.addCarryForwardAttribute("country", country, request);
        SessionAttributeManager.addCarryForwardAttribute("zip", zip, request);
        SessionAttributeManager.addCarryForwardAttribute("phone", phone, request);
        SessionAttributeManager.addCarryForwardAttribute("sameAddress", sameAddress, request);
        SessionAttributeManager.addCarryForwardAttribute("bstreet", bstreet, request);
        SessionAttributeManager.addCarryForwardAttribute("bprovince", bprovince, request);
        SessionAttributeManager.addCarryForwardAttribute("bcountry", bcountry, request);
        SessionAttributeManager.addCarryForwardAttribute("bzip", bzip, request);
        SessionAttributeManager.addCarryForwardAttribute("firstname", firstname, request);
        SessionAttributeManager.addCarryForwardAttribute("lastname", lastname, request);
    }

}
