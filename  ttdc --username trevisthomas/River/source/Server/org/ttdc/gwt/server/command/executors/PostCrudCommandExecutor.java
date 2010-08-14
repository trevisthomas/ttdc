package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.ImageDao;
import org.ttdc.gwt.server.dao.ImageDataDao;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.server.util.CalendarBuilder;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;

public class PostCrudCommandExecutor extends CommandExecutor<PostCommandResult>{
	
	@Override
	protected CommandResult execute() {
		PostCrudCommand cmd = (PostCrudCommand)getCommand();
		Post post = null;
		PostEventType broadcastType = null;
		try{
			beginSession();
			switch(cmd.getAction()){
//			case DELETE:
//				break;
			case UPDATE:
				post = update(cmd);
				broadcastType = PostEventType.EDIT;
				break;
			case UPDATE_META:
				post = updateMeta(cmd);
				broadcastType = PostEventType.EDIT;
				break;
			case CREATE:
				post = create(cmd);
				broadcastType = PostEventType.NEW;
				break;
			case READ:
				post = read(cmd);
				break;
			case REPARENT:
				post = reparent(cmd);
				break;
			default:
				throw new RuntimeException("I cant do that action. Feel free to teach me though.");
			}
			
			PostCommandResult result = null;
			if(post!= null){
				InboxDao inboxDao = new InboxDao(getPerson());
				GPost gPost = FastPostBeanConverter.convertPost(post, inboxDao); 
				result = new PostCommandResult(gPost);
				
				if(broadcastType != null){
					PostEvent event = new PostEvent(broadcastType,gPost);
					ServerEventBroadcaster.getInstance().broadcastEvent(event,getCommand().getConnectionId());
				}
			}
			
			commit();
			
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
	}
	
	protected Post updateMeta(PostCrudCommand cmd){
		Person creator = determinePerson(cmd);
		if(!creator.hasPrivilege(Privilege.POST) && !creator.isAdministrator()){
			throw new RuntimeException("You dont have the priviledges to flag this post.");
		}
		Post post = PostDao.loadPost(cmd.getPostId());
		PostDao dao = new PostDao();
		dao.setCreator(creator);
		dao.setPostId(cmd.getPostId());
		
		Long metaMask = cmd.getMetaMask();
		if(metaMask != null){
			dao.setMetaMask(metaMask);
			post = dao.update();
			return post;
		}
		throw new RuntimeException("Meta mask not provided for update!");
	}
	
	protected Post update(PostCrudCommand cmd) {
		
		Person creator;
		creator = determinePerson(cmd);
		
		Post post = PostDao.loadPost(cmd.getPostId());
		
		authenticateForAddUpdateAccess(creator, post);
		
		PostDao dao = new PostDao();
		dao.setCreator(creator);
		dao.setBody(cmd.getBody());
		dao.setPostId(cmd.getPostId());
		if(StringUtil.notEmpty(cmd.getTitle())){
			Tag titleTag = loadOrCreateTitleTag(cmd);
			dao.setTitle(titleTag);
		}
		dao.setUrl(cmd.getUrl());
		if(StringUtil.notEmpty(cmd.getYear())){
			dao.setPublishYear(Integer.parseInt(cmd.getYear()));
		}
		if(StringUtil.notEmpty(cmd.getImageUrl())){
			dao.setImageUrl(cmd.getImageUrl());
		}
		
		if(cmd.getMetaMask() != null)
			dao.setMetaMask(cmd.getMetaMask());
		
		post = dao.update();
		return post;
	}

	private void authenticateForAddUpdateAccess(Person creator, Post post) {
		if(!creator.hasPrivilege(Privilege.POST) && !creator.isAdministrator()){
			throw new RuntimeException("You dont have privledges to create new content.");
		}
		
		if(!(post.getCreator().equals(creator) || creator.isAdministrator()) ){
			throw new RuntimeException("You didnt create this post, Hacker.");
		}
	}

	private Person determinePerson(PostCrudCommand cmd) {
		Person creator;
		if(StringUtil.notEmpty(cmd.getLogin()) && StringUtil.notEmpty(cmd.getPassword())){
			creator = AccountDao.login(cmd.getLogin(), cmd.getPassword());
		}
		else{
			creator = PersonDao.loadPerson(getPerson().getPersonId());
		}
		return creator;
	}


	protected Post reparent(PostCrudCommand cmd) {
		//TODO secure for admin only
		Post target = PostDao.loadPost(cmd.getPostId());
		Post oldParent = target.getParent();
		Post newParent = PostDao.loadPost(cmd.getParentId());
		Post post = PostDao.reParent(newParent, target);
		
//		if(!newParent.getRoot().equals(oldParent.getRoot())){
//			//Fix title tag
//			AssociationPostTag ass = post.loadTitleTagAssociation();
//			AssociationPostTagDao.reTag(ass.getGuid(), newParent.loadTitleTagAssociation().getTag());
//			
//			
//			//Fix the titles of children
//			@SuppressWarnings("unchecked")
//			List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId ORDER BY path")
//				.setString("threadId", post.getThread().getPostId())
//				.list();
//			
//			for(Post p : posts){
//				ass = p.loadTitleTagAssociation();
//				AssociationPostTagDao.reTag(ass.getGuid(), newParent.loadTitleTagAssociation().getTag());
//			}
//		}
		
		return post;
	}


	protected Post read(PostCrudCommand cmd) {
		Post post = PostDao.loadPost(cmd.getPostId());
		
		if(cmd.isLoadRootAncestor()){
			if(!post.isRootPost()){
				post = post.getRoot();
			}
		}
		if(cmd.isLoadThreadAncestor()){
			if(!post.isRootPost() && !post.isPostThreadRoot()){
				post = post.getThread();
			}
		}
		
		
		return post;
	}
	
	
	protected Post create(PostCrudCommand cmd) {
		Person creator;
		
		creator = determinePerson(cmd);
		
		if(!creator.hasPrivilege(Privilege.POST) && !creator.isAdministrator())
			throw new RuntimeException("You dont have privledges to create new content.");
		
		Post parent = null;
		if(!StringUtils.isEmpty(cmd.getParentId()))
			parent = PostDao.loadPost(cmd.getParentId());
		
		PostDao dao = new PostDao();
		if(cmd.isDeleted())
			dao.setDeleted();
		if(cmd.isInf())
			dao.setInf();
		if(cmd.isMovie()){
			dao.setMovie();
			dao.setRatable();
		}
		if(cmd.isNws())
			dao.setNws();
		if(cmd.isPrivate())
			dao.setPrivate();
		if(cmd.isReview()){
			if(parent == null)
				throw new RuntimeException("Reviews must have parents");
			else if(!parent.isReviewable())
				throw new RuntimeException("Reviews only allowed on reviewable content");
			dao.setReview();
		}
		if(cmd.isLocked())
			dao.setLocked();
		
		
		dao.setParent(parent);
		dao.setBody(cmd.getBody());
		dao.setCreator(creator);
		dao.setImageUrl(cmd.getImageUrl());
		Tag titleTag = loadOrCreateTitleTag(cmd);
		dao.setTitle(titleTag);
		dao.setUrl(cmd.getUrl());
		if(StringUtil.notEmpty(cmd.getYear())){
			dao.setPublishYear(Integer.parseInt(cmd.getYear()));
		}
		
		dao.setEmbedMarker(cmd.getEmbedMarker());
		Post post = dao.create();
		
		for (GTag gTag : cmd.getTags()){
			Tag tag;
			if(StringUtil.notEmpty(gTag.getTagId()))
				tag = TagDao.loadTag(gTag.getTagId());
			else
				tag = findOrCreateTag(gTag.getValue(), Tag.TYPE_TOPIC);
			createTagAssociation(creator,post,tag);
		}
		
		return post;
	}

	private Tag findOrCreateTag(String value, String type) {
		TagDao dao;
		dao = new TagDao();
		dao.setValue(value);
		dao.setType(type);
		dao.setDate(new Date());
		Tag tag = dao.createOrLoad();
		return tag;
	}
	
	/* Apply calender info */
//	private void tagCalenderInfo(Post post, Person creator){
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.setTime(post.getDate());
//		Date date = post.getDate();
//		
//		String value = ""+cal.get(GregorianCalendar.DAY_OF_MONTH);
//		String type = Tag.TYPE_DATE_DAY;
//		Tag tag = findOrCreateTag(creator, value, type, date);
//		createTagAssociationForPost(creator, post, tag);
//		
//		value = CalendarBuilder.getMonthName(cal.get(GregorianCalendar.MONTH)+1);
//		type = Tag.TYPE_DATE_MONTH;
//		tag = findOrCreateTag(creator, value, type, date);
//		createTagAssociationForPost(creator, post, tag);
//		
//		value = ""+cal.get(GregorianCalendar.YEAR);
//		type = Tag.TYPE_DATE_YEAR;
//		tag = findOrCreateTag(creator, value, type, date);
//		createTagAssociationForPost(creator, post, tag);
//		
//		value = ""+cal.get(GregorianCalendar.WEEK_OF_YEAR);
//		type = Tag.TYPE_WEEK_OF_YEAR;
//		tag = findOrCreateTag(creator, value, type, date);
//		createTagAssociationForPost(creator, post, tag);
//		
//	}


//	private Tag findOrCreateTag(String value, String type, Date date) {
//		TagDao dao;
//		dao = new TagDao();
//		dao.setValue(value);
//		dao.setType(type);
//		dao.setDate(date);
//		Tag tag = dao.createOrLoad();
//		return tag;
//	}

//	private void createTitleTagAssociationForPost(Person creator, Post post, Tag tag) {
//		createTagAssociationImplementation(creator, post, tag, true);
//	}
//	
//	private void createTagAssociationForPost(Person creator, Post post, Tag tag) {
//		createTagAssociationImplementation(creator, post, tag, false);
//	}
	
//	private void createTagAssociationImplementation(Person creator, Post post, Tag tag, boolean isTitle) {
//		AssociationPostTagDao assDao = new AssociationPostTagDao();
//		assDao.setPost(post);
//		assDao.setTag(tag);
//		assDao.setCreator(creator);
//		assDao.setTitle(isTitle);
//		assDao.create();
//		
//		Persistence.session().flush();
//		Persistence.session().refresh(post);
//	}

	private void createTagAssociation(Person creator, Post post, Tag tag) {
		AssociationPostTagDao assDao = new AssociationPostTagDao();
		assDao.setPost(post);
		assDao.setTag(tag);
		assDao.setCreator(creator);
		assDao.create();
		
		Persistence.session().flush();
		Persistence.session().refresh(post);
	}

	private Tag loadOrCreateTitleTag(PostCrudCommand cmd) {
		Tag tag;
		if(StringUtils.isNotBlank(cmd.getParentId())){
			Post parent = PostDao.loadPost(cmd.getParentId());
			tag = parent.getTitleTag();
		}
		else{
			if(StringUtils.isBlank(cmd.getTitle()))
				throw new RuntimeException("Title is required to create a new topic");
			else if(cmd.getTitle().length() <= InitConstants.MIN_TITLE_LENGTH)
				throw new RuntimeException("Title has to be longer than "+InitConstants.MIN_TITLE_LENGTH+" characters.");
			else{
				TagDao dao = new TagDao();
				dao.setType(Tag.TYPE_TOPIC);
				dao.setValue(cmd.getTitle());
				tag = dao.createOrLoad();
			}
		}
		return tag;
	}
	
}
