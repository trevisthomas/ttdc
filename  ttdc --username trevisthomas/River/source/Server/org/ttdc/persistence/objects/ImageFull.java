package org.ttdc.persistence.objects;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@NamedQueries({
	@NamedQuery(name="imageFull.getByName", query="FROM ImageFull as image INNER JOIN FETCH image.owner WHERE image.name=:name"),
	@NamedQuery(name="imageFull.getById", query="FROM ImageFull as image INNER JOIN FETCH image.owner WHERE image.imageId=:imageId"),
	@NamedQuery(name="imageFull.getAllImages", query="FROM ImageFull as image INNER JOIN FETCH image.owner ORDER BY image.date DESC")
})
@Table(name="IMAGE")
public class ImageFull {
	private String imageId;	
	private String name; 
	private int width;
	private int height;
	private Person owner;
	private Blob image;
	private Blob sqareImage;
	private Date date = new Date();
		
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
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
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="OWNER_GUID")
	public Person getOwner() {
		return owner;
	}
	public void setOwner(Person owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Blob getImage() {
		return image;
	}
	public void setImage(Blob image) {
		this.image = image;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Column(name="IMAGE_SQUARE")
	public Blob getSqareImage() {
		return sqareImage;
	}
	public void setSqareImage(Blob sqareImage) {
		this.sqareImage = sqareImage;
	}
	
}
