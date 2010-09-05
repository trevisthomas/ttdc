package org.ttdc.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ttdc.persistence.objects.Post;
import static org.junit.Assert.*;

public class PathToTreeUtilTest {
	@Test
	public void testCreateArray(){
		
		Post p1 = new Post();
		p1.setPath("00001.00003.00002");
		
		Post p2 = new Post();
		p2.setPath("00003.00001.00012");
		
		Post p3 = new Post();
		p3.setPath("00002.00005");
		
		Post p4 = new Post();
		p4.setPath("00002");
		
		List<Post> posts = new ArrayList<Post>();
		posts.add(p1);
		posts.add(p2);
		posts.add(p3);
		posts.add(p4);
		
		PathSegmentizer util = new PathSegmentizer();
		
		int[] values = util.calculatePathSegmentMaximums(posts);
		
		assertEquals(3, values.length);
		assertEquals(3, values[0]);
		assertEquals(5, values[1]);
		assertEquals(12, values[2]);
	}
	
	@Test
	public void testArraizePath(){
		PathSegmentizer util = new PathSegmentizer();
		Post p1 = new Post();
		p1.setPath("00001.00003.00002");
		int[] values = util.segmentizePath(p1);
		
		assertEquals(3, values.length);
		assertEquals(1, values[0]);
		assertEquals(3, values[1]);
		assertEquals(2, values[2]);
		
	}
}
