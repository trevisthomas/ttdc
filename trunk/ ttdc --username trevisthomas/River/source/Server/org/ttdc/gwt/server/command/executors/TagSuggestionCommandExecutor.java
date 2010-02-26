package org.ttdc.gwt.server.command.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostSearchDao;
import org.ttdc.gwt.server.dao.TagSearchDao;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SearchSortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;

import static org.ttdc.persistence.Persistence.*;

public class TagSuggestionCommandExecutor extends CommandExecutor<TagSuggestionCommandResult>{
	private final static Logger log = Logger.getLogger(TagSuggestionCommandExecutor.class);
	
	//TODO: Trevis, you need to figure out a way to limit by topics that are titles!
	private final List<String> tagTypeFiltersPostCreate = 
		Collections.unmodifiableList(Arrays.asList(Tag.TYPE_TOPIC)); 
	
	private final List<String> tagTypeFiltersPostView = 
		Collections.unmodifiableList(Arrays.asList(Tag.TYPE_TOPIC));
	
	private final List<String> tagTypeFiltersSearch = 
		Collections.unmodifiableList(Arrays.asList(Tag.TYPE_TOPIC, Tag.TYPE_DATE_MONTH, Tag.TYPE_RELEASE_YEAR, Tag.TYPE_DATE_YEAR, Tag.TYPE_CREATOR));
	
	@Override
	protected CommandResult execute() {
		TagSuggestionCommand command = (TagSuggestionCommand) getCommand();
		SuggestOracle.Response response = new SuggestOracle.Response();
		
		try {
			switch(command.getMode()){
				case TOPIC_POSTS:
					postBased(command, response);
				break;
				default:
					tagBased(command, response);	
				break;
			}

		} catch (Exception e) {
			//throw new RuntimeException(e);
			//Sometimes garbage causes hibernates lucene query parser to throw weird exceptions so i just return the empty list
			log.error(e);
		}

		return new TagSuggestionCommandResult(response);
	}

	private void postBased(TagSuggestionCommand command, Response response) {
		SuggestOracle.Request request;
		request = command.getRequest();
		beginSession();
		
		PostSearchDao dao = new PostSearchDao();
		Person person = PersonDao.loadPerson(getPerson().getPersonId());
		List<String> notTagIds = person.getFilteredTagIds();
		
		dao.setSearchByTitle(true);
		dao.setPostSearchType(PostSearchType.TOPICS);
		dao.setNotTagIdList(notTagIds);
		dao.setCurrentPage(1);
		dao.setPageSize(request.getLimit());
		dao.setPhrase(request.getQuery());
		dao.setSortBy(SearchSortBy.POPULARITY);
		dao.setSortDirection(SortDirection.DESC);
		
		PaginatedList<Post> results = dao.search();
		List<TagSuggestion> suggestions = new ArrayList<TagSuggestion>();
		for(Post post : results.getList() ){
			GPost gp = new GPost();
			gp.setTitle(post.getTitle());
			gp.setPostId(post.getPostId());
			String highlightedValue = highlightRequestedValue(request.getQuery(), post.getTitle());
			if(post.getMass() > 1)
				suggestions.add(new TagSuggestion(gp, highlightedValue + " (" + post.getMass()+")"));
			else
				suggestions.add(new TagSuggestion(gp, highlightedValue));
		}
		
		if(StringUtils.isNotEmpty(request.getQuery()))
			suggestions.add(createSugestionForNewPost(request.getQuery()));
		response.setSuggestions(suggestions);
		commit();
	}

	private void tagBased(TagSuggestionCommand command,	SuggestOracle.Response response) {
		SuggestOracle.Request request;
		request = command.getRequest();

		TagSearchDao dao = new TagSearchDao();
		dao.setPageSize(request.getLimit());
		dao.setPhrase(request.getQuery());

		for (String tagId : command.getExcludeTagIdList()) {
			dao.addTagIdExclude(tagId);
		}
		for (String tagId : command.getUnionTagIdList()) {
			dao.addTagId(tagId);
		}
		for (String type : getTagTypeFilterListForMode(command.getMode())) {
			dao.addFilterForTagType(type);
		}

		if (TagSuggestionCommandMode.CREATE.equals(command.getMode())) {
			dao.setTitlesOnly(true);
		} else {
			dao.setTitlesOnly(false);
		}

		List<TagSuggestion> suggestions = new ArrayList<TagSuggestion>();
		
		if(!StringUtils.isEmpty(request.getQuery()) || command.isLoadDefault()){
			beginSession();

			PaginatedList<Tag> results = dao.search();
			
			for (Tag tag : results.getList()) {
				suggestions.add(createDynamicSugestion(request.getQuery(), tag));
			}
			
			if (!TagSuggestionCommandMode.SEARCH.equals(command.getMode())) {
				suggestions.add(createSugestionForNewTag(request.getQuery()));
			}
			commit();
		}
		response.setSuggestions(suggestions);
	}
	
	List<String> getTagTypeFilterListForMode(TagSuggestionCommandMode mode){
		List<String> filter;
		if(TagSuggestionCommandMode.VIEW.equals(mode)){
			filter = tagTypeFiltersPostView;		
		}
		else if(TagSuggestionCommandMode.CREATE.equals(mode)){
			filter = tagTypeFiltersPostCreate;
		}
		else if(TagSuggestionCommandMode.SEARCH.equals(mode)){
			filter = tagTypeFiltersSearch;
		}
		else{
			throw new RuntimeException("Unknown TagSuggestionCommandMode");
		}
		return filter;
		
	}

	private TagSuggestion createSugestionForNewTag(String query){
		GTag gTag = new GTag();
		gTag.setValue(query);
		gTag.setType(Tag.TYPE_TOPIC);
		gTag.setTagId(" ");
		return new TagSuggestion(gTag, query+" <b>(Create New)</b>");
	}
	
	private TagSuggestion createSugestionForNewPost(String query){
		GPost post = new GPost();
		post.setTitle(query);
		post.setPostId(" ");
		return new TagSuggestion(post, query+" <b>(Create New)</b>");
	}
	
	private TagSuggestion createDynamicSugestion(String query, Tag tag){
		if(StringUtils.isNotBlank(query)){
			GTag gTag = convertTagToGTag(tag);
			String requestedValue = query;
			String actualValue = gTag.getValue();
			
			String highlightedValue = highlightRequestedValue(requestedValue, actualValue);
			
			return new TagSuggestion(gTag, highlightedValue);
		}
		else{
			GTag gTag = convertTagToGTag(tag);
			return new TagSuggestion(gTag, gTag.getValue());
		}
	}

	private String highlightRequestedValue(String requestedValue, String actualValue) {
		if(StringUtils.isBlank(requestedValue))
			return actualValue;
		String lowerCaseValue = actualValue.toLowerCase();
		String lowerCaseQuery = requestedValue.toLowerCase();
		int startQueryMatch = lowerCaseValue.indexOf(lowerCaseQuery);
		int endQueryMatch = requestedValue.length() + startQueryMatch;
		if(startQueryMatch < 0){
			return actualValue;
		}
		String highlightedValue = highlightQueryString(actualValue, startQueryMatch, endQueryMatch);
		return highlightedValue;
	}

	
	private GTag convertTagToGTag(Tag tag) {
		GTag gTag = new GTag(); 
		gTag.setTagId(tag.getTagId());
		gTag.setType(tag.getType());
		gTag.setValue(tag.getValue());
		return gTag;
	}

	private String highlightQueryString(String value, int startQueryMatch, int endQueryMatch) {
		return value.substring(0, startQueryMatch) + "<b>" + value.substring(startQueryMatch, endQueryMatch) +"</b>" + value.substring(endQueryMatch);
	}

}
