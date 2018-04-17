package ctrl;

import bean.BookBean;
import bean.UserBean;
import model.BookStoreModel;
import model.UserModel;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Servlet implementation class CatalogServlet
 */
@WebServlet("/getProductInfo")
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CatalogServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookId = request.getParameter("bookId");
        //Todo: filter
        UserBean user = UserModel.getOrSetUser(request);
        if (bookId == null) {
            response.sendError(404, "Book missing book id");
        } else if (user.isCustomer() || user.isVisitor()) {
            response.sendError(404, "Invalid credentials");
        } else {
            try {
                BookBean book = BookStoreModel.getInstance().retrieveBookById(bookId);
                JAXBContext ctx = JAXBContext.newInstance(BookBean.class);
                Marshaller m = ctx.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                response.setContentType("application/xml");
                m.marshal(book, response.getOutputStream());
            } catch (Exception e) {
                response.sendError(404, "No Such Book");
            }
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
