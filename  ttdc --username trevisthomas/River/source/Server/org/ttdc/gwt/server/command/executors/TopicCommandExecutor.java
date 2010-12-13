package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.ThreadDao;
import org.ttdc.gwt.server.dao.TopicDao;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class TopicCommandExecutor  extends CommandExecutor<TopicCommandResult>{
	private final static Logger log = Logger.getLogger(TopicCommandExecutor.class);
	
	@Override
	protected CommandResult execute() {
		try{
			TopicCommandResult result = new TopicCommandResult();
			TopicCommand command = (TopicCommand)getCommand();
			result.setType(command.getType());
			beginSession();
			TopicCommandType type = command.getType();
			PaginatedList<Post> results = null;
			PaginatedList<GPost> gResults = null;
			
			if(type == null) throw new RuntimeException("Type is required. Otherwise how will I know what to do?");
			
			TopicDao dao = new TopicDao();
			Post post = PostDao.loadPost(command.getPostId());
			dao.setRootId(post.getRoot().getPostId());
			dao.setCurrentPage(command.getPageNumber());
			dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(getPerson()));
			
			if(TopicCommandType.FLAT.equals(type)){
				results = dao.loadFlat();
				gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
			}
			else if(TopicCommandType.HIERARCHY_UNPAGED_SUMMARY.equals(type)){
				// This version does not page, it just grabs every post in the thread.
				results = dao.loadHierarchyUnPaged();
				gResults = PaginatedResultConverters.convertSearchResultsHierarchy(results);
			}
			else if(TopicCommandType.HIERARCHY.equals(type)){
				results = dao.loadHierarchy();
				gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
			}
			else if(TopicCommandType.NESTED_THREAD_SUMMARY.equals(type)){
				results = buildNestedThreadResults(command,post);
				gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
			}
			else if(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE.equals(type)){
				results = buildNestedThreadMoreResults(command,post);
				gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
				for(GPost gp : gResults.getList()){
					gp.setSuggestSummary(true);
				}
				//Collections.reverse(gResults.getList());
			}
//			else if(TopicCommandType.REPLIES.equals(type)){
//				dao.setConversationId(command.getConversationId());
//				results = dao.loadReplies();
//				gResults = PaginatedResultConverters.convertSearchResults(results);
//			}
//			else if(TopicCommandType.STARTERS.equals(type)){
//				results = dao.loadStarters();
//				gResults = PaginatedResultConverters.convertSearchResults(results);
//			}
			else if(TopicCommandType.CONVERSATION.equals(type)){
				results = buildConversationResults(dao,post);
				gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
			}
			else{
				throw new RuntimeException("Type has no implementation. Otherwise how will I know what to do?");
			}
			
			result.setResults(gResults);
			commit();
			return result;
		}
		catch(RuntimeException e){
			rollback();
			log.error(e);
			throw(e);
		}
	}
	
	private PaginatedList<Post> buildNestedThreadResults(TopicCommand command, Post post) {
		ThreadDao dao = new ThreadDao();
		dao.setCurrentPage(command.getPageNumber());
		dao.setRootId(post.getRoot().getPostId());
		dao.setSourcePost(post);
		
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(getPerson()));
		
		PaginatedList<Post> results;
		if(command.isSortByDate())
			results = dao.loadByCreateDate();
		else
			results = dao.loadByReplyDate();
			
			
			//results = dao.loadThreadSummmary();
		
		return results;
	}
	
	private PaginatedList<Post> buildNestedThreadMoreResults(TopicCommand command, Post post) {
		ThreadDao dao = new ThreadDao();
		dao.setCurrentPage(command.getPageNumber());
		dao.setThreadId(post.getPostId());
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(getPerson()));
		
		PaginatedList<Post> results;
		results = dao.loadThreadSummmary();
		
		return results;
	}

	private PaginatedList<Post> buildConversationResults(TopicDao dao, Post post) {
		PaginatedList<Post> results;
		if(post.isRootPost()){
			dao.setRootId(post.getPostId());
			results = dao.loadStarters();
		}
		else if(post.isPostThreadRoot()){
			dao.setConversationId(post.getPostId());
			results = dao.loadReplies();
		}
		else{
			dao.setConversationId(post.getThread().getPostId());
			//TODO find post page number!!
			results = dao.loadReplies();
		}
		return results;
	}

}
