package org.ttdc.gwt.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.TagConstants;

import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

import static org.ttdc.gwt.server.dao.Helpers.*;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import static org.junit.Assert.*;

public class MovieDaoTest {
	private final static Logger log = Logger.getLogger(MovieDaoTest.class);
	
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
	private void dumpTitleWithAverageRating(PaginatedList<Post> results) {
		Inflatinator inf = new Inflatinator(results.getList());
		for(GPost p : inf.extractPosts()){
			log.debug(p.loadTagAssociation(TagConstants.TYPE_RELEASE_YEAR).getTag().getValue() 
				+" "+	p.getTitle() 
				+" "+p.loadTagAssociation(TagConstants.TYPE_AVERAGE_RATING).getTag().getValue()
				);
		}
	}
	
	private void dumpTitleWithRatingBy(PaginatedList<Post> results, String personId) {
		Inflatinator inf = new Inflatinator(results.getList());
		for(GPost p : inf.extractPosts()){
			log.debug(p.loadTagAssociation(TagConstants.TYPE_RELEASE_YEAR).getTag().getValue() 
					+" "+p.getTitle() +" "+p.loadTagAssociationByPerson(TagConstants.TYPE_RATING,personId).getTag().getValue());
		}
	}
	
	@Test
	public void testGetMovieRaters(){
		try{
			beginSession();
			MovieDao dao = new MovieDao();
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
			MovieDao dao = new MovieDao();
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
	public void testMoviesSortByYear(){
		try{
			beginSession();
			MovieDao dao = new MovieDao();
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortOrder(SortBy.BY_RELEASE_YEAR);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.ASC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortDirection(SortDirection.ASC);
			dao.setSortOrder(SortBy.BY_TITLE);
			PaginatedList<Post> results = dao.load();
			
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
			MovieDao dao = new MovieDao();
			dao.setPersonId(Helpers.personIdTrevis);
			dao.setSortOrder(SortBy.BY_TITLE);
			dao.setSortDirection(SortDirection.DESC);
			PaginatedList<Post> results = dao.load();
			
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
