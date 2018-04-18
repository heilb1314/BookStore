package DAO;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.UserBean;
import bean.UserType;

public class UserDAO extends ObjectDAO {

    public UserDAO() {
        super();
    }

    /**
     * Update user type
     *
     * @param id
     * @param userType
     * @throws Exception
     */
    public void updateUserType(int id, UserType userType) throws Exception {
        String query = "UPDATE User SET user_type = ? WHERE id = ?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, userType.toString());
            p.setInt(2, id);
            p.executeUpdate();
            p.close();
            con.close();
        }
    }

    /**
     * Get User by id
     *
     * @param uid
     * @return
     * @throws Exception
     */
    public UserBean getUserById(int uid) throws Exception {
        UserBean user = null;
        String query = "SELECT * FROM User WHERE id = ?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, uid);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                int id = r.getInt("id");
                String username = r.getString("username");
                String firstname = r.getString("fname");
                String lastname = r.getString("lname");
                UserType userType = UserType.getUserType(r.getString("user_type").toLowerCase());
                user = new UserBean(id, username, firstname, lastname, userType);
            }
            r.close();
            p.close();
            con.close();
            return user;
        }
    }

    /**
     * Get User by id
     *
     * @param username
     * @return
     * @throws Exception
     */
    public UserBean getUserByUsername(String uname) throws Exception {
        UserBean user = null;
        String query = "SELECT * FROM User WHERE username = ? LIMIT 1";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, uname);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                int id = r.getInt("id");
                String username = r.getString("username");
                String firstname = r.getString("fname");
                String lastname = r.getString("lname");
                UserType userType = UserType.getUserType(r.getString("user_type"));
                user = new UserBean(id, username, firstname, lastname, userType);
            }
            r.close();
            return user;
        }
    }
  
    /**
     * Log in with username and password.
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public UserBean login(String username, String password) throws Exception {
        this.validateUsername(username);
        this.validatePassword(password);

        UserBean user = null;
        String dbPassword = null;

        String query = "SELECT * FROM User WHERE username=?";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                int id = r.getInt("id");
                String firstname = r.getString("fname");
                String lastname = r.getString("lname");
                dbPassword = r.getString("password");
                UserType userType = UserType.getUserType(r.getString("user_type").toLowerCase());
                user = new UserBean(id, username, firstname, lastname, userType);
            }
            r.close();
            p.close();
            con.close();

            if (user == null) throw new Exception("Username doesn't exist.");

            String hashedPassword = UserDAO.hash(password);
            System.out.println("pass: " + hashedPassword);
            System.out.println("saved pass: " + dbPassword);

            if (hashedPassword.equals(dbPassword)) {
                return user;
            } else {
                throw new Exception("Password is not correct!");
            }
        }

    }

    /**
     * Sign up a new user. Note: user must log out first.
     *
     * @param username
     * @param password
     * @param fname
     * @param lname
     * @param userType
     * @throws Exception
     */
    public void signup(String username, String password, String fname, String lname, UserType userType) throws Exception {

        // validations
        this.validateUsername(username);
        this.validatePassword(password);
        this.validateFirstname(fname);
        this.validateLastname(lname);

        String query = "SELECT * FROM User WHERE username=?";
        String query2 = "INSERT INTO User (username, password, lname, fname, user_type) VALUES (?,?,?,?,?)";
        try (Connection con = this.ds.getConnection();
             PreparedStatement p = con.prepareStatement(query);
             PreparedStatement p2 = con.prepareStatement(query2)) {
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                // username already existed
                r.close();
                p.close();
                con.close();
                throw new Exception("Username is already taken.");
            }
            if (!r.isClosed()) {
                r.close();
            }
            p.close();
            // hash password


            String hashedPassword = UserDAO.hash(password);

            // insert user
            p2.setString(1, username);
            p2.setString(2, hashedPassword);
            p2.setString(3, lname);
            p2.setString(4, fname);
            p2.setString(5, userType.toString());
            p2.executeUpdate();
            p2.close();
            con.close();
        }
    }

    /***************************/
    /******* Validations *******/
    /***************************/

    private void validatePassword(String password) throws Exception {
        if (password == null || password.length() < 6)
            throw new Exception("Invalid Password");
    }


    private void validateUsername(String n) throws Exception {
        this.validateName(n, "Username");
    }

    private void validateFirstname(String n) throws Exception {
        this.validateName(n, "Firstname");
    }

    private void validateLastname(String n) throws Exception {
        this.validateName(n, "Lastname");
    }

    private void validateName(String name, String label) throws Exception {
        if (name == null || name.length() < 4) throw new Exception("Invalid " + label);
    }

    static private String hash(String password) throws Exception {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
