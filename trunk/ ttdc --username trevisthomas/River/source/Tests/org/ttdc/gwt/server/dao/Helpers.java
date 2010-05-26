package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.gwt.server.util.CalendarBuilder;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class Helpers {
	public final static String userObjectTemplateFlickr = "BA860158-9ADA-41AE-A298-088B07157540";
	public final static String tagGeneralStuff = "293C8189-44B9-41BD-BC75-F3DFD7CF670B";//Tag val:General stuff
	public final static String tagTrevis = "3325CE14-A37E-4236-875C-F1D97F006682"; // Tag val:Trevis
	public final static String tagCorporateGoodness = "3FE5F7A3-F91D-41E3-9225-E2538D59E5C3";//TagVal: Morsels of Corporate Goodness
	public final static String tagCoolGadgets = "8DD7D8E4-4BBA-43B3-925B-2D9AAE4232AE";//cool gadgets tag
	public final static String tagComputersNTech = "1C49C9B5-EB3D-459F-989C-CA1C84596066"; //TagVal: computers and tech
	public final static String tagKimD = "72DC620F-34F2-4F77-A7DC-2E9E747BC0C8";
	public final static String personIdLinten = "B99D6CCA-D1F4-4B58-9316-7AF07B8F341F";
	
	
	public final static String tagLinten = "57512607-DB8F-4604-8187-12877F1A68C5"; 
	
	public final static String rootIdVersion6Live="BCAF5553-27BE-469A-90A5-57CEF155611D";
	
	
	public final static String tagApril = "9e3eb897-6b48-4fba-9e1a-8443d42366bd";
	public final static String tagOSeven = "36938390-9f56-4fed-92a8-371b37450b99";
	public final static String tagOFour = "36DA398F-0AA3-4425-BA9C-66FA02C8C9DC";
	
	public final static String tagInf = "79E55E63-0D09-407A-A357-E057403C1A98";
	public final static String tagNws = "42D4DA1D-F82E-45A5-9166-5A0A3AAC6002";
	public final static String tagReview = "7CD560A1-192C-435A-AB9D-0278CB7A397C";
		
	public final static String personIdTrevis = "50E7F601-71FD-40BD-9517-9699DDA611D6";
	public final static String personIdCSam = "32CF168E-2C06-4A80-924C-9C824C1770D7";
	public final static String personIdAdmin = "3D9871D7-4889-41D1-9E7C-69351C8D022E";
	public final static String personIdMatt = "B9734648-B7B4-43A0-864C-9D0F6A148420";
	
	public static void assertTagged(List<Post> posts, String tagId){
		for(Post post : posts){
			assertTrue("Every post should contain this tag, but one doesnt!", associationListContainsTag(post.getTagAssociations(), tagId.toUpperCase()));
			//assertTagged(post.getPosts(), tagId);
		}
	}
	
	
	
	public static void assertNotTagged(List<Post> posts, List<String> tagIds){
		for(String tagId : tagIds){
			assertNotTagged(posts, tagId);
		}
	}
	
	public static void assertNotTagged(List<Post> posts, String tagId){
		for(Post post : posts){
			assertTrue("These tags should have been fitered out!", !associationListContainsTag(post.getTagAssociations(), tagId.toUpperCase()));
		}
	}
	
	public static void printResults(PaginatedList<Post> results, Logger log){
		for(Post post : results.getList()){
			log.info(post.getCreator().getLogin() +" "+ post.getDate() +" "+ post.getSummary());
		}
	}
	
	public static void printPosts(List<Post> posts, Logger log){
		for(Post post : posts){
			log.info(post.getCreator().getLogin() +" "+ post.getDate() +" "+ post.getSummary());
		}
	}
	public static void printPostPaths(List<Post> posts, Logger log){
		for(Post post : posts){
			log.info(post.getCreator().getLogin() +" "+ post.getDate() +" "+ post.getPostId() + " "+ post.getThread().getPostId() + " " + post.getPath());
		}
	}
	
	
/*	
	public static void printPostsRecursive(List<Post> posts, Logger log){
		for(Post post : posts){
			log.info(post.getCreator().getLogin() +" "+ post.getDate() +" "+ post.getSummary());
			printPostsRecursive(post.getPosts(),log);
		}
	}
	*/
	
	public static boolean associationListContainsTag(List<AssociationPostTag> asses, String tagId){
		for(AssociationPostTag ass : asses){
			if(ass.getTag().getTagId().equals(tagId))
				return true;
		}
		return false;
	}
	
	public static boolean associationListContainsTagAssociation(List<AssociationPostTag> asses, String assId){
		for(AssociationPostTag ass : asses){
			if(ass.getGuid().equals(assId))
				return true;
		}
		return false;
	}
	
	
	public static void assertTagged(Post post, String value){
		for(AssociationPostTag ass : post.getTagAssociations()){
			if(ass.getTag().getValue().equals(value))
				return;
		}
		fail("Post doesnt contain tag: "+value);
	}
	
	public static void assertCreator(PaginatedList<Post> results, String login) {
		for(Post post : results.getList()){
			Assert.assertEquals( login, post.getCreator().getLogin());
		}
	}
	public static void assertNotCreator(PaginatedList<Post> results, String login) {
		for(Post post : results.getList()){
			if(login.equals(post.getCreator().getLogin())){
				fail("Creator "+login+" should have been filtered out, but they exist");
			}
		}
	}
	public static void assertTagHasValueAndType(String type, String value, Tag tag) {
		assertNotNull("Returned tag was null", tag);
		Assert.assertEquals("Tag type is not correct",type,tag.getType());
		Assert.assertEquals("Tag value is not correct",value,tag.getValue());
	}
	
	/**
	 * Is the actual equal to one of the values in the list
	 * 
	 * @param expectedList
	 * @param actual
	 */
	public static void assertEqualsOneOfExpected(List<String> expectedList, String actual){
		boolean found = false;
		for(String expected : expectedList)
			if(expected.equals(actual)){
				found = true;
			}
		String str = "Actual: \"{1}\" is not in expected list: \"{0}\".";
		
		if(!found){
			fail(MessageFormat.format(str,expectedList.toString(),actual));
		}
	}	
	
	/**
	 * 
	 * Is sub in the source? 
	 * 
	 */
	public static void assertContains(String source, String sub){
		int index = source.toUpperCase().indexOf(sub.toUpperCase());
		
		if(index == -1){
			fail(MessageFormat.format("\"{0}\" did not contain \"{1}.\"",source,sub));
		}
	}
	
//	public void assertEquals(int a, int b){
//		Assert.assertEquals(new Integer(a), new Integer(b));
//	}
//	public void assertEquals(String msg, int a, int b){
//		Assert.assertEquals(msg,new Integer(a), new Integer(b));
//	}
	
	public static void assertRootId(String threadId, PaginatedList<Post> results) {
		for(Post post : results.getList()){
			Assert.assertEquals(threadId, post.getRoot().getPostId());
		}
	}
	public static void assertThreadId(String conversationId, PaginatedList<Post> results) {
		for(Post post : results.getList()){
			Assert.assertEquals(conversationId, post.getThread().getPostId());
		}
	}
	
	
	
	public static void assertPostCreatedWithBody(Post post, String body){
		assertNotNull("You didnt even get a result. sorry",post);
		assertTrue("No entry in new post",post.getEntries().size() > 0);
		Entry entry = post.getEntry();
		Assert.assertEquals("Entry doesn't match expected",body,entry.getBody());
		assertTrue(post.getReplyCount() == 0);
	}
	
	public static void assertPostCreatedWithProperPath(Post post){
		Post parent = post.getParent();
		String path = parent.getPath();
		String childpath = post.getPath();
		assertTrue("Path wasnt populated at all ",StringUtils.isNotEmpty(childpath));
		assertTrue(childpath.length() > path.length());
		
		assertTrue("The child path is not in the proper format check it out: [" +childpath+ "] .",childpath.matches("[0-9]{5}(\\.[0-9]{5})*"));
		
	}
	
	
//	public static void assertPostDateTagsCorrect(Post post) {
//		//Tag check
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.setTime(post.getDate());
//		
//		String tagName = Tag.TYPE_DATE_DAY;
//		String expected = ""+cal.get(GregorianCalendar.DAY_OF_MONTH);
//		assertPostTagged(post, tagName, expected);
//		
//		tagName = Tag.TYPE_DATE_MONTH;
//		expected = CalendarBuilder.getMonthName(cal.get(GregorianCalendar.MONTH)+1);
//		assertPostTagged(post, tagName, expected);
//		
//		tagName = Tag.TYPE_DATE_YEAR;
//		expected = ""+cal.get(GregorianCalendar.YEAR);
//		assertPostTagged(post, tagName, expected);
//		
//		tagName = Tag.TYPE_WEEK_OF_YEAR;
//		expected = ""+cal.get(GregorianCalendar.WEEK_OF_YEAR);
//		assertPostTagged(post, tagName, expected);
//	}
//	
//	
//	public static void assertPostTagged(Post post, String tagName, String expectedValue) {
//		AssociationPostTag association = post.loadTagAssociation(tagName);
//		assertNotNull(tagName + " does not exist",association);
//		assertEquals(expectedValue,association.getTag().getValue());
//	}
}
