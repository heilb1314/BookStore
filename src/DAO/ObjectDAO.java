package DAO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ObjectDAO {

    protected DataSource ds;

    public ObjectDAO() throws ClassNotFoundException {
        try {
            ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
