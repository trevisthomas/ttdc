package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;


public class FastLatestPostsDao extends FilteredPostPaginatedDaoBase{
	private int MAX_CONVERSATIONS = 10;
	private int MAX_FLAT = 4 * MAX_CONVERSATIONS;
	private InboxDao inboxDao = null;
	
	
	public InboxDao getInboxDao() {
		return inboxDao;
	}


	public void setInboxDao(InboxDao inboxDao) {
		this.inboxDao = inboxDao;
	}


	public PaginatedList<GPost> loadFlat(){
		if (getPageSize() < 1) {
			setPageSize(MAX_FLAT);
		}
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("LatestPostsDaoFast.Flat", false);
		return results;
	}
	
	public PaginatedList<GPost> loadGrouped(){
		if (getPageSize() < 1) {
			setPageSize(MAX_CONVERSATIONS);
		}
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("LatestPostsDaoFast.Grouped", true);
		return results;
	}
	
	protected PaginatedList<GPost> executeLoadQuery(String query,  boolean grouped) {
		return executeLoadQuery(query, query+"Count", grouped);
	}
	
	@SuppressWarnings("unchecked")
	protected PaginatedList<GPost> executeLoadQuery(String query, String countQuery, boolean grouped) {
		PaginatedList<GPost> results;
		List<String> ids;
		long count;
		long filterMask = buildFilterMask(getFilterFlags());
		if(getPageSize() > 0){
			count = (Long)session().getNamedQuery(countQuery)
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
				.uniqueResult();
			
			
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
		}
		else{
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
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

	
}
