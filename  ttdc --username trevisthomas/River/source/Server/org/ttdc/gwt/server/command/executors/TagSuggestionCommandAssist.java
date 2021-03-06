package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.dao.FastGPostLoader;
import org.ttdc.gwt.server.dao.FilteredPostPaginatedDaoBase;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;

/*
 * I'm only inheriting from the FilteredPost base class to get access to the filter masks
 *
 */
public class TagSuggestionCommandAssist extends FilteredPostPaginatedDaoBase{
	
	public void postBased(TagSuggestionCommand command, Response response) {
		lookupSuggestionsForAutocomplete(command, response, true);
	}
	public void postBasedSearch(TagSuggestionCommand command, Response response) {
		lookupSuggestionsForAutocomplete(command, response, false);
	}
	
	public void lookupSuggestionsForAutocomplete(TagSuggestionCommand command, Response response, boolean create) {
		SuggestOracle.Request request;
		request = command.getRequest();
		if(StringUtils.isEmpty(request.getQuery())){
			return;
		}
				
		long filterMask = buildFilterMask(getFilterFlags());
		
		@SuppressWarnings("unchecked")
		List<String> ids = session().getNamedQuery("TagSuggestion.TopicTitle")
			.setParameter("phrase", request.getQuery()+"%")
			.setParameter("phrase2", "% "+request.getQuery()+"%")
			.setParameter("filterMask", filterMask)
			.setMaxResults(request.getLimit()).list();
		
		FastGPostLoader loader = new FastGPostLoader(null);
		List<GPost> gPosts = loader.fetchPostsForIdsMovieSummary(ids);
		
		List<SuggestionObject> suggestions = new ArrayList<SuggestionObject>();
		
		for(GPost gp : gPosts ){
			String title = PostFormatter.getInstance().format(gp.getTitle());
			addFakeTitleTag(title, gp);
			String highlightedValue = highlightRequestedValue(request.getQuery(), title);

//			String movieYear = "";
//			if(gp.getRoot().isMovie()){
//				movieYear = " - "+gp.getRoot().getPublishYear();
//			}
			
			if(gp.getMass() > 1)
				suggestions.add(new SuggestionObject(gp, highlightedValue /*+ movieYear*/+ " (" + gp.getMass()+")"));
			else
				suggestions.add(new SuggestionObject(gp, highlightedValue /*+ movieYear*/));
		}
		response.setSuggestions(suggestions);
		
		if(StringUtils.isNotEmpty(request.getQuery()) && create)
			suggestions.add(createSugestionForNewPost(request.getQuery()));
		
	}
	
	
	
	private SuggestionObject createSugestionForNewPost(String query){
		GPost post = new GPost();
		addFakeTitleTag(query, post);
		post.setPostId(" ");
		return new SuggestionObject(post, "<b>"+query+" (Create New)</b>");
	}
	
	
	private void addFakeTitleTag(String title, GPost gp) {
		GTag gt = new GTag();
		gt.setValue(title);
		gp.setTitleTag(gt);
	}
	
	public static String highlightRequestedValue(String requestedValue, String actualValue) {
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
	
	private static String highlightQueryString(String value, int startQueryMatch, int endQueryMatch) {
		// return "<b>" + value.substring(0, startQueryMatch)+"</b>" + value.substring(startQueryMatch, endQueryMatch)
		// +"<b>" + value.substring(endQueryMatch)+"</b>";
		return value.substring(0, startQueryMatch) + "<b>" + value.substring(startQueryMatch, endQueryMatch) + "</b>"
				+ value.substring(endQueryMatch);
	}

	
}
