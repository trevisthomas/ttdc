package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.objects.InboxCache;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;


public class InboxDao extends FilteredPostPaginatedDaoBase{
	private Person person;
	private final static Date defaultStartDate;
	private Date lastReadDate = null;
	static{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(1970, 11, 21);
		defaultStartDate = cal.getTime();
	}
	public InboxDao(Person person) {
		this.person = person;
		
		addFilterThreadIds(person.getFrontPageFilteredThreadIds());
		//TREVIS this logic is also in LatestPostCommandExecutor.  I think that this should be moved to 
		//a shared location and implemented by the DAO like it is here for Inbox
//		if(!person.isNwsEnabled()){
//			addFlagFilter(PostFlag.NWS);
//		}
//		
//		if(!person.isPrivateAccessAccount()){
//			addFlagFilter(PostFlag.PRIVATE);
//		}
		
		setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(person));
		
		getLastReadDate();
	}
	
	public PaginatedList<Post> loadFlat(){
		PaginatedList<Post> results = new PaginatedList<Post>();
		results = executeLoadQuery("InboxDao.Flat");
		return results;
	}
	
	
	@SuppressWarnings("unchecked")
	private Date getLastReadDate(){
		if(person.isAnonymous())
			return defaultStartDate;
		
//		if(lastReadDate == null){
//			//This query bombs on SqlServer 2008 if the personId is not a valid uniqueidentifier, so i check for anon and exit before doing it
//			List<InboxCache> list = session().createCriteria(InboxCache.class)
//			.add(Restrictions.isNull("post"))
//			.add(Restrictions.eq("person.personId", person.getPersonId()))
//			.list();
//			
//			
//			if(list.size() == 0){
//				lastReadDate = defaultStartDate;
//			}
//			else{
//				lastReadDate = list.get(0).getDate();
//			}
//		}
		
		if(person.getSiteReadDate() == null){
			lastReadDate = defaultStartDate;
		}
		else{
			lastReadDate = person.getSiteReadDate();
		}
		
		return lastReadDate;
	}
	
	@SuppressWarnings("unchecked")
	private PaginatedList<Post> executeLoadQuery(String query) {
		PaginatedList<Post> results;
		if(getPageSize() > 0){
			List<Post> list;
			long count = (Long)session().getNamedQuery(query+"Count")
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.setParameter("startDate", getLastReadDate())
				.uniqueResult();
			
			
			list = session().getNamedQuery(query)
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.setParameter("startDate", getLastReadDate())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
			List<Post> list = session().getNamedQuery(query)
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.setParameter("startDate", getLastReadDate())
				.list();
			results = DaoUtils.createResults(this, list, list.size());
		}
		
		return results;
	}
	
	public long calculateInboxSize(){
		long count = (Long)session().getNamedQuery("InboxDao.FlatCount")
			.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.setParameterList("threadIds", getFilterThreadIds())
			.setParameter("startDate", getLastReadDate())
			.uniqueResult();
		return count;
	}

	public boolean isRead(Post post) {
		return post.getDate().before(getLastReadDate());
	}
	
	public boolean isRead(Date date) {
		return date.before(getLastReadDate());
	}

//	public void markSiteRead() {
//		session().createQuery("DELETE FROM InboxCache i WHERE i.person.personId=:personId").setString("personId", person.getPersonId()).executeUpdate();
//		
//		
//		InboxCache cache = new InboxCache();
//		cache.setPerson(person);
//		cache.setPost(null);
//		cache.setDate(new Date(System.currentTimeMillis()+2000));
//		
//		session().save(cache);
//		session().flush();
//	}
	
}
