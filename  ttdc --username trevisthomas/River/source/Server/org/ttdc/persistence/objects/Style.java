package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Table(name="STYLE")
@Entity
@NamedQueries({
	@NamedQuery(name="style.getById", query="FROM Style s WHERE s.styleId=:styleId"),
	@NamedQuery(name="style.getByName", query="FROM Style s WHERE s.name=:name"),
	/*DELETE THIS, THIS IS THE OLD WAY*/@NamedQuery(name="style.getDefault", query="FROM Style s WHERE s.name='Default'"),
	@NamedQuery(name="style.getDefaultStyle", query="FROM Style s WHERE s.defaultStyle=1"),
	@NamedQuery(name="style.getAll", query="FROM Style s"),
	@NamedQuery(name="style.clearDefaultStyle", query="UPDATE Style s SET defaultStyle=0")
})

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Style {
	private String styleId;
	private String name;
	private String css;
	private String description;
	private Boolean defaultStyle;
	private Date date = new Date();
	private Person creator;
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="CREATOR_GUID")
	public Person getCreator() {
		return creator;
	}
	public void setCreator(Person creator) {
		this.creator = creator;
	}
	
	public void setDefaultStyle(Boolean defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	@Column(name="DEFAULT_STYLE")
	public Boolean isDefaultStyle() {
		return defaultStyle;
	}
	

}
