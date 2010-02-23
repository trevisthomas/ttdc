package org.ttdc.gwt.client.beans;

import java.util.Date;

public class GImage extends GBase {
	private String imageId;	
	private String name;
	private String thumbnailName;
	private int width;
	private int height;
	private GPerson owner;
	
	private Date date = new Date();
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public GPerson getOwner() {
		return owner;
	}
	public void setOwner(GPerson owner) {
		this.owner = owner;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getThumbnailName() {
		return thumbnailName;
	}
	public void setThumbnailName(String thumbnailName) {
		this.thumbnailName = thumbnailName;
	}
	
	
	
}
