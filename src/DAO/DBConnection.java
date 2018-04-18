package DAO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection {

    private static class DBConnectionHolder {
        private static DataSource INSTANCE;

        static {
            try {
                INSTANCE = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
            } catch (NamingException e) {
                e.printStackTrace();
                System.err.println("Unable to find DB connection. Check your database sources");
            }
        }
    }

    public static DataSource getInstance(){
        if(DBConnectionHolder.INSTANCE == null)
            throw new IllegalArgumentException("No such DB found");
        else
            return DBConnectionHolder.INSTANCE;
    }

}
