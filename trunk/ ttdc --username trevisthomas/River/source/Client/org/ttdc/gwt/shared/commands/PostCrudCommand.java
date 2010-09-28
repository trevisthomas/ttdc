package org.ttdc.gwt.shared.commands;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

public class PostCrudCommand extends Command<PostCommandResult>{
	private String postId;
	private boolean loadRootAncestor = false;
	private boolean loadThreadAncestor = false;
	private PostActionType action = PostActionType.READ;
	private String login;
	private String password;
	private String parentId;
	private String body;
	private String type;
	private String title;
	private String url;
	private String imageUrl;
	private String year;
	private boolean movie = false;
	private String titleTagId;
	private String embedMarker;
	 
	private boolean deleted;
	private boolean review;
	private boolean inf;
	private boolean nws;
	private boolean privatePost;
	private boolean locked;
//	private List<String> tagIds = new ArrayList<String>();
	private List<GTag> tags = new ArrayList<GTag>();
	private String topicDescription;
	private String forumId;
	
	private Long metaMask = null;
	
	
	public PostCrudCommand() {}
	public PostCrudCommand(String postId) {
		this.postId = postId;
	}
	
	public PostActionType getAction() {
		return action;
	}
	public void setAction(PostActionType action) {
		this.action = action;
	}
	
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public void setThreadAncestor(boolean b) {
		loadThreadAncestor = b;
		loadRootAncestor = false;
	}
	public boolean isLoadRootAncestor() {
		return loadRootAncestor;
	}
	public void setLoadRootAncestor(boolean loadRootAncestor) {
		loadThreadAncestor = false;
		this.loadRootAncestor = loadRootAncestor;
	}
	public boolean isLoadThreadAncestor() {
		return loadThreadAncestor;
	}
	public void setLoadThreadAncestor(boolean loadThreadAncestor) {
		this.loadThreadAncestor = loadThreadAncestor;
		this.loadRootAncestor = false;
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
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public boolean isMovie() {
		return movie;
	}
	public void setMovie(boolean movie) {
		this.movie = movie;
	}
	public String getTitleTagId() {
		return titleTagId;
	}
	public void setTitleTagId(String titleTagId) {
		this.titleTagId = titleTagId;
	}
	public String getEmbedMarker() {
		return embedMarker;
	}
	public void setEmbedMarker(String embedMarker) {
		this.embedMarker = embedMarker;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isReview() {
		return review;
	}
	public void setReview(boolean review) {
		this.review = review;
	}
	public boolean isInf() {
		return inf;
	}
	public void setInf(boolean inf) {
		this.inf = inf;
	}
	public boolean isNws() {
		return nws;
	}
	public void setNws(boolean nws) {
		this.nws = nws;
	}
	public boolean isPrivate() {
		return privatePost;
	}
	public void setPrivate(boolean privatePost) {
		this.privatePost = privatePost;
	}
	
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
//	public List<String> getTagIds() {
//		return tagIds;
//	}
//	public void setTagIds(List<String> tagIds) {
//		this.tagIds = tagIds;
//	}
//	public void addTag(String tagId){
//		this.tagIds.add(tagId);
//	}
	public void addTag(GTag tag){
		tags.add(tag);
	}
	public List<GTag> getTags() {
		return tags;
	}
	public void setTags(List<GTag> tags) {
		this.tags = tags;
	}
	
	public Long getMetaMask() {
		return metaMask;
	}
	
	public void setMetaMask(long mask){
		metaMask = mask;
	}
	public String getTopicDescription() {
		return topicDescription;
	}
	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}
	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	
	
}
