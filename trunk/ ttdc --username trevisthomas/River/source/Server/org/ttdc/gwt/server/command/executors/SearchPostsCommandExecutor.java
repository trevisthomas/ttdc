package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.DateRange;
import org.ttdc.gwt.server.dao.FastGPostLoader;
import org.ttdc.gwt.server.dao.FastPostSearchDao;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.PostSearchDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.server.dao.UserObjectDao;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class SearchPostsCommandExecutor extends CommandExecutor<SearchPostsCommandResult>{
	@Override
	protected CommandResult execute() {
		try {
			SearchPostsCommand command = (SearchPostsCommand)getCommand();
			
			FastPostSearchDao dao = new FastPostSearchDao();
			
			beginSession();
			PostSearchType type = command.getPostSearchType();
			if(type.equals(PostSearchType.FILTERED_BY_USER)){
				return performFilteredThreadsLookup();
			}
			else{
				String phrase = command.getPhrase();
				
				dao.setPhrase(phrase);
				dao.setDateRange(new DateRange(command.getStartDate(),command.getEndDate()));
				dao.setSearchByTitle(command.isTitleSearch());
				dao.setPostSearchType(type);
				
				Set<PostFlag> filterList = ExecutorHelpers.createFlagFilterListForPerson(getPerson());
				//dao.setCreator(dao.getCreator());
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
				
				dao.setSortDirection(command.getSortDirection());
				dao.setSortBy(command.getSortOrder());
					
				dao.setCurrentPage(command.getPageNumber());
				dao.setFilterFlags(filterList);
				//PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(dao.search(), getPerson());
//				FastGPostLoader loader = new FastGPostLoader();
//				PaginatedList<GPost> gResults = loader.fetchPostsForPosts(dao.search());
				
				InboxDao inboxDao = new InboxDao(getPerson());
				dao.setInboxDao(inboxDao);
				
				PaginatedList<GPost> gResults = dao.search();
				
				if(command.getTagIdList().size() > 0){
					translateTagIdListToSearchPhrase(command, gResults);
				}
				
				return new SearchPostsCommandResult(gResults);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		finally{
			commit();
		}
	}


	/**
	 * Creates a fake paginated list for filtered posts.  Fast and dirty, pagination on this wont really work
	 * Also, it's only in this class because i needed an executor that returned a list of posts. 
	 * 
	 */
	private CommandResult performFilteredThreadsLookup() {
		Person person = getPerson();
		if(person.isAnonymous()){
			throw new RuntimeException("Anonymous users have no thread filteres.");
		}
		
		List<Post> list = new ArrayList<Post>();
		List<String> threadIds = UserObjectDao.loadFilteredThreadIds(person.getPersonId());
		
		int count = 0;
		for(String threadId : threadIds){
			if(count == InitConstants.MAX_FILTERED_THREADS_IN_DASHBOARD) 
				break;
			list.add(PostDao.loadPost(threadId));
		}
		
		PaginatedList<Post> paginatedPostList = new PaginatedList<Post>();
		paginatedPostList.setCurrentPage(1);
		paginatedPostList.setList(list);
		paginatedPostList.setPageSize(InitConstants.MAX_FILTERED_THREADS_IN_DASHBOARD);
		paginatedPostList.setTotalResults(threadIds.size());
		paginatedPostList.setPhrase("Filtered Threads");				
		
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(paginatedPostList, getPerson());
		return new SearchPostsCommandResult(gResults);
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
