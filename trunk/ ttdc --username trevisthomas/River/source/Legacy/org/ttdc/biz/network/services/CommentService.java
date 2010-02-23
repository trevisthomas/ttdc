package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostCounter;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;
import org.ttdc.util.ServiceException;
import org.ttdc.util.StringTools;
import org.ttdc.util.web.HTMLCalendar;

public final class CommentService {
	private final static Logger log = Logger.getLogger(CommentService.class);
	//public  final static int MAX_RESULTS_FRONT_PAGE = 40; //Should come from person obj
	public final static int MAX_TAG_BROWSER_RESULTS = 1000;
	public final static int RSS_POST_COUNT = 50;
	private final static int POST_THRESHOLD = 1; //Hm since this is one it is probably no longer intresting.
	
	private String latestPostId = "";
	private AssociationPostTag latestTagAssociation = null;
	
	/**
	 * A helper class to bundle post attributes together for use with creation and editing.
	 * 
	 * @author Trevis
	 *
	 */
	public static class TransientPost{
		private String title;
		private String body; 
		private String url;
		private String imageUrl;
		private String year;
		
		public TransientPost(){}

		public String getTitle() {
			return title != null ? title : "";
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBody() {
			return body != null ? body : "";
		}

		public void setBody(String body) {
			this.body = body.trim();
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url != null ? url.trim() : "";
		}

		public String getImageUrl() {
			return imageUrl != null ? imageUrl : "";
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl != null ? imageUrl.trim() : "";
		}


		public String getYear() {
			return year != null ? year : "" ;
		}
		public void setYear(String year){
			this.year = year;				
		}
	}
	
	private static class SingletonHolder {
		private final static CommentService INSTANCE = new CommentService();
	}

	public synchronized AssociationPostTag getLatestTagAssociation() {
		return latestTagAssociation;
	}
	public synchronized void setLatestTagAssociation(AssociationPostTag latestTagAssociation) {
		this.latestTagAssociation = latestTagAssociation;
	}
	public synchronized String getLatestPostId() {
		return latestPostId;
	}
	public synchronized void setLatestPostId(String latestPostId) {
		this.latestPostId = latestPostId;
	}
	
	/*
	public static class BumpThread extends Thread {
        public BumpThread() {
            start();
        }
        public void run() {
        	while(true){
	        	try{
	        		Date start = new Date();
	        		me.readFrontPagePosts(UserService.getInstance().getAnnonymousUser());
	        		Date end = new Date();
	        		log.info("Lame front page bumper took: "+(end.getTime() - start.getTime())/1000.0+" seconds.");
					sleep(10 * 60 * 1000);
	        	}
	        	catch(Throwable t){
	        		log.error(t);
	        	}
        	}
        }
    }
	*/
	public final static CommentService getInstance(){
		return SingletonHolder.INSTANCE;
	}
	public CommentService() {
		Session session = Persistence.beginSession();
		Query query = session.getNamedQuery("ass.getLatest").setMaxResults(1);
		AssociationPostTag ass = (AssociationPostTag)query.uniqueResult();
		setLatestTagAssociation(ass);
		
		InboxService.getInstance();//Prime the pump
		log.info("CommentService singleton has been created.");
	}
	
	
	
	
	/**
	 * Edit a post.
	 * 
	 * @param editor
	 * @param tp
	 * @return
	 * @throws ServiceException
	 */
	public Post editPost(Person editor, String postId, TransientPost tp) throws ServiceException{
		//Should probably re-authenticate?
		ImageFull fullImage = null;
		try{
			Session session = Persistence.beginSession();
				
			Post p = (Post)session.load(Post.class,postId);
			if(p == null){
				throw new ServiceException("Post not found");
			}
			
			if(p.isRootPost() &&  "".equals(tp.getTitle().trim())){
				throw new ServiceException("Thread title can not be blank!");
			}
			
			//Admin can edit anything, users can only edit things that they posted within a preset time frame.  
			//I decided not to check that the time window here but i am going to make sure that the post 
			//being edited was created by this user
			if(!(editor.isAdministrator() || p.getCreator().equals(editor))){
				throw new ServiceException("You are not authorized to edit this post.");
			}
				
			Image image = null;
			
			if(tp.getImageUrl().length() > 0 && !tp.getImageUrl().equals(p.getImage() != null ? p.getImage().getName() : "")){
				fullImage = ImageService.getInstance().createImage(editor, tp.getImageUrl(),System.currentTimeMillis()+"_"+tp.getTitle().replace(" ", ""));
				session.save(fullImage);
				Persistence.commit();
				session = Persistence.beginSession();
				Query query = session.getNamedQuery("image.getById").setString("imageId", fullImage.getImageId());
				image = (Image) query.uniqueResult();
			}
			
			p = (Post)session.load(Post.class,postId);//Since i comited above, i have to get it again? 
			Entry e = new Entry();
			e.setBody(tp.getBody());
			p.addEntry(e);
			if(image != null)
				p.setImage(image);
			session.update(p);
			
			AssociationPostTag ass = p.loadTitleTagAssociation();
			if(updateTag(ass,tp.getTitle())){
				ass = p.loadTagAssociation(Tag.TYPE_SORT_TITLE);
				updateTag(ass,StringTools.formatTitleForSort(tp.getTitle()));
			}
			ass = p.loadTagAssociation(Tag.TYPE_URL);
			updateTag(ass,tp.getUrl());
			
			ass = p.loadTagAssociation(Tag.TYPE_RELEASE_YEAR);
			updateTag(ass,tp.getYear().toString());
			
			//Trevis, should probably validate updates
			Persistence.commit();
			
			session = Persistence.beginSession();
			p = (Post)session.load(Post.class,postId);
			p.initialize();
			p.setHidden(false);
			
			Persistence.commit();
			
			UserMessageService.getInstance().refreshPostForAllUsers(p);	
			
			return p;
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw e;
		}
		catch(Exception t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Reads the value of the tag on an association and if the value is different the tag is updated.
	 * Be aware that the tag is updated, not the association. So if you edit a post which has a title 
	 * and you change the title but the title was linked to other posts, those are changed too.
	 * 
	 * @param ass
	 * @param newValue
	 * @return true if updated, false if no update was needed. Also returns false if ass is null
	 */
	private boolean updateTag(AssociationPostTag ass, String newValue){
		if (ass == null) return false;
		Tag tag = ass.getTag();
		if(!tag.getValue().equals(newValue)){
			tag.setValue(newValue);
			Session session = Persistence.session();
			session.update(tag);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Special posts like root and movie review need more decoration from the start so.. this does that.
	 * 
	 * @param type
	 * @param creator
	 * @param tp
	 * @return
	 * @throws ServiceException
	 */
	public Post createAdvPost(String type, Person creator, TransientPost tp) throws ServiceException {
		ImageFull fullImage = null;
		try{
			String postId;
			if(!creator.getHasPostPrivilege())
				throw new ServiceException("You dont have the privilege to add posts.");
			
			Post p = new Post();
			
			Image image = null;
			Post parent = null;
			
			if("".equals(tp.getTitle().trim())){
				throw new ServiceException("Thread title can not be blank!");
			}
			
			Session session = Persistence.beginSession();
			if(type.equals("normal")){
				//Find out if a thread already exists with this title
				//Query query = session.getNamedQuery("post.getRootPostWithTagValueAndType").setString("tag", tp.getTitle()).setString("type",Tag.TYPE_TITLE);
				Query query = session.getNamedQuery("post.findThreadRootForTitle").setString("tag", tp.getTitle());
				
				//parent = (Post)query.uniqueResult();
				parent = grabTheMostLikelyParent(query);
			}
			
			Date now = new Date();
			if(tp.getImageUrl().length() > 0){
				fullImage = ImageService.getInstance().createImage(creator, tp.getImageUrl(),tp.getTitle().replace(" ", ""));
				session.save(fullImage);
				Persistence.commit();
				session = Persistence.beginSession();
				Query query = session.getNamedQuery("image.getById").setString("imageId", fullImage.getImageId());
				image = (Image) query.uniqueResult();
			}
			Entry e = new Entry();
			e.setBody(tp.getBody());
			p.setDate(now);
			p.addEntry(e);
			if(image != null)
				p.setImage(image);
			session.save(p);
			
			postId = p.getPostId();
			
			tagCalenderInfo(p, creator);
			
			
			//If a parent was determined using the tag value, assign the parent and tag this post with the tag
			if(parent != null){
				parent.addChild(p);
				session.refresh(p.getParent());
			}else{
				//findOrCreateTag(p, tp.getTitle(), Tag.TYPE_TOPIC, creator, now);
				findOrCreateTag(p, tp.getTitle(), Tag.TYPE_TOPIC, creator, now, true);
			}
			findOrCreateTag(p,creator.getLogin(), Tag.TYPE_CREATOR, creator, now);
			
			if(type.equals("movie")){
				findOrCreateTag(p, StringTools.formatTitleForSort(tp.getTitle()), Tag.TYPE_SORT_TITLE, creator, now);
				findOrCreateTag(p, Tag.TYPE_MOVIE, Tag.TYPE_MOVIE, creator, now);
				findOrCreateTag(p, Tag.TYPE_RATABLE, Tag.TYPE_RATABLE, creator, now);
				if(tp.getUrl().length() > 0)
					findOrCreateTag(p, tp.getUrl(), Tag.TYPE_URL, creator, now);
				else
					throw new ServiceException("Url is required for movies.");
				
				findOrCreateTag(p, ""+tp.getYear(), Tag.TYPE_RELEASE_YEAR, creator, now);
			}
			
			Persistence.commit();
			
			setLatestPostId(postId);
			
			return p;
		}
		catch(ServiceException e){
			Persistence.rollback();
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			throw e;
		}
		catch(Exception t){
			log.error(t);
			Persistence.rollback();
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			throw new ServiceException(t);
		}
		
	}
	
	/*
	 * Trying to find the first non movie matching this topic tag
	 * In the future users should have a choice
	 */
	private Post grabTheMostLikelyParent(Query query){
		List<Post> list = (List<Post>)query.list();
		if(list.size() == 0)
			return null;
		else if(list.size() == 1)
			return list.get(0);
		else{
			for(Post p : list){
				if(!p.hasTagAssociation(Tag.TYPE_MOVIE)){
					return p;
				}
			}
		} 
		return null;
			
	}
	
	/**
	 * Use the tagValue to locate an existing tag, if one exists, use it
	 * if not, create a new tag and associate it with this post.
	 * 
	 * @param postId
	 * @param tagValue
	 * @return
	 */
	public Post tagPost(String postId, String tagValue, Person creator) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Post post = (Post)session.load(Post.class,postId); 
			if(post == null){
				throw new ServiceException("Post not found");
			}
			
			if(tagValue != null && tagValue.trim().length() > 0){
				tagValue = tagValue.trim();
				Query query = session.getNamedQuery("tag.getByValueAndType");
				Tag t = (Tag)query.setString("value", tagValue).setString("type",Tag.TYPE_TOPIC).uniqueResult();
				
				if(!post.containsTag(t))
					findOrCreateTag(post, tagValue, Tag.TYPE_TOPIC, creator, new Date());
			}
			return post;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Tags a post with a special tag signifying that the user would like to be able to quickly find this post
	 * 
	 * @param postId
	 * @param person
	 * @return
	 */
	public Post tagEarmarkPost(String postId, Person person) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Post post = (Post)session.load(Post.class,postId); 
			if(post == null){
				throw new ServiceException("Post not found");
			}
			String tagValue = person.getPersonId();
			Query query = session.getNamedQuery("tag.getByValueAndType");
			Tag t = (Tag)query.setString("value", tagValue).setString("type",Tag.TYPE_EARMARK).uniqueResult();
			
			if(!post.containsTag(t))
				findOrCreateTag(post, tagValue, Tag.TYPE_EARMARK, person, new Date());
			
			return post;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Finds the earmark tag for the person argument.  If none the tag doesnt exist, null is returned.
	 * 
	 * @param person
	 * @return
	 */
	public Tag readEarmarkTag(Person person) throws ServiceException{
		try{
			Tag tag = null;
			Session session = Persistence.beginSession();
			
			Query query = session.getNamedQuery("tag.getByValueAndType");
			tag = (Tag)query.setString("value", person.getPersonId()).setString("type",Tag.TYPE_EARMARK).uniqueResult();
			return tag;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Configures the display tag for a post.  If tagValue is one that i know about i set it. Else all are removed.
	 * 
	 * @param postId
	 * @param tagValue
	 * @param creator
	 * @return
	 * @throws ServiceException
	 */
	public Post tagPostForDisplay(String postId, String tagValue, Person creator) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Post post = (Post)session.load(Post.class,postId); 
			if(post == null){
				throw new ServiceException("Post not found");
			}

			List<AssociationPostTag> asses = post.loadTagAssociations(Tag.TYPE_DISPLAY);
			List<String> guids = new ArrayList<String>();
			for(AssociationPostTag ass : asses){
				guids.add(ass.getGuid());
			}
			
			if(guids.size() > 0){
				Query query = session.getNamedQuery("ass.deleteByIds").setParameterList("guids", guids);
				query.executeUpdate();
				post = (Post)session.load(Post.class,postId); 
			}
			
			if(Tag.VALUE_INF.equals(tagValue)){
				findOrCreateTag(post, Tag.VALUE_INF, Tag.TYPE_DISPLAY, creator, new Date());
			}
			else if(Tag.VALUE_NWS.equals(tagValue)){
				findOrCreateTag(post, Tag.VALUE_NWS, Tag.TYPE_DISPLAY, creator, new Date());
			}
			else if(Tag.VALUE_PRIVATE.equals(tagValue)){
				findOrCreateTag(post, Tag.VALUE_PRIVATE, Tag.TYPE_DISPLAY, creator, new Date());
				//recursiveTag(post.getPosts(), t, creator); // All children of a private post should also be private
			}
			else if(Tag.VALUE_DELETED.equals(tagValue)){
				findOrCreateTag(post, Tag.VALUE_DELETED, Tag.TYPE_DISPLAY, creator, new Date());
			}
			else if(Tag.VALUE_LOCKED.equals(tagValue)){
				findOrCreateTag(post, Tag.VALUE_LOCKED, Tag.TYPE_DISPLAY, creator, new Date());
			}
			else{
				//All have been cleared
			}
			
			return post;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	
	/**
	 * Remove a tag association. There will probably be security so that only
	 * certain people can do this.
	 * 
	 * @param postId
	 * @param tagId
	 * @return
	 * @throws ServiceException
	 */
	public Post unTagPost(String postId, String tagId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("ass.getByAssByTagAndPost").setString("postId",postId).setString("tagId", tagId);
			AssociationPostTag myAss = (AssociationPostTag)query.uniqueResult();
			Post post = null;
			if(myAss != null){
				post = myAss.getPost();
				//post.getTagAssociations().remove(myAss);
				query = session.getNamedQuery("ass.deleteById").setString("guid", myAss.getGuid());
				query.executeUpdate();
				
				session.refresh(post);
				
				AssociationPostTag deletedAss = new AssociationPostTag();
				deletedAss.setGuid("deleted_"+myAss.getGuid());
				deletedAss.setPost(myAss.getPost());
				deletedAss.setTag(myAss.getTag());
				setLatestTagAssociation(deletedAss);
				
			}
			else{
				throw new ServiceException("Tag association not found");				
			}
			return post;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Creates s simple post.  Intended primarily for replies.
	 * @param creator
	 * @param parentId
	 * @param body
	 * @return
	 * @throws ServiceException
	 */
	public Post createPost(Person creator, String parentId, String body) throws ServiceException {
		try{
			String postId;
			if(!creator.getHasPostPrivilege())
				throw new ServiceException("You dont have the privilege to add posts.");
			if(body == null || body.equals("") || body.length() < 1){
				throw new ServiceException("Say something fool!");
			}
			
			Post p = new Post();
			Post parent = readPost(parentId);
			
			Session session = Persistence.beginSession();
			Date now = new Date();
			Entry e = new Entry();
			e.setBody(body);
			p.setDate(now);
			p.addEntry(e);
			//parent.addChild(p); //Does not affect parent at all
			session.save(p);
			/*
			Persistence.commit();
			session = Persistence.session();
			*/
			//Trevis, you do this this way because if you let p do an update it touches every tag association for some reason
			Query query = session.getNamedQuery("post.updateParentAndRoot")
				.setString("postId", p.getPostId())
				.setString("parentId", parent.getPostId())
				.setString("rootId", parent.getRoot().getPostId());
			
			query.executeUpdate();
			
			query = session.getNamedQuery("post.getByPostId").setString("postId", p.getPostId());
			p = (Post)query.uniqueResult();
			
			postId = p.getPostId();
			
			tagCalenderInfo(p, creator);
			findOrCreateTag(p,creator.getLogin(), Tag.TYPE_CREATOR, creator, now);
			
			if(parent.isPrivate()){
				//If the parent is private, replies should also be private
				findOrCreateTag(p, Tag.VALUE_PRIVATE, Tag.TYPE_DISPLAY, creator, now);
			}
			
			if(parent.isReviewable())
				findOrCreateTag(p, Tag.TYPE_REVIEW,Tag.TYPE_REVIEW, creator, now);
			
			//p.getRoot().initialize(); //Trevis: you do this because some views want to show the root thread title!
			
			
			Persistence.commit();
			
			session = Persistence.beginSession();
			query = session.getNamedQuery("post.getByPostId").setString("postId", p.getPostId());
			p = (Post)query.uniqueResult();
			p.initialize();
//			if(p.getRoot().isMovie())
//				PostHelper.initializePosts(p.getRoot().getReviews()); //Init reviews always
			
			query = session.getNamedQuery("post.getByPostId").setString("postId", p.getParent().getPostId());
			parent = (Post)query.uniqueResult();
			parent.initialize();
			
			

			setLatestPostId(postId);
			UserService.getInstance().userHit(creator);//The ajax action doesnt do a hit update so i do it here
			
			InboxService.getInstance().flagUnreadPost(creator, p);
						
			return p;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	
	private Tag findOrCreateTag(Post post, String value, String type, Person creator, Date date){
		return findOrCreateTag(post, value, type, creator, date, false);
	}
	
	private Tag findOrCreateTag(Post post, String value, String type, Person creator, Date date, boolean title){
		Session session = Persistence.session();
		value = value.trim();
		Query query = session.getNamedQuery("tag.getByValueAndType");
		Tag t = (Tag)query.setString("value", value).setString("type",type).uniqueResult();
		if(t == null){
			t = new Tag();
			t.setCreator(creator);
			t.setDate(date);
			t.setType(type);
			t.setValue(value);
			session.save(t);
		}
		tag(post, t, creator,true,title);
		return t;
	}
	

	/**
	 * Recursively tag the posts in the list and their children wiht the provided tag
	 * 
	 * @param list
	 * @param tag
	 * @param creator
	 */
	/*
	public void recursiveTag(List<Post> list, Tag tag, Person creator){
		for(Post p : list){
			tag(p, tag, creator);
			recursiveTag(p.getPosts(),tag,creator);
		}
	}
	*/
	
	/**
	 * Parses the data and tags the post appropriately
	 * 
	 * @param session
	 * @param post
	 * @param creator
	 */
	private void tagCalenderInfo(Post post, Person creator){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(post.getDate());
		findOrCreateTag(post, ""+cal.get(GregorianCalendar.DAY_OF_MONTH), Tag.TYPE_DATE_DAY, creator , post.getDate());
		findOrCreateTag(post, HTMLCalendar.getMonthName(cal.get(GregorianCalendar.MONTH)+1), Tag.TYPE_DATE_MONTH, creator , post.getDate());
		findOrCreateTag(post, ""+cal.get(GregorianCalendar.YEAR), Tag.TYPE_DATE_YEAR, creator , post.getDate());
	}
	
	/**
	 * Associates a tag with a post. This is an internal method to be used when you already have
	 * an active session, the person, post and tag instances.
	 * 
	 * @param post
	 * @param tag
	 * @param person
	 */
	private void tag(Post post, Tag tag, Person person, boolean notify, boolean title){
		Session session = Persistence.session();
		AssociationPostTag tagass = new AssociationPostTag();
		tagass.setPost(post);
		tagass.setCreator(person);
		tagass.setTag(tag);
		tagass.setTitle(title);
		post.addTagAssociation(tagass);
		session.save(tagass);
		if(notify)
			setLatestTagAssociation(tagass);
		session.update(post);
	}
	private void tag(Post post, Tag tag, Person person){
		tag(post,tag,person,true, false);
	}
	
	/**
	 * Update a post.
	 *  
	 * @param post
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	public void updatePost(Post post) throws ServiceException{
		
	}
	
	/**
	 * Open the post so that it will except replies.
	 * 
	 * @param postId
	 * @throws ServiceException
	 */
	public void openPost(int postId) throws ServiceException{
		
	}
	
	/**
	 * Close the post so that it can not be replied to 
	 * 
	 * @param postId 
	 * @throws ServiceException
	 */
	public void closePost(int postId) throws ServiceException{
		
	}
	
	/**
	 * Get a single post
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceException
	 */
	
	public Post readPost(String guid) throws ServiceException {
		return readPost(guid,null);
	}
	
	
	/**
	 * 
	 * @param guid
	 * @param decorateLatest
	 * @return
	 * @throws ServiceException
	 */
	
	public Post readPost(String guid, Person person) throws ServiceException {
		try{
			Session session = Persistence.beginSession();
			Post post = (Post)session.load(Post.class,guid); 
			if(post == null){
				throw new ServiceException("Post not found");
			}
			/*
			if(person != null && !person.isAnonymous())
				InboxService.getInstance().flagUnreadPosts(person,post);
			*/		
			post.initialize();
			post.setHidden(false);
			if(person != null && !person.isAnonymous())
				InboxService.getInstance().flagUnreadPost(person,post);
			Persistence.commit();
			return post;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	
	/*
	@SuppressWarnings("unchecked")
	public Post readBranch(Person person, String guid) throws ServiceException{
		try{
			Post post =	readPost(guid);
			Post branch = null;
			Session session = Persistence.session();
			
			Query query = session.getNamedQuery("post.getAllLatest").setCacheable(false).setMaxResults(MAX_RESULTS_FRONT_PAGE);
			List<Post> latest = (List<Post>)query.list();
			Persistence.commit(); //You have to reset the session trevis. Or else the order is wrong in the final list (because of the way you speed load by the root)
			session = Persistence.session();
			
			query = session.getNamedQuery("post.getBranchByRootId").setCacheable(false).setString("rootId",post.getRoot().getPostId().toString());
			for(Post p : (List<Post>)query.list()){
				if(p.getPostId().equals(guid)){
					branch = p; 
					break;
				}
			}
			
			if(branch == null)
				throw new ServiceException("Cound not read branch!");
			
			List<String> postIds = readPostIdsFromHierarchy(branch);
			initializeTagAssociations(postIds);
			branch.setExpanded(true);
			expandLatestInHierarchy(branch.getPosts(),latest);
			
			Persistence.commit();
			
			return branch;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	*/
	/**
	 * Refresh a branch (used for post updates on the front page)
	 * 
	 * @param person
	 * @param guid
	 * @param sourcePostId - the clicked post when refreshing (refresh refreshes the parent unless it's root)
	 * @param decorateLatest this is set to true when the branch is being configured for the front page. It lets me set relative age, and expanded if we're using them
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Post readBranchAll(Person person,String guid, String sourcePostId, boolean decorateLatest) throws ServiceException{
		try{
			
			Post branch = null;
			Session session = Persistence.beginSession();
			Post post =	(Post)session.load(Post.class,guid);
			
			PostCounter pc = PostHelper.loadPostCounter(post.getRoot().getPostId());
			
			List<Post> temp = new ArrayList<Post>();
			Set<String> postIds = new HashSet<String>();
			Query query;
			
			query = session.getNamedQuery("post.getBranchByRootId").setCacheable(true).setString("rootId",post.getRoot().getPostId());
			temp = (List<Post>)query.list();
			
			if(decorateLatest && pc.getCount() > POST_THRESHOLD){
				query = session.getNamedQuery("post.getAllLatest").setCacheable(true).setMaxResults(person.getNumCommentsFrontpage());
				List<Post> latest = (List<Post>)query.list();
				Date d = null;
				
				if(!person.isAnonymous())
					d = person.getLastAccessDate();
				postIds = extractFromLatest(latest, d);//Gets all post id's that make up the latest and their direct ancestors
				
				PostHelper.extractIds(latest,postIds);
			}
			else{
				PostHelper.extractIds(temp, postIds);
			}
			
			if(sourcePostId != null)
				expandPost(temp,sourcePostId);
			
			
			
			
			for(Post p : temp){
				if(p.getPostId().equals(guid)){
					branch = p; 
					//branch.setExpanded(true);
				}
				/*
				else if(p.getPostId().equals(sourcePostId)){
					p.setExpanded(true);
				}
				*/
				
				if(decorateLatest && p.equals(post.getRoot()) && pc.getCount() > POST_THRESHOLD){
					p.setPostCounter(pc); //Dont set this for a full thread view
				}
			}
			
			
			if(branch == null)
				throw new ServiceException("Cound not read branch!");
			
			if(decorateLatest){
				/*
				query = session.getNamedQuery("post.getAllLatest").setCacheable(true).setMaxResults(MAX_RESULTS_FRONT_PAGE);
				List<Post> latest = (List<Post>)query.list();
				assignDefaultRelativeAges(branch);
				MedianCommentAgeUtil.assignPostRelativeAges(branch,latest);
				*/
			}
			/*
			if(!person.isAnonymous())
				setNewPostFlag(branch.getPosts(),person.getLastAccessDate());
			*/
			
			//debug
			List<AssociationPostTag> asses = branch.getTagAssociations();
			
			PostHelper.initializePosts(person,branch,postIds);
			
			//Trevis. you do this for the full list and not just the one you were looking for because refresh updates the child and parent
			/*
			if(!person.isAnonymous())
				InboxService.getInstance().flagUnreadPosts(person,temp);
			*/
			if(branch.isRootPost()){
				branch.setExpanded(true);//Always expand root posts
			}
			Persistence.commit();
			Post condensed = condense(branch);
			
			return condensed;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	
	
	/**
	 * I dont set the default relative age unless we're working on the frong page. This is for the post aging code 
	 * 
	 * @param p
	 */
	private void assignDefaultRelativeAges(Post p){
		List<Post> posts = new ArrayList<Post>();
		posts.add(p);
		assignDefaultRelativeAges(posts);
	}
	private void assignDefaultRelativeAges(List<Post> posts){
		for(Post p : posts){
			p.setRelativeAge(Post.RELATIVE_AGE_DEFAULT);
			assignDefaultRelativeAges(p.getPosts());
		}		
	}
	
	private List<String> readPostIdsFromHierarchy(Post post){
		List<String> postIds = new ArrayList<String>();
		postIds.add(post.getPostId());
		for(Post p : post.getPosts()){
			postIds.addAll(readPostIdsFromHierarchy(p));
		}
		return postIds;
	}
	
	/**
	 * Takes a post hierarchy and flatens it out into a map with the post id's being the keys
	 * Originally creating this for the front page post/entry merge
	 * 
	 * @param map
	 * @param posts
	 */
	private void readPostIdsFromHierarchyAsMap(Map<String,Post> map, List<Post> posts){
		for(Post p : posts){
			map.put(p.getPostId(),p);
			readPostIdsFromHierarchyAsMap(map,p.getPosts());
		}
	}
	
	/**
	 * loads the post ids from a hierarchy of posts into a list and sorts the children
	 * in post order.  I crammed the sort functionality into this method
	 * to save from having to recursively iterate the list a second time
	 *  
	 * @param posts
	 * @param postIds
	 */
	private void bloatedChildrenHelper(List<Post> posts, List<String> postIds){
		for(Post p : posts){
			postIds.add(p.getPostId());
			Collections.sort(p.getPosts(),new Post.DateComparator());
			bloatedChildrenHelper(p.getPosts(),postIds);
		}
	}
	
	/**
	 * Traverses a list of posts and sets the new flag to true if they are newer than 'date'
	 * 
	 * @param posts
	 * @param date
	 */
	private void setNewPostFlag(List<Post> posts, Date date){
		for(Post p : posts){
			if(p.getDate().getTime() >= date.getTime())
				p.setNewPost(true);
			setNewPostFlag(p.getPosts(), date);
		}
	}
	
	/**
	 * Delete a post AND all of it's posts. This is admin funcitonality.
	 * 
	 * @param id postId
	 * @throws ServiceException
	 */
	public void deletePost(int id) throws ServiceException {
		
	}
	
	/**
	 * 
	 * @param list
	 */
	/*
	private void initializePosts(List<Post> list){
		for(Post p : list){
			try{	
			Hibernate.initialize(p);
			Hibernate.initialize(p.getPosts());
			Hibernate.initialize(p.getTagAssociations());
			Hibernate.initialize(p.getEntry());
			if(p.getPosts() != null)
				initializePosts(p.getPosts());
			}
			catch(NullPointerException e){
				log.error(e);
			}	
		}
	}
	*/
	
	
	
	private Post condense(Post post){
		List<Post> list = new ArrayList<Post>();
		list.add(post);
		list = condense(list);
		if(list.size() > 0)
			return list.get(0);
		else
			return null;//This happens when a refreshed post has been filtered out!
	}
	private List<Post> condense(List<Post> posts){
		List<Post> condensed = new ArrayList<Post>();
		for(Post p : posts){
			if(!p.isHidden()){
				condensed.add(p);
				p.setPosts(condense(p.getPosts()));
			}
		}
		return condensed;
	}

	
	
	/**
	 * This version allows you to select which posts should show.
	 * 
	 * @param list
	 * @param toShow
	 */
	/*
	private void initializePosts(List<Post> list){
		for(Post p : list){
			Hibernate.initialize(p.getPosts());
			if(!p.isHidden())
				Hibernate.initialize(p.getTagAssociations());
			initializePosts(p.getPosts());
		}
	}
	*/
	
	
	
	
	
	
	/**
	 * Takes a hierarchy of posts and binds the posts with the entries
	 * 
	 * @param posts
	 * @param entries
	 */
	/*
	 * TREVIS! This may no longer be necessary!!! If the Entry is mapped to the post
	 * i may not have to attch it this way. This could be redundant.
	 */
	/*
	private void attachPostEntries(List<Post> posts, List<Entry> entries){
		
		Map<String,Post> map = new HashMap<String,Post>();
		readPostIdsFromHierarchyAsMap(map,posts);
		for(Entry entry : entries){
			Post p = map.get(entry.getPost().getPostId());
			p.setEntry(entry);
		}
	}
	*/
	/**
	 * Uses the reverseExtractor on a list of posts.
	 * 
	 * @param latest
	 * @return
	 */
	private Set<String> extractFromLatest(List<Post> latest, Date date){
		Set<String> set = new HashSet<String>();
		for(Post p : latest){
			reversePostIdExtractor(set,p,date);
		}
		return set;
	}
	/**
	 * This method will iterate through a post, backwards through it's parents
	 * grabbing just those id's.  I'm implementing it for the readFrontPage method
	 * so that i can walk through the latest post grabbing all of the id's that must be shown.
	 * 
	 * Iterating from the root through the children will cause hibernate to read all of the children
	 * which i want to avoid for fat roots
	 * 
	 * @param ids
	 * @param p
	 * @param date  - If date is not null i use that value to set the 'New flag' for posts newer than (or =) date 
	 */
	private void reversePostIdExtractor(Set<String> ids, Post p, Date date){
		ids.add(p.getPostId());
		if(p.getParent() != null)
			//if(!p.getRoot().equals(p.getParent()))
			reversePostIdExtractor(ids,p.getParent(), date);
	}
	
	/**
	 * Loads the latest posts in a flat data structure initialized as flat.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public List<Post> readRssPosts() throws ServiceException{
		try{
			//Session session = Persistence.currentSession();//Persistence.session();
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("post.getAllLatest").setMaxResults(RSS_POST_COUNT);
			@SuppressWarnings("unchecked") List<Post> latest = (List<Post>)query.list();
			PostHelper.initializePostsFlat(UserService.getInstance().getAnnonymousUser(), latest,true);
			return latest;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Gets a list of post hierarchies to be shown on the front page.  The user
	 * is required so that i can filter out muted users, NWS etc.
	 * 
	 * @param person user making the request  
	 * @return a sorted, filtered list of posts for the front page
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public String readFrontPagePosts(Person person, List<Post> retList, List<Post> flat) throws ServiceException{
		try{
			List<Post> list = new ArrayList<Post>();
			String postId;
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("post.getAllLatest").setMaxResults(person.getNumCommentsFrontpage());
			List<Post> latest = (List<Post>)query.list();
			Set<String> rootIds = new HashSet<String>();
			Set<Post> allRootPosts = new HashSet<Post>();
			postId = latest.get(0).getPostId();
			for(Post p : latest){
				rootIds.add(p.getRoot().getPostId());
				allRootPosts.add(p.getRoot());
			}
			Date d = null;
			
			if(!person.isAnonymous())
				d = person.getLastAccessDate();
			
			Set<String> postIdsToShow = extractFromLatest(latest, d);//Gets all post id's that make up the latest and their direct ancestors
		
			//List<PostCounter> totals = loadPostCounters(rootIds);
			Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(rootIds);
			List<PostCounter> totals = new ArrayList<PostCounter>();
			totals.addAll(totalsMap.values());
			
			Set<String> bloatedRootIds = new HashSet<String>();
			List<String> safeRootIds = new ArrayList<String>();
			
			bloatedRootIds.addAll(rootIds); //Treating all as bloated.
			/*
			for(PostCounter pct : totals){
				if(pct.getCount() <= POST_THRESHOLD)
					safeRootIds.add(pct.getRootId());
				else
					bloatedRootIds.add(pct.getRootId());
			}
			*/
			List<Post> bloatedRoots = new ArrayList<Post>();
			for(Post p : allRootPosts){
				if(bloatedRootIds.contains(p.getPostId())){
					bloatedRoots.add(p);
					p.setPostCounter(totalsMap.get(p.getPostId()));
				}
			}
			
			if(safeRootIds.size() > 0){
				query = session.getNamedQuery("post.getBranchsByRootId").setParameterList("rootIds", safeRootIds); 
				list = query.list();
				PostHelper.extractIds(list, postIdsToShow);
			}
			if(bloatedRootIds.size() > 0){
				//Investigate more closely.  I was trying to fix that bug that caused replies (who's parents were not roots) from the tag browser page not to show up on the front page.
				//postIdsToShow.addAll(bloatedRootIds);
				query = session.getNamedQuery("post.getBranchsByRootId").setParameterList("rootIds", bloatedRootIds);
				list.addAll(query.list());
			}
			list = chooseRoots(list);
			
			expandRoots(list);
			Collections.sort(list,new Post.ByReferenceComparator(latest));
			/*
			assignDefaultRelativeAges(list);
			MedianCommentAgeUtil.assignPostRelativeAges(latest);
			*/
			
			PostHelper.initializePosts(person, list, postIdsToShow, true);
			//Flat
			//PostHelper.initializePostsFlat(latest, filteredTagIds);
			
			Persistence.commit();
			if("".equals(getLatestPostId()))
				setLatestPostId(postId);
			
			/*
			if(!person.isAnonymous()){
				InboxService.getInstance().flagUnreadPosts(person,list);
				//InboxService.getInstance().flagUnreadPosts(person,latest);
			}
			*/

			retList.clear();
			flat.clear();
			
			//if(person.isHierarchy())
				retList.addAll(list);
			//else
				flat.addAll(latest);
			
			return postId;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Call this method with a list of posts and the ones which are unread
	 * will be set according.
	 * 
	 * @param person
	 * @param posts
	 */
	/*
	private void markUnreadPosts(Person person, List<Post> posts){
		InboxService ibx = InboxService.getInstance();
		List<String> unreadPosts = ibx.loadUserUnread(person);
		recurseUnread(posts,unreadPosts);
	}
	
	
	private void markUnreadPosts(Person person, Post post){
		InboxService ibx = InboxService.getInstance();
		List<String> unreadPosts = ibx.loadUserUnread(person);
		recurseUnread(post.getPosts(),unreadPosts);
	}
	*/
	/**
	 * Does the work of recursively iterating a list of posts to assign the unread flag
	 * @param posts
	 * @param unreadPostIds
	 */
	/*
	private void recurseUnread(List<Post> posts,List<String> unreadPostIds){
		for(Post p : posts){
			if(!p.isHidden()){
				if(unreadPostIds.contains(p.getPostId()))
					p.setUnread(true);
				recurseUnread(p.getPosts(),unreadPostIds);
			}
		}
	}
	*/
	/**
	 * 
	 * Checks to see if there is any content newer than the provided date. A list of posts 
	 * are returned if there is any new content
	 * 
	 * @param date
	 * @param postId
	 * @param refresh
	 * @param rootIds
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	/*
	public void pingForContent(String postId, List<Post> refresh, List<String> rootIds) throws ServiceException{
		
		try{
			Session session = Persistence.session();
			String latestPostId = getLatestPostId();
			if(!postId.equals(latestPostId)){
				
				Query query = session.getNamedQuery("post.getAllLatest").setMaxResults(MAX_RESULTS_FRONT_PAGE);
				List<Post> latest = (List<Post>)query.list();
				boolean found = false;
				for(Post p : latest){
					if(p.getPostId().equals(postId)){
						found = true;
					}
					if(!found){
						refresh.add(p);
					}
					
					String id = p.getRoot().getPostId();
					if(!rootIds.contains(id)){
						rootIds.add(id);
					}
				}
			}
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}*/
	
	
	
	/**
	 * This method recursively traverses a post hierarchy and extracts the rootId's
	 * It was implemented to traverse the hierarchy of latest posts and find the latest roots.
	 * those are later used to count the nodes in each root to determine
	 * if it is too big to be shown in it's entirety on the front page.
	 * 
	 * @param posts
	 * @param roots - empty set, to be filled with root posts
	 */
	/*
	private void rootExtractor(Set<Post> roots, List<Post> posts){
		for(Post p : posts){
			roots.add(findRoot(p));
		}
	}
	*/
	/**
	 * Will walk from a node up it's family tree to find the root
	 * 
	 * @param p
	 * @return
	 */
	/*
	private Post findRoot(Post p){
		if(p.getParent() == null)
			return p;
		else
			return findRoot(p.getParent());
	}
	*/
	
	/**
	 * Walks the tree to set the latest posts to be expanded.
	 * 
	 * @param posts
	 * @param latest
	 */
	private void expandLatestInHierarchy(List<Post> posts, List<Post> latest){
		//All root level posts are expanded.
		for(Post p : posts){
			if(p.getParent() == null || latest.contains(p) ){
				p.setExpanded(true);
			}
			expandLatestInHierarchy(p.getPosts(),latest);
		}
	}
	
	private void expandRoots(List<Post> posts){
		for(Post p : posts){
			if(p.getParent() == null){
				p.setExpanded(true);
			}
		}
	}
	private void expandPost(List<Post> posts, String postId){
		for(Post p : posts){
			if(p.getPostId().equals(postId)){
				p.setExpanded(true);
			}
		}
	}
	
	
	/*
	private void expandLatestInHierarchy(List<Post> posts, List<Post> latest){
		//All root level posts are expanded.
		boolean condense = false;
		if(posts.size() > POST_THRESHOLD){
			condense = true;
		}
		for(Post p : posts){
			if(p.getParent() == null || latest.contains(p) ){
				p.setExpanded(true);
			}
			else{
				if(condense && !p.getHasChildren()){
					p.setHidden(true);
				}
			}
			expandLatestInHierarchy(p.getPosts(),latest);
		}
	}
	*/
	
	/**
	 * Sets everything to expanded.
	 * @param posts
	 */
	/*
	private void setExpandedInHierarchy(List<Post> posts){
		//All root level posts are expanded.
		for(Post p : posts){
			p.setExpanded(true);
			setExpandedInHierarchy(p.getPosts());
		}
	}
	*/
	private List<Post> chooseRoots(List<Post> posts){
		//List<String> postIds = new ArrayList<String>();
		List<Post> list = new ArrayList<Post>();
		for(Post post : posts){
			if(post.getParent() == null)
				list.add(post);
			
			//postIds.add(post.getPostId());
		}
		//initializeTagAssociations(postIds);
		return list;
	}
	/*	
	private static void initializeTagAssociations(List<String> postIds){
		//There is a max of 2100 items in a param list.  Not sure if thats sql server or hibernate but still. that is why i chunked it.
		//Session session = Persistence.session();
		Session session = Persistence.currentSession();
		int j = 1000;
		for(int i = 0;i<postIds.size();i=i+j){
			int from = i;
			int to = i+j;
			if(to > postIds.size())
				to = postIds.size();
			Query query = session.getNamedQuery("ass.getByPostIds").setCacheable(false).setParameterList("postIds", postIds.subList(from, to));
			query.list();
		}
	}
	*/
	
	
	/**
	 * 
	 * For rating ratable content. One vote per person. If they send in a new rating, delete the old one and add
	 * the new one.
	 * 
	 * @param person
	 * @param postId
	 * @param rating
	 * @return  - Returns the updated list of ratings.
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<AssociationPostTag> rateContent(Person person, String postId, String rating) throws ServiceException{
		try{
			List<AssociationPostTag> ratings;
			Post post = readPost(postId);
			Session session = Persistence.beginSession();
			Query query;
		
			List<AssociationPostTag> asses = post.getRatings();
			AssociationPostTag myAss = null;
			for(AssociationPostTag ass : asses){
				if(ass.getCreator().getPersonId().equals(person.getPersonId())){
					myAss = ass;
				}
			}
			
			if(myAss != null){
				//I rated it
				post.getTagAssociations().remove(myAss); //Because i havent commited yet i have to remove this manually
				query = session.getNamedQuery("ass.deleteById").setString("guid", myAss.getGuid());
				query.executeUpdate();
				//session.delete(myAss); // For some reason this threw some werid exception. Something about duplicate objects in session?
			}
			
			
			query = session.getNamedQuery("tag.getByValue");
			Tag t = (Tag)query.setString("value", rating).uniqueResult();
			tag(post,t,person);
			//The tag *must* exist. An empty site running this engine should have these tags in the db before use
			//findOrCreateTag(post, rating, Tag.TYPE_RATING, person, new Date());
			
			ratings = post.getRatings();
			
			Persistence.commit();
			return ratings;
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AssociationPostTag> unrateContent(Person person, String postId) throws ServiceException{
		try{
			List<AssociationPostTag> ratings;
			Post post = readPost(postId);
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("ass.getByPostId").setString("postId", postId);
			query.list();
			
			List<AssociationPostTag> asses = post.getRatings();
			AssociationPostTag myAss = null;
			for(AssociationPostTag ass : asses){
				if(ass.getCreator().getPersonId().equals(person.getPersonId())){
					myAss = ass;
				}
			}
			if(myAss != null){
				//I rated it
				post.getTagAssociations().remove(myAss); //Because i havent commited yet i have to remove this manually
				query = session.getNamedQuery("ass.deleteById").setString("guid", myAss.getGuid());
				query.executeUpdate();
			}
			
			ratings = post.getRatings();
			
			Persistence.commit();
			return ratings;
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Auto complete handler for tags
	 * 
	 * @param phrase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TagLite> autoCompleteTag(String phrase, String type) throws ServiceException{
		try{
			List<TagLite> list = new ArrayList<TagLite>();
			Session session = Persistence.beginSession();
			String value;
			Query query;
			if(phrase.length() == 0){
				//Show the most popular
				query = session.getNamedQuery("tagLite.TagsForAutocompleteAll").setMaxResults(25).setString("type",type);

			}
			else {
				if (phrase.length() < 3)
					value = phrase + '%';
				else
					value = '%' + phrase + '%';

				query = session.getNamedQuery("tagLite.TagsForAutocomplete").setMaxResults(25).setString("value", value).setString("type", type);
			}
			
			list = query.list();
			TagLite.calculatePercentile(list);
			TagLite tl = new TagLite();
			tl.setValue(phrase);
			tl.setMass("");
			list.add(tl);

			return list;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Given a list of tag id's this method finds the full tag objects. This is used for the tag browser.
	 * @param tagIds
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> readTags(List<String> tagIds) throws ServiceException{
		try{
			if(tagIds == null || tagIds.size() == 0) return null;
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("tag.getByTagIds");
			List<Tag> tags = query.setParameterList("tagIds", tagIds).list();
			
			Collections.sort(tags,new Tag.ByReferenceComparator(tagIds));
			Persistence.commit();
			return tags;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	
	
	
	
	
	
	

	
	/**
	 * Loads entries for a list of post ids.
	 * @param postIds
	 */
	/*
	private void initEntries(Collection<String> postIds){
		Session session = Persistence.currentSession();
		Query query = session.getNamedQuery("entry.getByPostIds").setCacheable(true).setParameterList("postIds", postIds);
		query.list();//Calling list initializes the the entry objects which populates the posts that are to be shown
	}
	*/
	
	
	
	
	/**
	 * Get all of the comment hierarchies for a single day. Current thinking is that
	 * all methods that get comments will read full hierarchies
	 * 
	 * 
	 * @param person user making the request
	 * @param date date
	 * @param filter list of tag types to show
	 * @return a sorted, filtered list of posts for this date
	 * @throws ServiceException
	 */
	public List<Post> getPostsForDate(Person person, Date date, List<Tag> filter) throws ServiceException{
		List<Post> list = new ArrayList<Post>();
		return list;
	}
	/**
	 * Get a list of comment hierarchies in a date range
	 * 
	 * @param person user making the request
	 * @param fromDate from this date
	 * @param toDate to this date
	 * @param filter list of tag types to show
	 * 
	 * @return a sorted, filtered list of posts in the date range
	 * @throws ServiceException 
	 */
	public List<Post> getPostsForDateRange(Person person, Date fromDate, Date toDate, List<Tag> filter) throws ServiceException{
		List<Post> list = new ArrayList<Post>();
		return list;
	}
	/**
	 * A utility function to assist with construction of the history page.
	 * returns a list of years with content.
	 * 
	 * @return a sorted list of dates with content
	 * @throws ServiceException
	 */
	public List<String> getYearsWithContent() throws ServiceException{
		List<String> years = new ArrayList<String>();
		
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("tag.getDateYears").setCacheable(true);
			@SuppressWarnings("unchecked") List<Tag> tags = query.list();
			for(Tag t : tags){
				years.add(t.getValue());
			}
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
		
		return years;
	}

	/**
	 * Tag a post. Creates an AssociationPostTag record/object 
	 * 
	 * @param tag The tag.
	 * @param person the user who's doing the tagging
	 * @param postId the id of the post
	 * 
	 * @throws ServiceException
	 */
	public void tagPost(Tag tag, Person person, int postId) throws ServiceException{
		
	}
	

	/**
	 * create a new tag record
	 * 
	 * @param tag
	 * @throws ServiceException
	 */
	public void createTag(Tag tag) throws ServiceException{
		
	}
	/**
	 * update a tag record
	 * 
	 * @param tag
	 * @throws ServiceException
	 */
	public void updateTag(Tag tag) throws ServiceException{
		
	}
	/**
	 *  Delete tag. This removes a tag from the DB and is likely admin functionality.
	 *  
	 *  
	 * @param tagId
	 * @throws ServiceException 
	 */
	public void deleteTag(int tagId) throws ServiceException{
		
	}
	/**
	 * 
	 * @param tagId
	 * @throws ServiceException
	 */
	public void readTag(int tagId) throws ServiceException{
		
	}
	

	
	/*
	private Set<String> extractIdsLite(List<PostLite> posts){
		Set<String> postIds = new HashSet<String>();
		for(PostLite pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	*/
	
	/**
	 * Loads a list of people who have rated movies
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Person> readMovieVoters() throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getMovieReviewers").setCacheable(true);
			List<Person> people = query.list();
			for(Person p : people){
				p.initialize();
			}
			return people;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}		
}
