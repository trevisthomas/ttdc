package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.Post;

import static org.ttdc.gwt.server.dao.DaoUtils.*;

public class MovieDao extends PaginatedDaoBase{
	private SortBy sortBy = SortBy.BY_RATING;
	private SortDirection sortDirection = SortDirection.DESC;
	private String personId; 
	
	public MovieDao(){};
	
	public PaginatedList<Post> load(){
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
	
	private PaginatedList<Post> getMoviesSortedByTitle(){
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByTitleForPersonDesc",personId);
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByTitleForPerson",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByTitleDesc");
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByTitle");
				default: throw new RuntimeException("Un posible!");
			}
		}
	}
	
	private PaginatedList<Post> getMoviesSortedByYear(){
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByYearForPerson",personId);
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByYearForPersonDesc",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByYear");
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByYearDesc");
					
				default: throw new RuntimeException("Un posible!");
			}
		}
	}
	
	private PaginatedList<Post> getMoviesSortedByRating(){//Switched on purpose
		if(StringUtil.notEmpty(personId)){
			switch(sortDirection){
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByRatingForPerson",personId);
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByRatingForPersonDesc",personId);
				default: throw new RuntimeException("Un posible!");
			}
		}
		else{
			switch(sortDirection){//Switched on purpose
				case DESC:
					return executeQuery(this,"MovieDao.moviesSortedByAverageRating");
				case ASC:
					return executeQuery(this,"MovieDao.moviesSortedByAverageRatingDesc");
					
				default: throw new RuntimeException("Un posible!");
			}
		}
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
		List<Object[]> list = session().getNamedQuery("MovieDao.peopleWithMovieRatings").list();
		
		Map<String, Long> map = new HashMap<String, Long>();
		
		for(Object[] result : list){
			map.put((String)result[0], (Long)result[1]);
		}
		
		return map;
	}
	
}
