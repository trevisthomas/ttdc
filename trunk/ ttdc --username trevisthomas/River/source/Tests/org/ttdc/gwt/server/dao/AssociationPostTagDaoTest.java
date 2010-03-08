package org.ttdc.gwt.server.dao;

import static org.junit.Assert.*;
import static org.ttdc.persistence.Persistence.*;

import org.junit.Test;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class AssociationPostTagDaoTest {
	private final String postId = "C772E164-294D-4B8D-9345-0E2599564118"; //TTDC Version 6 Beta! thread root
	private final String tagId = "1FDCB845-0327-493A-AE41-5539334256E4"; //The title tag
	private final String personId = "50E7F601-71FD-40BD-9517-9699DDA611D6";
	
	@Test
	public void readAssociationPostTag(){
		try{
			beginSession();
			String associationId = "D8B4634C-0D72-4750-B8E2-AFFD949206EB";
			AssociationPostTag ass = AssociationPostTagDao.load(associationId);
			assertEquals("Wrong association",ass.getGuid(),associationId);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void createAssociation(){
		try{
			beginSession();
			
			AssociationPostTagDao dao = new AssociationPostTagDao();
			
			Post post = PostDao.loadPost(postId);
			Tag tag = TagDao.loadTag(tagId);
			Person creator = PersonDao.loadPerson(personId);
			int originalTagMass = tag.getMass();
			
			dao.setTag(tag);
			dao.setPost(post);
			dao.setCreator(creator);
			
			AssociationPostTag ass = dao.create();
			
			assertEquals("Tag didnt gain any mass!",originalTagMass+1,ass.getTag().getMass());
			
			assertNotNull("Association was not created. ",ass);
			assertEquals("Creator not properly assigned",creator, ass.getCreator());
			assertEquals("Tag not properly assigned",tag, ass.getTag());
			assertEquals("Post not properly associated",post, ass.getPost());
			
			rollback();
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void createAssociationMissingCreatorTest(){
		try{
			beginSession();
			AssociationPostTagDao dao = new AssociationPostTagDao();
			
			Post post = PostDao.loadPost(postId);
			Tag tag = TagDao.loadTag(tagId);
			
			dao.setTag(tag);
			dao.setPost(post);
			
			dao.create();
			fail("An exception should have been thrown!");
			rollback();
		}
		catch(Exception e){
			rollback();
		}
	}
	@Test
	public void createAssociationMissingTagTest(){
		try{
			beginSession();
			AssociationPostTagDao dao = new AssociationPostTagDao();
			
			Post post = PostDao.loadPost(postId);
			Person creator = PersonDao.loadPerson(personId);
			
			dao.setPost(post);
			dao.setCreator(creator);
			
			dao.create();
			fail("An exception should have been thrown!");
			rollback();
		}
		catch(Exception e){
			rollback();
		}
	}
	@Test
	public void createAssociationMissingPostTest(){
		try{
			beginSession();
			
			AssociationPostTagDao dao = new AssociationPostTagDao();
			
			Tag tag = TagDao.loadTag(tagId);
			Person creator = PersonDao.loadPerson(personId);
			
			dao.setTag(tag);
			dao.setCreator(creator);
						
			dao.create();
			fail("An exception should have been thrown!");
			rollback();
		}
		catch(Exception e){
			rollback();
		}
	}
	
	
	@Test
	public void removeAssociation(){
		try{
			beginSession();
			
			AssociationPostTagDao dao = new AssociationPostTagDao();
			
			Post post = PostDao.loadPost(postId);
			Tag tag = TagDao.loadTag(tagId);
			Person creator = PersonDao.loadPerson(personId);
			
			dao.setTag(tag);
			dao.setPost(post);
			dao.setCreator(creator);
			int originalTagMass = tag.getMass();
			String tagId = tag.getTagId(); 
			
			AssociationPostTag ass = dao.create();
			assertEquals("Tag didnt gain any mass!",originalTagMass+1,TagDao.loadTag(tagId).getMass());
			
			String assId=ass.getGuid();
			
			post = PostDao.loadPost(post.getPostId());
			assertTrue("Pre test condition failed. Post doesnt contain the tag that i want to test removing",
					Helpers.associationListContainsTagAssociation(post.getTagAssociations(),assId));
			
			
			
			commit();
			beginSession();
			
			ass = AssociationPostTagDao.remove(assId);
			
			
			
			commit(); 
			
			beginSession();

			assertEquals("Tag mass didnt return to original after creating and deleting!",originalTagMass,TagDao.loadTag(tagId).getMass());
			
			post = PostDao.loadPost(post.getPostId());
			assertFalse("Tag is still associated with this post",
					Helpers.associationListContainsTagAssociation(post.getTagAssociations(),assId));
			
			///////////////
		
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
		
	}
	
}
