package org.ttdc.gwt.server.command.executors;

import org.apache.commons.lang.StringUtils;
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
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

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
		
		AssociationPostTag ass = dao.create();
		//GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		GAssociationPostTag gAss = FastPostBeanConverter.convertAssociationPostTagWithPost(ass);
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.CREATE);
		result.setAssociationPostTag(gAss);
		result.setAssociationId(ass.getGuid());
		
		broadcastPostEvent(post, PostEventType.EDIT);
		broadcastTagAssociation(ass, TagEventType.NEW);
		
		Persistence.commit();
		return result;
	}

	private AssociationPostTagResult performRemoveAssociation(AssociationPostTagCommand command) {
		AssociationPostTagResult result;
		Persistence.beginSession();
		
		AssociationPostTag ass = AssociationPostTagDao.load(command.getAssociationId());
		Post post = ass.getPost();
		//GAssociationPostTag gAss = FastPostBeanConverter.convertAssociationPostTagWithPost(ass);
		
		AssociationPostTagDao.remove(command.getAssociationId());
		
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
		result.setAssociationId(ass.getGuid());
		//result.setAssociationPostTag(gAss);
		GPost gPost = FastPostBeanConverter.convertPost(post);
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
		return result;
	}

	private void broadcastTagAssociation(AssociationPostTag ass, TagEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		TagEvent tagEvent = new TagEvent(eventType,gAss);
		broadcaster.broadcastEvent(tagEvent, getCommand().getConnectionId());
	}
	
	private void broadcastPostEvent(Post post, PostEventType eventType) {
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		GPost gPost = FastPostBeanConverter.convertPost(post);
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
