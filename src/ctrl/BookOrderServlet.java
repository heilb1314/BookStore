package ctrl;

import bean.AnonPOBean;
import bean.UserBean;
import model.BookStoreModel;
import model.UserModel;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Servlet implementation class BookOrderServlet
 */
@WebServlet({"/getOrdersByBookId"})
public class BookOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookOrderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bookId = request.getParameter("bookId");
		UserBean user = UserModel.getOrSetUser(request);
		if(bookId == null) {
			response.sendError(404, "Missing book id to retrieve orders");
		} else if (user.isCustomer() || user.isVisitor()) {
			response.sendError(401, "Invalid credentials");
		} else {
			try {
				AnonPOBean anonPurchaseOrder =
						BookStoreModel.getInstance().getPoDAO().getOrdersByBookId(bookId);
				JAXBContext ctx = JAXBContext.newInstance(AnonPOBean.class);
				Marshaller m = ctx.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				response.setContentType("application/xml");
				m.marshal(anonPurchaseOrder, response.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(404, "No Such Book");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
