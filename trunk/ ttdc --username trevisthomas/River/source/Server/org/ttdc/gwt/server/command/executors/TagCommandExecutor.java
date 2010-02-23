package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.rollback;

import java.util.List;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.results.TagCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Tag;

public class TagCommandExecutor extends CommandExecutor<TagCommandResult>{

	@Override
	protected CommandResult execute() {
		TagCommandResult results = null;	
		try{
			Persistence.beginSession();
			TagCommand cmd = (TagCommand)getCommand();
			TagActionType action = cmd.getAction();
			
			switch(action){
			case LOAD_CREATORS: 
				results = getCreatorTagList();
				break;
			}	
			Persistence.commit();
		}
		catch(RuntimeException e){
			rollback();
			throw(e);
		}
		
		return results;
	}

	private TagCommandResult getCreatorTagList() {
		TagDao dao = new TagDao();
		dao.setType(Tag.TYPE_CREATOR);
		List<Tag> list = dao.loadList();
		
		List<GTag> gList = FastPostBeanConverter.convertTags(list);
		TagCommandResult results = new TagCommandResult();
		results.setTagList(gList);
		return results;
	}

}
