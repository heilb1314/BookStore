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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		String path = request.getRequestURI().substring(request.getContextPath().length());
		System.out.println(path);
		
		if(path.equals("/Start")) {
			String bookQueryCategory = request.getParameter("category");
			String bookQueryTitle = request.getParameter("title");
			List<BookBean> books = null;
			try {
				if (bookQueryTitle != null) {
					books = this.getModel().retrieveBooksByTitle(bookQueryTitle);
				} else {
					BookBean.Category c = BookBean.Category.getCategory(bookQueryCategory);
					books = this.getModel().retrieveBooksByCategory(c);
					System.out.println(books.toString());
				}
				// // Object to XML
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("books", books);

			request.getRequestDispatcher("/Home.jspx").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
}
