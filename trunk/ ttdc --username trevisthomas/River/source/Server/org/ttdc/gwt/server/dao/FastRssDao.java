package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class FastRssDao  extends FilteredPostPaginatedDaoBase{
		private Post sourcePost;
		private InboxDao inboxDao = null;
		
		public final static int MAX_RESULTS = 20; //TODO probably want to move this to being a user choice?
		
		public FastRssDao() {
			setPageSize(20);
		}
		
		public PaginatedList<GPost> loadTopicFlat() {
			PaginatedList<GPost> results = new PaginatedList<GPost>();
			results = executeLoadQuery("FastRssDao.TopicByCreateDate", "FastRssDao.TopicByCreateDateCount");
			return results;
		}
	
		@SuppressWarnings("unchecked")
		protected PaginatedList<GPost> executeLoadQuery(String query, String countQuery) {
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
			
			List<GPost> list = new ArrayList<GPost>();
			if(count > 0){
				FastGPostLoader loader = new FastGPostLoader(inboxDao);
			
				list = loader.fetchPostsForIdsFlat(ids);
				
			}
			
			results = DaoUtils.createResults(this, list, count);
			
			return results;
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
