package org.ttdc.gwt.client.beans;

import java.util.Date;


public class GStyle extends GBase{
	private String styleId;
	private String name;
	private String css;
	private String description;
	private Date date;
	private GPerson creator;
	private boolean defaultStyle;
	
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
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
	public GPerson getCreator() {
		return creator;
	}
	public void setCreator(GPerson creator) {
		this.creator = creator;
	}
	public void setDefaultStyle(boolean defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	public boolean isDefaultStyle() {
		return defaultStyle;
	}
}
