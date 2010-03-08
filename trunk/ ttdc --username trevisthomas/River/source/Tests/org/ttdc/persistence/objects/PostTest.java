package org.ttdc.persistence.objects;

import org.apache.log4j.Logger;
import org.junit.Test;
import static junit.framework.Assert.*;


public class PostTest {
	private final static Logger log = Logger.getLogger(PostTest.class);
	@Test
	public void bitmaskTest(){
		Post p = new Post();
		assertTrue("New post should not be a movie",!p.isMovie());
		p.setMovie();
		assertTrue("Should be a movie now",p.isMovie());
		p.setDeleted();
		
		assertTrue("Should still be a movie",p.isMovie());
		assertTrue("Should still deleted",p.isDeleted());
	}
}
