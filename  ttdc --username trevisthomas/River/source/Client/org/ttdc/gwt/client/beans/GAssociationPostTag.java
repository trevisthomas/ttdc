package org.ttdc.gwt.client.beans;

import java.util.Date;

public class GAssociationPostTag extends GBase{
	private String guid;
	private GTag tag;
	private GPost post;
	private GPerson creator;
	private Date date = new Date();
	private boolean title;
		
	@Override
	public String toString() {
		if(getTag() != null)
			return getTag().getValue();
		else 
			return "UnInitialized";
	}
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public GTag getTag() {
		return tag;
	}
	public void setTag(GTag tag) {
		this.tag = tag;
	}
	public GPost getPost() {
		return post;
	}
	public void setPost(GPost post) {
		this.post = post;
	}
	public GPerson getCreator() {
		return creator;
	}
	public void setCreator(GPerson creator) {
		this.creator = creator;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isTitle() {
		return title;
	}
	public void setTitle(boolean title) {
		this.title = title;
	}
	public boolean isType(String type){
		return type.equals(getTag().getType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GAssociationPostTag other = (GAssociationPostTag) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
	}
	
}
