package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class TopicDao extends PaginatedDaoBase{
	private String rootId;
	private String conversationId;
	
	private List<String> filteredTagIdList = new ArrayList<String>();

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public PaginatedList<Post> loadStarters() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadFromRootQuery("TopicDao.Starters");
		}
		else{
			results = executeLoadFromRootQueryNoFilter("TopicDao.StartersNoFilter");
		}
		return results;
	}
	
	public PaginatedList<Post> loadReplies() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadReplies();
		}
		else{
			results = executeLoadRepliesNoFilters();
		}
		return results;
	}
	
	public PaginatedList<Post> loadFlat() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadFromRootQuery("TopicDao.Flat");
		}
		else{
			results = executeLoadFromRootQueryNoFilter("TopicDao.FlatNoFilter");
		}
		return results;
	}
	
	//TODO: make this thing actually load summaries and not full posts.
	public PaginatedList<Post> loadHierarchy() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadFromRootQuery("TopicDao.Hierarchy");
		}
		else{
			results = executeLoadFromRootQueryNoFilter("TopicDao.HierarchyNoFilter");
		}
		return results;
	}
	
	public PaginatedList<Post> loadHierarchyUnPaged() {
		setPageSize(-1);
		setCurrentPage(1);
		return loadHierarchy();
	}
	
	@SuppressWarnings("unchecked") 
	private PaginatedList<Post> executeLoadRepliesNoFilters() {
		List<Post> list;
		int count = session().getNamedQuery("TopicDao.RepliesNoFilter")
			.setString("postId", getConversationId()).list().size();
		
		list = session().getNamedQuery("TopicDao.RepliesNoFilter")
			.setString("postId", getConversationId())
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize()).list();
		
		PaginatedList<Post> results = DaoUtils.createResults(this, list, count);
		
		return results;
	}

	@SuppressWarnings("unchecked") 
	private PaginatedList<Post> executeLoadReplies() {
		if(filteredTagIdList.size() == 0) throw new RuntimeException("You dont have a filter setup. ");
		
		List<Post> list;
		int count = session().getNamedQuery("TopicDao.Replies")
			.setParameterList("tagIds", filteredTagIdList)
			.setString("postId", getConversationId()).list().size();
			
		list = session().getNamedQuery("TopicDao.Replies")
			.setParameterList("tagIds", filteredTagIdList)
			.setString("postId", getConversationId())
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize()).list();
		
		PaginatedList<Post> results = DaoUtils.createResults(this, list, count);
		return results;
	}

	@SuppressWarnings("unchecked") 
	private PaginatedList<Post> executeLoadFromRootQueryNoFilter(String query) {
		
		List<Post> list;
		PaginatedList<Post> results = null;
		if(getPageSize() > 0){
			int count = session().getNamedQuery(query)
				.setString("rootId", getRootId()).list().size();
			
			list = session().getNamedQuery(query)
				.setString("rootId", getRootId())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			list = session().getNamedQuery(query)
				.setString("rootId", getRootId()).list();
			
			results = DaoUtils.createResults(this, list, list.size());
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadFromRootQuery(String query) {
		if(filteredTagIdList.size() == 0) throw new RuntimeException("You dont have a filter setup. ");
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
			int count = session().getNamedQuery(query)
				.setParameterList("tagIds", filteredTagIdList)
				.setString("rootId", getRootId()).list().size();
			
			
			list = session().getNamedQuery(query)
				.setParameterList("tagIds", filteredTagIdList)
				.setString("rootId", getRootId())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameterList("tagIds", filteredTagIdList)
				.setString("rootId", getRootId()).list();
			results = DaoUtils.createResults(this, list, list.size());
		}
		
		return results;
	}
	
	

	public List<String> getFilteredTagIdList() {
		return filteredTagIdList;
	}

	public void setFilteredTagIdList(List<String> filteredTagIdList) {
		this.filteredTagIdList = filteredTagIdList;
	}
	
	public void addFilterTagId(String filterTagId){
		this.filteredTagIdList.add(filterTagId);
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

}
