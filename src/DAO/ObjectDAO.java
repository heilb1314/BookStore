package DAO;

import javax.sql.DataSource;

public class ObjectDAO {

    protected DataSource ds;

    public ObjectDAO() {
        ds = DBConnection.getInstance();
    }

}
