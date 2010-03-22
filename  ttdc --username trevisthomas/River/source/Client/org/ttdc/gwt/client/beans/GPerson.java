package org.ttdc.gwt.client.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.constants.UserObjectConstants;

public class GPerson extends GBase{
	private String personId;
	private String login;
	private String password;
	private String name;
	private String email;
	private Date date = new Date();
	private Date lastAccessDate;
	private Date birthday;
	private int hits;
	private String status; //ACTIVE,LOCKED,INACTIVE
	private String bio;
	private List<GUserObject> objects = new ArrayList<GUserObject>();
	//private Style style; //I might remove this and just use a UserObj to represent it
	private GImage image;
	private List<GPrivilege> privileges = new ArrayList<GPrivilege>();
	private boolean anonymous;
	private String value; // This is kind of a hack, i just want to be able to stuff extra arbitrary info into a person. 
						  //initial use is for the movie page to return how many reviews each person has done
	
	private GStyle style;
	
	

//	private String creatorTagId; //The person's creator tag id.  Initially added for person profile
//	
//	public String getCreatorTagId() {
//		return creatorTagId;
//	}
//
//	public void setCreatorTagId(String creatorTagId) {
//		this.creatorTagId = creatorTagId;
//	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object that) {
		if(that == null) return false;
		if(that instanceof GPerson)
			return this.getPersonId().equals(((GPerson)that).getPersonId());
		else
			return super.equals(that);
	}
	
	@Override
	public int hashCode() {
		return this.getPersonId().hashCode();
	}
	
	public List<GUserObject> getObjects() {
		return objects;
	}
	public void setObjects(List<GUserObject> objects) {
		this.objects = objects;
	}
	public List<GPrivilege> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<GPrivilege> privileges) {
		this.privileges = privileges;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public GImage getImage() {
		return image;
	}
	public void setImage(GImage image) {
		this.image = image;
	}
	
	public GStyle getStyle() {
		return style;
	}

	public void setStyle(GStyle style) {
		this.style = style;
	}
	
	public String toString(){
		return "Login: " + getLogin() + " Avatar: " + (getImage() == null ? "" : getImage().getName());
	}
	
	//TODO: figure out where to put this stuff for real :-(
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	public boolean isAdministrator(){
		return hasPrivilege(PrivilegeConstants.ADMINISTRATOR);
	}
	
	/**
	 * Utility function to search for a specific privileges value. 
	 * @param value
	 * @return
	 */
	public boolean hasPrivilege(String value){		
		if(value == null || privileges == null) return false;
		List<GPrivilege> privileges = getPrivileges();
		for(GPrivilege priv : privileges){
			if(value.equals(priv.getValue()))
				return true;
		}
		return false;
	}
	
	public List<GUserObject> getWebPageUserObjects(){
		List<GUserObject> list = new ArrayList<GUserObject>();
		for(GUserObject uo : getObjects()){
			if(UserObjectConstants.TYPE_WEBPAGE.equals(uo.getType()))
				list.add(uo);
		}
		return list;
	}
	
	public boolean hasObject(String type){
		return getObjectType(type) != null;
	}
	
	public boolean isNwsEnabled(){
		return hasObject(UserObjectConstants.TYPE_ENABLE_NWS);
	}
	
	public GUserObject getObjectType(String type){
		if(objects == null) return null;
		for(GUserObject object : objects){
			if(type.equals(object.getType()))
				return object;
		}
		return null;
	}
}
