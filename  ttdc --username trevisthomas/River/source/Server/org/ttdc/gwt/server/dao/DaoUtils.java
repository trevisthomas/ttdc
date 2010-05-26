package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class DaoUtils {
	public static <T> PaginatedList<T> createResults(PaginatedDaoBase dao, List<T> list, long count) {
		PaginatedList<T> results = new PaginatedList<T>();
		results.setTotalResults((int)count);
		results.setList(list);
		results.setCurrentPage(dao.getCurrentPage());
		results.setPageSize(dao.getPageSize());
		return results;
	}
	
//	@SuppressWarnings("unchecked") 
//	public static PaginatedList<Post> executeLoadFromPostIdNoFilter(PaginatedDaoBase pageInfo, String query, String postId){
//		List<Post> list;
//		PaginatedList<Post> results = null;
//		if(pageInfo.getPageSize() > 0){
//			int count = session().getNamedQuery(query)
//				.setString("postId", postId).list().size();
//			
//			list = session().getNamedQuery(query)
//				.setString("postId", postId)
//				.setFirstResult(pageInfo.calculatePageStartIndex())
//				.setMaxResults(pageInfo.getPageSize()).list();
//			
//			results = DaoUtils.createResults(pageInfo, list, count);
//		}
//		else{
//			list = session().getNamedQuery(query)
//				.setString("postId", postId).list();
//			
//			results = DaoUtils.createResults(pageInfo, list, list.size());
//		}
//		return results;
//	}

	@SuppressWarnings("unchecked") 
	public static PaginatedList<Post> executeLoadFromPostId(PaginatedDaoBase pageInfo, String query, String countQuery, String postId, long filterMask){
		PaginatedList<Post> results;
		if(pageInfo.getPageSize() > 0){
			List<Post> list;
			long count = (Long)session().getNamedQuery(countQuery)
				.setParameter("filterMask", filterMask)
				.setString("postId", postId)
				.uniqueResult();
			
			
			list = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setString("postId", postId)
				.setFirstResult(pageInfo.calculatePageStartIndex())
				.setMaxResults(pageInfo.getPageSize()).list();
			
			results = DaoUtils.createResults(pageInfo, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setString("postId", postId).list();
			results = DaoUtils.createResults(pageInfo, list, list.size());
		}
		
		return results;
	}
	
	/**
	 * Perform SearchResults query with no filtering, with one guid arguement
	 */
	public static <T> PaginatedList<T> executeQuery(PaginatedDaoBase pageInfo, String query, String countQuery, String guid) {
		PaginatedList<T> results = new PaginatedList<T>();
		
		long count = (Long)session().getNamedQuery(countQuery)
			.setParameter("guid", guid).uniqueResult();
			
		@SuppressWarnings("unchecked")
		List<T> list = session().getNamedQuery(query)
			.setParameter("guid", guid)
			.setFirstResult(pageInfo.calculatePageStartIndex())
			.setMaxResults(pageInfo.getPageSize()).list();
		
		results = DaoUtils.createResults(pageInfo, list, count);
		
		return results;
	}
	
	public static <T> PaginatedList<T> executeQuery(PaginatedDaoBase pageInfo,String query, String countQuery) {
		PaginatedList<T> results = new PaginatedList<T>();
		
//		int count = session().getNamedQuery(countQuery)
//			.list().size();
		long count = (Long)session().getNamedQuery(countQuery).uniqueResult();
			
		@SuppressWarnings("unchecked")
		List<T> list = session().getNamedQuery(query)
			.setFirstResult(pageInfo.calculatePageStartIndex())
			.setMaxResults(pageInfo.getPageSize()).list();
		
		results = DaoUtils.createResults(pageInfo, list, (int)count);
		
		return results;
	}
	
	/**
	 * Perform SearchResults query with no filtering and no arguments
	 * 
	 * TODO, fix this.  Use a faster count. This is bad. Replace it with the above query!
	 */
	public static <T> PaginatedList<T> executeQuery(PaginatedDaoBase pageInfo,String query) {
		PaginatedList<T> results = new PaginatedList<T>();
		
		int count = session().getNamedQuery(query)
			.list().size();
			
		@SuppressWarnings("unchecked")
		List<T> list = session().getNamedQuery(query)
			.setFirstResult(pageInfo.calculatePageStartIndex())
			.setMaxResults(pageInfo.getPageSize()).list();
		
		results = DaoUtils.createResults(pageInfo, list, count);
		
		return results;
	}
	
	/**
	 * Loads a list of posts using a specified query and a specified list of postIds
	 * 
	 * Used by ThreadDao and LatestPostsDao
	 * 
	 * @param query
	 * @param postIds
	 * @return
	 */
	@SuppressWarnings("unchecked") 
	public static List<Post> executeLoadFromPostIdsNoFilter(String query, List<String> postIds){
		List<Post> list;
		list = session().getNamedQuery(query)
			.setParameterList("postIds", postIds).list();
		return list;
	}

	/**
	 * Loads a list of posts using a specified query and a specified list of postIds, with bitmask filter
	 * 
	 * Used by ThreadDao and LatestPostsDao
	 * 
	 * @param query
	 * @param postIds
	 * @param filteredTagIdList
	 * @return
	 */
	@SuppressWarnings("unchecked") 
	public static List<Post> executeLoadFromPostIds(String query, List<String> postIds, long filterMask){
		List<Post> list = session().getNamedQuery(query)
			.setParameter("filterMask", filterMask)
			.setParameterList("postIds", postIds).list();
		return list;
	}
	
	/**
	 * Create a date object representing the beginning of the day requested
	 * 
	 * @param yr
	 * @param mo
	 * @param dd
	 * @return
	 */
	public static Date createDateStart(int yr, int mo, int dd){
		Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, yr);
    	cal.set(Calendar.MONTH, mo-1);
    	cal.set(Calendar.DAY_OF_MONTH,dd);
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
	}

	/**
	 * Create a date object representing the end of the day requested
	 * 
	 * @param yr
	 * @param mo
	 * @param dd
	 * @return
	 */

	public static Date createDateEnd(int yr, int mo, int dd){
		Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, yr);
    	cal.set(Calendar.MONTH, mo-1);
    	cal.set(Calendar.DAY_OF_MONTH,dd);
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
	}
	
	/**
	 * Create a date object representing the beginning of the week requested
	 * 
	 * @param yr
	 * @param mo
	 * @param dd
	 * @return
	 */
	public static  Date getDateBeginningOfWeek(int yearNumber, int weekOfYear) {
		Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, yearNumber);
    	cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * Create a date object representing the end of the week requested
	 * 
	 * @param yr
	 * @param mo
	 * @param dd
	 * @return
	 */
	public static Date getDateEndOfWeek(int yearNumber, int weekOfYear) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, yearNumber);
    	cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
	}
	
	
}
