package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Transient;

import org.ttdc.gwt.shared.util.StringUtil;

@NamedQueries({
	@NamedQuery(name="LatestPostsDaoFast.Flat", query="" +
			"SELECT post.postId FROM Post post " +
			"WHERE post.root.postId NOT IN (:threadIds) " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0  " +
			"AND post.parent is not null "+
			"ORDER BY post.date DESC"),
			
	@NamedQuery(name="LatestPostsDaoFast.FlatCount", query="" +
		"SELECT count(post.postId) FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) AND " +
		"bitwise_and( post.metaMask, :filterMask ) = 0" ),	
			
})

@NamedNativeQueries({
	//Trevis, once every thing is working try to use the day/month + thread guid as the daily unique id for cross db compatibilityness
	@NamedNativeQuery(
		name="FullPost.flatPosts", query=""+
			"SELECT p.guid as postId, i.name imageName, i.height imageHeight, i.width imageWidth, e.body entryBody, e.summary entrySummary, " +
			"p.date date, p.EDIT_DATE editDate, p.REPLY_COUNT replyCount, p.MASS mass, p.ROOT_GUID rootId, p.THREAD_GUID threadId, p.PARENT_GUID parentId, " +
			"p.PERSON_GUID_CREATOR creatorId, c.login creatorLogin, ci.name creatorImageName, p.URL url, p.PUBLISH_YEAR publishYear, " +
			"parentCreator.GUID parentPostCreatorId, parentCreator.LOGIN parentPostCreator, tt.VALUE titleValue, avgr.VALUE ratingValue, " +
			"root.PUBLISH_YEAR rootPublishYear"+
			" FROM POST p "+
			"INNER JOIN ENTRY e on e.GUID = p.LATEST_ENTRY_GUID "+
			"INNER JOIN PERSON c on c.GUID = p.PERSON_GUID_CREATOR " +
			"INNER JOIN TAG tt on p.TAG_GUID_TITLE = tt.GUID " +
			"LEFT OUTER JOIN POST root on p.ROOT_GUID = root.GUID " +
			"LEFT OUTER JOIN POST parent on p.PARENT_GUID = parent.GUID " +
			"LEFT OUTER JOIN PERSON parentCreator on parent.PERSON_GUID_CREATOR = parentCreator.GUID "+
			"LEFT OUTER JOIN IMAGE ci on c.IMAGE_GUID = ci.GUID "+
			"LEFT OUTER JOIN TAG avgr on p.TAG_GUID_AVG_RATING = avgr.GUID "+
			"LEFT OUTER JOIN IMAGE i on i.GUID = p.IMAGE_GUID "+
			"WHERE p.guid in (:postIds)",
			resultSetMapping="fullPost"
	)
	
	
})

@SqlResultSetMappings({
	@SqlResultSetMapping(name="fullPost", entities=
		@EntityResult(entityClass=org.ttdc.persistence.objects.FullPost.class, fields = {
	        @FieldResult(name="postId", column="postId"),
	        @FieldResult(name="imageName", column="imageName"),
	        @FieldResult(name="imageHeight", column="imageHeight"),
	        @FieldResult(name="imageWidth", column="imageWidth"),
	        @FieldResult(name="entryBody", column="entryBody"),
	        @FieldResult(name="entrySummary", column="entrySummary"),
	        @FieldResult(name="date", column="date"),
	        @FieldResult(name="editDate", column="editDate"),
	        
	        @FieldResult(name="replyCount", column="replyCount"),
	        @FieldResult(name="mass", column="mass"),
	        @FieldResult(name="rootId", column="rootId"),
	        @FieldResult(name="threadId", column="threadId"),
	        @FieldResult(name="parentId", column="parentId"),
	        @FieldResult(name="creatorId", column="creatorId"),
	        @FieldResult(name="creatorLogin", column="creatorLogin"),
	        @FieldResult(name="creatorImageName", column="creatorImageName"),
	        @FieldResult(name="url", column="url"),
	        @FieldResult(name="publishYear", column="publishYear"),
	        @FieldResult(name="parentPostCreator", column="parentPostCreator"),
	        @FieldResult(name="parentPostCreatorId", column="parentPostCreatorId"),
	        @FieldResult(name="titleValue", column="titleValue"),
	        @FieldResult(name="ratingValue", column="ratingValue"),
	        @FieldResult(name="rootPublishYear", column="rootPublishYear")
    }))
})


@Entity
public class FullPost {
	private String postId;
	private String imageName;
	private Integer imageHeight;
	private Integer imageWidth;
	private String entryBody;
	private String entrySummary;
	private Date date;
	private Date editDate;
	
	private int replyCount;
	private int mass;
	private String rootId;
	private String threadId;
	private String parentId;
	private String creatorId;
	private String creatorLogin;
	private String creatorImageName;
	
//	private GTag titleTag;
//	private GTag avgRatingTag;
	private String url;
	private Integer publishYear;
	private String parentPostCreator;
	private String parentPostCreatorId;
	private String titleValue;
	private String ratingValue;
	private String rootPublishYear;
	
	@Transient
	public String getCreatorImageThumbnailName(){
		return translateImageNameToThumbnailName(getCreatorImageName());
	}
	
	@Transient
	public String getImageThumbnailName(){
		String n = getImageName();
		return translateImageNameToThumbnailName(n);
	}

	public static String translateImageNameToThumbnailName(String n) {
		if(StringUtil.empty(n)){
			return "";
		}
		int extentionStartIndex = n.lastIndexOf('.');
		String tnName;
		if(extentionStartIndex > 0){
			String prefix = n.substring(0,extentionStartIndex);
			String ext =  n.substring(extentionStartIndex);
			tnName = prefix+Image.SQUARE_THUMBNAIL_SUFFIX+ext;
		}
		else{
			tnName = n + Image.SQUARE_THUMBNAIL_SUFFIX;
		}
		
		return tnName;
	}
	
	//private GPerson creator;
	
	@Id
	public String getPostId() {
		return postId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getEntryBody() {
		return entryBody;
	}

	public void setEntryBody(String entryBody) {
		this.entryBody = entryBody;
	}

	public String getEntrySummary() {
		return entrySummary;
	}

	public void setEntrySummary(String entrySummary) {
		this.entrySummary = entrySummary;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
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

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorLogin() {
		return creatorLogin;
	}

	public void setCreatorLogin(String creatorLogin) {
		this.creatorLogin = creatorLogin;
	}

	public String getCreatorImageName() {
		return creatorImageName;
	}

	public void setCreatorImageName(String creatorImageName) {
		this.creatorImageName = creatorImageName;
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

	public String getParentPostCreator() {
		return parentPostCreator;
	}

	public void setParentPostCreator(String parentPostCreator) {
		this.parentPostCreator = parentPostCreator;
	}

	public String getParentPostCreatorId() {
		return parentPostCreatorId;
	}

	public void setParentPostCreatorId(String parentPostCreatorId) {
		this.parentPostCreatorId = parentPostCreatorId;
	}

	public String getTitleValue() {
		return titleValue;
	}

	public void setTitleValue(String titleValue) {
		this.titleValue = titleValue;
	}

	public String getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(String ratingValue) {
		this.ratingValue = ratingValue;
	}

	public String getRootPublishYear() {
		return rootPublishYear;
	}

	public void setRootPublishYear(String rootPublishYear) {
		this.rootPublishYear = rootPublishYear;
	}
	


	
}
