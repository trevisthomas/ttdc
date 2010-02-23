package org.ttdc.persistence.migration;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@Entity
@NamedQueries({
	//@NamedQuery(name="webuser.getall", query="SELECT u FROM WebUsers as u WHERE dateLastAccess is not NULL")
	//@NamedQuery(name="webuser.getall", query="SELECT u FROM WebUsers as u WHERE id in (select distinct userId from WebComments)")
	@NamedQuery(name="webuser.getall", query="SELECT u FROM WebUsers as u WHERE id in (select distinct userId from WebComments) or DateLastAccess is not null")
})
public class WebUsers {
	private int id;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private Date dateAdded;
	private String bio;
	private String website;
	private Date birthday;
	private int hits;
	private String guid;
	
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	@Column(name="msrepl_tran_version")
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@Id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
