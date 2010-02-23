package org.ttdc.persistence.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DocumentId;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name="SHACKTAG")
@NamedQueries({
	@NamedQuery(name="shacktag.getAll", query="FROM Shacktag")
})
public class Shacktag {
	private String guid;
	private String name;
	private String openKey;
	private String closeKey;
	private String openTag;
	private String closeTag;
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	@DocumentId
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getOpenKey() {
		return openKey;
	}
	public void setOpenKey(String openKey) {
		this.openKey = openKey;
	}
	public String getCloseKey() {
		return closeKey;
	}
	public void setCloseKey(String closeKey) {
		this.closeKey = closeKey;
	}
	public String getOpenTag() {
		return openTag;
	}
	public void setOpenTag(String openTag) {
		this.openTag = openTag;
	}
	public String getCloseTag() {
		return closeTag;
	}
	public void setCloseTag(String closeTag) {
		this.closeTag = closeTag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
