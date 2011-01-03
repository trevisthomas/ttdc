package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;


public class FastTopicDao extends FilteredPostPaginatedDaoBase{
	private Post sourcePost;
	private InboxDao inboxDao = null;
	
	private final static String HQL_ThreadIdsByDate = "SELECT post.postId FROM Post post " +
	"WHERE post.parent.postId=:parentId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
	"ORDER BY post.threadReplyDate DESC";

	private final static String HQL_ThreadIdsByCreateDate = "SELECT post.postId FROM Post post " +
	"WHERE post.parent.postId=:parentId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
	"ORDER BY post.date DESC";

	
	public final static int THREAD_REPLY_MAX_RESULTS = 10; //TODO probably want to move this to being a user choice?
	
	public FastTopicDao() {
		setPageSize(THREAD_REPLY_MAX_RESULTS);
	}
	
	public PaginatedList<GPost> loadByCreateDate() {
		findPageForPost(HQL_ThreadIdsByCreateDate);
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("FastTopicDao.ConversationsByCreateDate", "FastTopicDao.ConversationCount", true);
		return results;
	}
	
	
	public PaginatedList<GPost> loadByReplyDate() {
		findPageForPost(HQL_ThreadIdsByDate);
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("FastTopicDao.ConversationsByReplyDate", "FastTopicDao.ConversationCount", true);
		return results;
	}

	private void findPageForPost(String query) {
		int page = 1;
		if(!sourcePost.isRootPost() && getCurrentPage() == -1){
			page = determineThreadPage(query);
		}
		else if(getCurrentPage() > 0){
			page = getCurrentPage();
		}
		
		setCurrentPage(page);
	}
	
	@SuppressWarnings("unchecked")
	protected PaginatedList<GPost> executeLoadQuery(String query, String countQuery, boolean grouped) {
		PaginatedList<GPost> results;
		List<String> ids;
		long count;
		long filterMask = buildFilterMask(getFilterFlags());
		if(getPageSize() > 0){
			count = (Long)session().getNamedQuery(countQuery)
				.setParameter("postId", sourcePost.getRoot().getPostId())
				.setParameter("filterMask", filterMask)
				.uniqueResult();
			
			
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setParameter("postId", sourcePost.getRoot().getPostId())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
		}
		else{
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", sourcePost.getRoot().getPostId())
				.setParameter("postId", filterMask)
				.list();
			
			count = ids.size();
		}
		
		FastGPostLoader loader = new FastGPostLoader(inboxDao);
		
		List<GPost> list;
		if(grouped){
			list = loader.fetchPostsForIdsGrouped(ids, filterMask);
		}
		else{
			list = loader.fetchPostsForIdsFlat(ids);
		}
		
		results = DaoUtils.createResults(this, list, count);
		
		return results;
	}

	private int determineThreadPage(String query) {
		int page = 1;
		String threadId = sourcePost.getThread().getPostId();
		@SuppressWarnings("unchecked")
		List<String> list = session().createQuery(query)
			.setParameter("parentId", sourcePost.getRoot().getPostId())
			.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.list();
		
		int ndx = list.indexOf(threadId);
		if(ndx >= 0){
			page = (ndx / getPageSize())+1;
		}
		return page;
	}
	
	
	public void setSourcePost(Post post){
		this.sourcePost = post;
	}
	
	public InboxDao getInboxDao() {
		return inboxDao;
	}

	public void setInboxDao(InboxDao inboxDao) {
		this.inboxDao = inboxDao;
	}

}
