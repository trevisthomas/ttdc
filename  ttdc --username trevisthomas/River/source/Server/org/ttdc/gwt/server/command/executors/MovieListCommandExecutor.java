package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.MovieDao;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;

public class MovieListCommandExecutor extends CommandExecutor<SearchPostsCommandResult>{
	@Override
	protected CommandResult execute() {
		MovieListCommand cmd = (MovieListCommand) getCommand();
		try{
			Persistence.beginSession();
			MovieDao dao = new MovieDao();
			dao.setCurrentPage(cmd.getPageNumber());
			dao.setPersonId(cmd.getPersonId());
			dao.setSortDirection(cmd.getSortDirection());
			dao.setSortOrder(cmd.setSortBy());
			dao.setInvertFilter(cmd.isSpeedRate());
			dao.setPageSize(50);
			PaginatedList<Post> results = dao.load();
			PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
			return new SearchPostsCommandResult(gResults);
			
		}
		finally{
			Persistence.commit();
		}
	}
	
}
