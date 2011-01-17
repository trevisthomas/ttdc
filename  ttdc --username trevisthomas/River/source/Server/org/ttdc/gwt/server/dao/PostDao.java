package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.util.PostFlagBitmasks;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;


final public class PostDao {
	public interface PostCreationListener{
		void newPostCreated(Post post);
	}
	
	private String body;
	private Image image;
	private Post parent;
	private Person creator;
	private Tag titleTag;
	private String url;
	private Integer publishYear;
	private long metaMask = 0;
	private String postId;
	private String imageUrl;
	private String description;
	private String forumId;
	
	private final static List<PostCreationListener> postCreationListeners = new ArrayList<PostCreationListener>();
	
	public PostDao(){}

	final static String postListQuery = "SELECT p FROM Post p WHERE p.postId IN (:postIds)";
	final static String postByTitleQuery = "SELECT p FROM Post p WHERE p.titleTag.tagId = (:titleTagId) AND p.parent.postId IS NULL";
	@SuppressWarnings("unchecked")
	public static List<Post> loadPosts(List<String> postIds){
		List<Post> list = session().createQuery(postListQuery)
			.setParameterList("postIds", postIds)
			.list();
		return list;
	}
	
	public static Post loadPost(String postId){
		Post p = (Post)session().load(Post.class, postId);
		return p;
	}
	
	public static List<Post> loadPostByTitleTag(String titleTagId){
		List<Post> list = session().createQuery(postByTitleQuery)
			.setParameter("titleTagId", titleTagId)
			.list();
		return list;
	}
	
	public static void registerPostCreationListener(PostCreationListener listener){
		postCreationListeners.add(listener);
	}
	
	public Post create(){
		Post post = performPostCreate();
		for(PostCreationListener listener : postCreationListeners){
			listener.newPostCreated(post);
		}
		return post;
	}

	private Post performPostCreate() {
		if(isMovie()){
			return createMoviePost();
		}
		
		validation();
		if(parent == null){
			//Create this conversation in a new topic
			Post newTopic =  createTraditionalPost(getDescription());
			parent = newTopic;
			
			return createTraditionalPost(getBody());
		}
		else{
			return createTraditionalPost(getBody());
		}
	}

	private boolean isMovie() {
		return (metaMask & PostFlagBitmasks.BITMASK_MOVIE) == PostFlagBitmasks.BITMASK_MOVIE;
	}

	private Post createMoviePost() {
		validateParamsForMovie();
		
		ImageFull imageFull = createFullImage(getTitle().getValue());
		
		Post post = buildPost();
		
		Entry entry = buildEntry(post, "");
		post.addEntry(entry);
		post.setLatestEntry(entry);
		session().flush();
		
		addImageToPost(imageFull, post);
		session().update(post);
		return post;
	}

	private void addImageToPost(ImageFull imageFull, Post post) {
		Image image = ImageDao.loadImage(imageFull.getImageId());
		post.setImage(image);
	}

	private ImageFull createFullImage(String title) {
		ImageDataDao imageDataDao = new ImageDataDao(creator);
		String compressedTitle = title.replace(" ", "");
		ImageFull imageFull = imageDataDao.createImage(getImageUrl(), compressedTitle);
		session().save(imageFull);
		return imageFull;
	}

	
	private void validateParamsForMovie() {
		if(titleTag == null){
			throw new RuntimeException("Title is required for movies.");
		}
		if(StringUtil.empty(url)){
			throw new RuntimeException("URL is required");
		}
		if(publishYear == null){
			throw new RuntimeException("Publish year is requred for movies");
		}
		if(imageUrl == null){
			throw new RuntimeException("Movie poster image is required for movies");
		}
	}

	private Post createTraditionalPost(String entryText) {
		Post post = buildPost();
		Entry entry = buildEntry(post, entryText);
		post.addEntry(entry);
		post.setLatestEntry(entry);
		
		
		session().flush();
		//This refresh was added so that movie root's would be updated when reviews were added
		if(post.getParent() != null)
			session().refresh(post.getParent());
		return post;
	}

	private void validation() {
		if(StringUtil.empty(body)){
			throw new RuntimeException("A Post cannot be created without content.");
		}
		if(StringUtil.empty(description) && parent == null){
			throw new RuntimeException("A Topic cannot be created without a description.");
		}
	}

	private Entry buildEntry(Post post, String entryText) {
		Entry entry = new Entry();
		entry.setBody(entryText);
		entry.setSummary(PostFormatter.getInstance().formatSummary(entry.getBody()));		
		entry.setPost(post);
		session().save(entry);
		session().flush();
		return entry;
	}
	
	public Post update(){
		Post post = loadPost(getPostId());
		if(StringUtil.notEmpty(getBody())){
			Entry entry = buildEntry(post, getBody());
			post.addEntry(entry);
			post.setLatestEntry(entry);
			post.setEditDate(entry.getDate());
		}
		if(titleTag != null)
			post.setTitleTag(titleTag);
		if(publishYear != null)
			post.setPublishYear(publishYear);
		if(StringUtil.notEmpty(url))
			post.setUrl(url);
		ImageFull imageFull = null;
		if(StringUtil.notEmpty(imageUrl)){
			imageFull = createFullImage(post.getTitle());
			addImageToPost(imageFull, post);
		}
		
		post.setMetaMask(metaMask);
		
		session().update(post);
		session().flush();
		return post;
	}

	private Post buildPost() {
		Post post = new Post();
		
		post.setCreator(creator);
		post.setTitleTag(titleTag);
		post.setPublishYear(publishYear);
		post.setUrl(url);
		if(image != null)
			post.setImage(image);
		
		if(parent != null){
			if(parent.isPrivate()){
				setPrivate();
			}
				
			if(parent.isLegacyThreadHolder())
				throw new RuntimeException("You can't reply to a legacy thread holder.");
			
			if(parent.getThread() != null && parent.getThread().isLegacyThreadHolder()){
				//Move this old post to being a new style conversation starter
				reParent(parent.getRoot(),parent);
			}
			
			post.setParent(parent);
			post.setRoot(parent.getRoot());
			
			String path = generatePath(parent);
			post.setPath(path);
			
			if(post.getRoot().equals(parent)){
				post.setThread(post);//Conversation starter
				post.setThreadReplyDate(post.getDate());
			}
			else{
				post.setThread(parent.getThread());//regurlar reply
			}
			//Increment the parent count
			parent.setReplyCount(parent.getReplyCount() + 1);
			parent.getRoot().setMass(parent.getRoot().getMass() + 1);
			//If the post is not a thread (well, and not a parent) we should increment the mass of the conversation
			if(!post.isThreadPost()){
				post.getThread().setMass(post.getThread().getMass() + 1);
				post.getThread().setThreadReplyDate(post.getDate());
				session().save(post.getThread());
			}
			
			session().save(parent.getRoot());
			session().save(parent);
		}
		
		post.setMetaMask(metaMask);
		
		session().save(post);
		session().flush();
		return post;
	}
	
	/**
	 * Moves the source post so that it becomes a reply of the target.
	 * 
	 * @param newParent
	 * @param source
	 * @return
	 */
	public static Post reParent(Post newParent, Post source){
		if(!newParent.isRootPost())
			throw new RuntimeException("Posts can only be reparented to root topics");
		if(newParent.getPostId() != source.getRoot().getPostId()){
			//fix paths.
			String originalSourcePath = source.getPath();
			source.setPath(generatePath(newParent)); 
			
			/*
			 * TREVIS after an hour or two of pain you figured out that you had to do this
			 * BEFORE doing the other stuff or else the generate path query was reading the wrong
			 * post.  Actually, a query that should have returned one post returned two and the first
			 * one appeared to be garbage!?!? the values made no sense.  Thank god for unittests.
			 */
			Post oldParent = source.getParent();
			Post oldRoot = source.getRoot();
			
			Post newRoot = newParent.getRoot();
			Post oldThread = source.getThread();
			
			if(source.getHasChildren()){
				relocateSomeChildrenOfAThread(originalSourcePath, source, oldRoot, newRoot, oldThread);
			}
			
			if(oldParent == null){
				throw new RuntimeException("Moving a root into a different hierarchy isnt implemented yet.");
			}
			if(!source.isThreadPost()){
				decreaseMass(oldThread);
			}
			
			oldParent.setReplyCount(oldParent.getReplyCount() - 1);
			decreaseMass(oldRoot);
			
			session().save(oldParent);
			session().save(oldRoot);
			session().save(oldThread); 
			
			
			source.setParent(newParent);
			source.setRoot(newRoot);
			Tag titleTag = newRoot.getTitleTag();
			source.setTitleTag(titleTag);
			
			newParent.setReplyCount(newParent.getReplyCount()+1);
			increaseMass(newRoot);
			
			if(newParent.isRootPost()){
				source.setThread(source);
			}
			else{
				source.setThread(newParent.getThread());
			}
			
			session().save(newParent);
			session().save(newRoot);
			session().save(source);
			
			//After setting the thread... fix the new guys offspring
			applyThreadTitleToNewFamilyMember(source, titleTag);
			
			session().flush();
			
		}
		else{
			throw new RuntimeException("Re parenting to the seame root is not here yet. TODO :-D");
		}
			
		return source;	
	}

	private static void applyThreadTitleToNewFamilyMember(Post post,
			Tag titleTag) {
		@SuppressWarnings("unchecked")
		List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId ORDER BY path")
			.setString("threadId", post.getThread().getPostId())
			.list();
		
		for(Post p : posts){
			p.setTitleTag(titleTag);
			session().save(p);
		}
	}

	private static void relocateSomeChildrenOfAThread(String originalSourcePath, Post source, Post oldRoot, Post newRoot,Post oldThread) {
		String topicId;
		if(!source.isThreadPost())
			topicId = oldThread.getPostId();
		else
			topicId = source.getPostId();
		
		@SuppressWarnings("unchecked")
		List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId AND path like :path ORDER BY path")
			.setString("threadId", topicId)
			.setString("path", originalSourcePath+"%")
			.list();
		
		String newPath = source.getPath();
		
		for(Post p : posts){
			if(source.getPostId().equals(p.getPostId())) continue; //The first post is already moved
			p.setRoot(newRoot);
			decreaseMass(oldRoot);
			increaseMass(newRoot);
			if(!source.isThreadPost()){
				decreaseMass(oldThread);
				p.setThread(source);
				increaseMass(source);
			}
			String path = p.getPath();
			p.setPath(newPath + path.substring(originalSourcePath.length()));
			session().save(p);
		}
		
	}

	private static void decreaseMass(Post oldRoot) {
		oldRoot.setMass(oldRoot.getMass() - 1);
	}

	private static void increaseMass(Post newRoot) {
		newRoot.setMass(newRoot.getMass() + 1);
	}

	static String generatePath(Post parent){
		String path;
		Post nearestSiblingPost = readNewestDirectDescendant(parent);
		
		if(nearestSiblingPost != null){
			path = calculateNextPath(nearestSiblingPost.getPath());
		}
		else{
			if(parent.isRootPost()){
				path = "00000";
			}
			else{
				String parentPath = parent.getPath();
				path = parentPath+".00000";
			}
		}
		return path;
	}

	
	static Post readNewestDirectDescendant(Post parent) {
		Post post;

		@SuppressWarnings("unchecked")
		List<Post> list = session().createCriteria(Post.class)
			.add(Restrictions.eq("parent.postId", parent.getPostId()))
			.addOrder(Order.desc("path"))
			.setMaxResults(1)
			.setFirstResult(0)
			.list();
		
		if(list.size() > 1){
			throw new RuntimeException("readNewestDirectDescendant is broken.");
		}
		if(list.size() == 0)
			post = null;
		else
			post = list.get(0);
		return post;
	}
	
	static String calculateNextPath(String newestSiblingPath){
		String parentPath = "";
		if(newestSiblingPath.lastIndexOf('.') > 0)
			parentPath = newestSiblingPath.substring(0,newestSiblingPath.lastIndexOf('.')) + '.';
		String nesetdIndexStr = newestSiblingPath.substring(newestSiblingPath.lastIndexOf('.')+1);
		int nestedIndex = Integer.parseInt(nesetdIndexStr);
		int nextNestedIndex = nestedIndex + 1;
		String nextNestedIndexStr = String.format("%05d", nextNestedIndex);
		String path = parentPath + nextNestedIndexStr;
		return path;
	}
	
	
	/*
	public static Post readPost(String postId){
		Post post = (Post)session().createCriteria(Post.class)
			.add(Restrictions.eq("postId", postId))
			.uniqueResult();
		return post;
	}
	*/

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	public Tag getTitle() {
		return titleTag;
	}

	public void setTitle(Tag title) {
		this.titleTag = title;
	}

	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}

	public Post getParent() {
		return parent;
	}

	public void setParent(Post parent) {
		this.parent = parent;
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
	
	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	

	public long getMetaMask() {
		return metaMask;
	}

	public void setMetaMask(long metaMask) {
		this.metaMask = metaMask;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public void setDeleted(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_DELETED;
	}
	
	public void setInf(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_INF;
	}
	
	public void setLink(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_LINK;
	}
	
	public void setMovie(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_MOVIE;
	}
	
	public void setNws(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_NWS;
	}
	
	public void setPrivate(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_PRIVATE;
	}
	
	public void setRatable(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_RATABLE;
	}
	
	public void setReview(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_REVIEW;
	}
	
	public void setLocked(){
		metaMask = metaMask | PostFlagBitmasks.BITMASK_LOCKED;
	}

		
}
