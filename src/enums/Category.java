package enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Category {
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
