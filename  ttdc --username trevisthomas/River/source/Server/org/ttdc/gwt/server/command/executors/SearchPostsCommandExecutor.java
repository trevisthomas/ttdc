package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.Set;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.DateRange;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostSearchDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.util.PostFlag;

public class SearchPostsCommandExecutor extends CommandExecutor<SearchPostsCommandResult>{
	@Override
	protected CommandResult execute() {
		try {
			SearchPostsCommand command = (SearchPostsCommand)getCommand();
			
			PostSearchDao dao = new PostSearchDao();
			
			beginSession();
			
			String phrase = command.getPhrase();
			
			dao.setPhrase(phrase);
			dao.setDateRange(new DateRange(command.getStartDate(),command.getEndDate()));
			dao.setSearchByTitle(command.isTitleSearch());
			dao.setPostSearchType(command.getPostSearchType());
			
			Set<PostFlag> filterList = ExecutorHelpers.createFlagFilterListForPerson(getPerson());
			dao.setCreator(dao.getCreator());
			dao.setNotTagIdList(command.getNotTagIdList());
			dao.setTagIdList(command.getTagIdList());
			dao.setRootId(command.getRootId());
			dao.setThreadId(command.getThreadId());
			if(command.getPersonId() != null)
				dao.setCreator(PersonDao.loadPerson(command.getPersonId()));
			
			if(command.getPageSize() > 0)
				dao.setPageSize(command.getPageSize());
			//Trevis, eventually you'll want to use the user chosen default when not over ridden by the specific command
			
			if(command.isNonReviewsOnly()){
				filterList = ExecutorHelpers.createFlagFilterListForPerson(getPerson());
				filterList.add(PostFlag.REVIEW);
			} 
			else if(command.isReviewsOnly()){
				filterList.clear();//We're going to invert the filter so, better clear it out first. (NWS and INF maybe in there!!)
				filterList.add(PostFlag.REVIEW);
				dao.setInvertFilterFuction(true);
			}
				
			dao.setCurrentPage(command.getPageNumber());
			dao.setFilterFlags(filterList);
			PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(dao.search());
			
			if(command.getTagIdList().size() > 0){
				translateTagIdListToSearchPhrase(command, gResults);
			}
			
			return new SearchPostsCommandResult(gResults);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		finally{
			commit();
		}
	}

	private void translateTagIdListToSearchPhrase(SearchPostsCommand command, PaginatedList<GPost> gResults) {
		StringBuilder sb = new StringBuilder();
		sb.append("Tagged");
		sb.append(" ");
		StringBuilder tagValues = new StringBuilder();
		for(String tagId : command.getTagIdList()){
			Tag tag = TagDao.loadTag(tagId);
			if(tagValues.length() > 0)
				tagValues.append(",");
			tagValues.append("\'");
			tagValues.append(tag.getValue());
			tagValues.append("\'");
		}
		sb.append(tagValues);
		gResults.setPhrase(sb.toString());
	}
}
