package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

@Deprecated
public class LatestPostsFlatDao {
	private final static Logger log = Logger.getLogger(LatestPostsFlatDao.class);
	private List<String> filteredTagIdList = new ArrayList<String>();
	private int pageSize;
	
	private StopWatch stopwatch = new StopWatch();
	
	public LatestPostsFlatDao(){}
	
	public List<Post> load(){
		log.debug("load() started.");
		stopwatch.start();
		try{
			List<Post> list;
			if(filteredTagIdList.size() > 0){
				list = executeFlatQuery();
			}
			else{
				list = executeFlatQueryNoFilters();
			}
			return list;
		}
		finally{
			log.debug("load() completed in: " +stopwatch);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Post> executeFlatQueryNoFilters() {
		List<Post> flat;
		flat = session().getNamedQuery("LatestPostsFlatDao.FlatNoFilters")
			.setFirstResult(0)
			.setMaxResults(pageSize).list();
		return flat;
	}

//	public void preFetchFlat(List<Post> posts){
//		List<String> postIds = PostHelper.extractIds(posts);
//		List<AssociationPostTag> asses;
//		session().createQuery("SELECT ass, p, t FROM AssociationPostTag ass INNER JOIN FETCH ass.post as p " +
//		" JOIN FETCH ass.tag as t WHERE ass.post.postId IN (:postIds)")
//		.setParameterList("postIds", postIds).list();
//		
//		/*
//		asses = session().createQuery("SELECT ass FROM AssociationPostTag ass INNER JOIN FETCH ass.post as p " +
//				" INNER JOIN FETCH ass.tag as t WHERE ass.post.postId IN (:postIds)")
//				.setParameterList("postIds", postIds).list();
//		
//		//Hibernate.initialize(asses);
//		
//		
//		for(AssociationPostTag a : asses){
//			log.debug(a.getPost().getPath());
//			log.debug(a.getPost().getTagAssociations());
//			log.debug(a.getPost().getEntry());
//			break;
//		}
//		*/
//		
//		log.info("Done prefetching ()((((()()())(()");
//		/*
//		List<AssociationPostTag> asses = session().createCriteria(AssociationPostTag.class)
//			.add(Restrictions.in("post.postId", postIds)).list();
//		
//		List<Entry> entries = session().createCriteria(Entry.class)
//			.add(Restrictions.in("post.postId", postIds)).list();
//		
//		for(Entry e : entries){
//			Hibernate.initialize(e.getPost());
//		}
//		for(AssociationPostTag ass : asses){
//			Hibernate.initialize(ass.getTag());
//		}
//		*/
//	} 

	@SuppressWarnings("unchecked")
	private List<Post> executeFlatQuery() {
		List<Post> flat;
		flat = session().getNamedQuery("LatestPostsFlatDao.Flat")
			.setParameterList("tagIds", filteredTagIdList)
			.setFirstResult(0)
			.setMaxResults(pageSize).list();
		return flat;
	}

	public List<String> getFilteredTagIdList() {
		return filteredTagIdList;
	}

	public void setFilteredTagIdList(List<String> filteredTagIdList) {
		if(filteredTagIdList != null)
			this.filteredTagIdList.addAll(filteredTagIdList);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
