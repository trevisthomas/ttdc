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
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Loader;

/**
 * At first i was just going to keep the user objects and make 'template' a type of user object
 * but i changed my mind.  They should be separate from the user object. 
 * 
 * @author Trevis
 *
 */
@NamedQueries({
	@NamedQuery(name="userObjectTemplate.getAll", query="SELECT t FROM UserObjectTemplate as t"),
	@NamedQuery(name="userObjectTemplate.getById", query="SELECT t FROM UserObjectTemplate as t WHERE t.templateId=:guid"),
	@NamedQuery(name="userObjectTemplate.getOfType", query="SELECT t FROM UserObjectTemplate as t WHERE t.type=:type"),
	@NamedQuery(name="userObjectTemplate.getForValue", query="SELECT t FROM UserObjectTemplate as t WHERE t.value=:value")
	
})
@Entity
@Table(name="USER_OBJECT_TEMPLATE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Loader(namedQuery="userObjectTemplate.getById")
public class UserObjectTemplate {
	public final static String TEMPLATE_WEBPAGE = "TEMPLATE_WEBPAGE";
	public final static String TEMPLATE_WIDGET = "TEMPLATE_WIDGET";
	
	public final static String WIDGET_TRAFFIC = "trafficWidget";
	public final static String WIDGET_CALENDAR = "calendarWidget";
	public final static String WIDGET_MOVIE = "movieWidget";
	public final static String WIDGET_NEW_THREADS = "newThreadsWidget";
	public final static String WIDGET_HOT_TOPICS = "hotTopicsWidget";
	public final static String WIDGET_MOST_POPULAR_THREADS = "mostPopularThreadsWidget";
	
	private String templateId;
	private String value;
	private Image image;
	private String name;
	private Person creator;
	private String type;
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	
	
	@ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="IMAGE_GUID")
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="CREATOR_GUID")
	public Person getCreator() {
		return creator;
	}
	public void setCreator(Person creator) {
		this.creator = creator;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
