package model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import bean.BookBean;

@XmlRootElement(name = "books")
public class BookListWrapper {

    @XmlAttribute(name = "category")
    public BookBean.Category category;

    @XmlElement(name = "book")
    public List<BookBean> list;

    public BookListWrapper() {
        this(null, null);
    }

    public BookListWrapper(BookBean.Category c, List<BookBean> list) {
        this.category = c;
        this.list = list;
    }
}
