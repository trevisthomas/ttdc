package org.ttdc.persistence.objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.hibernate.annotations.GenericGenerator;

/**
 * This is for storing the people who currently have an active inbox for the front page.  
 * @author Trevis
 * @deprecated
 */
@Entity
@NamedQueries({
	@NamedQuery(name="inbox.getAll", query="FROM Inbox inbox")
})
public class Inbox {
	private Person person;
	private String guid;

	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@JoinColumn(name="PERSON_GUID")
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
}
