package org.ttdc.persistence;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.search.FullTextSession;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class FullTextIndexBuilder {
	private static Logger log = Logger.getLogger(FullTextIndexBuilder.class);
	/**
	 * Indexes the existing item records.  Only needed for data already in db.  
	 * all further data added/updated with hibernate builds indices on the fly.
	 */
	
	
	private void buildTagIndex(){
		log.info("Building Full Text Index for 'Tag' table");
		FullTextSession fullTextSession = Persistence.fullTextSession();
		@SuppressWarnings("unchecked")
		List<Tag> entries = fullTextSession.createQuery("SELECT t FROM Tag t order by t.date").list(); //.setMaxResults(10)
		for (Tag entry : entries) {
		    fullTextSession.index(entry);
		}
		Persistence.commit();       
	}
	
	private void buildPostIndex(){
		log.info("Building Full Text Index for 'Post' table");
		FullTextSession fullTextSession = Persistence.fullTextSession();
		@SuppressWarnings("unchecked")
		List<Post> posts = fullTextSession.createQuery("SELECT p FROM Post p ORDER BY p.date").list();
		for (Post p : posts) {
		    fullTextSession.index(p);
		}
		Persistence.commit();       
	}
	public static void main(String[] args) { 
		FullTextIndexBuilder builder = new FullTextIndexBuilder();
		builder.start();
		//builder.buildTagIndex();
		builder.buildPostIndex();
		builder.end();
		System.exit(0);
	}
	private StopWatch stopwatch = new StopWatch();

	public void start(){
		stopwatch.start();
	}
	public void end(){
		stopwatch.stop();
		log.info("Time taken: "+stopwatch);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
}

