package org.ttdc.gwt.server.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;

import static org.ttdc.persistence.Persistence.*;

import static junit.framework.Assert.*;

public class PersonDaoTest {
	private final static  Logger log = Logger.getLogger(PersonDaoTest.class);
	String[] personIds = {"2C82443C-044C-4E86-BD85-3B66C29EAAF1",
			"B99D6CCA-D1F4-4B58-9316-7AF07B8F341F",
			"50E7F601-71FD-40BD-9517-9699DDA611D6",
			"32CF168E-2C06-4A80-924C-9C824C1770D7",
			"DAE569B8-F246-4B14-96CA-A63ED498C7B9",
			"659CBC8C-1AED-410E-ACF2-AB66A12F67F5",
			"69AAB175-53C7-4EDC-AA20-BFFFF01EEFF5",
			"E3EFEC5E-3864-4B3F-9BE5-C041CE790B29",
			"7A58F344-E0A2-4E89-9B39-CDFDE7FAB5EA"
		};
	@Before
	public void setup(){
		
	}
	@Test
	public void loadPeople(){
		beginSession();
		List<Person> people = PersonDao.loadPeople(Arrays.asList(personIds));
		assertTrue(people.size() == 9);
		commit();
	}
	
	@Test
	public void loadActiveUsers(){
		beginSession();
		PersonDao dao = new PersonDao();
		dao.setActiveOnly(true);
		PaginatedList<Person> results = dao.load();
		assertTrue(results.getList().size() == 20); //Meaningless test
		commit();
	}
	
	@Test
	public void loadAllUsers(){
		beginSession();
		PersonDao dao = new PersonDao();
		dao.setActiveOnly(false);
		PaginatedList<Person> results = dao.load();
		assertTrue(results.getList().size() == 20); //Meaningless test
		commit();
	}
		
	
	@Test
	public void deactivateActivate(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam); 
			assertEquals(Person.STATUS_ACTIVE, person.getStatus());
			PersonDao.deactivate(person.getPersonId());
			commit();
			beginSession();
			person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertEquals(Person.STATUS_INACTIVE, person.getStatus());
			PersonDao.activate(person.getPersonId());
			commit();
			beginSession();
			assertEquals(Person.STATUS_ACTIVE, person.getStatus());
			commit();
		}
		catch(Throwable t){
			log.error(t);
			rollback();
		}
	}
	
	@Test
	public void lockUnlock(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam); 
			assertEquals(Person.STATUS_ACTIVE, person.getStatus());
			PersonDao.lock(person.getPersonId());
			commit();
			beginSession();
			person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertEquals(Person.STATUS_LOCKED, person.getStatus());
			PersonDao.unlock(person.getPersonId());
			commit();
			beginSession();
			assertEquals(Person.STATUS_ACTIVE, person.getStatus());
			commit();
		}
		catch(Throwable t){
			log.error(t);
			rollback();
		}
	}
	
	@Test
	public void testInvalidStateSwitchActivate(){
		beginSession();
		try{
			Person person = PersonDao.loadPerson(Helpers.personIdCSam); 
			assertEquals(Person.STATUS_ACTIVE, person.getStatus());
			PersonDao.activate(person.getPersonId());
			fail("Should not be able to activate an active person");
		}
		catch(RuntimeException e){
			
			//expected
		}
		finally{
			rollback();
		}
		
	}
	
	@Test
	public void testGrantRevokePrivilege(){
		
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam); 
			
			assertFalse(person.isAdministrator());
			
			Privilege privilege = PrivilegeDao.loadPrivilege("E9CC2394-7C8B-4B48-B5FE-E3987596B8AF");
			PersonDao.grantPrivilege(person.getPersonId(), privilege);
			assertTrue(person.isAdministrator());
			
//			commit();
//			beginSession();
			
			person = PersonDao.loadPerson(Helpers.personIdCSam);
			privilege = PrivilegeDao.loadPrivilege("E9CC2394-7C8B-4B48-B5FE-E3987596B8AF");
			PersonDao.revokePrivilege(person.getPersonId(), privilege);
			assertFalse(person.isAdministrator());
			commit();	
			
		}
		catch(RuntimeException e){
			rollback();
		}
		
	}
	
}
