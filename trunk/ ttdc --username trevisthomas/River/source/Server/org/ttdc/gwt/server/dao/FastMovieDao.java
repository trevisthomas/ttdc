package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;

public class FastMovieDao extends PaginatedDaoBase{
	private SortBy sortBy = SortBy.BY_RATING;
	private SortDirection sortDirection = SortDirection.DESC;
	private String personId; 
	private boolean invertFilter = false;
	public FastMovieDao(){};
	
	public PaginatedList<GPost> load(){
		switch(sortBy){
			case BY_RATING:
				return getMoviesSortedByRating();
			case BY_TITLE:
				return getMoviesSortedByTitle();
			case BY_RELEASE_YEAR:
				return getMoviesSortedByYear();
			default:
				throw new RuntimeException("Un possible sort order!");
		}
	}
	
	private PaginatedList<GPost> getMoviesSortedByTitle(){
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByTitleForPersonDesc","FastMovieDao.moviesRatedByPersonCount",personId);
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByTitleForPerson","FastMovieDao.moviesRatedByPersonCount",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByTitleDesc","FastMovieDao.moviesCount");
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByTitle","FastMovieDao.moviesCount");
				default: throw new RuntimeException("Un posible!");
			}
		}
	}
	
	private PaginatedList<GPost> getMoviesSortedByYear(){
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByYearForPerson","FastMovieDao.moviesRatedByPersonCount",personId);
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByYearForPersonDesc","FastMovieDao.moviesRatedByPersonCount",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByYear","FastMovieDao.moviesCount");
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByYearDesc","FastMovieDao.moviesCount");
					
				default: throw new RuntimeException("Un posible!");
			}
		}
	}
	
	private PaginatedList<GPost> getMoviesSortedByRating(){//Switched on purpose
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByRatingForPerson","FastMovieDao.moviesRatedByPersonCount", personId);
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByRatingForPersonDesc","FastMovieDao.moviesRatedByPersonCount",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"FastMovieDao.moviesSortedByAverageRating","FastMovieDao.moviesCount");
				case ASC:
					return executeQuery(this,"FastMovieDao.moviesSortedByAverageRatingDesc","FastMovieDao.moviesCount");
					
				default: throw new RuntimeException("Un posible!");
			}
		}
	}

	
	public PaginatedList<GPost> executeQuery(PaginatedDaoBase pageInfo,String query, String countQuery) {
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		long count = (Long)session().getNamedQuery(countQuery).uniqueResult();
			
		@SuppressWarnings("unchecked")
		List<String> ids = session().getNamedQuery(query)
			.setFirstResult(pageInfo.calculatePageStartIndex())
			.setMaxResults(pageInfo.getPageSize()).list();
		
		FastGPostLoader loader = new FastGPostLoader(null);
		List<GPost> list = loader.fetchPostsForIdsMovieSummary(ids);
		
		results = DaoUtils.createResults(pageInfo, list, (int)count);
		
		return results;
	}
	
	/**
	 * Perform SearchResults query with no filtering, with one guid arguement
	 */
	public PaginatedList<GPost> executeQuery(PaginatedDaoBase pageInfo, String query, String countQuery, String guid) {
		PaginatedList<GPost> results;
		
		long count = (Long)session().getNamedQuery(countQuery)
			.setParameter("guid", guid).uniqueResult();
			
		@SuppressWarnings("unchecked")
		List<String> ids = session().getNamedQuery(query)
			.setParameter("guid", guid)
			.setFirstResult(pageInfo.calculatePageStartIndex())
			.setMaxResults(pageInfo.getPageSize()).list();
		
		FastGPostLoader loader = new FastGPostLoader(null);
		List<GPost> list = loader.fetchPostsForIdsMovieSummary(ids);
		
		results = DaoUtils.createResults(pageInfo, list, count);
		
		return results;
	}

	public SortBy getSortOrder() {
		return sortBy;
	}

	public void setSortOrder(SortBy sortBy) {
		this.sortBy = sortBy;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public Map<String, Long> loadMovieRaters() {
		@SuppressWarnings("unchecked")
		List<Object[]> list = session().getNamedQuery("FastMovieDao.peopleWithMovieRatings").list();
		
		Map<String, Long> map = new HashMap<String, Long>();
		
		for(Object[] result : list){
			map.put((String)result[0], (Long)result[1]);
		}
		
		return map;
	}

	public void setInvertFilter(boolean invertFilter) {
		this.invertFilter = invertFilter;
	}

	public boolean isInvertFilter() {
		return invertFilter;
	}
	
}
