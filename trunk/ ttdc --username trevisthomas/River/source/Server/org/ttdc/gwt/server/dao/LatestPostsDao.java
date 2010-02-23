package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class LatestPostsDao extends PaginatedDaoBase{
	
	private static final int DEFAULT_REPLY_MAX_RESULTS = 5;
	private int replyMaxResults = DEFAULT_REPLY_MAX_RESULTS;
	
	private List<String> filteredTagIdList = new ArrayList<String>();
	
	public PaginatedList<Post> loadConversations(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadQuery("LatestPostsDao.Conversations");
		}
		else{
			results = executeLoadQueryNoFilter("LatestPostsDao.ConversationsNoFilter");
		}
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	public PaginatedList<Post> loadFlat(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadQuery("LatestPostsDao.Flat");
		}
		else{
			results = executeLoadQueryNoFilter("LatestPostsDao.FlatNoFilter");
		}
//		List<Post> posts = loadAllPostsForThreads(results.getList());
//		for(Post p : results.getList()){
//			loadRepliesFromPostList(p, posts);
//		}
		return results;
	}
	
	public PaginatedList<Post> loadThreads(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadQuery("LatestPostsDao.Threads");
		}
		else{
			results = executeLoadQueryNoFilter("LatestPostsDao.ThreadsNoFilter");
		}
//		List<Post> posts = loadAllPostsForThreads(results.getList());
//		for(Post p : results.getList()){
//			loadRepliesFromPostList(p, posts);
//		}
		return results;
	}
	
	public PaginatedList<Post> loadNested() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		if(filteredTagIdList.size() > 0){
			results = executeLoadQuery("LatestPostsDao.Nested");
		}
		else{
			results = executeLoadQueryNoFilter("LatestPostsDao.NestedNoFilter");
		}
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}

	private void loadRepliesFromPostList(Post thread, List<Post> posts) {
		List<Post> flatReplyHierarchy = new ArrayList<Post>();
		for(Post p : posts){
			if(!p.isThreadPost() && p.getThread().getPostId().equals(thread.getPostId()))
				flatReplyHierarchy.add(p);
		}	
		if(flatReplyHierarchy.size() > replyMaxResults){
			thread.setPosts(flatReplyHierarchy.subList(flatReplyHierarchy.size()-replyMaxResults, flatReplyHierarchy.size()));
		}
		else
			thread.setPosts(flatReplyHierarchy);
		
	}

	private List<Post> loadAllPostsForThreads(List<Post> threadStarters){
		List<String> postIds = new ArrayList<String>();
		for(Post post : threadStarters){
			postIds.add(post.getPostId());
		}
		List<Post> posts;
		if(filteredTagIdList.size() > 0){
			posts = DaoUtils.executeLoadFromPostIds("LatestPostsDao.RepliesInThreads", postIds, filteredTagIdList);
		}
		else{
			posts = DaoUtils.executeLoadFromPostIdsNoFilter("LatestPostsDao.RepliesInThreadsNoFilter", postIds);
		}
		return posts;
	}
	
	@SuppressWarnings("unchecked") 
	private PaginatedList<Post> executeLoadQueryNoFilter(String query) {
		List<Post> list;
		PaginatedList<Post> results = null;
		if(getPageSize() > 0){
			int count = session().getNamedQuery(query+"Count")
				.list().size();
			
			list = session().getNamedQuery(query)
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			list = session().getNamedQuery(query).list();
			
			results = DaoUtils.createResults(this, list, list.size());
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadQuery(String query) {
		if(filteredTagIdList.size() == 0) throw new RuntimeException("You dont have a filter setup. ");
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
			int count = session().getNamedQuery(query+"Count")
				.setParameterList("tagIds", filteredTagIdList)
				.list().size();
			
			
			list = session().getNamedQuery(query)
				.setParameterList("tagIds", filteredTagIdList)
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameterList("tagIds", filteredTagIdList)
				.list();
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
	
	public int getReplyMaxResults() {
		return replyMaxResults;
	}

	public void setReplyMaxResults(int replyMaxResults) {
		this.replyMaxResults = replyMaxResults;
	}

}
