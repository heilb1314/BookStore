package listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;

import bean.BookBean;
import bean.JsonBean;
import bean.VisitEventBean;

/**
 * Application Lifecycle Listener implementation class NewPurchase
 *
 */
@WebListener
public class NewPurchase implements ServletRequestAttributeListener {

	/**
	 * A book class wrapper contains a BookBean and a count
	 * 
	 * @author jianxiongwang
	 *
	 */
	public class Book extends bean.JsonBean implements Comparable<Book> {
		private BookBean book;
		private int count = 1;

		public Book(BookBean book) {
			this.book = book;
		}

		public BookBean getBook() {
			return this.book;
		}

		public void incrementCount() {
			this.count++;
		}

		public int getCount() {
			return this.count;
		}
		
		public JsonObjectBuilder toJsonObjectBuilder() {
			return Json.createObjectBuilder()
					.add("count", this.getCount())
					.add("book", this.getBook().toJsonObjectBuilder());
		}

		@Override
		public int compareTo(Book o) {
			return Integer.compare(this.getCount(), o.getCount());
		}

		@Override
		public int hashCode() {
			return this.getBook().getBid().hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (other instanceof BookBean) {
				return this.getBook().getBid().equals(((BookBean) other).getBid());
			}
			return false;
		}

		@Override
		public String toString() {
			return String.format("Book: %s, count=%d", this.getBook().toString(), this.getCount());
		}

	}

	private Map<String, Book> books;
	private List<Book> top10Purchases;

	/**
	 * Default constructor.
	 */
	public NewPurchase() {
		this.books = new HashMap<String, Book>();
		this.top10Purchases = new ArrayList<Book>();
	}

	/**
	 * Handle new purchase, update the top 10 purchase
	 * 
	 * @param arg0
	 */
	private void handleNewPurchase(ServletRequestAttributeEvent arg0) {
		@SuppressWarnings("unchecked")
		List<VisitEventBean> visits = (List<VisitEventBean>) arg0.getServletRequest().getAttribute("newPurchase");
		if (visits == null)
			return;
		System.out.println("handle new purchase");
		for(VisitEventBean visit : visits) {
			if (visit.getEventType() == VisitEventBean.VisitEventType.PURCHASE) {
				BookBean b = visit.getBook();
				String bid = b.getBid();
				if (!this.books.containsKey(bid)) {
					Book book = new Book(b);
					this.books.put(bid, book);
				} else {
					this.books.get(bid).incrementCount();
				}
			}
		}
		this.sortTop10Purchase();
		arg0.getServletContext().setAttribute("popularBooks", this.top10Purchases);
		// debug
		for(int i=0; i<this.top10Purchases.size(); i++) {
			Book b = this.top10Purchases.get(i);
			if(b!=null) {
				System.out.println(b.toString());
			}
		}
	}

	private void sortTop10Purchase() {
		List<Book> bookList = new ArrayList<Book>(this.books.values());
		Collections.sort(bookList, Collections.reverseOrder());
		int size = Math.min(bookList.size(), 10);
		List<Book> arr = new ArrayList<Book>();
		arr.addAll(bookList.subList(0, size));
		this.top10Purchases = arr;
	}

	/**
	 * @see ServletRequestAttributeListener#attributeRemoved(ServletRequestAttributeEvent)
	 */
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletRequestAttributeListener#attributeAdded(ServletRequestAttributeEvent)
	 */
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
		this.handleNewPurchase(arg0);
		
	}

	/**
	 * @see ServletRequestAttributeListener#attributeReplaced(ServletRequestAttributeEvent)
	 */
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		this.handleNewPurchase(arg0);
	}

}
