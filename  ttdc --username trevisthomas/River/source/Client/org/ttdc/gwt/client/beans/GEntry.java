package org.ttdc.gwt.client.beans;

import java.util.Date;

public class GEntry extends GBase{
		
	private String entryId;
	private String body;
	//private String bodyFormatted;
	private String summary;
	
	private GPost post; 
	private Date date = new Date();
	
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public GPost getPost() {
		return post;
	}
	public void setPost(GPost post) {
		this.post = post;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
//	@Deprecated
//	public String getBodyFormatted() {
//		return bodyFormatted;
//	}
//	@Deprecated
//	public void setBodyFormatted(String bodyFormatted) {
//		this.bodyFormatted = bodyFormatted;
//	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
