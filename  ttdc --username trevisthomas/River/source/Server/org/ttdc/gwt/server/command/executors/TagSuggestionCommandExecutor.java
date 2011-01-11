package org.ttdc.gwt.server.command.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostSearchDao;
import org.ttdc.gwt.server.dao.TagSearchDao;
import org.ttdc.gwt.server.util.PostFormatter;
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
		Collections.unmodifiableList(Arrays.asList(Tag.TYPE_TOPIC));
	
	@Override
	protected CommandResult execute() {
		TagSuggestionCommand command = (TagSuggestionCommand) getCommand();
		SuggestOracle.Response response = new SuggestOracle.Response();
		
		try {
			switch(command.getMode()){
				case TOPIC_POSTS:
					postBased(command, response);
					break;
				case SEARCH_POSTS:
					postBasedSearch(command, response);
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

	private void postBasedSearch(TagSuggestionCommand command, Response response) {
		SuggestOracle.Request request;
		request = command.getRequest();
		if(StringUtils.isEmpty(request.getQuery())){
			return;
		}
		beginSession();
		
		PostSearchDao dao = new PostSearchDao();
		
		dao.setSearchByTitle(true);
		dao.setPostSearchType(PostSearchType.TOPICS);
		dao.setCurrentPage(1);
		dao.setPageSize(request.getLimit());
		dao.setPhrase(request.getQuery());
		dao.setSortBy(SearchSortBy.POPULARITY);
		dao.setSortDirection(SortDirection.DESC);
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(getPerson()));
		
		PaginatedList<Post> results = dao.search();
		List<SuggestionObject> suggestions = new ArrayList<SuggestionObject>();
		
//		if(results.getList().size() > 0){
//			if(StringUtils.isNotEmpty(request.getQuery()))
//				suggestions.add(createSugestionForSearch(request.getQuery()));
//		}
		
		for(Post post : results.getList() ){
			GPost root = new GPost();
			root.setMetaMask(post.getRoot().getMetaMask());
			root.setPublishYear(post.getRoot().getPublishYear());
			GPost gp = new GPost();
			gp.setRoot(root);
			
			String title = PostFormatter.getInstance().format(post.getTitle());
			addFakeTitleTag(title, gp);
			gp.setPostId(post.getPostId());
			String highlightedValue = highlightRequestedValue(request.getQuery(), title);

			String movieYear = "";
			if(root.isMovie()){
				movieYear = " - "+root.getPublishYear();
			}
			
			if(post.getMass() > 1)
				suggestions.add(new SuggestionObject(gp, highlightedValue + movieYear+ " (" + post.getMass()+")"));
			else
				suggestions.add(new SuggestionObject(gp, highlightedValue + movieYear));
		}
		response.setSuggestions(suggestions);
		
		commit();
		
	}

	private void postBased(TagSuggestionCommand command, Response response) {
		SuggestOracle.Request request;
		request = command.getRequest();
		beginSession();
		
		PostSearchDao dao = new PostSearchDao();
		Person person = PersonDao.loadPerson(getPerson().getPersonId());
		
		dao.setSearchByTitle(true);
		dao.setPostSearchType(PostSearchType.TOPICS);
		dao.setCurrentPage(1);
		dao.setPageSize(request.getLimit());
		dao.setPhrase(request.getQuery());
		dao.setSortBy(SearchSortBy.POPULARITY);
		dao.setSortDirection(SortDirection.DESC);
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(getPerson()));
		
		PaginatedList<Post> results = dao.search();
		List<SuggestionObject> suggestions = new ArrayList<SuggestionObject>();
		for(Post post : results.getList() ){
			GPost root = new GPost();
			root.setMetaMask(post.getRoot().getMetaMask());
			root.setPublishYear(post.getRoot().getPublishYear());
			GPost gp = new GPost();
			gp.setRoot(root);
			addFakeTitleTag(post.getTitle(), gp);
			gp.setPostId(post.getPostId());
			String highlightedValue = highlightRequestedValue(request.getQuery(), post.getTitle());

			String movieYear = "";
			if(root.isMovie()){
				movieYear = " - "+root.getPublishYear();
			}
			
			if(post.getMass() > 1)
				suggestions.add(new SuggestionObject(gp, highlightedValue + movieYear+ " (" + post.getMass()+")"));
			else
				suggestions.add(new SuggestionObject(gp, highlightedValue + movieYear));
		}
		
		if(StringUtils.isNotEmpty(request.getQuery()))
			suggestions.add(createSugestionForNewPost(request.getQuery()));
		response.setSuggestions(suggestions);
		commit();
	}

	private void addFakeTitleTag(String title, GPost gp) {
		GTag gt = new GTag();
		gt.setValue(title);
		gp.setTitleTag(gt);
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

		List<SuggestionObject> suggestions = new ArrayList<SuggestionObject>();
		
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

	private SuggestionObject createSugestionForNewTag(String query){
		GTag gTag = new GTag();
		gTag.setValue(query);
		gTag.setType(Tag.TYPE_TOPIC);
		gTag.setTagId(" ");
		return new SuggestionObject(gTag, "<b>"+query+" (Create New)</b>");
	}
	
	private SuggestionObject createSugestionForNewPost(String query){
		GPost post = new GPost();
		addFakeTitleTag(query, post);
		post.setPostId(" ");
		return new SuggestionObject(post, "<b>"+query+" (Create New)</b>");
	}
	
	private SuggestionObject createSugestionForSearch(String query){
		GPost post = new GPost();
		addFakeTitleTag(query, post);
		post.setPostId(" ");
		return new SuggestionObject(post, "<b> Search for: "+query+"</b>");
	}
	
	private SuggestionObject createDynamicSugestion(String query, Tag tag){
		if(StringUtils.isNotBlank(query)){
			GTag gTag = convertTagToGTag(tag);
			String requestedValue = query;
			String actualValue = gTag.getValue();
			
			String highlightedValue = highlightRequestedValue(requestedValue, actualValue);
			
			return new SuggestionObject(gTag, highlightedValue);
		}
		else{
			GTag gTag = convertTagToGTag(tag);
			return new SuggestionObject(gTag, gTag.getValue());
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
		return  "<b>" + value.substring(0, startQueryMatch)+"</b>" + value.substring(startQueryMatch, endQueryMatch) +"<b>" + value.substring(endQueryMatch)+"</b>";
	}

}
