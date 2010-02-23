package org.ttdc.gwt.server.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

import static junit.framework.Assert.*;

public class AccountDaoTest {
	@Test
	public void createAccount(){
		try{
			Persistence.beginSession();
			String login ="Naboo";
			AccountDao dao = new AccountDao();
			dao.setName("Not Trevis");
			dao.setBio("All about not trevis.");
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			try {
				dao.setBirthday(df.parse("5/14/1977"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dao.setEmail("trevisthomas@gmail.com");
			dao.setLogin(login);
			dao.setPassword("hazah!");
			
			Person p = dao.create();
			
			assertNotNull(p.getPersonId());
			assertEquals(login,p.getLogin());
			assertNotSame("hazah!", p.getPassword());
			
			//Trevis, look at WebHelper.java for what v6 did
			AccountDao.sendActivateionEmail(p,"http://localhost:8888/authorize.jsp");
		}
		finally{
			Persistence.rollback();
		}
	}
	
	@Test
	public void updateAccount(){
		try{
			Persistence.beginSession();
			String email = "faketestemail@email.com";
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertNotSame(email, person.getEmail());
			
			String testLogin="testlogin";
			String testPwd="testpwd";
			
			AccountDao dao = new AccountDao();
			dao.setPersonId(Helpers.personIdCSam);
			dao.setBio(person.getBio());
			dao.setBirthday(person.getBirthday());
			dao.setEmail(email);
			dao.setPassword(testLogin);
			dao.setLogin(testPwd);
			dao.setName("Dave TheBontempo Bontempo");
			
			Person updatedPerson = dao.update();
			
			assertEquals(email, updatedPerson.getEmail());
			assertNotSame(testLogin, updatedPerson.getLogin());
			assertNotSame(testPwd, updatedPerson.getPassword());
		}
		finally{
			Persistence.rollback();
		}
	}
}	
