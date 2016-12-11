package org.ttdc.gwt.server.command.executors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.FullTextSession;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.util.RatingUtility;

public class AssociationPostTagCommandExecutor extends CommandExecutor<AssociationPostTagResult>{
	@Override
	protected CommandResult execute() {
		AssociationPostTagResult result = new AssociationPostTagResult();
		AssociationPostTagCommand command = (AssociationPostTagCommand)getCommand();
		try{
			if(command.isRemove()){
				result = performRemoveAssociation(command);
			}
			else if(command.isCreate()){
				result = performCreateAssociation(command);
			}
			else{
				throw new RuntimeException("Command has no idea what to do");
			}
		}
		catch(Exception e){
			Persistence.rollback();
			throw new RuntimeException(e); 
		}
		
		return result;
	}

	private AssociationPostTagResult performCreateAssociation(AssociationPostTagCommand command) {
		AssociationPostTagResult result;
		AssociationPostTagDao dao = new AssociationPostTagDao();
		Persistence.beginSession();
		Post post = PostDao.loadPost(command.getPostId());
		GTag gTag = command.getTag();
		Tag tag;
		tag = getTagObject(gTag);
		
		Person creator = getPerson();
		
		dao.setTag(tag);
		dao.setPost(post);
		dao.setCreator(creator);
		
		if(post.hasTagAssociation(tag.getType(), getPerson().getPersonId())){
			throw new RuntimeException("Person already has the requested tag association.");
		}
		
		AssociationPostTag ass = dao.create();
		if(Tag.TYPE_RATING.equals(tag.getType())){
			RatingUtility.updateAverageRating(post);
		}
		if(Tag.TYPE_EARMARK.equals(tag.getType())){
			creator.setEarmarks(creator.getEarmarks() + 1);
			Persistence.session().save(creator);
			broadcastPersonEvent(creator, PersonEventType.USER_EARMKARK_COUNT_CHANGED);
		}
		// InboxDao inboxDao = new InboxDao(getPerson());
		//GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		GAssociationPostTag gAss = FastPostBeanConverter.convertAssociationPostTagWithPost(ass);
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.CREATE);
		result.setAssociationPostTag(gAss);
		result.setAssociationId(ass.getGuid());
		
		broadcastPostEvent(gAss.getPost(), PostEventType.EDIT);
		broadcastTagAssociation(gAss, TagEventType.NEW_TAG);
		
		Persistence.commit();
		
		reIndexTagsOnPost(post);
		
		
		return result;
	}

	//Hm, should i refactor and move this to the ass DAO?
	private void reIndexTagsOnPost(Post post){
		FullTextSession fullTextSession = Persistence.fullTextSession();
		
		Post localPost = PostDao.loadPost(post.getPostId());
		
		for (AssociationPostTag ass : localPost.getTagAssociations()) {
		    fullTextSession.index(ass.getTag());
		}
	}
	
	private AssociationPostTagResult performRemoveAssociation(AssociationPostTagCommand command) {
		AssociationPostTagResult result;
		Persistence.beginSession();
		
		AssociationPostTag ass = AssociationPostTagDao.load(command.getAssociationId());
		Post post = ass.getPost();
		
		boolean isRatingTagAss = ass.getTag().getType().equals(Tag.TYPE_RATING);

		GAssociationPostTag gAss = FastPostBeanConverter.convertAssociationPostTagWithPost(ass);
		
		AssociationPostTagDao.remove(command.getAssociationId());

		// Manually removing the removed tag from the dead tag association so that the post within it accurate before
		// broadcasting and returning.
		List<GAssociationPostTag> asses = gAss.getPost().getTagAssociations();
		asses.remove(gAss);

		if(isRatingTagAss){
			RatingUtility.updateAverageRating(post);
		}
		Person creator = getPerson();
		if(Tag.TYPE_EARMARK.equals(ass.getTag().getType())){
			creator.setEarmarks(creator.getEarmarks() - 1);
			Persistence.session().save(creator);
			broadcastPersonEvent(creator, PersonEventType.USER_EARMKARK_COUNT_CHANGED);
		}
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
		result.setAssociationId(ass.getGuid());
		result.setAssociationPostTag(gAss);
		
		// GPost gPost = FastPostBeanConverter.convertPost(post);
		result.setPost(gAss.getPost()); // Because i updated it manually after removing the association
		
		broadcastPostEvent(gAss.getPost(), PostEventType.EDIT);
		
//		AssociationPostTag ass = AssociationPostTagDao.remove(command.getAssociationId());
//		
//		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
//		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
//		result.setMessage(ass.getGuid());
//		result.setAssociationPostTag(gAss);
//		
		//Should probably instead be broadcasting a post update...
		broadcastTagAssociation(gAss, TagEventType.REMOVED_TAG);
		
		Persistence.commit();
		
		reIndexTag(ass.getTag());
		reIndexTagsOnPost(post);
		
		return result;
	}

	private void reIndexTag(Tag tag) {
		FullTextSession fullTextSession = Persistence.fullTextSession();
		Tag localTag = TagDao.loadTag(tag.getTagId());
		fullTextSession.index(localTag);
	}

	private void broadcastTagAssociation(GAssociationPostTag gAss, TagEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		TagEvent tagEvent = new TagEvent(eventType,gAss);
		broadcaster.broadcastEvent(tagEvent, getCommand().getConnectionId());
	}
	
	private void broadcastPostEvent(Post post, PostEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		InboxDao inboxDao = new InboxDao(getPerson());
		GPost gPost = FastPostBeanConverter.convertPost(post);
		PostEvent postEvent = new PostEvent(eventType,gPost);
		broadcaster.broadcastEvent(postEvent, getCommand().getConnectionId());
	}
	
	private void broadcastPostEvent(GPost gPost, PostEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		InboxDao inboxDao = new InboxDao(getPerson());
		PostEvent postEvent = new PostEvent(eventType, gPost);
		broadcaster.broadcastEvent(postEvent, getCommand().getConnectionId());
	}

	private void broadcastPersonEvent(Person person, PersonEventType eventType){
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		GPerson gPerson = FastPostBeanConverter.convertPerson(person);
		PersonEvent event = new PersonEvent(eventType, gPerson);
		broadcaster.broadcastEvent(event, getCommand().getConnectionId());
	}

	private Tag getTagObject(GTag gTag) {
		Tag tag;
		if(StringUtils.isNotBlank(gTag.getTagId())){
			tag = TagDao.loadTag(gTag.getTagId());
		}
		else{
			TagDao tagDao = new TagDao();
			tagDao.setValue(gTag.getValue());
			tagDao.setType(gTag.getType());
			tag = tagDao.createOrLoad();
		}
		return tag;
	}
}
