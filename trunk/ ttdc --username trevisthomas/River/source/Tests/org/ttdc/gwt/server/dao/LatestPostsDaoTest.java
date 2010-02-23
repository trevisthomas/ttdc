package org.ttdc.gwt.server.dao;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

import static junit.framework.Assert.*;

public class LatestPostsDaoTest {
	private final static Logger log = Logger.getLogger(LatestPostsDaoTest.class);
	private StopWatch stopwatch = new StopWatch();
	@Before 
	public void setup(){
//		stopwatch.start();
	}
	@After 
	public void taredown(){
		stopwatch.stop();
//		log.info("Time taken: "+stopwatch);
//		log.info("ass count: "+AssociationPostTag.iCount);
//		log.info("post count: "+Post.iCount);
	}
	
	private void startTimer() {
		stopwatch.start();
	}
	
	private void dumpTimer(){
		//stopwatch.stop();
		log.info("Time taken: "+stopwatch);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
	
	@Test
	public void nestedUnfiltered(){
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		PaginatedList<Post> results = dao.loadNested();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPostsNested();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	@Test
	public void nestedFiltered(){
		String privateTagId = InitConstants.PRIVATE_TAG_ID;
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		dao.addFilterTagId(privateTagId);
		PaginatedList<Post> results = dao.loadNested();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPostsNested();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	@Test
	public void flatFiltered(){
		String privateTagId = InitConstants.PRIVATE_TAG_ID;
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		dao.addFilterTagId(privateTagId);
		PaginatedList<Post> results = dao.loadFlat();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	@Test
	public void flatUnFiltered(){
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		PaginatedList<Post> results = dao.loadFlat();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	@Test
	public void threads(){
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		PaginatedList<Post> results = dao.loadThreads();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	@Test
	public void threadsFiltered(){
		String privateTagId = InitConstants.PRIVATE_TAG_ID;
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		dao.addFilterTagId(privateTagId);
		PaginatedList<Post> results = dao.loadThreads();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
		}
		
		dumpTimer();
	}
	
	
	@Test
	public void conversations(){
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		PaginatedList<Post> results = dao.loadConversations();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
			assertTrue(post.isThreadPost());
		}
		
		dumpTimer();
	}
	
	@Test
	public void conversationsFiltered(){
		String privateTagId = InitConstants.PRIVATE_TAG_ID;
		Persistence.beginSession();
		startTimer();
		LatestPostsDao dao = new LatestPostsDao();
		dao.addFilterTagId(privateTagId);
		PaginatedList<Post> results = dao.loadConversations();
		
		Inflatinator inf = new Inflatinator(results.getList());
		List<GPost> gPosts = inf.extractPosts();
		
		Persistence.commit();
		
		for(GPost post : gPosts){
			log.debug(post.getTitle() + post.getDate() + " [" + post.getPosts()+ "] " );
			assertTrue(post.isThreadPost());
		}
		
		dumpTimer();
	}
	
}
