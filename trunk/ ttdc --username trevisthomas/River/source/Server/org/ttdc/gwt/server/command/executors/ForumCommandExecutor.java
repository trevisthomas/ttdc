package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.rollback;

import java.util.List;

import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.ForumDao;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Forum;

public class ForumCommandExecutor extends CommandExecutor<GenericListCommandResult<GForum>>{

	@Override
	protected CommandResult execute() {
		GenericListCommandResult<GForum> results = null;	
		try{
			Persistence.beginSession();
			ForumCommand cmd = (ForumCommand)getCommand();
			ForumActionType action = cmd.getAction();
			
			switch(action){
			case LOAD_FORUMS: 
				results = getForumList();
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

	private GenericListCommandResult<GForum> getForumList() {
		ForumDao dao = new ForumDao();
		
		List<Forum> list = dao.loadForums();
		
		List<GForum> gList = FastPostBeanConverter.convertForums(list);
		
		GenericListCommandResult<GForum> results = new GenericListCommandResult<GForum>(gList);
		return results;
	}

}
