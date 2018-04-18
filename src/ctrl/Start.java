package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.*;
import javafx.util.Pair;
import listener.NewPurchase;
import model.BookStoreModel;
import model.BookStoreUtil;
import model.UserModel;

/**
 * Servlet implementation class Start
 */
@WebServlet({"/Start", "/Start/*"})
@MultipartConfig
public class Start extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String successMessage = "";
    private String errorMessage = "";
    private Map<String, Object> requestAttributes = new HashMap<>();

    public Start() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // set message attribute
        if (this.errorMessage != null && !this.errorMessage.isEmpty()) {
            request.setAttribute("errorMessage", this.errorMessage);
        } else if (this.successMessage != null && !this.successMessage.isEmpty()) {
            request.setAttribute("successMessage", this.successMessage);
        }
        // set possible request attributes
        for (Map.Entry<String, Object> entry : this.requestAttributes.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        this.resetAttributes();

        if (path.equals("/Start")) {
            this.handleGetHomePageRequest(request, response);
        } 
        else if (path.equals("/Start/Cart")) {
            this.handleGetShoppingCartPageRequest(request, response);
        } 
        else if (path.equals("/Start/Register")) {
            this.handleGetRegisterPageRequest(request, response);
        } 
        else if (path.equals("/Start/Login")) {
            this.handleGetLoginPageRequest(request, response);
        } else if (path.equals("/Start/Logout")) {
            BookStoreModel.getInstance().getUserModel().logout(request);
            response.sendRedirect(contextPath + "/Start");
        } else if (path.equals("/Start/Payment")) {
            if (!UserModel.isLoggedIn(request)) {
                request.setAttribute("errorMessage", "Please Login first!");
            }
            request.getRequestDispatcher("/Payment.jspx").forward(request, response);
        } 
        else if (path.equals("/Start/Analytics")) {
            this.handleGetAnalyticsPageRequest(request, response);
        }
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        this.resetAttributes();

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String route = contextPath + path;
        String submit = request.getParameter("submit");

        if (path.contains("/Start/Ajax/")) {
            String routine = path.substring(path.lastIndexOf('/') + 1);
            this.handleAjaxRequest(routine, request, response);
            return;
        }

        if (submit == null) {
            doGet(request, response);
        }
        System.out.println("POST: " + path + "  Submit: " + submit);
        switch (path) {
            case "/Start":
                this.handlePostHomePath(route, submit, request, response);
                return;
            case "/Start/Cart":
                this.handlePostShoppingCartPath(route, submit, request, response);
                return;
            case "/Start/Register":
                this.handlePostRegisterPath(route, submit, request, response);
                return;
            case "/Start/Login":
                this.handlePostLoginPath(route, submit, request, response);
                return;
            case "/Start/Payment":
                this.handlePostPaymentPath(route, submit, request, response);
                return;
            case "/Start/Analytics":
                this.handlePostAnalyticsPath(route, submit, request, response);
                return;
            default:
                doGet(request, response);
                return;
        }
    }

    /**
     * Clean up messages and request attributes
     */
    private void resetAttributes() {
        this.successMessage = "";
        this.errorMessage = "";
        this.requestAttributes.clear();
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
        this.requestAttributes.put("street", street);
        this.requestAttributes.put("province", province);
        this.requestAttributes.put("country", country);
        this.requestAttributes.put("zip", zip);
        this.requestAttributes.put("phone", phone);
        this.requestAttributes.put("sameAddress", sameAddress);
        this.requestAttributes.put("bstreet", bstreet);
        this.requestAttributes.put("bprovince", bprovince);
        this.requestAttributes.put("bcountry", bcountry);
        this.requestAttributes.put("bzip", bzip);
        this.requestAttributes.put("firstname", firstname);
        this.requestAttributes.put("lastname", lastname);
    }

    /**
     * Fetch books with requirements and direct to home page
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void handleGetHomePageRequest(HttpServletRequest request, HttpServletResponse response)
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
     * Go to shopping cart page
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void handleGetShoppingCartPageRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ShoppingCartItemBean> books = new ArrayList<ShoppingCartItemBean>(
                BookStoreModel.getInstance().getCartModel().getMyCart(request).values());
        request.setAttribute("books", books);
        request.getRequestDispatcher("/ShoppingCart.jspx").forward(request, response);
    }

    /**
     * Go to register page
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handleGetRegisterPageRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Register.jspx").forward(request, response);
    }

    /**
     * Go to login page
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handleGetLoginPageRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Login.jspx").forward(request, response);
    }

    private UserBean getOrSetUser(HttpServletRequest request) {
        UserBean user = UserModel.getUser(request);
        if (user == null) {
            user = UserBean.newVisitor();
            UserModel.setUser(request, user);
        }
        return user;
    }

    /**
     * Go to analytics page
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handleGetAnalyticsPageRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (getOrSetUser(request).isAdmin())
                throw new Exception("Permission Denied!");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
        }
        request.getRequestDispatcher("/AnalyticsPage.jspx").forward(request, response);
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
    private void handlePostBookReview(String route, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bid = request.getParameter("bid");
        String rating = request.getParameter("rating");
        String review = request.getParameter("review");
        try {
            BookStoreModel.getInstance().rateBook(bid, rating, review, request);
            this.successMessage = "Review successfully submitted!";
        } catch (Exception e) {
            this.errorMessage = e.getMessage();
        }
        response.sendRedirect(route);
    }

    /**
     * Handle all ajax POST requests
     *
     * @param routine
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handleAjaxRequest(String routine, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = null;
        try {
            System.out.println("handling Ajax request!");
            if (routine.equals("Analytics")) {
                // get popular books list for ajax request
                if (!getOrSetUser(request).isAdmin())
                    throw new Exception("Permission Denied!");
                @SuppressWarnings("unchecked")
                List<NewPurchase.Book> popularBooks = (List<NewPurchase.Book>) request.getServletContext()
                        .getAttribute("popularBooks");
                if (popularBooks == null) {
                    popularBooks = new ArrayList<NewPurchase.Book>();
                    request.getServletContext().setAttribute("popularBooks", popularBooks);
                }
                json = BookStoreUtil.constructAjaxResponse(popularBooks);
            } else if (routine.equals("Review")) {
                // get book's reviews for ajax request
                String bid = request.getParameter("bid");
                System.out.println("receive book review fetch request bid=" + bid);
                if (bid == null || bid.isEmpty())
                    throw new Exception("Must provide Book id.");
                List<BookReviewBean> reviews = BookStoreModel.getInstance().retrieveBookReviewsByBookId(bid);
                System.out.println("review: " + reviews);
                json = BookStoreUtil.constructAjaxResponse(reviews);
                System.out.println(json.toString());
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
    private void handlePostHomePath(String route, String submit, HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException {
        // Home page POST
        if (submit.equals("Add To Cart")) {
            String bid = request.getParameter("bid");
            String quantityStr = request.getParameter("quantity");
            try {
                BookStoreModel.getInstance().getCartModel().addToCart(bid, quantityStr, request);
            } catch (Exception e) {
                e.printStackTrace();
                this.errorMessage = e.getMessage();
            }
            response.sendRedirect(route);
        } else if (submit.equals("Submit Review")) {
            this.handlePostBookReview(route, request, response);
        }
    }

    /**
     * Handle Shopping Cart Post request
     *
     * @param route
     * @param submit
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handlePostShoppingCartPath(String route, String submit, HttpServletRequest request,
                                            HttpServletResponse response) throws ServletException, IOException {
        // Shopping Cart page POST
        if (submit.equals("Remove")) {
            // Remove a book from cart
            String bid = request.getParameter("bid");
            try {
                BookStoreModel.getInstance().getCartModel().removeFromCart(bid, request);
                this.successMessage = "Item successfully removed!";
            } catch (Exception e) {
                e.printStackTrace();
                this.errorMessage = e.getMessage();
            }
            response.sendRedirect(route);
        } else if (submit.equals("Update")) {
            // update a book's quantity
            String bid = request.getParameter("bid");
            String quantityStr = request.getParameter("quantity");
            try {
                BookStoreModel.getInstance().getCartModel().updateCartItemQuantity(bid, quantityStr, request);
                this.successMessage = "Item successfully updated!";
            } catch (Exception e) {
                e.printStackTrace();
                this.errorMessage = e.getMessage();
            }
            response.sendRedirect(route);
        } else if (submit.equals("Submit Review")) {
            this.handlePostBookReview(route, request, response);
        }
    }

    /**
     * Handle all register post request
     *
     * @param route
     * @param submit
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handlePostRegisterPath(String route, String submit, HttpServletRequest request,
                                        HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String password = request.getParameter("password");
        String verifiedPassword = request.getParameter("verifiedPassword");
        if(UserModel.isLoggedIn(request)){
            response.sendError(403, "Already logged in");
        } else {
            try {
                UserBean user = BookStoreModel.getInstance().getUserModel().registerCustomerUser(username, firstname,
                        lastname, password, verifiedPassword,
                        request);
                UserModel.setUser(request, user);
                this.successMessage = "User successfully registered!";
            } catch (Exception e) {
                e.printStackTrace();
                // setup error message and store form info
                this.errorMessage = e.getMessage();
                this.requestAttributes.put("username", username);
                this.requestAttributes.put("firstname", username);
                this.requestAttributes.put("lastname", username);
                response.sendError(403, "Already logged in");
            }
            response.sendRedirect(route);
        }
    }

    /**
     * Handle login POST request
     *
     * @param route
     * @param submit
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handlePostLoginPath(String route, String submit, HttpServletRequest request,
                                     HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(UserModel.isLoggedIn(request)){
            response.sendError(403, "Already logged in");
        } else {
            try {
                BookStoreModel.getInstance().getUserModel().loginUser(username, password, request);
                this.successMessage = "Welcome back " + username;
                response.sendRedirect("/bookStore/Start");
            } catch (Exception e) {
                e.printStackTrace();
                this.errorMessage = e.getMessage();
                this.requestAttributes.put("username", username);
                response.sendError(403, errorMessage);
            }
        }
    }

    /**
     * Handle Payment POST requests
     *
     * @param route
     * @param submit
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handlePostPaymentPath(String route, String submit, HttpServletRequest request,
                                       HttpServletResponse response) throws ServletException, IOException {
        if (submit.equals("Confirm Order")) {
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
                this.successMessage = "Order Successfully Completed!";
                List<VisitEventBean> visitEvents = model.retrieveVisitEventsFromShoppingCart(request);
                model.addVisitEvents(visitEvents);
                this.requestAttributes.put("newPurchase", visitEvents);
                model.getCartModel().updateCart(new HashMap<String, ShoppingCartItemBean>(), request);
            } catch (Exception e) {
                e.printStackTrace();
                this.savePurchaseFormInfo(street, province, country, zip, phone, sameAddress, bstreet, bprovince,
                        bcountry, bzip, firstname, lastname, request);
                this.errorMessage = e.getMessage();
            }
            response.sendRedirect(route);
        } else if (submit.equals("Go To Payment")) {
            // go to payment page
            response.sendRedirect(route);
        }
    }

    /**
     * Handle Analytics POST requests
     *
     * @param route
     * @param submit
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handlePostAnalyticsPath(String route, String submit, HttpServletRequest request,
                                         HttpServletResponse response) throws ServletException, IOException {
        if (submit.equals("Get Statistics")) {
            try {
                List<Pair<Integer, String>> results = BookStoreModel.getInstance().retrieveUserPurchaseStatistics(request);
                request.getSession().setAttribute("stats", results);
                response.sendRedirect(route);
            } catch (Exception e) {
                e.printStackTrace();
                this.errorMessage = e.getMessage();
                response.sendError(401, e.getMessage());
            }
        }
    }


}

//// Object to XML
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