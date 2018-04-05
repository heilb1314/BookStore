package bean;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="book")
@XmlType(propOrder={"bid","title","price","category","description"})
public class BookBean {
	
	@XmlType
	@XmlEnum(String.class)
	public static enum Category {
		@XmlEnumValue("Science") SCIENCE,
		@XmlEnumValue("Fiction") FICTION,
		@XmlEnumValue("Engineering") ENGINEERING;
		
		public static Category getCategory(String category) {
			if(category==null) return null;
			switch(category.toLowerCase()) {
			case "science": return Category.SCIENCE;
			case "fiction": return Category.FICTION;
			case "engineering": return Category.ENGINEERING;
			default: return null;
			}
		}
		
	}
	
	
	private String bid;
	private String title;
	private int price;
	private int rating;
	private BookBean.Category category;
	private String description;
	private Set<PoItemBean> poItems = new HashSet<PoItemBean>(0);
	
	public BookBean() {
		this("","",0,null,0,"");
	}
	
	public BookBean(String bid, String title, int price, Category category, int rating, String description) {
		super();
		this.bid = bid;
		this.title = title;
		this.price = price;
		this.category = category;
		this.rating = 0;
		this.description = description;
	}

	public String getBid() {
		return bid;
	}


	public void setBid(String bid) {
		this.bid = bid;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}

	public BookBean.Category getCategory() {
		return category;
	}

	public void setCategory(BookBean.Category category) {
		this.category = category;
	}
	
	@XmlTransient
	public Set<PoItemBean> getPoItems() {
		return poItems;
	}

	public void setPoItems(Set<PoItemBean> poItems) {
		this.poItems = poItems;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((poItems == null) ? 0 : poItems.hashCode());
		result = prime * result + price;
		result = prime * result + rating;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		BookBean other = (BookBean) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		if (category != other.category)
			return false;
		if (poItems == null) {
			if (other.poItems != null)
				return false;
		} else if (!poItems.equals(other.poItems))
			return false;
		if (price != other.price)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

}
