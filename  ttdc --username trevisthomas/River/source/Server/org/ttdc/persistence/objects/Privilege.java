package org.ttdc.persistence.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
	@NamedQuery(name="privilege.getAll", query="SELECT priv FROM Privilege as priv ORDER BY priv.name"),
	@NamedQuery(name="privilege.getByGuid", query="SELECT priv FROM Privilege as priv WHERE priv.privilegeId=:guid"),
	@NamedQuery(name="privilege.getByValue", query="SELECT priv FROM Privilege as priv WHERE priv.value=:value ORDER BY priv.name")
})
public class Privilege implements HasGuid{
	private String privilegeId;
	private String name;
	private String value;
	
	public final static String ADMINISTRATOR = "ADMIN";
	public final static String POST = "POST";
	public final static String VOTER = "VOTE";
	public final static String PRIVATE = "PRIVATE";
	
	
	@Override
	public String toString() {
		return value;
	}
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}
	@Transient
	public String getUniqueId() {
		return getPrivilegeId();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
