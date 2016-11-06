package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;

public class PostCrudCommandExecutor extends CommandExecutor<PostCommandResult>{
	
	@Override
	protected CommandResult execute() {
		PostCrudCommand cmd = (PostCrudCommand)getCommand();
		Post post = null;
		GPost gPost = null;
		PostEventType broadcastType = null;
		
		if(PostActionType.PREVIEW.equals(cmd.getAction())){
			return formatBodyForPreview(cmd);
		}
		
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
				//Why wont this check in?
				// InboxDao inboxDao = new InboxDao(getPerson());
				gPost = FastPostBeanConverter.convertPost(post, !cmd.isUnformatted(),
						cmd.isAddReviewsToMovies());
				
				//The following code to inflate the parent was added for dynamic front page refresh
				if(post.getParent() != null){
					GPost gParent = FastPostBeanConverter.convertPost(post.getParent());
					gPost.setParent(gParent);
				}
				
				result = new PostCommandResult(gPost);
			}
			
			commit();
			
			if(broadcastType != null && gPost != null){
				PostEvent event = new PostEvent(broadcastType,gPost);
				ServerEventBroadcaster.getInstance().broadcastEvent(event,getCommand().getConnectionId());
			}
			
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
	}
	
	private CommandResult formatBodyForPreview(PostCrudCommand cmd) {
		GPost gPost = new GPost();
		GEntry gEntry = new GEntry();
		gEntry.setBody(PostFormatter.getInstance().format(cmd.getBody()));
		gPost.setLatestEntry(gEntry);
		PostCommandResult result = new PostCommandResult(gPost);
		return result;
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
		if(StringUtil.notEmpty(cmd.getTitle()) && post.isRootPost()){
			Tag titleTag = post.getTitleTag();
			TagDao tagDao = new TagDao();
			tagDao.setTagId(titleTag.getTagId());
			tagDao.setValue(cmd.getTitle());
			tagDao.setType(Tag.TYPE_TOPIC);
			titleTag = tagDao.update();
			dao.setTitle(titleTag);
			
			//Tag titleTag = findOrCreateTag(cmd.getTitle(),Tag.TYPE_TOPIC);
			//Tag titleTag = loadOrCreateTitleTag(cmd);
			//dao.setTitle(titleTag);
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
		else if(post.getMetaMask() != null){
			dao.setMetaMask(post.getMetaMask());
		}
		
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
		incrementUserHitCount(creator);
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
		
		//if anon, check for login
		if(creator.isAnonymous()){
			creator = AccountDao.login(cmd.getLogin(), cmd.getPassword());
		}
		
		if(!creator.hasPrivilege(Privilege.POST) && !creator.isAdministrator())
			throw new RuntimeException("You dont have privledges to create new content.");
		
		Post parent = null;
		if(!StringUtil.empty(cmd.getParentId())){
			parent = PostDao.loadPost(cmd.getParentId());
			if(parent.isLocked() && !creator.isAdministrator()){
				throw new RuntimeException("Cant reply to a locked post.");
			}
		}
		
		if(!cmd.isMovie() && parent == null && StringUtils.isEmpty(cmd.getForumId())){
			throw new RuntimeException("Forum is required for new Topics.");
		}
		
		if(cmd.isMovie() && movieExists(cmd)){
			throw new RuntimeException("Movie: \""+ cmd.getTitle() + " ("+cmd.getYear()+")\" already exists.");
		}
		
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
		dao.setDescription(cmd.getTopicDescription());
		
		Tag titleTag = loadOrCreateTitleTag(cmd);
		dao.setTitle(titleTag);
		dao.setUrl(cmd.getUrl());
		if(StringUtil.notEmpty(cmd.getYear())){
			dao.setPublishYear(Integer.parseInt(cmd.getYear()));
		}
		
		Post post = dao.create();
		
		if(StringUtils.isNotEmpty(cmd.getForumId())){
			Tag tag = TagDao.loadTag(cmd.getForumId());
			createTagAssociation(creator,post.getParent(),tag);
		}
		
		//Mark as read for Coderhead
		maybeMarkSiteRead();
		
//		for (GTag gTag : cmd.getTags()){
//			Tag tag;
//			if(StringUtil.notEmpty(gTag.getTagId()))
//				tag = TagDao.loadTag(gTag.getTagId());
//			else
//				tag = findOrCreateTag(gTag.getValue(), Tag.TYPE_TOPIC);
//			createTagAssociation(creator,post,tag);
//		}
		
		

		return post;
	}

	private void maybeMarkSiteRead() {
//		InboxDao dao = new InboxDao(getPerson());
//		if(dao.calculateInboxSize() == 1){
//			dao.markSiteRead();
//		}
		
		InboxDao dao = new InboxDao(getPerson());
		if(dao.calculateInboxSize() == 1){
			PersonDao.markSiteRead(getPerson().getPersonId());
			ExecutorHelpers.broadcastMarkSiteRead(getPerson(), getCommand().getConnectionId());
		}
	}

	private boolean movieExists(PostCrudCommand cmd) {
		TagDao dao;
		dao = new TagDao();
		dao.setValue(cmd.getTitle());
		dao.setType(Tag.TYPE_TOPIC);
		dao.setDate(new Date());
		Tag tag = dao.load();
		
		if(tag != null){
			try{
				List<Post> list = PostDao.loadPostByTitleTag(tag.getTagId());
				//This is a list because a single title can be reused multiple times.
				for(Post exists : list){
					if(exists.isMovie() && exists.getPublishYear() == Integer.parseInt(cmd.getYear())){
						return true;
					}
				}
			}
			catch(Exception e){}
		}
		return false;
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
