package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class TopicDao extends FilteredPostPaginatedDaoBase{
	private String rootId;
	private String conversationId;
	
	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public PaginatedList<Post> loadStarters() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadFromRootQuery("TopicDao.Starters",
										   "TopicDao.StartersCount",
										   buildFilterMask(getFilterFlags()));
		return results;
	}
	
	public PaginatedList<Post> loadReplies() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadReplies(buildFilterMask(getFilterFlags()));
		return results;
	}
	
	public PaginatedList<Post> loadFlat() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		
		results = executeLoadFromRootQuery("TopicDao.Flat",
										   "TopicDao.FlatCount",
										    buildFilterMask(getFilterFlags()));
		
		return results;
	}
	
	//TODO: make this thing actually load summaries and not full posts.
	public PaginatedList<Post> loadHierarchy() {
		PaginatedList<Post> results = new PaginatedList<Post>();
			results = executeLoadFromRootQuery("TopicDao.Hierarchy",
											   "TopicDao.HierarchyCount",
											    buildFilterMask(getFilterFlags()));
		
		return results;
	}
	
	public PaginatedList<Post> loadHierarchyUnPaged() {
		setPageSize(-1);
		setCurrentPage(1);
		return loadHierarchy();
	}
	
	@SuppressWarnings("unchecked") 
	private PaginatedList<Post> executeLoadReplies(long filterMask) {
		
		List<Post> list;
		int count = session().getNamedQuery("TopicDao.Replies")
			.setParameter("filterMask", filterMask)
			.setString("postId", getConversationId()).list().size();
			
		list = session().getNamedQuery("TopicDao.Replies")
			.setParameter("filterMask", filterMask)
			.setString("postId", getConversationId())
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize()).list();
		
		PaginatedList<Post> results = DaoUtils.createResults(this, list, count);
		return results;
	}

	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadFromRootQuery(String query, String countQuery, long filterMask) {
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
				long count = (Long)session().getNamedQuery(countQuery)
				.setParameter("filterMask", filterMask)
				.setString("rootId", getRootId()).uniqueResult();
			
			
			list = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setString("rootId", getRootId())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setString("rootId", getRootId()).list();
			results = DaoUtils.createResults(this, list, list.size());
		}
		
		return results;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

}
