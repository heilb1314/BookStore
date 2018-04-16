package model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import DAO.UserDAO;
import bean.UserBean;
import bean.UserBean.UserType;

public class UserModel {

    private UserDAO userDAO;
    private UserBean user = null;

    public UserModel() throws Exception {
        this.userDAO = new UserDAO();
    }

    /**
     * Get Session User
     *
     * @param request
     * @return
     */
    public UserBean getUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            UserBean user = (UserBean) session.getAttribute("user");
            if (user == null) return null;
            if (!user.equals(this.user)) {
                this.user = user;
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            this.user = null;
            return null;
        }
    }

    /**
     * If current login user is administor or not
     *
     * @param request
     * @return
     * @throws Exception
     */
    public boolean isAdmin(HttpServletRequest request) throws Exception {
        if (this.loggedIn(request)) {
            UserBean user = this.getUser(request);
            return user.getUserType() == UserType.ADMIN;
        } else {
            throw new Exception("Please login first!");
        }
    }

    /**
     * Update a visitor user type to customer
     *
     * @param request
     * @throws Exception
     */
    public void updateVisitorToCustomer(HttpServletRequest request) throws Exception {
        UserBean user = this.getUser(request);
        if (user == null) throw new Exception("Please Log out first.");
        if (user.getUserType() != UserType.VISITOR) throw new Exception("User is not a visitor!");
        this.userDAO.updateUserType(this.getUser(request).getId(), UserType.CUSTOMER);
        this.user.setUserType(UserType.CUSTOMER);
    }

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
    public void registerUser(String username, String firstname, String lastname, String password, String verifiedPassword, HttpServletRequest request) throws Exception {
        if (!password.equals(verifiedPassword)) throw new Exception("Passwords are not matched.");
        this.userDAO.signup(username, password, firstname, lastname, UserType.VISITOR.toString());
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
        if (this.loggedIn(request)) throw new Exception("Please Log out first.");
        UserBean user = this.userDAO.login(username, password);
        request.getSession().setAttribute("user", user);
        this.user = user;
    }

    /**
     * Check if user with given username is logged in or not.
     *
     * @param username
     * @param request
     * @return
     */
    public boolean loggedIn(HttpServletRequest request) {
        try {

            UserBean user = this.getUser(request);
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
        this.user = null;
    }
}
