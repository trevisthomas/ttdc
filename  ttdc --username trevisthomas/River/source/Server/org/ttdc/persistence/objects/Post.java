package org.ttdc.persistence.objects;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.ClassBridges;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.util.BridgeForBodyOnPost;
import org.ttdc.persistence.util.BridgeForCreatorOnPost;
import org.ttdc.persistence.util.BridgeForPostFlag;
import org.ttdc.persistence.util.BridgeForTagOnPost;
import org.ttdc.persistence.util.BridgeForPostType;
import org.ttdc.persistence.util.BridgeForRootIdOnPost;
import org.ttdc.persistence.util.BridgeForSortTitleOnPost;
import org.ttdc.persistence.util.BridgeForTagIdsOnPost;
import org.ttdc.persistence.util.BridgeForThreadIdOnPost;
import org.ttdc.persistence.util.BridgeForTitleOnPost;
import org.ttdc.persistence.util.FilterFactoryCreator;
import org.ttdc.persistence.util.FilterFactoryForPostFlags;
import org.ttdc.persistence.util.FilterFactoryForPostDateRange;
import org.ttdc.persistence.util.FilterFactoryForType;
import org.ttdc.persistence.util.FilterFactoryForRootId;
import org.ttdc.persistence.util.FilterFactoryForThreadId;
import org.ttdc.persistence.util.FilterFactoryForTokenizedTagIds;
import org.ttdc.util.CalculateAverageRating;

@Table(name="POST")
@Indexed
@Entity
@NamedQueries({
	@NamedQuery(name="post.updateParentAndRoot", query="UPDATE Post post SET post.root.postId=:rootId, post.parent.postId=:parentId WHERE post.postId=:postId"),
	@NamedQuery(name="post.getAll", query="FROM Post post"),
	@NamedQuery(name="post.getByPostId", query="FROM Post post WHERE post.postId=:postId"),
	@NamedQuery(name="post.getBranchByRootId", query="FROM Post post WHERE post.root.postId=:rootId ORDER BY post.date"),
	@NamedQuery(name="post.getThreadByRootId", query="FROM Post post WHERE post.root.postId=:rootId ORDER BY post.date DESC"),
	@NamedQuery(name="post.getBranchsByRootId", query="SELECT post FROM Post post WHERE post.root.postId IN (:rootIds) ORDER BY post.date"),
	@NamedQuery(name="post.getByPostIds", query="FROM Post post WHERE postId IN (:postIds) ORDER BY post.date desc"),
	@NamedQuery(name="post.getAllLatest", query="FROM Post post ORDER BY post.date DESC"),
	@NamedQuery(name="post.getNewerThanDate", query="FROM Post post where post.date > :date ORDER BY post.date DESC"),
	//@NamedQuery(name="post.getByTag", query="SELECT post FROM Post post where post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId in(:tagIds))"),
	@NamedQuery(name="post.getByTopicTag", query="SELECT ass.post FROM AssociationPostTag ass WHERE ass.tag.tagId in(:tagIds) AND ass.tag.type='TOPIC'"), 
	@NamedQuery(name="post.getWithTitleTags", query="SELECT ass.post FROM AssociationPostTag ass WHERE ass.tag.tagId in(:tagIds) AND ass.title='1'"),
			
	@NamedQuery(name="object.postCounter", query="SELECT count(p.postId), p.root.postId " +
			"FROM Post p " +
			"WHERE p.root.postId IN (:rootIds) " +
			"GROUP BY p.root.postId"),
			
	@NamedQuery(name="object.daysWithContent", query="select distinct Day(p.date) as dayofmonth from Post p where Year(date) = :year AND Month(date) = :month"),
				
	@NamedQuery(name="post.getRootPostWithTagValue", query="SELECT post FROM Post post where post.parent.postId = NULL AND post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.value = :tag)"),
	//@NamedQuery(name="post.getRootPostWithTagValueAndType", query="SELECT post FROM Post post where post.parent.postId = NULL AND post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.value = :tag AND ass.tag.type = :type)"),
	@NamedQuery(name="post.findThreadRootForTitle", query="SELECT ass.post FROM AssociationPostTag ass WHERE ass.title is 1 AND ass.tag.value=:tag"),
	
	@NamedQuery(name="post.getThreadSiblings", query="SELECT post FROM Post post where post.parent.postId = NULL AND post.postId <> :threadId AND post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId = :tagId)")
	
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)


@ClassBridges({
	@ClassBridge(name="body", impl=BridgeForBodyOnPost.class, index=Index.TOKENIZED),
	@ClassBridge(name="tagIds", impl=BridgeForTagIdsOnPost.class, index=Index.TOKENIZED),
	@ClassBridge(name="rootId", impl=BridgeForRootIdOnPost.class, index=Index.UN_TOKENIZED),
	@ClassBridge(name="threadId", impl=BridgeForThreadIdOnPost.class, index=Index.UN_TOKENIZED),
	@ClassBridge(name="type", impl=BridgeForPostType.class, index=Index.UN_TOKENIZED),
	@ClassBridge(name="flag", impl=BridgeForPostFlag.class, index=Index.TOKENIZED),
	@ClassBridge(name="title_sort", impl=BridgeForSortTitleOnPost.class, index=Index.UN_TOKENIZED),
	@ClassBridge(name="title", impl=BridgeForTitleOnPost.class, index=Index.TOKENIZED),
	@ClassBridge(name="tag", impl=BridgeForTagOnPost.class, index=Index.TOKENIZED),
	@ClassBridge(name="creator", impl=BridgeForCreatorOnPost.class, index=Index.TOKENIZED),
})
@FullTextFilterDefs({
	@FullTextFilterDef( name="postWithTagFilter", impl=FilterFactoryForTokenizedTagIds.class ),
	@FullTextFilterDef( name="postWithRootIdFilter", impl=FilterFactoryForRootId.class ),
	@FullTextFilterDef( name="postWithThreadIdFilter", impl=FilterFactoryForThreadId.class ),
	@FullTextFilterDef( name="postWithTypeFilter", impl=FilterFactoryForType.class ),
	@FullTextFilterDef( name="postDateRangeFilter", impl=FilterFactoryForPostDateRange.class ),
	@FullTextFilterDef( name="postFlagFilter", impl=FilterFactoryForPostFlags.class ),
	@FullTextFilterDef( name="postCreatorFilter", impl=FilterFactoryCreator.class )
	
	
})



public class Post implements Comparable<Post>, HasGuid {
	private String postId;
	private Post parent;
	private Post root;
	private Post thread; 
	public List<Post> posts = new ArrayList<Post>();
	private List<AssociationPostTag> tagAssociations = new ArrayList<AssociationPostTag>();
	private Image image;
	private Date date = new Date();
	private Date threadReplyDate;
	private List<Entry> entries = new ArrayList<Entry>();
	private final static int EDIT_WINDOW_MS = 1000*60*20;
	private String path = "";
	private Date editDate = date;
	private Entry LatestEntry;
	private int replyCount;
	private int mass;
	
	private Person creator; 
	private long metaMask;
	private Tag titleTag;
	private Tag avgRatingTag;
	private String url;
	private Integer publishYear;
	
	public static int iCount = 0; 
	public Post(){
		iCount++;
	}
	
	public static String RELATIVE_AGE_0 = "RELATIVE_AGE_0";
	public static String RELATIVE_AGE_1 = "RELATIVE_AGE_1";
	public static String RELATIVE_AGE_2 = "RELATIVE_AGE_2";
	public static String RELATIVE_AGE_3 = "RELATIVE_AGE_3";
	public static String RELATIVE_AGE_4 = "RELATIVE_AGE_4";
	public static String RELATIVE_AGE_5 = "RELATIVE_AGE_5";
	public static String RELATIVE_AGE_6 = "RELATIVE_AGE_6";
	public static String RELATIVE_AGE_7 = "RELATIVE_AGE_7";
	public static String RELATIVE_AGE_DEFAULT = "RELATIVE_AGE_DEFAULT";
	
	
//	public final static long BITMASK_DELETED = 1L;
//	public final static long BITMASK_INF = 2L;
//	public final static long BITMASK_LEGACY = 4L;
//	public final static long BITMASK_LINK = 8L;
//	public final static long BITMASK_MOVIE = 16L;
//	public final static long BITMASK_NWS = 32L;
//	public final static long BITMASK_PRIVATE = 64L;
//	public final static long BITMASK_RATABLE = 128L;
//	public final static long BITMASK_REVIEW = 256L;
//	public final static long BITMASK_LOCKED = 512L;  //There are none of these in the v7 at the moment this has been added.
	
//	public static class ByReferenceComparator implements Comparator<Post>{
//		private List<Post> rootlist;
//		/**
//		 * 
//		 * @param reference This is a list of root posts that are sorted in the order 
//		 * that you want their roots to be listed in.  (this is for sorting the hierarchies on the
//		 * main page)
//		 */
//		public ByReferenceComparator(List<Post> reference){
//			rootlist = new ArrayList<Post>();
//			for(Post p : reference){
//				if(!rootlist.contains(p))
//					rootlist.add(p.getRoot());
//			}
//		}
//		public int compare(Post o1, Post o2) {
//			Integer r1 = rootlist.indexOf(o1.getRoot());
//			Integer r2 = rootlist.indexOf(o2.getRoot());
//			return r1.compareTo(r2);
//		}
//	}
//	
//	public static class ByLiteReferenceComparator implements Comparator<Post>{
//		private List<String> sortedList;
//		/**
//		 * 
//		 * @param reference This is a list of root posts that are sorted in the order 
//		 * that you want their roots to be listed in.  (this is for sorting the hierarchies on the
//		 * main page)
//		 */
//		public ByLiteReferenceComparator(List<PostLite> reference){
//			sortedList = new ArrayList<String>();
//			for(PostLite p : reference){
//				sortedList.add(p.getPostId());
//			}
//		}
//		public int compare(Post o1, Post o2) {
//			Integer r1 = sortedList.indexOf(o1.getPostId());
//			Integer r2 = sortedList.indexOf(o2.getPostId());
//			return r1.compareTo(r2);
//		}
//	}
//	
//	public static class ByPostIdReferenceComparator implements Comparator<Post>{
//		private List<String> sortedList;
//		/**
//		 * 
//		 * @param reference This is a list of root posts that are sorted in the order 
//		 * that you want their roots to be listed in.  (this is for sorting the hierarchies on the
//		 * main page)
//		 */
//		public ByPostIdReferenceComparator(List<String> reference){
//			sortedList = new ArrayList<String>();
//			this.sortedList.addAll(reference);
//		}
//		public int compare(Post o1, Post o2) {
//			Integer r1 = sortedList.indexOf(o1.getPostId());
//			Integer r2 = sortedList.indexOf(o2.getPostId());
//			return r1.compareTo(r2);
//		}
//	}
//	
//	public static class ThreadPathComparator implements Comparator<Post>{
//		private final List<String> sortedList;
//		/**
//		 * 
//		 * @param reference This is a list of root posts that are sorted in the order 
//		 * that you want their roots to be listed in.  (this is for sorting the hierarchies on the
//		 * main page)
//		 */
//		public ThreadPathComparator(List<String> reference){
//			this.sortedList = Collections.unmodifiableList(reference);
//		}
//		
//		public int compare(Post o1, Post o2) {
//			Integer r1 = sortedList.indexOf(o1.getThread().getPostId());
//			Integer r2 = sortedList.indexOf(o2.getThread().getPostId());
//			
//			if(r1 == -1 || r2 == -1)
//				throw new RuntimeException("ThreadPathComparator failed to sort values by reference due to a missing sort field in the reference list.");
//			
//			int diff = r1.compareTo(r2);
//			if (diff == 0)
//				diff = o1.getPath().compareTo(o2.getPath());
//			return diff;
//		}
//		
//		/*
//		public int compare(Post p1, Post p2) {
//			//return (p1.getThread().getPostId()+p1.getPath()).compareTo(p2.getThread().getPostId() + p2.getPath());
//		}
//		*/
//	}
//	
//
//	
//	public static class DateComparator implements Comparator<Post>{
//		public int compare(Post p1, Post p2) {
//			return p1.getDate().compareTo(p2.getDate());
//		}
//		
//	}
//	
//	public static class PathComparator implements Comparator<Post>{
//		public int compare(Post p1, Post p2) {
//			return p1.getPath().compareTo(p2.getPath());
//		}
//		
//	}
//	public static class DateComparatorDesc implements Comparator<Post>{
//		public int compare(Post p1, Post p2) {
//			return p2.getDate().compareTo(p1.getDate());
//		}
//		
//	}
//	public static class PostCounterComparator implements Comparator<Post>{
//		public int compare(Post p1, Post p2) {
//			Long c1 = p2.getPostCounter().getCount();
//			Long c2 = p1.getPostCounter().getCount();
//			return c1.compareTo(c2);
//			
//		}
//		
//	}
	
	
	/**
	 * This is a helper method so that post views can quickly determine if a post is within it's edit window
	 * 
	 * @return true if post is within it's editable time window, false otherwise. 
	 */
	@Transient
	public boolean isWithinEditWindow() {
		long time = getDate().getTime();
		long now = System.currentTimeMillis();
		if(now > time + EDIT_WINDOW_MS){
			return false;
		}
		return true;
	}
	
	/**
	 * Counts the posts beneath this branch
	 */
//	public int count(){
//		int count;
//		count = posts.size();
//		for(Post p : posts){
//			count += p.count();
//		}
//		return count;
//	}
	
	/**
	 * I overroad equals initially because i wanted to do 'List.contains' when recursively setting the expanded status. 
	 */
	@Override
	public boolean equals(Object that) {
		if(that == null) return false;
		if(that instanceof Post)
			return this.getPostId().equals(((Post)that).getPostId());
		else
			return super.equals(that);
	}
	
	/**
	 * You must override hashCode when override equals.
	 * 
	 * Spec: This relationship also enforces that whenever you override the equals method, you must override 
	 * the hashCode method as well. Failing to comply with this requirement usually results in undetermined, 
	 * undesired behavior of the class when confronted with Java collection classes or any other Java classes.
	 */
	@Override
	public int hashCode() {
		return this.getPostId().hashCode();
	}
	
	
	/**
	 * Sorts in reverse order by date.  I'll probably create real compairitors if i need to sort any other way.
	 */
	public int compareTo(Post that) {
		return -this.date.compareTo(that.date);
	}
	
	public String toString(){
		return "\nPOST_ID: "+getPostId()+"\nROOT_ID"+getRoot().getPostId()+"\nPARENT_ID: "+(getParent() != null ? getParent().getPostId() : "NULL") +"\nENTRY:"+(getEntry() != null ? getEntry().getBody() : "NOT POPULATED")+
		"\n\t{TAGS:"+tagAssociations+"}"
		+"\n\t"+getPosts();
	}
	
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getPostId() {
		return postId;
	}
	@Transient
	public String getUniqueId() {
		return getPostId();
	}
	
	/*
	 * This funky self reference is being done so that even the root node has a root value set making reading an
	 * entire branch with a single query quite easy.
	 */
	public void setPostId(String postId) {
		this.postId = postId;
		if(root == null)
			root = this;
	}
	
	
	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="PARENT_GUID")
	public Post getParent() {
		return parent;
	}
	
	/**
	 * WARNING!  calling set parent will put the child object into the parent's posts list!  
	 * This method is implemented this way to trick hibernate into building my hierarchy when a root 
	 * is read.
	 * 
	 * @param parent
	 */
	public void setParent(Post parent) {
		/*
		if(parent != null){
			parent.posts.add(this);
		}
		*/
		this.parent = parent;
	}
	
	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="ROOT_GUID")
	public Post getRoot() {
		return root;
	}
	public void setRoot(Post root) {
		this.root = root;
	}
	
	@OneToMany(mappedBy="post", cascade={CascadeType.ALL})
	//@Fetch(value=FetchMode.JOIN)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    //@OrderBy(value="post.date asc")
    public List<AssociationPostTag> getTagAssociations() {
		return tagAssociations;
	}
	public void setTagAssociations(List<AssociationPostTag> tagAssociations) {
		this.tagAssociations = tagAssociations;
	}
	public void addTagAssociation(AssociationPostTag a){
		tagAssociations.add(a);		
	}
	
	
	@ManyToOne ( cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="IMAGE_GUID")
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
	public void addChild(Post post){
		post.setParent(this); //Which adds me to my parents list!
		post.setRoot(this.getRoot());
	}
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)
	//@Fetch(value=FetchMode.SUBSELECT)
    //@OrderBy("date asc") //I was getting an ambiguous field exception in date in mysql
	@OrderBy("path asc")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    //@BatchSize(size=50)
    public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	
	
	@Field(index=Index.UN_TOKENIZED)
    //@DateBridge(resolution=Resolution.DAY)
	@DateBridge(resolution=Resolution.MINUTE)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	@Transient
	public long getRawDate(){
		return date.getTime();
	}

	
	@OneToMany(mappedBy="post", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    //@Fetch(value=FetchMode.JOIN)
    @OrderBy("date desc")
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	public void addEntry(Entry entry){
		entry.setPost(this);
		entries.add(entry);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Column(name="REPLY_COUNT")
	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
	@Column(name="THREAD_REPLY_DATE")
	public Date getThreadReplyDate() {
		return threadReplyDate;
	}

	public void setThreadReplyDate(Date threadReplyDate) {
		this.threadReplyDate = threadReplyDate;
	}

//	
	
//	@Transient
//	public Person getCreator() {
//		AssociationPostTag creatorAss = loadTagAssociation(Tag.TYPE_CREATOR);
//		if(creatorAss != null){
//			return creatorAss.getTag().getCreator();
//		}
//		return null;
//	}
//	
	
//	private String title = "poor title";
//	@Transient  //This guy kills the performance on mySQL
//	private String title;
//	@Formula(" (SELECT t.value FROM ASSOCIATION_POST_TAG ass JOIN TAG t ON ass.tag_guid=t.guid  WHERE ass.post_guid=GUID AND ass.title=1) ")
//	public String getTitle(){
//		return title;
//	}
//	
//	public void setTitle(String title){
//		this.title = title;
//	}
	
	
//	@Transient 
//	public String getTitle(){
//		String title = "";
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(a.isTitle()){
//				title = a.getTag().getValue();
//				break;
//			}
//		}
//		return title;
//	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *  I wanted to put these utilitity methods into a seperate case but i couldnt come up with an interface/
	 *  casting solution that would be clean enough, so i dirtied this object :/
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 *  
	 * 
	 */
	
	
//	private boolean expanded = true;
//	@Transient
//	public boolean isExpanded() {
//		return expanded || isNewPost();
//	}
//
//	public void setExpanded(boolean expanded) {
//		this.expanded = expanded;
//	}
//
//	/**
//	 * Expands this post and all of the ones benath it.
//	 */
//	public void expandAll(){
//		this.expanded = true;
//		for(Post p : posts){
//			p.expandAll();
//		}
//	}
//	public void contractAll(){
//		this.expanded = false;
//		for(Post p : posts){
//			p.contractAll();
//		}
//	}
//	private boolean hidden = true;
//	@Transient
//	public boolean isHidden() {
//		return hidden;
//	}
//
//	public void setHidden(boolean hidden) {
//		this.hidden = hidden;
//		if(!hidden){ //If a child is set visible then the ancestors must also be visable for stuff to work.  Made this change for thread view.
//			if(getParent() != null && getParent().isHidden())
//				getParent().setPlaceholder(true);  //Trevis... this may not do anything anymore.  I saw it being called from search.  But not the main page.
//		}
//	}
//	
//	
//	
//	
//	private boolean placeholder = false;
//	@Transient
//	public boolean isPlaceholder() {
//		return placeholder;
//	}

//	public void setPlaceholder(boolean placeholder) {
//		this.placeholder = placeholder;
//		if(getParent() != null && getParent().isHidden()){
//			getParent().setPlaceholder(true);
//		}
//	}
	
	
	
//	@Transient
//	public boolean getHasUrl(){
//		if(hasTagAssociation(Tag.TYPE_URL))
//			return true;
//		return false;
//	}
	
	/* 
	@Transient
	public boolean getHasRating(){
		if(hasTagAssociation(Tag.TYPE_RATING))
			return true;
		return false;
	}
	
	public String getRating(){
		List<AssociationPostTag> ratings = getRatingAssociations();
		if(ratings.size() > 0)
			return ratings.get(0).getTag().getValue();
		else
			return 	"";
	}
	*/
	

	
	@Transient
	public boolean getHasChildren(){
		return posts.size() > 0;
	}
//	@Transient
//	public String getAverageRating(){
//		List<Tag> ratings = loadTags(Tag.TYPE_RATING);
//		try {
//			return CalculateAverageRating.determineAverageRating(ratings);
//		} catch (Exception e) {
//			return Tag.VALUE_RATING_0;
//		}
//	}
//	@Transient
//	public String getMedianRating(){
//		String value;
//		List<Tag> ratings = loadTags(Tag.TYPE_RATING);
//		try {
//			value = CalculateAverageRating.determineMedianRating(ratings);
//		} catch (Exception e) {
//			value = Tag.VALUE_RATING_0;
//		}
//		
//		return value.replace('.','_');//For the css style images. Sucks 
//	}
//	@Transient
//	public String getDisplayTag(){
//		AssociationPostTag ass = loadTagAssociation(Tag.TYPE_DISPLAY);
//		if(ass != null)
//			return ass.getTag().getValue();
//		else 
//			return "";
//	}
	
	/**
	 * Returns a string of information about how the rating was determined
	 * @return
	 */
	/*
	@Transient
	public String getRatingInfo(){
		List<AssociationPostTag> ratings = loadTagAssociations(Tag.TYPE_RATING);
		
		return ratings;
	}
	*/
	@Transient
	public List<AssociationPostTag> getRatingAssociations(){
		List<AssociationPostTag> ratings = loadTagAssociations(Tag.TYPE_RATING);
		return ratings;
	}
	
	/**
	 * Returns the rating association instance for a rating by a specific person.
	 * This was created so that speed rater can use the same ratings.jsp that the movie
	 * post uses but only show the rating by an individual.
	 * 
	 * Modified to search for ratings in the root when requested for a child. This was added
	 * so that i could show user ratings in the reply!
	 * 
	 * @param personId
	 * @return
	 */
//	@Transient
//	public AssociationPostTag getRatingAssociationByPerson(String personId){
//		List<AssociationPostTag> ratings;
//		if(isRootPost())
//			ratings = loadTagAssociations(Tag.TYPE_RATING);
//		else
//			ratings = getRoot().loadTagAssociations(Tag.TYPE_RATING);
//		
//		for(AssociationPostTag ass : ratings){
//			if(ass.getCreator().getPersonId().equals(personId)){
//				return ass;
//			}
//		}
//		return null;
//	}
//	
//	
//	@Transient
//	/**
//	 *  Returns the rating given by the person referenced by the id param.  If none
//	 *  exists, an empty string is returned;
//	 *  
//	 */
//	public String getRatingByPerson(String personId){
//		AssociationPostTag ass = getRatingAssociationByPerson(personId);
//		if(ass != null)
//			return ass.getTag().getValueRating();
//		else
//			return "";
//	}
//	
//	/**
//	 * Test to see if this post is rated by a specific person. This was added for 
//	 * the speed rating feature in the movies page.
//	 * 
//	 * @param personId
//	 * @return
//	 */
//	public boolean hasRatingByPerson(String personId){
//		if(getRatingAssociationByPerson(personId) != null)
//			return true;
//		else
//			return false;
//	}

	/**
	 * Returns true if this Post is the last sibling in the list
	 */
	/*
	public boolean isLastNode(){
		Post parent = getParent();
		if(parent != null){
			List<Post> siblings = parent.getPosts();
			Post last = siblings.get(siblings.size()-1);
			if(last.getPostId().equals(getPostId()))
				return true;
		}
		return false;
	}
	*/
	

	
	/**
	 * Title is stored in a tag (since not all posts have them). This method will find the 
	 * title if one exists and return it.  Blank is returned otherwise.
	 * 
	 * @return
	 */
//	@Transient
//	public String getTitle(){
//		String title = "";
//		
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(a.isTitle()){
//				title = a.getTag().getValue();
//				break;
//			}
//		}
//		
//		if("".equals(title)){
//			if(isRootPost()){
//				return "";
//			}
//			else{
//				title = getRoot().getTitle(); //I added this for the hot topics widget but it could be usefull. If you call get title on
//				//post this will make the call roll up to the root to find the thread title for posts.  To keep sanity though, 
//				//it will still return false from 'hasTitle'
//			}
//		}
//		
//		return PostFormatter.getInstance().format(title);
//	}
	
//	@Transient
//	public boolean getHasTitle(){
//		return loadTitleTagAssociation() != null;
//		//return !(StringUtils.isEmpty(title)||title.equals("BLANK"));
//	}
//	
//	@Transient
//	public AssociationPostTag loadTitleTagAssociation(){
//		AssociationPostTag ass = null;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(a.isTitle()){
//				return a;
//			}
//		}
//		return ass;
//	}
	
	/**
	 * Test if this tag is already associated with this post
	 * 
	 * @param t
	 * @return
	 */
	public boolean containsTag(Tag t){
		List<AssociationPostTag> tagasses = getTagAssociations();
		for(AssociationPostTag ass : tagasses){
			if(ass.getTag().equals(t)) 
				return true;
		}
		return false;
	}
	public boolean containsTag(String tagId){
		Tag t = new Tag();
		t.setTagId(tagId);
		return containsTag(t);
	}
	
//	@Transient
//	public String getUrl(){
//		if(hasTagAssociation(Tag.TYPE_URL)){	
//			AssociationPostTag ass = loadTagAssociation(Tag.TYPE_URL);
//			return ass.getTag().getValue();
//		}
//		return "";
//	}
//	
//	@Transient
//	public String getReleaseYear(){
//		if(hasTagAssociation(Tag.TYPE_RELEASE_YEAR)){
//			AssociationPostTag ass = loadTagAssociation(Tag.TYPE_RELEASE_YEAR);
//			return ass.getTag().getValue();
//		}
//		return "";
//	}
	
	//	@Transient
//	public Person getCreator(){
//		AssociationPostTag ass = loadTagAssociation(Tag.TYPE_CREATOR);
//		if(ass != null){
//			return ass.getTag().getCreator();
//		}
//		return null;
//	}
	
	@Transient
	public boolean isReviewable(){
		return isMovie(); //Just in case i want to review other types of things down the road?
	}
	
	@Transient
	public List<AssociationPostTag> getRatings(){
		return loadTagAssociations(Tag.TYPE_RATING);
	}
	
	@Transient
	public List<AssociationPostTag> getTopics(){
		return loadTagAssociations(Tag.TYPE_TOPIC);
	}
	
	@Transient
	public boolean isMovie(){
		return hasBit(PostFlag.MOVIE.getBitmask());
	}
	@Transient
	public boolean isRatable(){
		return hasBit(PostFlag.RATABLE.getBitmask());
	}
	
	@Transient
	public boolean isReview(){
		return hasBit(PostFlag.REVIEW.getBitmask());
	}
	
	@Transient
	public boolean isLegacyThreadHolder(){
		return hasBit(PostFlag.LEGACY.getBitmask());
	}
	
	@Transient
	public boolean isLinkContained(){
		return hasBit(PostFlag.LINK.getBitmask());
	}
	
	@Transient
	public boolean isNWS(){
		return hasBit(PostFlag.NWS.getBitmask());
	}
	@Transient
	public boolean isINF(){
		return hasBit(PostFlag.INF.getBitmask());
	}
	
	@Transient
	public boolean isPrivate(){
		return hasBit(PostFlag.PRIVATE.getBitmask());
	}
	@Transient
	public boolean isDeleted(){
		return hasBit(PostFlag.DELETED.getBitmask());
	}
	
	@Transient
	public boolean isLocked(){
		return hasBit(PostFlag.LOCKED.getBitmask());
	}
	
	@Transient
	private boolean hasBit(long bitmask){
		long mask = getMetaMask();
		return (mask & bitmask) == bitmask;
	}
	
	@Transient
	public void setMovie(){
		applyBitMask(PostFlag.MOVIE.getBitmask());
	}
	@Transient
	public void setRatable(){
		applyBitMask(PostFlag.RATABLE.getBitmask());
	}
	
	@Transient
	public void setReview(){
		applyBitMask(PostFlag.REVIEW.getBitmask());
	}
	
	@Transient
	public void setLegacyThreadHolder(){
		applyBitMask(PostFlag.LEGACY.getBitmask());
	}
	
	@Transient
	public void setLinkContained(){
		applyBitMask(PostFlag.LINK.getBitmask());
	}
	
	@Transient
	public void setNWS(){
		applyBitMask(PostFlag.NWS.getBitmask());
	}
	@Transient
	public void setINF(){
		applyBitMask(PostFlag.INF.getBitmask());
	}
	
	@Transient
	public void setPrivate(){
		applyBitMask(PostFlag.PRIVATE.getBitmask());
	}
	@Transient
	public void setDeleted(){
		applyBitMask(PostFlag.DELETED.getBitmask());
	}
	
	@Transient
	public void setLocked(){
		applyBitMask(PostFlag.LOCKED.getBitmask());
	}
	
	@Transient
	private void applyBitMask(long bitmask){
		long mask = getMetaMask();
		setMetaMask(mask | bitmask);
	}
	
	/*
	public List<String> getRatings(){
		List<String> list = new ArrayList<String>();
		List<AssociationPostTag> ratings;
		ratings = loadTagAssociations(Tag.TYPE_RATING);
		for(AssociationPostTag ass : ratings){
			list.add(ass.getTag().getValue());
		}
		return list;
	}
	*/
	
	
	/**
	 * returns the first tag of the requested type.  This method is intended for 
	 * tag associations that should be unique like Title for example
	 * @param type
	 * @return
	 */
//	public AssociationPostTag loadTagAssociation(String type){
//		AssociationPostTag ass = null;
//		if(type == null) return null;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(type.equals(a.getTag().getType())){
//				ass = a;
//				break;
//			}
//		}
//		return ass;
//	}
	
	/**
	 * Returns a list of AssociationPostTag objects matching a specific type
	 * 
	 * @param type
	 * @return Returns an empty list if the type is either null or not found.
	 */
	private List<AssociationPostTag> loadTagAssociations(String type){
		List<AssociationPostTag> list = new ArrayList<AssociationPostTag>();
		if(type == null) return list;
		List<AssociationPostTag> tagasses = getTagAssociations();
		for(AssociationPostTag a : tagasses){
			if(type.equals(a.getTag().getType())){
				list.add(a);
			}
		}
		return list;
	}
	
	
	/**
	 * Load tag associations of this type by this person 
	 * @param type
	 * @param person
	 * @return
	 */
//	public List<AssociationPostTag> loadTagAssociations(String type, Person person){
//		List<AssociationPostTag> list = new ArrayList<AssociationPostTag>();
//		if(type == null) return list;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(type.equals(a.getTag().getType()) && a.getCreator().equals(person)){
//				list.add(a);
//			}
//		}
//		return list;
//	}
	/**
	 * Searches the child posts for comments tagged review.  Hm, only checks at one level deep.
	 * 
	 * Is that a problem Trevis?
	 * 
	 * @return
	 */
//	@Transient
//	public List<Post> getReviews(){
//		List<Post> reviews = new ArrayList<Post>();
//		for(Post p : getPosts()){
//			if(p.hasTagAssociation(Tag.TYPE_REVIEW)){
//				reviews.add(p);
//			}
//		}
//		return reviews;
//	}
	
	
	/**
	 * Pretty much the same as above except it's the tag only.
	 * 
	 * @param type
	 * @return
	 */
//	public List<Tag> loadTags(String type){
//		List<Tag> list = new ArrayList<Tag>();
//		if(type == null) return list;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		for(AssociationPostTag a : tagasses){
//			if(type.equals(a.getTag().getType())){
//				list.add(a.getTag());
//			}
//		}
//		return list;
//	}
	
	/**
	 * Determines if a post has a specific tag type.
	 * @param type
	 * @return
	 */
//	public boolean hasTagAssociation(String type) {
//		if(type == null) return false;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		boolean retval = false;
//		for(AssociationPostTag ass : tagasses){
//			if(type.equals(ass.getTag().getType())){
//				return true;
//			}
//		}
//		return retval;
//	}
	
	/**
	 * Has tag by this person!
	 * 
	 * @param type
	 * @param person
	 * @return
	 */
//	public boolean hasTagAssociation(String type, Person person) {
//		if(type == null) return false;
//		List<AssociationPostTag> tagasses = getTagAssociations();
//		boolean retval = false;
//		for(AssociationPostTag ass : tagasses){
//			if(type.equals(ass.getTag().getType()) && ass.getCreator().equals(person)){
//				return true;
//			}
//		}
//		return retval;
//	}
	
	public boolean hasRatingByPerson(String personId){
		String type = Tag.TYPE_RATING;
		
		if(type == null) return false;
		List<AssociationPostTag> tagasses = getTagAssociations();
		boolean retval = false;
		for(AssociationPostTag ass : tagasses){
			if(type.equals(ass.getTag().getType()) && ass.getCreator().getPersonId().equals(personId)){
				return true;
			}
		}
		return retval;
	}
	
	public AssociationPostTag getRatingByPerson(String personId){
		String type = Tag.TYPE_RATING;
		
		if(type == null) return null;
		List<AssociationPostTag> tagasses = getTagAssociations();
		for(AssociationPostTag ass : tagasses){
			if(type.equals(ass.getTag().getType()) && ass.getCreator().getPersonId().equals(personId)){
				return ass;
			}
		}
		return null;
	}
	
	/**
	 * Just return the first entry.  Multiple entries mean that a post has been edited
	 * ... fixed.  Latest is now linked directly to post
	 * 
	 * @return
	 */
	@Transient
	public Entry getEntry(){
		return getLatestEntry();
	}
	
	@Transient
	public String getSummary(){
		return getEntry().getSummary();
	}

	/**
	 * When a root post has too many children to be shown on the front page
	 * i set a populated postCounter object here which contains the total count
	 * of posts in this root and is a warning about this massive thread.
	 */
//	private PostCounter postCounter;
//	@Transient
//	public PostCounter getPostCounter() {
//		return postCounter;
//	}
//
//	public void setPostCounter(PostCounter postCounter) {
//		this.postCounter = postCounter;
//	}

//	private boolean newPost = false;
//	@Transient	
//	public boolean isNewPost() {
//		return newPost;
//	}
//
//	public void setNewPost(boolean newPost) {
//		this.newPost = newPost;
//	}
	
	@Transient
	public boolean isRootPost() {
		return getParent()==null;
	}
	
	public void setRootPost(boolean rootPost) {
		//Nothing to do.
	}
	
	@Transient
	public boolean isThreadPost(){
		if(!isRootPost())
			return getThread().getPostId() == getPostId();
		else
			return false;
	}
	
	public void setThreadPost(boolean threadPost) {
		//Nothing to do.
	}
	
//	private String relativeAge = "";
//	@Transient
//	public String getRelativeAge() {
//		return relativeAge;
//	}
//
//	public void setRelativeAge(String relativeAge) {
//		this.relativeAge = relativeAge;
//	}

	
	
	//Trevis... this cascade might cause problems you think.
	/*
	@OneToOne(fetch=FetchType.LAZY)
	@PrimaryKeyJoinColumn
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    */
	/* Since entry is now it's own table with a MANY_TO_ONE relationship this 
	 * mapping may make sense again
	 * 
	 */
	
	//@Transient
	
	/*
	@Transient //If i let hibernate know about this association it performs it too often.
	public Entry getEntry() {
		if(entry != null)
			return entry;
		else
			return getEntries().get(0);
		//return(getEntries().get(0));
	}

	public void setEntry(Entry entry) {
		//entry.setPost(this);
		//entry.setPost(this);
		setHidden(false);
		this.entry = entry;
	}
	*/

	
	
	/**
	 * Tests if the post has been edited
	 * 
	 *  @return returns true if the post has been edited. false otherwise.
	 */
//	@Transient
//	public boolean isModified(){
//		return getEntries().size() > 1;
//	}
	
	/**
	 *  Gets the date that the post was edited. Returns the last modified date if the post 
	 *  has been modified, returns the creation date if it hasn't been updated.
	 *  
	 *  @return modified date. 
	 *  
	 */
//	@Transient
//	public Date getModifiedDate(){
//		List<Entry> list = new ArrayList<Entry>(getEntries());
//		return list.get(0).getDate();
//	}
//	@Transient
//	public long getRawModifiedDate(){
//		return getModifiedDate().getTime();
//	}
	
	/*
	 * I manage this mapping in a custom way because i want to manage when Entries are loaded.
	 * I could probably make them a one to many relationship and manage it that way but...
	 * i really dont feel like it right now :-)
	 */
	/*
	@Column(name="ENTRY_GUID")
	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	*/
	

//	boolean unread = false;
//	@Transient
//	public boolean isUnread() {
//		return unread;
//	}
//
//	public void setUnread(boolean unread) {
//		this.unread = unread;
//		if(unread){
//			getRoot().setThreadRead(false);
//		}
//	}
//	
	
//	boolean threadRead = true;
//	@Transient
//	/**
//	 * Is the thread read.  
//	 * 
//	 * @return returns false if any unread comments are in this thread hierarchy, true otherwise.
//	 * @throws throws illegal state exception if the method is called
//	 * 
//	 * When a reply in a thread is unread,  
//	 */
//	public boolean isThreadRead() {
//		if(isRootPost())
//			return threadRead;
//		else
//			throw new UnsupportedOperationException("This method should only be called on root posts");
//	}

//	public void setThreadRead(boolean threadRead) {
//		this.threadRead = threadRead;
//	}
	
//	public void initialize(){
//		Hibernate.initialize(getPosts());
//		Hibernate.initialize(getImage());
//		Hibernate.initialize(getEntries());
//		Hibernate.initialize(getTagAssociations());
//		if(getParent()!=null)
//			Hibernate.initialize(getParent().getTagAssociations());//For showing the in reply to stuff
//		Hibernate.initialize(getRoot().getTagAssociations());//So that i can show which thread a comment is in
//		for(Post p : posts){
//			p.initialize();
//		}
//	}

//	//This is for filtering NWS and Private posts from view.  Users can also select threads which are filtered.
//	private boolean filtered = false;
//	@Transient
//	public boolean isFiltered() {
//		return filtered;
//	}
//	public void setFiltered(boolean filtered) {
//		this.filtered = filtered;
//	}
	
	/**
	 * Earmark is piggybacking on this functionality which was originally created for movies. 
	 * 
	 * @param person
	 * @return
	 */
//	private boolean earmarked = false;
//	@Transient 
//	public boolean isEarmarked() {
//		return earmarked;
//	}
//
//	public void setEarmarked(boolean earmarked) {
//		this.earmarked = earmarked;
//	}
	
	@Transient
	public String getAverageRating(){
		return getAvgRatingTag().getValueRating();
	}
	
	@Transient
	public String getTitle(){
		return getTitleTag().getValue();
	}	
	@Transient
	public String getSortTitle(){
		return getTitleTag().getSortValue();
	}	
	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="THREAD_GUID")
    public Post getThread() {
		return thread;
	}
	public void setThread(Post thread) {
		this.thread = thread;
	}

	@Column(name="EDIT_DATE")
	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	
	//@ManyToOne ( cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="LATEST_ENTRY_GUID")
	public Entry getLatestEntry() {
		return LatestEntry;
	}

	public void setLatestEntry(Entry latestEntry) {
		LatestEntry = latestEntry;
	}

	@Transient
	public boolean isPostThreadRoot(){
		if(getThread() == null) return false;
		return getThread().getPostId() == getPostId();
	}

	
	@Field(index=Index.UN_TOKENIZED, name="mass" )
	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	@Column(name="META_MASK")
	public Long getMetaMask() {
		return metaMask;
	}
	public void setMetaMask(Long metaMask) {
		this.metaMask = metaMask;
	}

	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="TAG_GUID_TITLE")
	public Tag getTitleTag() {
		return titleTag;
	}

	public void setTitleTag(Tag titleTag) {
		this.titleTag = titleTag;
	}

	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="TAG_GUID_AVG_RATING")
	public Tag getAvgRatingTag() {
		return avgRatingTag;
	}
	public void setAvgRatingTag(Tag avgRatingTag) {
		this.avgRatingTag = avgRatingTag;
	}

	@Column(name="PUBLISH_YEAR")
	public Integer getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(Integer publishYear) {
		this.publishYear = publishYear;
	}

	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToOne ( cascade = {CascadeType.ALL})
	@JoinColumn(name="PERSON_GUID_CREATOR")
	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}
	
//	META_MASK	binary(8)	Unchecked
//	TAG_GUID_TITLE	uniqueidentifier	Unchecked
//	PERSON_GUID_CREATOR	uniqueidentifier	Unchecked
//	TAG_GUID_AVG_RATING	uniqueidentifier	Checked
//	URL	varchar(500)	Checked
//	PUBLISH_YEAR	smallint	Checked
	
	
	
	


}
