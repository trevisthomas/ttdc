package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.DateRange;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PostSearchDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

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
			List<String> notTagIds = command.getNotTagIdList();
			
			//TODO: Trevis... test that the tag filters are really working!
			notTagIds.addAll(getPerson().getFilteredTagIds());
			
			dao.setNotTagIdList(notTagIds);
			dao.setTagIdList(command.getTagIdList());
			dao.setRootId(command.getRootId());
			dao.setThreadId(command.getThreadId());
			
			if(command.getPageSize() > 0)
				dao.setPageSize(command.getPageSize());
			//Trevis, eventually you'll want to use the user chosen default when not over ridden by the specific command
			
			
			if(command.isNonReviewsOnly()){
				dao.addNotTagId(InitConstants.REVIEW_ID);
			} 
			else if(command.isReviewsOnly()){
				dao.addTagId(InitConstants.REVIEW_ID);
			}
				
			
			dao.setCurrentPage(command.getPageNumber());
			PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(dao.search());
			
			if(command.getTagIdList().size() > 0){
				translateTagIdListToSearchPhrase(command, gResults);
			}
			
//			dao.setSearchByTitle(!command.isTitleSearch());
//			PaginatedList<GPost> gSecondaryResults = PaginatedResultConverters.convertSearchResults(dao.search());
			
//			return new SearchPostsCommandResult(gResults,gSecondaryResults);
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
