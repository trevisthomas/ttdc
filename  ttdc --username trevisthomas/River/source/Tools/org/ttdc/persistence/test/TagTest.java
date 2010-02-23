package org.ttdc.persistence.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.SearchService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.SearchResultsBundle;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;

import junit.framework.TestCase;

public class TagTest extends TestCase {
	private static Logger log = Logger.getLogger(PostTest.class);
	
	private void save(Tag tag){
		Session session = Persistence.beginSession();
		session.save(tag);
		Persistence.commit();
	}
	
	public List<Tag> load(){
		Session session = Persistence.beginSession();
		
		Query query = session.getNamedQuery("tag.getAll")
			.setCacheable(true);
		ArrayList<Tag> list = (ArrayList<Tag>)query.list();

		for(Object t : list){
			log.info(t);
		}
		
		Persistence.commit();
		
		return list;
	}
	
	
	private Tag load(String id){
		Session session = Persistence.beginSession();
		
		Query query = session.getNamedQuery("tag.getByTagId").setString("tagId", id);
		Tag t = null;
		Object o = query.uniqueResult();
		//Object o = session.load(Tag.class,id);
		
		t = (Tag)o;
		

		
		//Tag t = new Tag();
		//t = (Tag)session.load(Tag.class, id);
		log.info(t);
		
		Persistence.commit();
		
		return t;	
	}
	
	public final void testCreateTag(){
		try{
			
			/*
			PersonTester pt = new PersonTester();
			List<Person> people = pt.load();
			assertTrue(people.size() > 0);
			
			Tag tag = new Tag();
			tag.setType(Tag.TYPE_DISPLAY);
			tag.setValue(Tag.VALUE_INF);
			tag.setCreator(people.get(0));
			save(tag);
			*/
			/*
			load();
			
			load();
			
			*/
			/*
			load();
			load();
			*/
			//load();
			
			//Person p = new PersonTester().loadPerson("D379886C-8A0F-4BC4-AC24-99E495CCFEF0");
			
			//load("71e4003f-ce7e-4674-afba-7059b71dbb93");
			//load("71e4003f-ce7e-4674-afba-7059b71dbb93");
			
			//load("d084b539-6ede-4ecd-9a14-dcce3b7f8d2e");
			/*
			load("d084b539-6ede-4ecd-9a14-dcce3b7f8d2e");
			load("d084b539-6ede-4ecd-9a14-dcce3b7f8d2e");
			load("d084b539-6ede-4ecd-9a14-dcce3b7f8d2e");*/
			//Persistence.commit();
			
			//CommentService.getInstance().search("Mechassault");
			//CommentService.getInstance().search("linten");
			
			//CommentService.getInstance().autoCompleteTag("trev");

			
			List<String> list = new ArrayList();
			list.add("2B994A54-59BF-4043-8F19-19A6AA599566");
			List<TagLite> sugestions  = new ArrayList<TagLite>();
			//SearchResultsBundle	results = CommentService.getInstance().spiderPhrase("4.5",list);
			SearchResultsBundle	results = SearchService.getInstance().spiderPhrase(UserService.getInstance().getAnnonymousUser(), "Morsels of Political Goodness",list);
			
			
			Persistence.getCache("org.ttdc.biz.network.beans.Tag");
			Persistence.getCache("org.ttdc.biz.network.beans.Person");
			
			//Persistence.getCache("longLived");
			
			
			//Persistence.getCache(Persistence.CACHE_REGION_SHORT_LIVED);
			
			
		}	
		catch(Throwable t){
			log.error(t);
			fail(t.getMessage());
		}
	}
}
