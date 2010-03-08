package org.ttdc.gwt.client.beans;

import static junit.framework.Assert.*;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.TagConstants;

@Deprecated
public class GPostTest {
	
	final String title = "Beyond Public Goodness";
	GPost p;
	@Before
	public void setup(){
		p = new GPost();
		//p.setTitle(title);
		//I decided to just set the damned post title for all posts on the server side.
		/*
		GAssociationPostTag ass = PostBeanMother.createTitleAssociation(title);
		
		List<GAssociationPostTag> asses = new ArrayList<GAssociationPostTag>();
		asses.add(ass);
		
		p.setTagAssociations(asses);
		*/
		
	}

	
	
	@Test
	public void testAttributes(){
		assertEquals(title,p.getTitle());
	}
}
