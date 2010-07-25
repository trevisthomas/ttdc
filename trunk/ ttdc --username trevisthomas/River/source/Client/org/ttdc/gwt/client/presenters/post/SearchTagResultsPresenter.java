package org.ttdc.gwt.client.presenters.post;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.tag.SearchTagListPresenter;
import org.ttdc.gwt.client.presenters.util.DateRangeLite;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.*;

public class SearchTagResultsPresenter extends BasePresenter<SearchTagResultsPresenter.View>{
	private final PostCollectionPresenter postCollection;
	private final SearchTagListPresenter tagListPresenter; 
	private final PaginationPresenter paginationPresenter;
	private HistoryToken lastToken;
	
	
	public interface View extends BaseView {
		HasWidgets getPostResultsTarget();
		HasWidgets getTagResultsTarget();
		HasText getStatusText();
		HasWidgets getSearchWithinResultsTarget();
		HasWidgets paginationTarget();
		void show();
	}
	
	@Inject
	public SearchTagResultsPresenter(Injector injector) {
		super(injector, injector.getSearchTagResultsView());
		postCollection = injector.getPostCollectionPresenter();
		tagListPresenter = injector.getSearchTagListPresenter();
		paginationPresenter = injector.getPaginationPresenter();
	}
	
	public void performTagLookup(BatchCommandTool batcher, List<String> tagIds, int pageNumber){
		SearchTagsCommand command = new SearchTagsCommand(tagIds);
		command.setPageNumber(pageNumber);
		
		DateRangeLite dateRange = new DateRangeLite(lastToken);
		command.setStartDate(dateRange.getStartDate());
		command.setEndDate(dateRange.getEndDate());
		
		//TODO figure out why you dont have to set the connection id?
		
		CommandResultCallback<SearchTagsCommandResult> callback = new CommandResultCallback<SearchTagsCommandResult>(){
			@Override
			public void onSuccess(SearchTagsCommandResult result) {
				PaginatedList<GTag> results = result.getResults();
				
				tagListPresenter.setTagIdList(results.getList());
				view.getTagResultsTarget().clear();
				view.getTagResultsTarget().add(tagListPresenter.getWidget());
			}
		};
		batcher.add(command, callback);
	}
	public void performTagBrowse(BatchCommandTool batcher, final List<String> tagIds,final String phrase, int pageNumber){
		SearchPostsCommand command = new SearchPostsCommand();
		
		DateRangeLite dateRange = new DateRangeLite(lastToken);
		command.setPostSearchType(PostSearchType.ALL);
		command.setStartDate(dateRange.getStartDate());
		command.setEndDate(dateRange.getEndDate());
		
		command.setTagIdList(tagIds);
		command.setPhrase(phrase);
		command.setPageNumber(pageNumber);
		
		//TODO figure out why you dont have to set the connection id?
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				PaginatedList<GPost> results = result.getResults();
				postCollection.setPostList(results.getList());
				view.getPostResultsTarget().clear();
				view.getPostResultsTarget().add(postCollection.getWidget());
				
				view.getStatusText().setText(results.toString());
				
				final HistoryToken topicToken = buildHistoryToken(tagIds, phrase, SEARCH_MODE_VALUE_TOPICS);
				showPagination(results, topicToken);
			}
		};
				
		batcher.add(command, callback);
	}
	
	private HistoryToken buildHistoryToken(final List<String> tagIds, final String phrase, final String mode) {
		final HistoryToken token = new HistoryToken();
		//token.load(lastToken);
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_TAG_RESULTS);
		
		for(String tagId : tagIds){
			token.addParameter(SEARCH_TAG_ID_KEY, tagId);
		}
		if(phrase != null && phrase.trim().length() > 0){
			token.setParameter(SEARCH_PHRASE_KEY,phrase);
		}
		return token;
	}
	
	private void showPagination(PaginatedList<GPost> results, final HistoryToken topicToken) {
		if(results.calculateNumberOfPages() > 1){
			paginationPresenter.initialize(topicToken, results);
			view.paginationTarget().add(paginationPresenter.getWidget());
		}
	}
	
	public void show(HistoryToken args){
		view.show();
		
		lastToken = args;
		
		int pageNumber = Integer.parseInt(args.getParameter(PAGE_NUMBER_KEY,"1"));
		List<String> tagIds = args.getParameterList(SEARCH_TAG_ID_KEY);
		String phrase = args.getParameter(SEARCH_PHRASE_KEY);
		
		SearchBoxPresenter searchPresenter = injector.getSearchBoxPresenter();
		searchPresenter.setTagIdList(tagIds);
		searchPresenter.setPhrase(phrase);
		searchPresenter.init(args);
		view.getSearchWithinResultsTarget().clear();
		view.getSearchWithinResultsTarget().add(searchPresenter.getWidget());
		if(args.hasParameter(SEARCH_TAG_ID_KEY)){
			BatchCommandTool batcher = new BatchCommandTool();
			
			performTagBrowse(batcher, tagIds,phrase,pageNumber);
			performTagLookup(batcher, tagIds, 1); //Trevis.... hmm
			view.getStatusText().setText("Searching tags matching...");
			
			getService().execute(batcher.getActionList(),batcher);
		}
		else{
			//Not sure, probably should send them to the search page
		}
	}
}

