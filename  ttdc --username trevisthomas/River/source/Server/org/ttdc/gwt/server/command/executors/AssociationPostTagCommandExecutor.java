package org.ttdc.gwt.server.command.executors;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.FullTextSession;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
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
		
		dao.setTag(tag);
		dao.setPost(post);
		dao.setCreator(getPerson());
		
		if(post.hasTagAssociation(tag.getType(), getPerson().getPersonId())){
			throw new RuntimeException("Person already has the requested tag association.");
		}
		
		AssociationPostTag ass = dao.create();
		if(Tag.TYPE_RATING.equals(tag.getType())){
			RatingUtility.updateAverageRating(post);
		}
		//GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		GAssociationPostTag gAss = FastPostBeanConverter.convertAssociationPostTagWithPost(ass);
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.CREATE);
		result.setAssociationPostTag(gAss);
		result.setAssociationId(ass.getGuid());
		
		broadcastPostEvent(post, PostEventType.EDIT);
		broadcastTagAssociation(ass, TagEventType.NEW);
		
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
		if(isRatingTagAss){
			RatingUtility.updateAverageRating(post);
		}
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
		result.setAssociationId(ass.getGuid());
		result.setAssociationPostTag(gAss);
		InboxDao inboxDao = new InboxDao(getPerson());
		GPost gPost = FastPostBeanConverter.convertPost(post, inboxDao);
		result.setPost(gPost);
		
		broadcastPostEvent(post, PostEventType.EDIT);
		
//		AssociationPostTag ass = AssociationPostTagDao.remove(command.getAssociationId());
//		
//		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
//		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
//		result.setMessage(ass.getGuid());
//		result.setAssociationPostTag(gAss);
//		
		//Should probably instead be broadcasting a post update...
		broadcastTagAssociation(ass, TagEventType.REMOVED);
		
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

	private void broadcastTagAssociation(AssociationPostTag ass, TagEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		TagEvent tagEvent = new TagEvent(eventType,gAss);
		broadcaster.broadcastEvent(tagEvent, getCommand().getConnectionId());
	}
	
	private void broadcastPostEvent(Post post, PostEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		InboxDao inboxDao = new InboxDao(getPerson());
		GPost gPost = FastPostBeanConverter.convertPost(post, inboxDao);
		PostEvent postEvent = new PostEvent(eventType,gPost);
		broadcaster.broadcastEvent(postEvent, getCommand().getConnectionId());
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
