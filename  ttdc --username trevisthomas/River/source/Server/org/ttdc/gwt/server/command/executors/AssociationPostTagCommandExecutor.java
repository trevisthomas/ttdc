package org.ttdc.gwt.server.command.executors;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
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
		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.CREATE);
		result.setAssociationPostTag(gAss);
		result.setMessage(ass.getGuid());
		
		broadcastTagAssociation(ass, TagEventType.NEW);
		Persistence.commit();
		return result;
	}

	private AssociationPostTagResult performRemoveAssociation(AssociationPostTagCommand command) {
		AssociationPostTagResult result;
		Persistence.beginSession();
		AssociationPostTag ass = AssociationPostTagDao.remove(command.getAssociationId());
		
		GAssociationPostTag gAss = GenericBeanConverter.convertAssociationPostTag(ass);
		result = new AssociationPostTagResult(AssociationPostTagResult.Status.REMOVE);
		result.setMessage(ass.getGuid());
		result.setAssociationPostTag(gAss);
		
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

	private Tag getTagObject(GTag gTag) {
		Tag tag;
		if(StringUtils.isNotBlank(gTag.getTagId())){
			tag = TagDao.loadTag(gTag.getTagId());
		}
		else{
			TagDao tagDao = new TagDao();
			tagDao.setValue(gTag.getValue());
			tagDao.setType(gTag.getType());
			tagDao.setCreator(getPerson());
			tag = tagDao.createOrLoad();
		}
		return tag;
	}
}
