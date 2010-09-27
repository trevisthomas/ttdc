package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.ForumDao;
import org.ttdc.gwt.shared.commands.ForumTopicListCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;

public class ForumTopicListCommandExecutor extends CommandExecutor<PaginatedListCommandResult<GPost>>{
	
	@Override
	protected CommandResult execute() {
		ForumTopicListCommand cmd = (ForumTopicListCommand)getCommand();
		PaginatedListCommandResult<GPost> result;
		
		try{
			Persistence.beginSession();
			switch (cmd.getAction()) {
			case LOAD_TOPIC_PAGE:
				result = loadTopicPage(cmd);	
				break;
			default:
				throw new RuntimeException("ForumTopicListCommandExecutor doesnt understand that action type");
			}
			Persistence.commit();
		}
		catch (RuntimeException e) {
			Persistence.rollback();
			throw e;
		}
		return result;
	}
	
	private PaginatedListCommandResult<GPost> loadTopicPage(ForumTopicListCommand cmd) {
		ForumDao dao = new ForumDao();
		dao.setForumId(cmd.getForumId());
		dao.setCurrentPage(cmd.getCurrentPage());
		PaginatedList<Post> results = dao.loadTopics();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
}
