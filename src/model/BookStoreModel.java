package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAO.*;
import bean.AddressBean;
import bean.BookBean;
import bean.BookReviewBean;
import bean.PoBean;
import bean.ShoppingCartItemBean;
import bean.UserBean;
import bean.UserType;
import javafx.util.Pair;
import bean.VisitEventBean;

public class BookStoreModel {

    private BookDAO bookDAO;
    private PoDAO poDAO;
    private AddressDAO addressDAO;
    private VisitEventDAO visitEventDAO;
    private ShoppingCartModel cartModel;
    private UserModel userModel;

    private static class BookStoreModelHolder {
        private static final BookStoreModel INSTANCE = new BookStoreModel();
    }

    private BookStoreModel() {
        try {
            this.bookDAO = new BookDAO();
            this.poDAO = new PoDAO();
            this.addressDAO = new AddressDAO();
            this.visitEventDAO = new VisitEventDAO();
            this.cartModel = new ShoppingCartModel();
            this.userModel = new UserModel();
        } catch (Exception e) {
            e.printStackTrace(); //Todo: ???
        }
    }

    public static BookStoreModel getInstance() {
        return BookStoreModelHolder.INSTANCE;
    }

    /**
     * Get user model
     *
     * @return
     */
    public UserModel getUserModel() {
        return this.userModel;
    }

    /**
     * Get shopping cart model
     *
     * @return
     */
    public ShoppingCartModel getCartModel() {
        return this.cartModel;
    }

    /**
     * get book by id
     *
     * @param bid
     * @return
     * @throws Exception
     */
    public BookBean retrieveBookById(String bid) throws Exception {
        return this.bookDAO.getBookById(bid);
    }

    /**
     * Get books by a category
     *
     * @param category
     * @return
     * @throws Exception
     */
    public List<BookBean> retrieveBooksByCategory(BookBean.Category category) throws Exception {
        return this.bookDAO.getListOfBooksByCategory(category);
    }

    /**
     * Get books by search text
     *
     * @param title
     * @return
     * @throws Exception
     */
    public List<BookBean> retrieveBooksByTitle(String title) throws Exception {
        return this.bookDAO.getListOfBooksByTitle(title);
    }

    /**
     * Get a list of book reviews
     *
     * @param bid
     * @return
     * @throws Exception
     */
    public List<BookReviewBean> retrieveBookReviewsByBookId(String bid) throws Exception {
        BookBean book = this.retrieveBookById(bid);
        if (book == null) throw new Exception("Book Not found!");
        return this.poDAO.getBookReviewsById(bid);
    }

    /**
     * Get purchase order by id
     *
     * @param id
     * @return
     * @throws Exception
     */
    public PoBean retrievePurchaseOrderById(int id) throws Exception {
        return this.poDAO.getPoById(id);
    }

    /**
     * Add an address
     *
     * @param street
     * @param province
     * @param country
     * @param zip
     * @param phone
     * @return new address id
     * @throws Exception
     */
    public int addAddress(String street, String province, String country, String zip, String phone) throws Exception {
        this.validateAddress(street, province, country, zip);
        return addressDAO.addAddress(street, province, country, zip, phone);
    }

    /**
     * Update an existing address
     *
     * @param id
     * @param street
     * @param province
     * @param country
     * @param zip
     * @param phone
     * @throws Exception
     */
    public void updateAddress(int id, String street, String province, String country, String zip, String phone) throws Exception {
        this.validateAddress(street, province, country, zip);
        addressDAO.updateAddress(id, street, province, country, zip, phone);
    }

    /**
     * Get an address
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AddressBean getAddress(int id) throws Exception {
        return addressDAO.getAddressById(id);
    }

    /**
     * Process purchase order
     *
     * @param street
     * @param province
     * @param country
     * @param zip
     * @param phone
     * @param bstreet
     * @param bprovince
     * @param bcountry
     * @param bzip
     * @param firstname
     * @param lastname
     * @param cardNumber
     * @param month
     * @param year
     * @param cvc
     * @param request
     * @throws Exception
     */
    public void processPo(String street, String province, String country, String zip,
                          String phone, String bstreet, String bprovince, String bcountry,
                          String bzip, String firstname, String lastname, String cardNumber,
                          String month, String year, String cvc, HttpServletRequest request) throws Exception {
        UserBean user = UserModel.getUser(request);
        if (user == null) throw new Exception("Please log in first!");
        this.validateShoppingCart(request);
        this.validateAddress(street, province, country, zip);
        this.validatePoPayment(bstreet, bprovince, bcountry, bzip, firstname, lastname, cardNumber, month, year, cvc);
        int addressId = this.addAddress(street, province, country, zip, phone);
        int pid = this.poDAO.processPo(addressId, user.getId());
        List<ShoppingCartItemBean> cartItems = new ArrayList<>(this.getCartModel().getMyCart(request).values());
        this.poDAO.addPoItems(cartItems, pid);
        this.orderPo(pid, request);
        //todo: This shouldn't happen. you can't update a visitor to a customer if you are not registered
//        if (user.getUserType() == UserType.VISITOR) {
//            this.getUserModel().updateVisitorToCustomer(request);
//        }
    }

    /**
     * Confirm Purchase Order / Deny every 3rd request
     *
     * @param pid
     * @param request
     * @throws Exception
     */
    private void orderPo(int pid, HttpServletRequest request) throws Exception {
        Integer n = (Integer) request.getSession().getAttribute("numOfPoTry");
        if (n == null) {
            n = 1;
        }
        System.out.println("num of PO Try: " + n);
        request.getSession().setAttribute("numOfPoTry", n + 1);
        if (n % 3 == 0) {
            poDAO.denyPo(pid);
            throw new Exception("Credit Card Authorization Failed.");
        } else {
            poDAO.orderPo(pid);
        }
    }

    /**
     * Get a list of visit events from current user shopping cart.
     *
     * @param request
     * @return
     * @throws Exception
     */
    public List<VisitEventBean> retrieveVisitEventsFromShoppingCart(HttpServletRequest request) throws Exception {
        return this.visitEventDAO.getListOfVisitEventsFromCartPurchases(this.getCartModel().getMyCart(request));
    }

    /**
     * add a list of visit events into database
     *
     * @param visitEvents
     * @throws Exception
     */
    public void addVisitEvents(List<VisitEventBean> visitEvents) throws Exception {
        this.visitEventDAO.addVisitEvents(visitEvents);
    }

    /**
     * Get user purchase statistics
     *
     * @param request
     * @return
     * @throws Exception
     */
    public List<Pair<Integer, String>> retrieveUserPurchaseStatistics(HttpServletRequest request) throws Exception {
        UserBean user = UserModel.getUser(request);
        if (user == null || !user.isAdmin()) throw new Exception("No Permission!");
        return this.poDAO.getPurchaseStats();
    }

    /**
     * Rate a book
     *
     * @param bid
     * @param rating
     * @param review
     * @param request
     * @throws Exception
     */
    public void rateBook(String bid, String rating, String review, HttpServletRequest request) throws Exception {
        if (!UserModel.isLoggedIn(request)) throw new Exception("Please Log in first!");
        try {
            int rate = Integer.parseInt(rating);
            if (rate < 1 || rate > 5) throw new NumberFormatException();
            if (review.length() > 255) throw new Exception("Review is too long! Must be less than 255 characters.");
            UserBean user = UserModel.getUser(request);
            BookBean book = this.retrieveBookById(bid);
            if (book == null) throw new Exception("Invalid Book id");
            int pid = this.poDAO.getAvailableRatingPurchaseItemId(user.getId(), bid);
            if (pid == 0) throw new Exception("Cannot rate book without purchase.");
            this.poDAO.ratePoItem(pid, bid, rate, review);
            this.bookDAO.updateBookRating(bid);
        } catch (NumberFormatException e) {
            throw new Exception("Rating must be an Integer between 1-5.");
        }
    }


    /***************/
    /* validations */

    /***************/

    private void validateShoppingCart(HttpServletRequest request) throws Exception {
        Map<String, ShoppingCartItemBean> cart = this.getCartModel().getMyCart(request);
        if (cart == null || cart.isEmpty()) throw new Exception("No Item to purchase!");
    }

    private void validatePoPayment(String street, String province, String country, String zip, String firstname, String lastname, String cardNumber, String month, String year, String cvc) throws Exception {
        this.validateAddress(street, province, country, zip);
        this.validateCreditCard(firstname, lastname, cardNumber, month, year, cvc);
    }

    private void validateAddress(String street, String province, String country, String zip) throws Exception {
        if (street == null || street.isEmpty()) throw new Exception("Street cannot be empty!");
        if (province == null || province.isEmpty()) throw new Exception("Province cannot be empty!");
        if (country == null || country.isEmpty()) throw new Exception("Country cannot be empty!");
        if (zip == null || zip.isEmpty()) throw new Exception("Zip cannot be empty!");
    }

    private void validateCreditCard(String firstname, String lastname, String cardNumber, String month, String year, String cvc) throws Exception {
        if (firstname == null || firstname.isEmpty())
            throw new Exception("Invalid firstname!");
        if (lastname == null || lastname.isEmpty())
            throw new Exception("Invalid lastname!");
        if (cardNumber == null || cardNumber.isEmpty() || cardNumber.length() < 12 || cardNumber.length() > 19 || !cardNumber.matches("^[0-9]+$"))
            throw new Exception("Invalid Credit Card Number!");
        int m, y;
        try {
            m = Integer.parseInt(month);
            if (m < 1 || m > 12) throw new Exception("Invalid Month");
        } catch (NumberFormatException e) {
            throw new Exception("Invalid Month!");
        }
        try {
            y = Integer.parseInt(year);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            if (y < currentYear || (y == currentYear && m < currentMonth))
                throw new Exception("Credit Card expired!");
        } catch (NumberFormatException e) {
            throw new Exception("Invalid Month!");
        }
        if (cvc == null || cvc.length() < 3 || cvc.length() > 4 || !cvc.matches("^[0-9]+$"))
            throw new Exception("Invalid CVV");
    }

}
