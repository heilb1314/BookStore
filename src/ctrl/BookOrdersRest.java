package ctrl;

import bean.AnonPOBean;
import bean.UserBean;
import model.BookStoreModel;
import model.UserModel;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("BookOrders")
public class BookOrdersRest {
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookOrdersRest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Path("/getOrdersByBookId")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getOrdersByBookId(@QueryParam("bookId") String bookId, @Context HttpServletRequest request) throws Exception {
		UserBean user = UserModel.getOrSetUser(request);
		if(bookId == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} else if (user.isCustomer() || user.isVisitor()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} else {
			try {
				AnonPOBean anonPurchaseOrder =
						BookStoreModel.getInstance().getPoDAO().getOrdersByBookId(bookId);
				return Response.ok(anonPurchaseOrder).build();
			} catch (Exception e) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
    }
}
