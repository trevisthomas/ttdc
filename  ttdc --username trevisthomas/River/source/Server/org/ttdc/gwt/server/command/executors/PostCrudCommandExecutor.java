package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.server.util.CalendarBuilder;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;

public class PostCrudCommandExecutor extends CommandExecutor<PostCommandResult>{
	
	@Override
	protected CommandResult execute() {
		PostCrudCommand cmd = (PostCrudCommand)getCommand();
		Post post = null;
		try{
			beginSession();
			switch(cmd.getAction()){
			case DELETE:
				break;
			case UPDATE:
				break;
			case CREATE:
				post = create(cmd);
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
				GPost gPost = FastPostBeanConverter.convertPost(post); 
				result = new PostCommandResult(gPost);
			}
			
			commit();
			
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
	}
	
	
	protected Post reparent(PostCrudCommand cmd) {
		//TODO secure for admin only
		Post target = PostDao.loadPost(cmd.getPostId());
		Post oldParent = target.getParent();
		Post newParent = PostDao.loadPost(cmd.getParentId());
		Post post = PostDao.reParent(newParent, target);
		
		if(!newParent.getRoot().equals(oldParent.getRoot())){
			//Fix title tag
			AssociationPostTag ass = post.loadTitleTagAssociation();
			AssociationPostTagDao.reTag(ass.getGuid(), newParent.loadTitleTagAssociation().getTag());
			
			//Fix the titles of children
			@SuppressWarnings("unchecked")
			List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId ORDER BY path")
				.setString("threadId", post.getThread().getPostId())
				.list();
			
			for(Post p : posts){
				ass = p.loadTitleTagAssociation();
				AssociationPostTagDao.reTag(ass.getGuid(), newParent.loadTitleTagAssociation().getTag());
			}
		}
		
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
		Person creator = PersonDao.loadPerson(getPerson().getPersonId());
		
		if(!creator.hasPrivilege(Privilege.POST) || creator.isAdministrator())
			throw new RuntimeException("You dont have privledges to create new content.");
		
		Post parent = null;
		if(!StringUtils.isEmpty(cmd.getParentId()))
			parent = PostDao.loadPost(cmd.getParentId());
		
		PostDao dao = new PostDao();
		dao.setParent(parent);
		dao.setBody(cmd.getBody());
		dao.setTitle(cmd.getTitle());
		Post post = dao.create();
		
		Tag creatorTag = loadOrCreateCreatorTag(creator);
		createTagAssociationForPost(creator, post, creatorTag);
		
		Tag titleTag = loadOrCreateTitleTag(cmd, creator);
		createTitleTagAssociationForPost(creator, post, titleTag);
		
		tagCalenderInfo(post,creator);
		
		return post;
	}

	/* Apply calender info */
	private void tagCalenderInfo(Post post, Person creator){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(post.getDate());
		Date date = post.getDate();
		
		String value = ""+cal.get(GregorianCalendar.DAY_OF_MONTH);
		String type = Tag.TYPE_DATE_DAY;
		Tag tag = findOrCreateTag(creator, value, type, date);
		createTagAssociationForPost(creator, post, tag);
		
		value = CalendarBuilder.getMonthName(cal.get(GregorianCalendar.MONTH)+1);
		type = Tag.TYPE_DATE_MONTH;
		tag = findOrCreateTag(creator, value, type, date);
		createTagAssociationForPost(creator, post, tag);
		
		value = ""+cal.get(GregorianCalendar.YEAR);
		type = Tag.TYPE_DATE_YEAR;
		tag = findOrCreateTag(creator, value, type, date);
		createTagAssociationForPost(creator, post, tag);
		
		value = ""+cal.get(GregorianCalendar.WEEK_OF_YEAR);
		type = Tag.TYPE_WEEK_OF_YEAR;
		tag = findOrCreateTag(creator, value, type, date);
		createTagAssociationForPost(creator, post, tag);
		
	}


	private Tag findOrCreateTag(Person creator, String value, String type, Date date) {
		TagDao dao;
		dao = new TagDao();
		dao.setValue(value);
		dao.setType(type);
		dao.setCreator(creator);
		dao.setDate(date);
		Tag tag = dao.createOrLoad();
		return tag;
	}

	private Tag loadOrCreateCreatorTag(Person creator) {
		TagDao tagDao = new TagDao();
		tagDao.setCreator(creator);
		tagDao.setType(Tag.TYPE_CREATOR);
		Tag creatorTag = tagDao.createOrLoad();
		return creatorTag;
	}

	private void createTitleTagAssociationForPost(Person creator, Post post, Tag tag) {
		createTagAssociationImplementation(creator, post, tag, true);
	}
	private void createTagAssociationForPost(Person creator, Post post, Tag tag) {
		createTagAssociationImplementation(creator, post, tag, false);
	}
	private void createTagAssociationImplementation(Person creator, Post post, Tag tag, boolean isTitle) {
		AssociationPostTagDao assDao = new AssociationPostTagDao();
		assDao.setPost(post);
		assDao.setTag(tag);
		assDao.setCreator(creator);
		assDao.setTitle(isTitle);
		assDao.create();
		
		Persistence.session().flush();
		Persistence.session().refresh(post);
	}


	private Tag loadOrCreateTitleTag(PostCrudCommand cmd, Person creator) {
		Tag tag;
		if(StringUtils.isNotBlank(cmd.getParentId())){
			Post parent = PostDao.loadPost(cmd.getParentId());
			AssociationPostTag ass = parent.loadTitleTagAssociation();
			//This should never be null... if it is, then this parent post is corrupt
			if(ass == null){
				throw new RuntimeException("Parent post doesnt have a title tag, post: "+parent.getPostId()+" is corrupt!");
			}
			tag = ass.getTag();
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
				dao.setCreator(creator);
				tag = dao.createOrLoad();
			}
		}
		
		return tag;
	}
	
}
