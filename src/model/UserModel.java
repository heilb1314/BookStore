package model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import DAO.UserDAO;
import bean.UserBean;
import bean.UserType;

public class UserModel {

    private UserDAO userDAO;
    //Todo: possibly load dynamically from config.
    private static final String SESSION_KEY = "user";

    public UserModel() throws Exception {
        this.userDAO = new UserDAO();
    }

    /**
     * Get Session User
     *
     * @param request
     * Todo: Possibly define on `UserBean` instead
     * @return
     */
    public static UserBean getUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (UserBean) session.getAttribute(SESSION_KEY);
    }

    public static void setUser(HttpServletRequest request, UserBean user){
        request.getSession().setAttribute(SESSION_KEY, user);
    }

    //Todo: posssibly remove?
//    /**
//     * Update a visitor user type to customer
//     *
//     * @param request
//     * @throws Exception
//     */
//    public void updateVisitorToCustomer(HttpServletRequest request) throws Exception {
//        UserBean user = this.getUser(request);
//        if (user == null) throw new Exception("Please Log out first.");
//        if (user.getUserType() != UserType.VISITOR) throw new Exception("User is not a visitor!");
//        this.userDAO.updateUserType(user.getId(), UserType.CUSTOMER); //Todo: This requires registration
//        this.user.setUserType(UserType.CUSTOMER);
//    }

    /**
     * Register a user
     *
     * @param username
     * @param firstname
     * @param lastname
     * @param password
     * @param verifiedPassword
     * @param request
     * @throws Exception
     */
    public void registerCustomerUser(String username, String firstname, String lastname, String password,
                                     String verifiedPassword, HttpServletRequest request) throws Exception {
        if (!password.equals(verifiedPassword)) throw new Exception("Passwords are not matched.");
        this.userDAO.signup(username, password, firstname, lastname, UserType.CUSTOMER);
    }

    /**
     * Login a user
     *
     * @param username
     * @param password
     * @param request
     * @throws Exception
     */
    public void loginUser(String username, String password, HttpServletRequest request) throws Exception {
        if (isLoggedIn(request)) throw new Exception("Please Log out first.");

        UserBean user = this.userDAO.login(username, password);
        setUser(request, user);
    }

    /**
     * Check if user with given username is logged in or not.
     * Todo: maybe move to userbean
     *
     * @param request
     * @return
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        try {

            UserBean user = getUser(request);
            if (user != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Logout
     *
     * @param request
     */
    public void logout(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
    }

    public static UserBean getOrSetUser(HttpServletRequest request) {
        UserBean user = UserModel.getUser(request);
        if (user == null) {
            user = UserBean.newVisitor();
            UserModel.setUser(request, user);
        }
        return user;
    }
}
