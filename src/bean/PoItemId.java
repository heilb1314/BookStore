package bean;

public class PoItemId {
	
	private Book book;
	private Po po;
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public Po getPo() {
		return po;
	}
	public void setPo(Po po) {
		this.po = po;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PoItemId other = (PoItemId) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.equals(other.book))
			return false;
		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		return true;
	}
	
	

}
