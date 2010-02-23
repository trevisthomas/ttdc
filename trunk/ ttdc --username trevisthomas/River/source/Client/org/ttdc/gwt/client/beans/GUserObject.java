package org.ttdc.gwt.client.beans;

import java.util.Date;


public class GUserObject extends GBase{
	private String objectId;
	private String url;
	private String name;
	private String type; //This will probably be pretty important since this obj can be almost anything
	private String value;
	private String description;
	private GImage thumbnail;  //TREVIS REMOVE THIS!!
	private Date date = new Date();
	private GPerson owner;
	private GUserObjectTemplate template;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public GUserObjectTemplate getTemplate() {
		return template;
	}
	public void setTemplate(GUserObjectTemplate template) {
		this.template = template;
	}
	public GPerson getOwner() {
		return owner;
	}
	public void setOwner(GPerson owner) {
		this.owner = owner;
	}
}
