package org.ttdc.gwt.client.forms;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data transport for a comment
 * @author Trevis
 *
 */
@Deprecated
public class PostFormData implements IsSerializable{
	private String login;
	private String password;
	private String parentId;
	//private String personId;
	private String body;
	private String type;
	private String title;
	private String url;
	private String imageUrl;
	private String year;
	
	public final static String TYPE_MOVIE = "movie";
	public final static String TYPE_NORMAL = "normal";
	
	//TODO: add other stuff
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	/*
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	*/
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	public boolean isReply(){
		return getParentId() != null;
	}
	public boolean isComment(){
		return getParentId() == null;
	}
	
}
