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
import org.ttdc.gwt.shared.util.PostFlag;
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
		TagSuggestionCommandAssist assist = new TagSuggestionCommandAssist();
		
		try {
			beginSession();
			
			if(!getPerson().isPrivateAccessAccount()){
				assist.addFlagFilter(PostFlag.PRIVATE);
			}
			
			if(!getPerson().isAdministrator()){
				assist.addFlagFilter(PostFlag.DELETED);
			}
			
			switch(command.getMode()){
				case TOPIC_POSTS:
					assist.postBased(command, response);
					break;
				case SEARCH_POSTS:
					assist.postBasedSearch(command, response);
					break;
				default:
					tagBased(command, response);	
					break;
			}
			
			commit();

		} catch (Exception e) {
			//throw new RuntimeException(e);
			//Sometimes garbage causes hibernates lucene query parser to throw weird exceptions so i just return the empty list
			log.error(e);
		}

		return new TagSuggestionCommandResult(response);
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
	
	private SuggestionObject createDynamicSugestion(String query, Tag tag){
		if(StringUtils.isNotBlank(query)){
			GTag gTag = convertTagToGTag(tag);
			String requestedValue = query;
			String actualValue = gTag.getValue();
			
			String highlightedValue = TagSuggestionCommandAssist.highlightRequestedValue(requestedValue, actualValue);
			
			return new SuggestionObject(gTag, highlightedValue);
		}
		else{
			GTag gTag = convertTagToGTag(tag);
			return new SuggestionObject(gTag, gTag.getValue());
		}
	}

	private GTag convertTagToGTag(Tag tag) {
		GTag gTag = new GTag(); 
		gTag.setTagId(tag.getTagId());
		gTag.setType(tag.getType());
		gTag.setValue(tag.getValue());
		return gTag;
	}
}
