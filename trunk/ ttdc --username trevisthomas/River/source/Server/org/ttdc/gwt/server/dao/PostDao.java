package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.util.PostFlagBitmasks;


final public class PostDao {
	private String body;
	private Image image;
	private Post parent;
	private String embedMarker;
	private Person creator;
	private Tag titleTag;
	private String url;
	private Integer publishYear;
	private long metaMask = 0;
	
	public PostDao(){}

	public static Post loadPost(String postId){
		Post p = (Post)session().load(Post.class, postId);
		return p;
	}
	
	public Post create(){
		if(StringUtils.isEmpty(body)){
			throw new RuntimeException("A Post cannot be created without content.");
		}
		
		Post post = buildPost();
		Entry entry = buildEntry(post);
		post.addEntry(entry);
		post.setLatestEntry(entry);
		
		session().flush();
		return post;
	}

	private Entry buildEntry(Post post) {
		Entry entry = new Entry();
		if(StringUtils.isNotBlank(embedMarker)){
			String fixedBody = body.replaceAll(embedMarker, post.getPostId());
			entry.setBody(fixedBody);
		}
		else{
			entry.setBody(body);
		}
		
		entry.setPost(post);
		session().save(entry);
		session().flush();
		return entry;
	}

	private Post buildPost() {
		Post post = new Post();
		
		post.setCreator(creator);
		post.setTitleTag(titleTag);
		post.setPublishYear(publishYear);
		post.setUrl(url);
		post.setMetaMask(metaMask);
		
		if(parent != null){
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

	public String getEmbedMarker() {
		return embedMarker;
	}

	public void setEmbedMarker(String embedMarker) {
		this.embedMarker = embedMarker;
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
