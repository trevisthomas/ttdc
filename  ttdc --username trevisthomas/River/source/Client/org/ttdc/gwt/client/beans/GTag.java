package org.ttdc.gwt.client.beans;

import java.util.Date;

public class GTag extends GBase{
	private String tagId;
	private String type; 
	private String value; 
	private Date date = new Date(); 
	private GPerson creator;
	private String description;
	private int mass;
	private int cloudRank = -1; 
	
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
	public GPerson getCreator() {
		return creator;
	}
	public void setCreator(GPerson creator) {
		this.creator = creator;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMass() {
		return mass;
	}
	public void setMass(int mass) {
		this.mass = mass;
	}
	public int getCloudRank() {
		return cloudRank;
	}
	public void setCloudRank(int cloudRank) {
		this.cloudRank = cloudRank;
	}
	
	public boolean isType(String type){
		return type.equals(getType());
	}
}
