package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;

public class PersonDao extends PaginatedDaoBase{
	private boolean activeOnly = false;
	private SortBy sortBy;
	private SortDirection sortDirection;

	//private final static  Logger log = Logger.getLogger(PersonDao.class);
	/**
	 * Loads a person from hibernate cache or from db
	 * 
	 * @param personId
	 * @return
	 */
	public static Person loadPerson(String personId){
		if(!InitConstants.ANON_PERSON_ID.equals(personId)){
			Person p = (Person)session().load(Person.class, personId);
			p.initialize();
			return p;
		}
		else
			return InitConstants.ANONYMOUS;
		
	}
	
	public static Person loadPersonByLogin(String login){
		Query query = session().getNamedQuery("person.getByLogin").setString("login", login);
		Person p = (Person)query.uniqueResult();
		if(p == null) 
			throw new RuntimeException(login + " account not found.");
		return p;
	}
	
	public static List<Person> loadPeople(Collection<String> personIds){
		@SuppressWarnings("unchecked")	
		List<Person> list = session().getNamedQuery("PersonDao.loadPersonList")
			.setParameterList("personIds", personIds)
			.list();
		return list;
	}
	
	public static void lock(String personId){
		updateAccountStatus(personId, Person.STATUS_LOCKED);
	}

	public static void unlock(String personId){
		if(!isStatus(personId, Person.STATUS_LOCKED)){
			throw new RuntimeException("Only locked users can be unlocked");
		}
		updateAccountStatus(personId, Person.STATUS_ACTIVE);
	}
	
	public static Person activate(String personId){
		if(!isStatus(personId, Person.STATUS_INACTIVE)){
			throw new RuntimeException("Only deactive users can be activated");
		}
		Person p = updateAccountStatus(personId, Person.STATUS_ACTIVE);
		
//		if(getActiveUsers().size() == 1){
//			grantPrivilege(guid, Privilege.ADMINISTRATOR); //If this is the first user make them admin
//		}
//		else{
//			grantDefaultPrivileges(guid);
//		}
		
		return p;
	}
	
	/**
	 * Trevis, there is probably no reason to expose this. Why would you ever want to inactivate an account?
	 * 
	 * @param personId
	 */
	public static void deactivate(String personId){
		if(!isStatus(personId, Person.STATUS_ACTIVE)){
			throw new RuntimeException("Only active users can be deactivated");
		}
		updateAccountStatus(personId, Person.STATUS_INACTIVE);	
	}
	
	/**
	 * Helper method to check if a person is a the requested status
	 * @param personId
	 * @param status
	 * @return
	 */
	private final static boolean isStatus(String personId, String status){
		Person p = PersonDao.loadPerson(personId);
		if(p == null){
			throw new RuntimeException("Can not find user.");
		}
		return p.getStatus().equals(status);
	}
	
	/**
	 * A helper method
	 * @param personId
	 * @param status
	 */
	private final static Person updateAccountStatus(String personId, String status) {
		Person p = PersonDao.loadPerson(personId);
		if(p == null){
			throw new RuntimeException("Can not find user.");
		}
		p.setStatus(status);
		session().update(p);
		return p;
	} 
	
	public PaginatedList<Person> load(){
		PaginatedList<Person> results = null;
		if(activeOnly){
			if(sortBy == null)
				throw new RuntimeException("Sort column is required for active list");
			String queryName;
			switch(sortBy){
				case BY_EMAIL:
					if(sortDirection.equals(SortDirection.DESC))
						queryName = "person.getAllActiveOrderByEmailDesc";
					else
						queryName = "person.getAllActiveOrderByEmail";
					break;
				case BY_LAST_ACCESSED: //I think the 'on purpose' comments are talking about inverting the definition of asc and desc
					if(!sortDirection.equals(SortDirection.DESC))//Trevis did this on purpose for the userlist
						queryName = "person.getAllActiveOrderByLastAccessedDesc";
					else
						queryName = "person.getAllActiveOrderByLastAccessed";
					break;
				case BY_HITS:
					if(!sortDirection.equals(SortDirection.DESC)) //Trevis did this on purpose for the userlist
						queryName = "person.getAllActiveOrderByHitsDesc";
					else
						queryName = "person.getAllActiveOrderByHits";
					break;
				case BY_LOGIN:
					if(sortDirection.equals(SortDirection.DESC))
						queryName = "person.getAllActiveOrderByLoginDesc";
					else
						queryName = "person.getAllActiveOrderByLogin";
					break;
				case BY_NAME:
					if(sortDirection.equals(SortDirection.DESC))
						queryName = "person.getAllActiveOrderByNameDesc";
					else
						queryName = "person.getAllActiveOrderByName";
					break;
				default:
					queryName = "person.getAllActive";
			}
			 
			results = DaoUtils.executeQuery(this,queryName);
		}
		else{
			if(sortDirection != null || sortBy != null) 
				throw new RuntimeException("Sort not implemented for getAll");
			results = DaoUtils.executeQuery(this,"person.getAll");
		}
		return results;
	}
	
	public static Person grantPrivilege(String personId, Privilege privilege){
		Person p = PersonDao.loadPerson(personId);
		if(!p.addPrivilege(privilege)){
			throw new RuntimeException(p.getLogin()+" already has "+privilege.getName());
		}
		else{
			session().save(p);
		}
		return p;
	}
	
	public static Person revokePrivilege(String personId, Privilege privilege){
		Person p = PersonDao.loadPerson(personId);
		if(!p.removePrivilege(privilege)){
			throw new RuntimeException(p.getLogin()+" already has "+privilege.getName());
		}
		else{
			session().save(p);
		}
		return p;
	}
	
	public boolean isActiveOnly() {
		return activeOnly;
	}

	public void setActiveOnly(boolean activeOnly) {
		this.activeOnly = activeOnly;
	}

	public SortBy getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortBy sortBy) {
		this.sortBy = sortBy;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}
	
}
