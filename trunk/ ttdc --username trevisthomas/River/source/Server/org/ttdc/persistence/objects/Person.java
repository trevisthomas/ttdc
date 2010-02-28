package org.ttdc.persistence.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.PostFormatter;
import org.ttdc.struts.network.common.SecureUser;

@Entity
@NamedQueries({
	//experimenting with FROM clause queries
	//@NamedQuery(name="person.getByGuid", query="SELECT p, priv, uo FROM  Privilege priv, UserObject uo, Person as p LEFT OUTER JOIN FETCH p.objects LEFT OUTER JOIN FETCH p.style LEFT OUTER JOIN FETCH p.image WHERE p.personId=:guid"),
	@NamedQuery(name="person.getAllForCache", query="FROM Person"),
	@NamedQuery(name="person.getByGuid", query="SELECT p FROM Person as p  WHERE p.personId=:guid"),
	@NamedQuery(name="person.getByGuids", query="SELECT p FROM Person as p  WHERE p.personId in(:personIds)"),
	@NamedQuery(name="person.authenticate", query="SELECT p FROM Person as p  WHERE p.login=:login AND p.password=:password"),
	@NamedQuery(name="person.authenticateByGuid", query="SELECT p FROM Person as p  WHERE p.personId=:guid AND p.password=:password"),
	@NamedQuery(name="person.getAll", query="SELECT p FROM Person as p LEFT OUTER JOIN FETCH p.style LEFT OUTER JOIN FETCH p.image order by hits desc"),
	@NamedQuery(name="person.getAllActive", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY login"),
	@NamedQuery(name="person.getByLogin", query="SELECT p FROM Person as p  WHERE p.login=:login"),
	//@NamedQuery(name="person.getMovieReviewers", query="SELECT p FROM Person as p WHERE p.personId IN ()")
	@NamedQuery(name="person.getMovieReviewers", query="SELECT p from Person as p where p.personId in (SELECT DISTINCT ass.creator.personId FROM AssociationPostTag as ass INNER JOIN ass.tag WHERE ass.tag.type='RATING')"),
	@NamedQuery(name="person.getTraffic", query="SELECT p from Person as p ORDER BY p.lastAccessDate DESC"),
	
	@NamedQuery(name="person.getAllActiveOrderByLogin", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY login"),
	@NamedQuery(name="person.getAllActiveOrderByLoginDesc", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY login DESC"),
	@NamedQuery(name="person.getAllActiveOrderByHits", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY hits"),
	@NamedQuery(name="person.getAllActiveOrderByHitsDesc", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY hits DESC"),
	@NamedQuery(name="person.getAllActiveOrderByLastAccessed", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY lastAccessDate"),
	@NamedQuery(name="person.getAllActiveOrderByLastAccessedDesc", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY lastAccessDate DESC"),
	@NamedQuery(name="person.getAllActiveOrderByEmail", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY email"),
	@NamedQuery(name="person.getAllActiveOrderByEmailDesc", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY email DESC"),
	@NamedQuery(name="person.getAllActiveOrderByName", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY name"),
	@NamedQuery(name="person.getAllActiveOrderByNameDesc", query="SELECT p FROM Person as p WHERE p.status!='"+Person.STATUS_INACTIVE+"' ORDER BY name DESC"),
	
})


	


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements SecureUser, HasGuid{
	public final static String STATUS_ACTIVE = "ACTIVE";
	public final static String STATUS_LOCKED = "LOCKED";
	public final static String STATUS_INACTIVE = "INACTIVE";
	
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
	private List<UserObject> objects = new ArrayList<UserObject>();
	private Style style; //I might remove this and just use a UserObj to represent it
	private Image image;
	private List<Privilege> privileges = new ArrayList<Privilege>();
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof Person){
			Person that = (Person)obj;
			return this.personId.equals(that.getPersonId());
		}
		else
			return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.personId.hashCode();
	}
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	@Transient
	public String getUniqueId() {
		return getPersonId();
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
	@Transient
	public long getRawDate(){
		return getDate().getTime();
	}
	
	@Column(name="LAST_ACCESS_DATE")
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	@Transient
	public String getRawLastAccessDate(){
		return ""+lastAccessDate.getTime();
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
	
	@Transient
	public String getFormattedBio(){
		return  PostFormatter.getInstance().format(bio);
	}
	
	@OneToMany(mappedBy="owner")
	@Fetch(value=FetchMode.SUBSELECT)
    //@OrderBy("UserObject.date")//This has problems in MySQL for some reason.
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public List<UserObject> getObjects() {
		return objects;
	}
	public void setObjects(List<UserObject> objects) {
		this.objects = objects;
	}
	/*
	public void addObject(UserObject obj){
		if(this.objects == null){
			objects = new ArrayList<UserObject>();
		}
		objects.add(obj);
	}
	*/
	public boolean hasObject(String type){
		return getObjectType(type) != null;
	}
	
	public boolean removeUserObject(UserObject object){
		return objects.remove(object);
	}
	
	@Transient
	public boolean isNwsEnabled(){
		return hasObject(UserObject.TYPE_ENABLE_NWS);
	}
	
	
	
	
	/**
	 * This method assumes that a user wont have more than one object of the same type.
	 * It's intended for the ones that work that way.
	 * 
	 * @param type
	 * @return
	 */
	@Transient
	public UserObject getObjectType(String type){
		if(objects == null) return null;
		for(UserObject object : objects){
			if(type.equals(object.getType()))
				return object;
		}
		return null;
	}
	/*
	@Transient 
	public boolean isAutoRefresh(){
		UserObject mode = getObjectType(UserObject.TYPE_AUTO_UPDATE_MODE);
		if(mode == null) return false;
		if(UserObject.VALUE_AUTO_UPDATE_FULL.equals(mode.getValue()) || UserObject.VALUE_AUTO_UPDATE_NOTIFY.equals(mode.getValue()))
			return true;
		else
			return false;
	}
	@Transient
	public boolean isAutoRefreshNotify(){
		UserObject mode = getObjectType(UserObject.TYPE_AUTO_UPDATE_MODE);
		if(mode == null) return false;
		if(UserObject.VALUE_AUTO_UPDATE_NOTIFY.equals(mode.getValue())){
			return true;
		}
		else
			return false;
		
		
		
	}
	@Transient
	public boolean isAutoRefreshFull(){
		UserObject mode = getObjectType(UserObject.TYPE_AUTO_UPDATE_MODE);
		if(mode == null) return false;
		if(UserObject.VALUE_AUTO_UPDATE_FULL.equals(mode.getValue())){
			return true;
		}
		else
			return false;
	}
	
	
	private String notificationMode = null;
	@Transient 
	public String getNotificationMode(){
		if(notificationMode == null){
			UserObject mode = getObjectType(UserObject.TYPE_AUTO_UPDATE_MODE);
			if(mode == null) 
				notificationMode = "";
			else
				notificationMode = mode.getValue();
		}
		return notificationMode;
	}
	public void setNotificationMode(String mode){
		notificationMode = mode;
	}
	*/
	
	@Transient
	public boolean isHierarchy(){
		return getFrontPageMode().equals(UserObject.VALUE_HIERARCHY);
	}
	@Transient
	public boolean isFlat(){
		return getFrontPageMode().equals(UserObject.VALUE_FLAT);
	}

	
	public String frontPageMode = null;
	@Transient
	public String getFrontPageMode() {
		if(frontPageMode == null){
			UserObject mode = getObjectType(UserObject.TYPE_FRONTPAGE_MODE);
			if(mode == null) 
				frontPageMode = UserObject.VALUE_FLAT;//UserObject.VALUE_HIERARCHY;
			else
				frontPageMode = mode.getValue();
		}
		return frontPageMode;
	}

	public void setFrontPageMode(String frontPageMode) {
		this.frontPageMode = frontPageMode;
	}
	
	
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
	@JoinColumn(name="STYLE_GUID")
	public Style getStyle() {
//		if(style != null)
//			return style;
//		else
//			return ThemeService.getInstance().getDefaultStyle(); //Last hack before production :-/
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="IMAGE_GUID")
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
	@Override
	public String toString() {
		return personId+":"+name+"("+login+")" + getObjects();
	}
	
	@ManyToMany(
		targetEntity=Privilege.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
    	name="ASSOCIATION_PERSON_PRIVILEGE",
        joinColumns={@JoinColumn(name="PERSON_GUID")},
        inverseJoinColumns={@JoinColumn(name="PRIVILEGE_GUID")}
    )
    @Fetch(value=FetchMode.SUBSELECT)
    @OrderBy(value="name")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Privilege> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
	
	

	/**
	 * 
	 * @return
	 */
	boolean anonymous = false;
	@Transient
	public boolean isAnonymous(){
		return anonymous;
	}
	public void setAnonymous(boolean anonymous){
		this.anonymous = anonymous;
	}
	
	/**
	 * Checks the Person's privileges to see if they are an administrator.
	 * @return
	 */
	@Transient
	public boolean isAdministrator(){
		return hasPrivilege(Privilege.ADMINISTRATOR);
	}
	
	@Transient
	public boolean isActive(){
		return STATUS_ACTIVE.equals(getStatus());
	}
	
	@Transient
	public boolean isLocked(){
		return STATUS_LOCKED.equals(getStatus());
	}

	/**
	 * Utility function to search for a specific privileges value. 
	 * @param value
	 * @return
	 */
	public boolean hasPrivilege(String value){		
		if(value == null || privileges == null) return false;
		List<Privilege> privileges = getPrivileges();
		for(Privilege priv : privileges){
			if(value.equals(priv.getValue()))
				return true;
		}
		return false;
	}
	
	/**
	 * Adds the privilege to this user.  Returns true if i works, false if the user already has it
	 * @param priv
	 * @return
	 */
	public boolean addPrivilege(Privilege priv){
		boolean result = false;
		if(hasPrivilege(priv.getValue())){
			result = false;
		}
		else{
			privileges.add(priv);
			result = true;
		}
		return result;
	}
	/**
	 * Removes this privledge from the user 
	 * @param priv
	 * @return
	 */
	public boolean removePrivilege(Privilege priv){
		boolean result = false;
		if(!hasPrivilege(priv.getValue())){
			result = false;
		}
		else{
			privileges.remove(priv);
			result = true;
		}
		return result;
	}
	
	/**
	 * Gets a filtered list of user objects.
	 * 
	 * @return
	 */
	@Transient
	public List<UserObject> getWebPageUserObjects(){
		List<UserObject> list = new ArrayList<UserObject>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_WEBPAGE.equals(uo.getType()))
				list.add(uo);
		}
		return list;
	}
	@Transient
	public List<UserObject> getWidgetUserObjects(){
		List<UserObject> list = new ArrayList<UserObject>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_WIDGET.equals(uo.getType()))
				list.add(uo);
		}
		return list;
	}
	
	@Transient
	public List<UserObject> getFilterUserObjects(){
		List<UserObject> list = new ArrayList<UserObject>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_FILTER_TAG.equals(uo.getType()))
				list.add(uo);
		}
		return list;
	}
	
	
	/**
	 * retrieves the lits of tag id's that this user has filtered out
	 * 
	 * @return
	 */
	@Transient
	public List<String> getFilteredTagIds(){
		List<String> filteredTagIds = new ArrayList<String>();
		
		if(!isNwsEnabled())
			filteredTagIds.add(UserService.getInstance().getNwsTagId());
		if(!getHasPrivateAccess())
			filteredTagIds.add(UserService.getInstance().getPrivateTagId());
		return filteredTagIds;
	}
	
	/**
	 * Get Front Page filtered TagId's
	 * @return
	 */
	@Transient
	public List<String> getFrontPageFilteredTagIds(){
		List<String> filteredTagIds = new ArrayList<String>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_FILTER_TAG.equals(uo.getType()))
				filteredTagIds.add(uo.getValue());
		}
		if(!isNwsEnabled())
			filteredTagIds.add(UserService.getInstance().getNwsTagId());
		if(!getHasPrivateAccess())
			filteredTagIds.add(UserService.getInstance().getPrivateTagId());
		return filteredTagIds;
	}
	
	
	
	
	
	/**
	 * Returns left right or both.  This is to let the front page know which layout should be used
	 * to render this user and their widgets.  It's really a sad hack because it looks like my CSS
	 * so far will need to know 
	 * 
	 * @return
	 */
	/*
	@Transient
	public String getWidgetDisplayLayoutHint(){
		boolean left = false;
		boolean right = false;
		
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_WIDGET.equals(uo.getType())){
				if(UserObject.POSITION_RIGHT.equals(uo.getName()))
					right = true;
				else
					left = true;
			}
		}
		
		if(left & right)
			return "layout-both";
		else if(left)
			return "layout-left";
		else if(right)
			return "layout-right";
		else
			return "layout-none";
				
	}
	*/
	
	/*
	 * Trevis this is a hack. you just needed a place to store location so you stuck it in name for widgets. Might be ok. Maybe 
	 * you should add an options attribute to the class.
	 */
	@Transient
	public List<UserObject> getWidgetUserObjectsLeft(){
		List<UserObject> list = new ArrayList<UserObject>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_WIDGET.equals(uo.getType()) && !UserObject.POSITION_RIGHT.equals(uo.getName()))
				list.add(uo);
		}
		return list;
	}
	@Transient
	public List<UserObject> getWidgetUserObjectsRight(){
		List<UserObject> list = new ArrayList<UserObject>();
		for(UserObject uo : getObjects()){
			if(UserObject.TYPE_WIDGET.equals(uo.getType())&& UserObject.POSITION_RIGHT.equals(uo.getName()))
				list.add(uo);
		}
		return list;
	}
	@Transient
	public boolean getHasPostPrivilege(){
		if(isAdministrator()) return true;
		else
			return hasPrivilege(Privilege.POST);
	} 
	@Transient
	public boolean getHasPrivateAccess(){
		if(isAdministrator()) return true;
		else
			return hasPrivilege(Privilege.PRIVATE);
	}
	@Transient
	public boolean getHasVotingPrivilege(){
		if(isAdministrator()) return true;
		else
			return hasPrivilege(Privilege.VOTER);
	}
	
	public final static int VALUE_DEFAULT_NUM_COMMENTS_FRONTPAGE = 20;
	public final static int VALUE_DEFAULT_NUM_COMMENTS_PERTHREAD = 50;
	@Transient
	public int getNumCommentsFrontpage(){
		UserObject uo =getObjectType(UserObject.TYPE_NUM_COMMENTS_FRONTPAGE);
		if(uo != null)
			return  Integer.valueOf(uo.getValue());
		else
			return VALUE_DEFAULT_NUM_COMMENTS_FRONTPAGE;
			
	}
	
	@Transient
	public int getNumCommentsThreadPage(){
		UserObject uo =getObjectType(UserObject.TYPE_NUM_COMMENTS_PERTHREAD);
		if(uo != null)
			return  Integer.valueOf(uo.getValue());
		else
			return VALUE_DEFAULT_NUM_COMMENTS_PERTHREAD;
	}
	
	public void initialize(){
		Hibernate.initialize(objects);
		Hibernate.initialize(image);
		for(UserObject o : objects){
			Hibernate.initialize(o.getTemplate());
		}
		Hibernate.initialize(privileges);
	}

	

	
	
}
