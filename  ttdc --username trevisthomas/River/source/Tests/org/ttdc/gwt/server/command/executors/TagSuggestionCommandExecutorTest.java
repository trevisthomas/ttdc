package org.ttdc.gwt.server.command.executors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;
import org.ttdc.persistence.objects.Person;

import static org.junit.Assert.assertFalse;
import static org.ttdc.gwt.server.dao.Helpers.*;

import com.google.gwt.user.client.ui.SuggestOracle;

import static junit.framework.Assert.*;

public class TagSuggestionCommandExecutorTest{
	private final static Logger log = Logger.getLogger(TagSuggestionCommandExecutorTest.class);
	
	
	/**
	 * Show all topic tags, allow user to create a new tag if it doesn't exist.
	 * Exclude the tags currently on the post.
	 *  
	 * 
	 */
	@Test
	public void testInViewMode(){
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("mors");
		
		//MODE_POST_CREATE, MODE_SEARCH
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.VIEW, request);
		cmd.addTagIdExclude(tagCorporateGoodness);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue(tags.size() > 0);
		
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.VIEW), t.getType());
			assertContains(t.getValue(),request.getQuery());
			log.info(t.getTagId()+" "+t.getValue());
			assertFalse(" Tag : "+t.getValue()+" should have been excluded!", t.getTagId().equals(tagCorporateGoodness));
		}
	}
	
	
	/**
	 * Create will be used for creating the title of a new thread.  A distinction should be made if the tag is
	 * not currently used as a title.
	 *   
	 */
	@Test
	public void testInCreateMode(){
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("mors");
		
		//MODE_POST_CREATE, MODE_SEARCH
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.VIEW, request);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue(tags.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.CREATE), t.getType());
			assertContains(t.getValue(),request.getQuery());
			log.info(t.getTagId()+" "+t.getValue());
			if(suggestion.getDisplayString().equals(request.getQuery() + " <strong>(Create New)</strong>")){
				createOption = true;
			}
		}
		assertTrue("No option to create the tag as added. It should be for this type of search",createOption);
	}
	
	@Test
	public void testTagSearch(){
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("mors");
		
		//MODE_POST_CREATE, MODE_SEARCH
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.SEARCH, request);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue(tags.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.SEARCH), t.getType());
			assertContains(t.getValue(),request.getQuery());
			log.info(t.getTagId()+" "+t.getValue());
			
			//Just making sure that it's *not* here.
			if(suggestion.getDisplayString().equals(request.getQuery() + " <strong>(Create New)</strong>")){
				createOption = true;
			}
		}
		assertFalse("An option to create should not come up in search",createOption);
	}
	@Test
	public void testTagSearchDefault(){
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("");
		
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.SEARCH, request);
		
		cmd.setLoadDefault(true);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue("Found nothing and expected stuff. ", tags.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.SEARCH), t.getType());
			log.info(t.getTagId()+" "+t.getValue());
			
		}
		
	}
	
	
	@Test
	public void testTagSearchUnionError1(){
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("trevis");
		
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.SEARCH, request);
		cmd.addTagIdUnion("3FE5F7A3-F91D-41E3-9225-E2538D59E5C3");		
		cmd.setLoadDefault(true);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue("Found nothing and expected stuff. ", tags.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.SEARCH), t.getType());
			log.info(t.getTagId()+" "+t.getValue());
			
		}
		
	}
	
	@Test
	public void testTagSearchTitleError1(){
		
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("t");
		
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.CREATE, request);
		//cmd.addTagIdUnion("3FE5F7A3-F91D-41E3-9225-E2538D59E5C3");		
		cmd.setLoadDefault(true);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> tags  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue("Found nothing and expected stuff. ", tags.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : tags){
			GTag t = suggestion.getTag();
			assertTrue(StringUtils.isNotEmpty(t.getTagId()));
			
			assertEqualsOneOfExpected(cmdexec.getTagTypeFilterListForMode(TagSuggestionCommandMode.SEARCH), t.getType());
			log.info(t.getTagId()+" "+t.getValue());
			
		}
		
	}
	
	@Test
	public void testPostSearch(){
		
		Person anon = InitConstants.ANONYMOUS;//Just priming the machnine
		
		SuggestOracle.Request request = new SuggestOracle.Request(); 
		
		request.setQuery("t");
		
		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.TOPIC_POSTS, request);
		//cmd.addTagIdUnion("3FE5F7A3-F91D-41E3-9225-E2538D59E5C3");		
		cmd.setLoadDefault(true);
		
		TagSuggestionCommandExecutor cmdexec = (TagSuggestionCommandExecutor)CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		TagSuggestionCommandResult result = (TagSuggestionCommandResult)cmdexec.executeCommand();
		
		List<TagSuggestion> suggestions  = (List<TagSuggestion>)result.getResponse().getSuggestions();
		
		assertTrue("Found nothing and expected stuff. ", suggestions.size() > 0);
		
		boolean createOption = false;
		for(TagSuggestion suggestion : suggestions){
			//GTag t = suggestion.getTag();
			//assertTrue(suggestion.getTag() == null);
			GPost post = suggestion.getPost();
			assertNotNull("Post came back null!",post);
			assertTrue("Post id was blank",StringUtils.isNotEmpty(post.getPostId()));
			assertTrue("The display string was blank...?",StringUtils.isNotBlank(suggestion.getDisplayString()));
			assertTrue("The replacement string was blank...?",StringUtils.isNotBlank(suggestion.getReplacementString()));
			//assertEquals(suggestion.getDisplayString().indexOf(post.getTitle()));
			
		}
		
	}
	
}
