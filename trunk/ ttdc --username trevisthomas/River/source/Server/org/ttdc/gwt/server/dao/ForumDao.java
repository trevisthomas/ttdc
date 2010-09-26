package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Forum;
import org.ttdc.persistence.objects.Post;

public class ForumDao extends FilteredPostPaginatedDaoBase{
	private String forumId;
	
	public List<Forum> loadForums() {
		
		@SuppressWarnings("unchecked")
		List<Forum> list = session().getNamedQuery("ForumDao.fetchForumListByMass").list();
		
		
		return list;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public PaginatedList<Post> loadTopics() {
		PaginatedList<Post> posts = executeLoadQuery("ForumDao.topicsList");
		return posts;
	}
	
	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadQuery(String query) {
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
			long count = (Long)session().getNamedQuery(query+"Count")
				.setParameter("forumId", getForumId())
				.uniqueResult();
			
			
			list = session().getNamedQuery(query)
				.setParameter("forumId", getForumId())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameter("forumId", getForumId())
				.setFirstResult(calculatePageStartIndex())
				.list();
			
			results = DaoUtils.createResults(this, list, list.size());
		}
		
		return results;
	}
}
