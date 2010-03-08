package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.gwt.server.dao.Helpers.assertContains;
import static org.ttdc.gwt.server.dao.Helpers.assertEqualsOneOfExpected;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.ttdc.gwt.server.dao.Helpers.*;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

public class TagSearchDaoTest {
	private final static Logger log = Logger.getLogger(TagSearchDaoTest.class);
	
	@Test
	public void testSearchForTagsByKeyword(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			String phrase = "morsels";
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.setPhrase(phrase);
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBrowseTags(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String t3 = "50DA2CF8-7AF6-4B78-AAA9-3427F41A4BF4";//Funny
			String t1 = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
			//String t2 = "3325CE14-A37E-4236-875C-F1D97F006682"; // Tag val:Trevis
			
			 
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(t1);
			dao.addTagId(t3);
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTagsByType(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String t3 = "3FE5F7A3-F91D-41E3-9225-E2538D59E5C3";//Morsels of Corporate Goodness
			String t1 = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
			String t2 = "3325CE14-A37E-4236-875C-F1D97F006682"; // Tag val:Trevis
			
			
			String type=Tag.TYPE_TOPIC;
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(t1);
			dao.addTagId(t2);
			dao.addFilterForTagType(type);
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
			}
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTagsByTypeForSubstr(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String t1 = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
			
			String type=Tag.TYPE_TOPIC;
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(t1);

			dao.addFilterForTagType(type);
			String phrase = "Funny";
			dao.setPhrase(phrase);
			
			PaginatedList<Tag> result = dao.search();
			log.debug(result.toString());
			assertTrue("Didnt find anything and i expected to", result.getList().size() > 0);
			
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				assertContains(t.getValue(),phrase);
			}
			
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTagsByTypeWithExcludes(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String t3 = "50DA2CF8-7AF6-4B78-AAA9-3427F41A4BF4";//Funny
			String t1 = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
			String t2 = "3325CE14-A37E-4236-875C-F1D97F006682"; // Tag val:Trevis
			
			
			String type=Tag.TYPE_TOPIC;
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			//dao.addTagIdUnion(t1);
			dao.addTagIdExclude(t3);
			dao.addTagId(t1);
			dao.addFilterForTagType(type);
			String phrase = "Morsels";
			dao.setPhrase(phrase);
			
			PaginatedList<Tag> result = dao.search();
			assertTrue("Didnt find anything and i expected to", result.getList().size() > 0);
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				assertContains(t.getValue(),phrase);
				log.info(t.getTagId()+" "+t.getValue());
				assertFalse(" Tag : "+t.getValue()+" should have been excluded!", t.getTagId().equals(t3));
			}
			
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	
	@Test
	public void browseWithInvalidArguements(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String t3 = "3FE5F7A3-F91D-41E3-9225-E2538D59E5C3";//Morsels of Corporate Goodness
			String t1 = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
			String t2 = "3325CE14-A37E-4236-875C-F1D97F006682"; // Tag val:Trevis
			
			
			String type=Tag.TYPE_TOPIC;
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(t1);
			dao.addTagIdExclude(t3);
			String phrase = "Morsels";
			dao.setPhrase(phrase);
			
			dao.search();
			
			fail("Unions with excludes are not supported.");
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			/*expected*/
		}
	}
	
	@Test
	public void searchForTopicThatAreTitles(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			dao.setTitlesOnly(true);
			String phrase = "stuff";
			dao.setPhrase(phrase);
			
			PaginatedList<Tag> result = dao.search();
			
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				assertContains(t.getValue(),phrase);
				log.info(t.getTagId()+" "+t.getValue());
				assertFalse(" Tag : "+t.getValue()+" should have been excluded because it is not a thread title!", t.getTagId().equals(tagGeneralStuff));
			}
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	@Test
	public void invalidSearchForTagsThatAreTitles(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(tagCorporateGoodness);
			String phrase = "Morsels";
			dao.setPhrase(phrase);
			dao.setTitlesOnly(true);
			dao.search();
			
			fail("Should have thrown an exception. Titles only is not supported with unions or excludes");
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			/*expected*/
		}
	}
	
	
	@Test
	public void browseTagsByMostPopular(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String type=Tag.TYPE_TOPIC;
			dao.addFilterForTagType(type);
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			int mass = -1;
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				if(mass == -1) mass = t.getMass();
				log.debug(mass);
				assertTrue("Doah, results are not sorted" ,mass >= t.getMass());
				mass = t.getMass();
				
			}
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	/**
	 * This functionality is for showing the tags that match a search phrase that are not
	 * threads/topics.  Those are captured and shown seperately in search results ui
	 * 
	 */
	@Test
	public void textSearchForNonTitleTags(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String type=Tag.TYPE_TOPIC;
			dao.addFilterForTagType(type);
			//FullText mode works but it seems quite crappy.
			//dao.setForceFullTextSearch(true);
			dao.setTagsOnly(true);
	//		dao.setPhrase("political");
			//TODO: test more cases. More words and more invalid states
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				log.debug(t.getValue());
			}

			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Ignore
	public void searchForMostPopularTags(){
		//TODO: this too. // I have no idea what this is about
	}
	
	//WARNING these date range tests cant be trusted because they are checking the date of the tag which
	//is not how the dates work.  Remeber, the dates are bridged in from the associations
	
	@Test
	public void searchForTopicThatAreTitlesWithDateRange(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			dao.setTitlesOnly(true);
			String phrase = "stuff";
			dao.setPhrase(phrase);
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(2003, 1, 1);
			dao.setDateRange(new DateRange(null, cal.getTime()));
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				assertContains(t.getValue(),phrase);
				log.info(t.getTagId()+" "+t.getValue());
				assertFalse(" Tag : "+t.getValue()+" should have been ecluded because it is not a thread title!", t.getTagId().equals(tagGeneralStuff));
				
				assertTrue("Date is out of the requested range", cal.getTime().after(t.getDate()) );
			}
			
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTagsByMostPopularWithDateRange(){
		try{
			beginSession();
			TagSearchDao dao = new TagSearchDao();
			
			String type=Tag.TYPE_TOPIC;
			dao.addFilterForTagType(type);
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(2009, 8, 1);
			dao.setDateRange(new DateRange(cal.getTime(),null)); //Newer than 2006
			
			PaginatedList<Tag> result = dao.search();
			
			assertTrue("Didnt find anything and i expected to", result.getList().size()>0);
			
			int mass = -1;
			for(Tag t : result.getList()){
				assertEqualsOneOfExpected(dao.getTagTypeFilterList(), t.getType());
				if(mass == -1) mass = t.getMass();
				log.debug(mass);
				assertTrue("Doah, results are not sorted" ,mass >= t.getMass());
				mass = t.getMass();
				assertTrue("Date is out of the requested range", cal.getTime().before(t.getDate()) );
				
			}
			commit();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
}
