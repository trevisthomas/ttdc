package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class FastMovieDaoTest {
private final static Logger log = Logger.getLogger(FastMovieDaoTest.class);
	
	Date start = new Date();
	Date end = new Date();
	
	@Before 
	public void setup(){
		start = new Date();
	}
	@After 
	public void taredown(){
		end = new Date();
		log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
	private void dumpTitleWithAverageRating(PaginatedList<GPost> results) {
		List<GPost> posts = results.getList();
		for(GPost p : posts){
			log.debug(p.getPublishYear() + " " +p.getTitle() + p.getAvgRatingTag());
		}
	}
	
	private void dumpTitleWithRatingBy(PaginatedList<GPost> results, String personId) {
		//Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> posts =  results.getList();
		for(GPost p : posts){
			log.debug(p.getPublishYear() 
					+" "+p.getTitle() +" "+p.getRatingByPerson(personId).getTag().getValue());
		}
	}
	
	@Test
	public void testGetMovieRaters(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			Map<String,Long> map = dao.loadMovieRaters();
			assertTrue(map.size()> 0);
			for(String s : map.keySet()){
				log.debug(s);
			}
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByRating(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByRatingDesc(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByRatingForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testMoviesSortByRatingDescForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			for(GPost p : results.getList()){
				Assert.assertEquals("Wrong creator",Helpers.personIdTrevis,p.getCreator().getPersonId());
				
			}
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByYear(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByYearDesc(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByYearForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testMoviesSortByYearDescForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByTitle(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByTitleDesc(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithAverageRating(results);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMoviesSortByTitleForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			dao.setSortOrder(SortBy.BY_TITLE);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testMoviesSortByTitleDescForTrevis(){
		try{
			beginSession();
			FastMovieDao dao = new FastMovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<GPost> results = dao.load();
			
			assertTrue(results.getList().size() > 0);
			dumpTitleWithRatingBy(results,Helpers.personIdTrevis);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
}
