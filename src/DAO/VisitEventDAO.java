package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bean.BookBean;
import bean.ShoppingCartItemBean;
import bean.VisitEventBean;
import bean.VisitEventBean.VisitEventType;

public class VisitEventDAO extends ObjectDAO {

	private static String DATE_FORMAT = "mmddyyyy";

	public VisitEventDAO() throws Exception {
		super();
	}

	public List<VisitEventBean> getListOfVisitEventsFromCartPurchases(Map<String, ShoppingCartItemBean> cart) {
		List<VisitEventBean> visitEvents = new ArrayList<VisitEventBean>();
		String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
		for (ShoppingCartItemBean item : cart.values()) {
			visitEvents.add(new VisitEventBean(date, item.getBook(), VisitEventType.PURCHASE));
		}
		return visitEvents;
	}

	/**
	 * Get a list of visit events by month and year
	 * @param m
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public List<VisitEventBean> getListOfVisitEventsByMonthYear(int m) throws Exception {
		if (m < 1 || m > 12)
			throw new Exception("Invalid Month.");
		String query = "SELECT b.*, v.day, v.eventtype FROM book b INNER JOIN VisitEvent v ON b.bid=v.bid WHERE day LIKE ? ORDER BY v.day ASC";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, String.format("%2d%%", m));
		ResultSet r = p.executeQuery();
		List<VisitEventBean> results = new ArrayList<>();
		BookDAO bookDAO = new BookDAO();
		while (r.next()) {
			String day = r.getString("day");
			BookBean book = bookDAO.parseBookBean(r);
			String type = r.getString("eventtype").toLowerCase();
			results.add(new VisitEventBean(day, book, VisitEventType.getVisitEventType(type)));
		}
		r.close();
		p.close();
		con.close();
		return results;
	}
	
	public void addVisitEvents(List<VisitEventBean> visitEvents) throws Exception {
		String query = "INSERT INTO VisitEvent (day,bid,eventtype) VALUES (?,?,?);";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		for(VisitEventBean v : visitEvents) {
			p.setString(1, v.getDay());
			p.setString(2, v.getBook().getBid());
			p.setString(3, v.getEventType().toString());
			p.addBatch();
		}
		p.executeBatch();
		p.close();
		con.close();
	}

}
