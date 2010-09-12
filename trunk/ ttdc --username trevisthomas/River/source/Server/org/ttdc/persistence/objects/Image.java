package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Table(name="IMAGE")
@Entity
@NamedQueries({
	@NamedQuery(name="image.getAll", query="FROM Image"),
	@NamedQuery(name="image.getByName", query="FROM Image as image WHERE image.name=:name"),
	@NamedQuery(name="image.getById", query="FROM Image as image WHERE image.imageId=:imageId"),
	@NamedQuery(name="image.getAllImages", query="FROM Image as image ORDER BY image.date DESC"),
	@NamedQuery(name="image.getAllImagesByName", query="FROM Image as image ORDER BY image.name")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Image {
	public final static String SQUARE_THUMBNAIL_SUFFIX = "_stn";
	private String imageId;	
	private String name; 
	private int width;
	private int height;
	private Person owner;
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
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Transient
	public String getSquareThumbnailName(){
		String n = getName();
		int extentionStartIndex = n.lastIndexOf('.');
		String tnName;
		if(extentionStartIndex > 0){
			String prefix = n.substring(0,extentionStartIndex);
			String ext =  n.substring(extentionStartIndex);
			tnName = prefix+SQUARE_THUMBNAIL_SUFFIX+ext;
		}
		else{
			tnName = n + SQUARE_THUMBNAIL_SUFFIX;
		}
		
		return tnName;
	}
	
}
