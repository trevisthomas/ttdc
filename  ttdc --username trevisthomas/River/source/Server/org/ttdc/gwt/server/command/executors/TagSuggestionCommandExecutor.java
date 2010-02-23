package org.ttdc.gwt.server.command.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.TagSearchDao;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

import com.google.gwt.user.client.ui.SuggestOracle;
import static org.ttdc.persistence.Persistence.*;

public class TagSuggestionCommandExecutor extends CommandExecutor<TagSuggestionCommandResult>{
	
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
		SuggestOracle.Request request;
		SuggestOracle.Response response;
		try {
			request = command.getRequest();

			// req has request properties that you can use to perform a db
			// search
			// or some other query. Then populate the suggestions up to
			// req.getLimit() and
			// return in a SuggestOracle.Response object.
			response = new SuggestOracle.Response();

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

			if (TagSuggestionCommandMode.POST_CREATE.equals(command.getMode())) {
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

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new TagSuggestionCommandResult(response);
	}
	
	List<String> getTagTypeFilterListForMode(TagSuggestionCommandMode mode){
		List<String> filter;
		if(TagSuggestionCommandMode.POST_VIEW.equals(mode)){
			filter = tagTypeFiltersPostView;		
		}
		else if(TagSuggestionCommandMode.POST_CREATE.equals(mode)){
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
		return new TagSuggestion(gTag, query+" <strong>(Create New)</strong>");
	}
	
	private TagSuggestion createDynamicSugestion(String query, Tag tag){
		if(query != null){
			String lowerCaseValue = tag.getValue().toLowerCase();
			String lowerCaseQuery = query.toLowerCase();
			int startQueryMatch = lowerCaseValue.indexOf(lowerCaseQuery);
			int endQueryMatch = query.length() + startQueryMatch;
			
			GTag gTag = convertTagToGTag(tag);
			
			return new TagSuggestion(gTag, highlightQueryString(gTag.getValue(), startQueryMatch, endQueryMatch));
		}
		else{
			GTag gTag = convertTagToGTag(tag);
			return new TagSuggestion(gTag, gTag.getValue());
		}
	}

	/*
	 * TODO: consider using the tag GenericBeanConverter method for this?
	 * 
	 */
	private GTag convertTagToGTag(Tag tag) {
		GTag gTag = new GTag(); 
		gTag.setTagId(tag.getTagId());
		gTag.setType(tag.getType());
		gTag.setValue(tag.getValue());
		return gTag;
	}

	private String highlightQueryString(String value, int startQueryMatch, int endQueryMatch) {
		return value.substring(0, startQueryMatch) + "<strong>" + value.substring(startQueryMatch, endQueryMatch) +"</strong>" + value.substring(endQueryMatch);
	}
	
	/*
	private boolean isValidSugestionMatch(String value, int startPosition){
		if(startPosition == 0) return true;
		if(startPosition > 0 && value.charAt(startPosition - 1) == ' ') return true;
		return false;
	}
	*/

}
