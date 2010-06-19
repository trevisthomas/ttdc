package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class EarmarkedPostDao extends PaginatedDaoBase{
	private String personId;
	private String tagId;
	public PaginatedList<Post> loadEarmarkedPosts(){
		String query = "SELECT ass.post FROM AssociationPostTag ass WHERE ass.creator.personId=:personId AND ass.tag.tagId=:tagId ORDER BY ass.date DESC";
		String queryCount = "SELECT count(ass.post.postId) FROM AssociationPostTag ass WHERE ass.creator.personId=:personId AND ass.tag.tagId=:tagId";
		PaginatedList<Post> results = executeQuery(query,queryCount,personId,tagId);
		return results;
	}
	
	private PaginatedList<Post> executeQuery(String query, String countQuery, String personId, String tagId) {
		PaginatedList<Post> results = new PaginatedList<Post>();
		
		long count = (Long)session().createQuery(countQuery)
			.setParameter("personId", personId)
			.setParameter("tagId", tagId).uniqueResult();
			
		@SuppressWarnings("unchecked")
		List<Post> list = session().createQuery(query)
			.setParameter("personId", personId)
			.setParameter("tagId", tagId)
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize()).list();
		
		results = DaoUtils.createResults(this, list, count);
		
		return results;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	
	
}
