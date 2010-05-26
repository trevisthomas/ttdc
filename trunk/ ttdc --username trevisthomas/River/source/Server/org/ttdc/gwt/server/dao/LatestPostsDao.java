package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class LatestPostsDao extends FilteredPostPaginatedDaoBase{
	
	private static final int DEFAULT_REPLY_MAX_RESULTS = 5;
	private int replyMaxResults = DEFAULT_REPLY_MAX_RESULTS;
	
	
	public PaginatedList<Post> loadConversations(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadQuery("LatestPostsDao.Conversations");
		
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	public PaginatedList<Post> loadFlat(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadQuery("LatestPostsDao.Flat");
		return results;
	}
	
	public PaginatedList<Post> loadThreads(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadQuery("LatestPostsDao.Threads");
		return results;
	}
	
	public PaginatedList<Post> loadNested() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadQuery("LatestPostsDao.Nested");
		
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
		if(threadStarters.size() == 0){
			System.err.println("Smelly badness");
		}
		posts = DaoUtils.executeLoadFromPostIds("LatestPostsDao.RepliesInThreads", postIds, buildFilterMask(getFilterFlags()));
		
		return posts;
	}
	
	
	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadQuery(String query) {
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
			long count = (Long)session().getNamedQuery(query+"Count")
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.uniqueResult();
			
			
			list = session().getNamedQuery(query)
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.list();
			results = DaoUtils.createResults(this, list, list.size());
		}
		
		return results;
	}
	
	
	public int getReplyMaxResults() {
		return replyMaxResults;
	}

	public void setReplyMaxResults(int replyMaxResults) {
		this.replyMaxResults = replyMaxResults;
	}

}
