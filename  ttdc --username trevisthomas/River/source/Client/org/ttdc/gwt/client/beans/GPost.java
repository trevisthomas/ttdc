package org.ttdc.gwt.client.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.shared.util.PostFlag;


public class GPost extends GBase{
	private String postId;
	private GPost parent;
	private GPost root; 
	private GPost thread;
	private List<GPost> posts = new ArrayList<GPost>();
	private List<GAssociationPostTag> tagAssociations = new ArrayList<GAssociationPostTag>();
	private GImage image;
	private Date date = new Date();
	private Date editDate = date;
	private List<GEntry> entries = new ArrayList<GEntry>();
	private GEntry latestEntry;
	private String path;
	private int replyCount;
	private int mass;
	private boolean rootPost;
	private boolean threadPost;
	private boolean suggestSummary;
	private GPerson creator;
	private long metaMask;
	private GTag titleTag;
	private GTag avgRatingTag;
	private String url;
	private Integer publishYear;
	private boolean inEditWindow = false; 
		
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Creator: ").append(getCreator());
		sb.append(" Tags: ").append(getTagAssociations());
		sb.append(" Entry: ").append(getEntry());
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object that) {
		if(that instanceof GPost)
			return this.getPostId().equals(((GPost)that).getPostId());
		else
			return super.equals(that);
	}
	
	@Override
	public int hashCode() {
		return this.getPostId().hashCode();
	}
	
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public List<GPost> getPosts() {
		return posts;
	}
	public void setPosts(List<GPost> posts) {
		this.posts = posts;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<GEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<GEntry> entries) {
		this.entries = entries;
	}
	public String getEntry(){
		if(latestEntry == null) throw new RuntimeException("Latest entry is null.");
		return latestEntry.getBody();
	}
	public List<GAssociationPostTag> getTagAssociations() {
		return tagAssociations;
	}
	public void setTagAssociations(List<GAssociationPostTag> tagAssociations) {
		this.tagAssociations = tagAssociations;
	}
	public GImage getImage() {
		return image;
	}
	public void setImage(GImage image) {
		this.image = image;
	}
	
	public boolean hasChildren(){
		return posts.size() > 0;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public GEntry getLatestEntry() {
		return latestEntry;
	}

	public void setLatestEntry(GEntry latestEntry) {
		this.latestEntry = latestEntry;
	}
	
	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public GPost getParent() {
		return parent;
	}

	public void setParent(GPost parent) {
		this.parent = parent;
	}

	public GPost getRoot() {
		return root;
	}

	public void setRoot(GPost root) {
		this.root = root;
	}

	public GPost getThread() {
		return thread;
	}

	public void setThread(GPost thread) {
		this.thread = thread;
	}
	
	
	
	
	/**
	 * Gets the first tag of this type
	 */
	private GAssociationPostTag loadTagAssociation(String type){
		GAssociationPostTag ass = null;
		if(type == null) return null;
		List<GAssociationPostTag> tagasses = getTagAssociations();
		for(GAssociationPostTag a : tagasses){
			if(type.equals(a.getTag().getType())){
				ass = a;
				break;
			}
		}
		return ass;
	}
	
	/**
	 * Get the association type by a specific person
	 */
	private GAssociationPostTag loadTagAssociationByPerson(String type, String personId){
		GAssociationPostTag ass = null;
		if(type == null) return null;
		List<GAssociationPostTag> tagasses = getTagAssociations();
		for(GAssociationPostTag a : tagasses){
			if(type.equals(a.getTag().getType()) && personId.equals(a.getCreator().getPersonId())){
				ass = a;
				break;
			}
		}
		return ass;
	}
	public GAssociationPostTag getRatingByPerson(String personId){
		return loadTagAssociationByPerson(TagConstants.TYPE_RATING, personId);
	}
	
	
	public List<GAssociationPostTag> readTagAssociations(String type){
		List<GAssociationPostTag> foundAsses = new ArrayList<GAssociationPostTag>();
		List<GAssociationPostTag> tagasses = getTagAssociations();
		for(GAssociationPostTag a : tagasses){
			if(type.equals(a.getTag().getType())){
				foundAsses.add(a);
			}
		}
		return foundAsses;
	}

	public String getTitle() {
		return getTitleTag().getValue();
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public boolean isRootPost() {
		return rootPost;
	}

	public void setRootPost(boolean rootPost) {
		this.rootPost = rootPost;
	}

	public boolean isThreadPost() {
		return threadPost;
	}

	public void setThreadPost(boolean threadPost) {
		this.threadPost = threadPost;
	}

	public boolean isSuggestSummary() {
		return suggestSummary;
	}
	public void setSuggestSummary(boolean b) {
		suggestSummary = b;
	}

	public GPerson getCreator() {
		return creator;
	}

	public void setCreator(GPerson creator) {
		this.creator = creator;
	}

	public long getMetaMask() {
		return metaMask;
	}

	public void setMetaMask(long metaMask) {
		this.metaMask = metaMask;
	}

	public GTag getTitleTag() {
		return titleTag;
	}

	public void setTitleTag(GTag titleTag) {
		this.titleTag = titleTag;
	}

	public GTag getAvgRatingTag() {
		return avgRatingTag;
	}

	public void setAvgRatingTag(GTag avgRatingTag) {
		this.avgRatingTag = avgRatingTag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(Integer publishYear) {
		this.publishYear = publishYear;
	}
	
	public boolean isMovie(){
		return hasBit(PostFlag.MOVIE.getBitmask());
	}

	public boolean isRatable(){
		return hasBit(PostFlag.RATABLE.getBitmask());
	}
	
	
	public boolean isReview(){
		return hasBit(PostFlag.REVIEW.getBitmask());
	}
	
	
	public boolean isLegacyThreadHolder(){
		return hasBit(PostFlag.LEGACY.getBitmask());
	}
	
	
	public boolean isLinkContained(){
		return hasBit(PostFlag.LINK.getBitmask());
	}
	
	
	public boolean isNWS(){
		return hasBit(PostFlag.NWS.getBitmask());
	}
	
	public boolean isINF(){
		return hasBit(PostFlag.INF.getBitmask());
	}
	
	
	public boolean isPrivate(){
		return hasBit(PostFlag.PRIVATE.getBitmask());
	}
	
	public boolean isDeleted(){
		return hasBit(PostFlag.DELETED.getBitmask());
	}
	
	
	public boolean isLocked(){
		return hasBit(PostFlag.LOCKED.getBitmask());
	}
	
	public boolean isInEditWindow() {
		return inEditWindow;
	}

	public void setInEditWindow(boolean inEditWindow) {
		this.inEditWindow = inEditWindow;
	}
	
	private boolean hasBit(long bitmask){
		long mask = getMetaMask();
		return (mask & bitmask) == bitmask;
	}

	public boolean isReviewedBy(String personId) {
		for(GPost post : getPosts()){
			if(post.isReview() && post.getCreator().getPersonId().equals(personId)){
				return true;
			}
		}
		return false;
	}

	
	
}
