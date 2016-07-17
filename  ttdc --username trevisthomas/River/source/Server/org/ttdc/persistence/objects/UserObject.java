package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Loader;

/* This class will represent any user specific information. 
 * Person homepage, gTalk id, facebook link, ttdc settings. anything
 * I'm thinking that one flexible table will make the DB better normalized
 */

@Entity
@Table(name="USER_OBJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
	@NamedQuery(name="object.getAll", query="FROM UserObject"),
	@NamedQuery(name="object.getByGuid", query="SELECT o FROM UserObject as o LEFT OUTER JOIN FETCH o.template LEFT OUTER JOIN FETCH o.template.image WHERE o.objectId=:objectId"),
	@NamedQuery(name="object.getForOwner", query="SELECT o FROM UserObject as o LEFT OUTER JOIN FETCH o.template LEFT OUTER JOIN FETCH o.template.image WHERE o.owner.personId=:personId")
})
//@SQLDelete( sql)

@Loader(namedQuery = "object.getByGuid")	//Not sure if this getby guid loader works properly.  Verify
public class UserObject implements HasGuid {
	public final static String TYPE_OPTION = "OPTION"; //For site settings.
	public final static String TYPE_WEBPAGE = "WEBPAGE"; //For links to other user pages. Personal sites, facebook, myspace whatever.
	public final static String TYPE_WIDGET = "WIDGET"; //For personal widget choices.
	public final static String TYPE_FILTER_TAG = "FILTER_TAG"; //For filtering items with this tag
	public final static String TYPE_FILTER_THREAD = "FILTER_THREAD"; //For posts from the front page that are in this tag
	
	public final static String TYPE_ENABLE_NWS = "ENABLE_NWS"; //When this UO is present nws content is shown to this user
	public final static String TYPE_FRONTPAGE_MODE = "FRONT_PAGE_MODE"; //When this UO is present nws content is shown to this user
	public final static String TYPE_NUM_COMMENTS_FRONTPAGE = "TYPE_NUM_COMMENTS_FRONTPAGE";
	public final static String TYPE_NUM_COMMENTS_PERTHREAD = "TYPE_NUM_COMMENTS_THREAD_PAGE";
	
	public final static String TYPE_DEVICE_TOKEN_FOR_PUSH_NOTIFICATION = "deviceTokenForPushNotification";

	public final static String TYPE_AUTO_UPDATE_MODE = "AUTO_UPDATE_MODE";
	
	public final static String VALUE_HIERARCHY = "HIERARCHY";
	public final static String VALUE_FLAT = "FLAT";
	
	public final static String VALUE_AUTO_UPDATE_FULL = "AUTO_UPDATE_FULL"; 
	public final static String VALUE_AUTO_UPDATE_NOTIFY = "AUTO_UPDATE_NOTIFY";
	public final static String VALUE_AUTO_UPDATE_NONE = "AUTO_UPDATE_NONE";
	public final static String POSITION_LEFT = "POSITION_LEFT";
	public final static String POSITION_RIGHT = "POSITION_RIGHT";
	
	
	private String objectId;
	private String url;
	private String name;
	private String type; //This will probably be pretty important since this obj can be almost anything
	private String value;
	private String description;
	private Image thumbnail;  //TREVIS REMOVE THIS!!
	private Date date = new Date();
	private Person owner;
	private UserObjectTemplate template;
	
	@Override
	public String toString() {
		return objectId+":"+type+":"+url;
	}
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	@Transient
	public String getUniqueId() {
		return getObjectId();
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	/*
	 * Trevis: During early testing you realized that without specifying 
	 * lazy that reading a user always caused it to grab these thumbnails.
	 * Meaning that fairly very complex hibernate query was generated to read the
	 * images after reading the user table.  Because of this you moved the user image
	 * to the user table and left the UserObject thumbnail to be used for other things
	 * that are read less often like site link graphics.
	 */
	@ManyToOne (fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="THUMBNAIL_GUID")
	public Image getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="OWNER_GUID")
	public Person getOwner() {
		return owner;
	}
	public void setOwner(Person owner) {
		this.owner = owner;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="TEMPLATE_GUID")
	public UserObjectTemplate getTemplate() {
		return template;
	}
	public void setTemplate(UserObjectTemplate template) {
		this.template = template;
	}
}
