package enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Status {
	@XmlEnumValue("Ordered") ORDERED,
	@XmlEnumValue("Processed") PROCESSED,
	@XmlEnumValue("Denied") DENIED;
	
	public static Status getStatus(String status) {
		if(status==null) return null;
		switch(status.toLowerCase()) {
		case "ordered": return Status.ORDERED;
		case "processed": return Status.PROCESSED;
		case "denied": return Status.DENIED;
		default: return null;
		}
	}
}