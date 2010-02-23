package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.ServiceException;

/**
 * Cache for the traffic widget.  Their is one instance of this class
 * and the timestamp field is updated anytime a new person is inserted.
 * 
 * @author Trevis
 *
 */
public class TrafficCache {
	private final static Logger log = Logger.getLogger(TrafficCache.class);
	//private List<Person> people = new CopyOnWriteArrayList<Person>();
	private List<String> peopleIds = new CopyOnWriteArrayList<String>();
	private volatile long timestamp;
	public final static int TRAFFIC_CACHE_SIZE = 5;
	
	@SuppressWarnings("unchecked")
	private TrafficCache() throws RuntimeException{
		Session session = Persistence.beginSession();
		Query query = session.getNamedQuery("person.getTraffic").setMaxResults(TRAFFIC_CACHE_SIZE);
		List<Person> people = query.list();
		for(Person p : people){
			peopleIds.add(p.getPersonId());
		}
		Date d = new Date();
		setTimestamp(d.getTime());
	}
	
	private static class SingletonHolder {
		private final static TrafficCache INSTANCE = new TrafficCache();
	}
	
	public static TrafficCache getInstance(){
		return SingletonHolder.INSTANCE;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public void insert(Person p){
		Date d = new Date();
		setTimestamp(d.getTime());
		if(peopleIds.contains(p.getPersonId())){
			peopleIds.remove(peopleIds.indexOf(p.getPersonId()));
		}
		peopleIds.add(0,p.getPersonId());
		
		if(peopleIds.size() > TRAFFIC_CACHE_SIZE){
			peopleIds = peopleIds.subList(0, TRAFFIC_CACHE_SIZE);
		}
		
		//refreshPeople();
	}
	
	public boolean isUpdated(Date d){
		return d.getTime() < timestamp;
	}

	public List<Person> getPeople() throws ServiceException{
		List<Person> people = new ArrayList<Person>();
		for(String id : peopleIds){
			Person p = UserService.getInstance().loadPerson(id);
			/*
			if(p.getWebPageUserObjects().size() > 2){
				log.info("/n/n"+p.getLogin()+"MROE THAN TWO USER OBJECTS");
			}	
			for(UserObject uo : p.getWebPageUserObjects()){
				log.info(p.getLogin()+" "+uo.getUrl());
			}
			*/
			people.add(p);
		}
		
		return people;
	}
	
	/*
	public void setPeople(List<Person> people) {
		Date d = new Date();
		setTimestamp(d.getTime());
		this.people.addAll(people);
		
	}
	
	private void refreshPeople(){
		for(Person p : people){
			p.initialize();
		}		
	}
	*/
}
