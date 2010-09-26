package org.ttdc.persistence.objects;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Forum {
	private String tagId;
	private String type;
	private String value;
	private Date date = new Date(); 
	private int mass;
	private String sortValue;
	
	@Id
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getMass() {
		return mass;
	}
	public void setMass(int mass) {
		this.mass = mass;
	}
	public String getSortValue() {
		return sortValue;
	}
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}
	
	
	
}
