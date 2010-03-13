package org.ttdc.persistence.objects;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.ClassBridges;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;
import org.ttdc.persistence.util.BridgeForIsTagTitle;
import org.ttdc.persistence.util.BridgeForRelatedCreatorOnTag;
import org.ttdc.persistence.util.BridgeForRelatedDatesOnTag;
import org.ttdc.persistence.util.BridgeForRelatedTagsOnTag;
import org.ttdc.persistence.util.FilterFactoryExcludeTagTypes;
import org.ttdc.persistence.util.FilterFactoryForPostDateRange;
import org.ttdc.persistence.util.FilterFactoryForTokenizedTagIds;
import org.ttdc.persistence.util.FilterFactoryTagIsTitle;


/**
 * Tags are everything.   Flagged NWS, Tagged, Person ratings... maybe other things. In case you forgot, 
 * the idea was that you'd create tags for every rating level and just tag posts with them 
 * @author Trevis
 *
 */
@Table(name="TAG")
@Entity
@NamedQueries({
	@NamedQuery(name="tag.getAll", query="FROM Tag"),
//	@NamedQuery(name="tag.getByTagId", query="select tag FROM Tag as tag WHERE tag.tagId=:tagId"),
//	@NamedQuery(name="tag.getByValue", query="select tag FROM Tag as tag INNER JOIN FETCH tag.creator LEFT JOIN tag.creator.style WHERE tag.value=:value"),
//	@NamedQuery(name="tag.getByValueAndType", query="select tag FROM Tag as tag INNER JOIN FETCH tag.creator LEFT JOIN tag.creator.style WHERE tag.value=:value AND tag.type=:type"),
//	@NamedQuery(name="tag.getByValueLike", query="select tag FROM Tag as tag WHERE tag.value LIKE :value"),
	@NamedQuery(name="tag.getByTagIds", query="select tag FROM Tag as tag WHERE tag.tagId IN (:tagIds)"),
//	@NamedQuery(name="tag.getDateYears", query="select t from Tag t where t.type = 'DATE_YEAR' order by t.value"),
	//Never used
	//@NamedQuery(name="tag.getCreatorTag", query="select t from Tag t where t.creator.personId = :personId AND t.type = 'CREATOR'")
	
})



@ClassBridges({
	@ClassBridge(name="creator", impl=BridgeForRelatedCreatorOnTag.class, index=Index.TOKENIZED),
	@ClassBridge(name="tagIds", impl=BridgeForRelatedTagsOnTag.class, index=Index.TOKENIZED),
	@ClassBridge(name="date", impl=BridgeForRelatedDatesOnTag.class, index=Index.TOKENIZED),
	@ClassBridge(name="title", impl=BridgeForIsTagTitle.class, index=Index.UN_TOKENIZED),
})

//testing!!
@FullTextFilterDefs({
	@FullTextFilterDef( name="tagWithTagFilter", impl=FilterFactoryForTokenizedTagIds.class ),
	@FullTextFilterDef( name="tagDateRangeFilter", impl=FilterFactoryForPostDateRange.class ),
	@FullTextFilterDef( name="tagTypeExcludeFilter", impl=FilterFactoryExcludeTagTypes.class ),
	@FullTextFilterDef( name="tagIsTitleFilter", impl=FilterFactoryTagIsTitle.class ),
})






//@Loader(namedQuery = "tag.getByTagId")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Indexed
public class Tag implements HasGuid{
	public final static String TYPE_TOPIC = "TOPIC";
	public final static String TYPE_RATING = "RATING";
	public final static String TYPE_AVERAGE_RATING = "AVERAGE_RATING";
	
//	public final static String TYPE_DISPLAY = "DISPLAY";
//	public final static String TYPE_AVERAGE_RATING = "AVERAGE_RATING";
//	public final static String TYPE_CREATOR = "CREATOR"; 
//	public final static String TYPE_DATE_YEAR = "DATE_YEAR"; //Check and create when need since the set grows over time
//	public final static String TYPE_DATE_MONTH = "DATE_MONTH"; //(Should probably pre-create all of these since the set is fixed)
//	public final static String TYPE_DATE_DAY = "DATE_DAY"; //Precreate for same reason above
//	public final static String TYPE_STATUS = "STATUS"; //Values: LOCKED 
//	public final static String TYPE_VISIBILITY = "VISIBILITY"; //Values: TRUSTED, ADMIN
//	public final static String TYPE_SORT_TITLE = "SORT_TITLE"; 
//	public final static String TYPE_REVIEW = "REVIEW";
//	public final static String TYPE_MOVIE = "MOVIE";
//	public final static String TYPE_RELEASE_YEAR = "RELEASE_YEAR"; //Initially for movies. Because the year it came out is often different from the year i added it
//	public final static String TYPE_LEGACY_THREAD = "LEGACY_THREAD";
//	public final static String TYPE_RATABLE = "RATABLE";//This tag
//	public final static String TYPE_EARMARK = "EARMARK";//Authenticated users can ear mark a post so that they can find it later. Value of this tag type should be the creator's guid 
//	public final static String TYPE_URL = "URL"; //Initially for imdb links to movies but could be used for lots of things
//	public final static String VALUE_NWS = "NWS";
//	public final static String VALUE_INF = "INF";
//	public final static String VALUE_PRIVATE = "PRIVATE";
//	public final static String VALUE_LOCKED = "LOCKED";  //This is intended for root posts to lock a thread
//	public final static String VALUE_DELETED = "DELETED"; //Once tagged as deleted the post wont show up for anyone (maybe admin will still see)
//	public final static String VALUE_LINK = "LINK";//Tag for posts with links in them.
//	public static final String TYPE_WEEK_OF_YEAR = "WEEK_OF_YEAR";

	
	public final static String VALUE_RATING_5 = "5.0";
	public final static String VALUE_RATING_4_5 = "4.5";
	public final static String VALUE_RATING_4 = "4.0";
	public final static String VALUE_RATING_3_5 = "3.5";
	public final static String VALUE_RATING_3 = "3.0";
	public final static String VALUE_RATING_2_5 = "2.5";
	public final static String VALUE_RATING_2 = "2.0";
	public final static String VALUE_RATING_1_5 = "1.5";
	public final static String VALUE_RATING_1 = "1.0";
	public final static String VALUE_RATING_0_5 = "0.5";
	public final static String VALUE_RATING_0 = "0.0"; //Not sure about this one
	
	
		
	
	private String tagId;
	private String type; //THEME, DISPLAY, RATING? ...CREATOR
	private String value; //RATING_5, Morstles of Political Goodness, Review etc
	private Date date = new Date(); //could be informative for some, highly functional for others (like review)
//	private Person creator;
//	private String description; //I only added this field to capture the 'subject' from old ttdc forums, which are now tags
	//private int count; //Transient except in special cases (used by named native query for v7) 
	private int mass;
	private String sortValue;
	
	
	public static class ByReferenceComparator implements Comparator<Tag>{
		private List<String> reference;
		/**
		 * 
		 * @param A list of tagId's to be used as a template for sotring
		 */
		public ByReferenceComparator(List<String> reference){
			this.reference = reference;
		}
		public int compare(Tag o1, Tag o2) {
			Integer r1 = reference.indexOf(o1.getTagId());
			Integer r2 = reference.indexOf(o2.getTagId());
			return r1.compareTo(r2);
		}
	}
	
	/**
	 * I created this for version 7's tag popularity page.  
	 * 
	 */
	public static class AlphebeticalByValueComparator implements Comparator<Tag>{
		public AlphebeticalByValueComparator(){
			
		}
		public int compare(Tag o1, Tag o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Tag)
			return this.getTagId().equals(((Tag)obj).getTagId());
		else
			return false;
	}
	@Override
	public String toString() {
//		return tagId +":"+ type +":"+ value + " ["+ creator +"]";
		return tagId +":"+ type +":"+ value ;
	}
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	@DocumentId
	public String getTagId() {
		return tagId;
	}
	@Transient
	public String getUniqueId() {
		return getTagId();
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	
	@Field(index=Index.UN_TOKENIZED)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Field(index=Index.TOKENIZED, store=Store.NO, name="topic")
	public String getValue() {
//		if(type == TYPE_CREATOR){ 
//			//Added so that the value returned from creator tags is always the current login.
//			//Should probably set real value to blank just to reinforce that it is ignored
//			return getCreator().getLogin();
//		}
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
		
//	@Field(index=Index.UN_TOKENIZED)
//    @DateBridge(resolution=Resolution.DAY)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
//	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
//	@JoinColumn(name="CREATOR_GUID")
//	public Person getCreator() {
//		return creator;
//	}
//	public void setCreator(Person creator) {
//		this.creator = creator;
//	}
	
	@Field(index=Index.UN_TOKENIZED, name="mass_tag" )
	@Formula(" (SELECT count(ass.guid) FROM ASSOCIATION_POST_TAG ass WHERE ass.tag_guid=GUID) ")
	public int getMass() {
		return mass;
	}
	public void setMass(int mass) {
		this.mass = mass;
	}
	
	@Field(index=Index.UN_TOKENIZED, store=Store.NO, name="sort_topic")
	@Column(name="SORT_VALUE")
	public String getSortValue() {
		return sortValue;
	}
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}

//	@Transient
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	} 
	
	/**
	 * I now use the rating value as the css tag but css cant have '.' in the
	 * locator so i replace then with '_' this change was done when i switched
	 * to css derived stars.
	 * @return
	 */
	@Transient
	public String getValueRating(){
		return getValue().replace('.', '_');
	}
	/**
	 * This custom method either returns the actual value or a decorated version of the value using the type to decide which.
	 * 
	 * I added this so that earmark tags could use the creator's id as the value, but display a nice string in the tag browser.
	 * 
	 * @return
	 */
//	@Transient
//	public String getDisplayValue(){
//		if(TYPE_EARMARK.equals(getType())){
//			return getCreator().getLogin()+"'s Earmark";
//		}
//		else{
//			return value;
//		}
//	}
	
//	@Transient
//	public int getCount() {
//		return count;
//	}
//	public void setCount(int count) {
//		this.count = count;
//	}
	
	
	
}
