package org.ttdc.persistence.migration;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="webComments.getall", query="SELECT c FROM WebComments as c WHERE deleted != 1"),
	@NamedQuery(name="webComments.getForMainId", query="SELECT c FROM WebComments as c WHERE deleted != 1 AND mainId=:mainId")
})
public class WebComments {
	private int id;
	private Date dateAdded;
	private int userId;
	private int mainId;
	private String entry;
	
	@Id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getMainId() {
		return mainId;
	}
	public void setMainId(int mainId) {
		this.mainId = mainId;
	}
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	
}
