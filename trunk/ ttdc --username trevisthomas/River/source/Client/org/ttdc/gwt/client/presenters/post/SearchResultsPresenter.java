package org.ttdc.gwt.client.presenters.post;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.*;

import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.util.DateRangeLite;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchResultsPresenter extends BasePresenter<SearchResultsPresenter.View> {
	private final PostCollectionPresenter postCollection;
	private final SearchBoxPresenter searchPresenter;
	private final PaginationPresenter paginationPresenter;
	private HistoryToken lastToken;
	
	public interface View extends BaseView {
		HasText getSummaryDetail();
		HasWidgets getSiteSearchTarget();
		void refreshResults(Widget resultsWidget);
		HasWidgets getResultsTarget();
		HasWidgets getTagResultsTarget();
		HasWidgets toggleResultsTarget();
		HasWidgets paginationTarget();
		void show();
	}
	
	Date startDate = null;
	Date endDate = null;
	
	@Inject
	public SearchResultsPresenter(Injector injector) {
		super(injector, injector.getSearchResultsView());
		postCollection = injector.getPostCollectionPresenter();
		paginationPresenter = injector.getPaginationPresenter();
		searchPresenter = injector.getSearchBoxPresenter();
		view.getSiteSearchTarget().add(searchPresenter.getWidget());
		//Trevis!? Is this a singleton?
	}
	
	/**
	 * My thinking is that top level presenters have this show method so that a 
	 * presenter can take over the whole screen.
	 * 
	 * @param args
	 */
	public void show(HistoryToken args){
		lastToken = args;
		//int pageNumber = Integer.parseInt(args.getParameter(PAGE_NUMBER_KEY,"1"));
		String phrase = args.getParameter(SEARCH_PHRASE_KEY);
		
		searchPresenter.setPhrase(phrase);
		searchPresenter.init(args);
		
		if(args.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_COMMENTS)){
			performSearchForComments(args);
		}
		else if(args.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_TOPICS)){
			BatchCommandTool batcher = new BatchCommandTool();
			performSearchForTags(batcher, args);
			
			performSearchForTopics(batcher, args);
			performSearchForReplySummary(batcher, args);
			
			getService().execute(batcher.getActionList(), batcher);
		}
		else if(args.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_ROOT)){
			performSearchInTopic(args);
		}
		else if(args.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_THREAD)){
			performSearchInConversation(args);
		}
		else{
			throw new RuntimeException("SearchResultsPresenter was told to do something that it didn't understand.");
			//TODO: just redirect them to the real search page.
		}
		view.show();
	}
	
	

	private void performSearchForComments(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		command.setPostSearchType(PostSearchType.REPLIES);
		view.getSummaryDetail().setText("Searching comments for "+command.getPhrase()+"...");
		final String phrase = command.getPhrase(); 
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result);
			}
		};
				
		getService().execute(command, callback);
	}
	
	
	private void performSearchForReplySummary(BatchCommandTool batcher, HistoryToken token){
		SearchPostsCommand command = createSearchPostsCommand(token);
		command.setPostSearchType(PostSearchType.ALL);//This was replies but... it's in flux
		
		final HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
		final HistoryToken tokenToExpandResults = new HistoryToken();
		tokenToExpandResults.load(token);
		tokenToExpandResults.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		tokenToExpandResults.setParameter(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_COMMENTS);
		
		
		//final HistoryToken token = buildHistoryToken(phrase, SEARCH_MODE_VALUE_COMMENTS);		
		
		
//		SearchPostsCommand command = new SearchPostsCommand();
//		
//		DateRangeLite dateRange = new DateRangeLite(lastToken);
//		command.setStartDate(dateRange.getStartDate());
//		command.setEndDate(dateRange.getEndDate());
//
//		command.setPhrase(phrase);
//		command.setTitleSearch(false);
//		command.setPostSearchType(PostSearchType.ALL);//This was replies but... it's in flux
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				if(result.getResults().getTotalResults() > 0){
					linkPresenter.setToken(tokenToExpandResults, "See " + result.getResults().getTotalResults() + " replies containing \"" + result.getResults().getPhrase() + "\". ");
					view.toggleResultsTarget().add(linkPresenter.getWidget());
				}
				else{
					view.toggleResultsTarget().clear();
				}
			}
		};
		
		batcher.add(command, callback);
	}
	
	/**
	 * Use this method to initialize commands.
	 * @param token
	 * @return
	 */
	private SearchPostsCommand createSearchPostsCommand(HistoryToken token){
		SearchPostsCommand command = new SearchPostsCommand();
		String rootId = token.getParameter(HistoryConstants.ROOT_ID_KEY);
		String threadId = token.getParameter(HistoryConstants.THREAD_ID_KEY);
		int pageNumber = Integer.parseInt(token.getParameter(PAGE_NUMBER_KEY,"1"));
		String phrase = token.getParameter(SEARCH_PHRASE_KEY);
		List<String> tagIds = token.getParameterList(SEARCH_TAG_ID_KEY);
		String creatorId = token.getParameter(SEARCH_CREATOR_ID_KEY);
		
		
		DateRangeLite dateRange = new DateRangeLite(token);
		command.setStartDate(dateRange.getStartDate());
		command.setEndDate(dateRange.getEndDate());
		command.setPhrase(phrase);
		command.setPageNumber(pageNumber);
		command.setTagIdList(tagIds);
		command.setThreadId(threadId);
		command.setRootId(rootId);
		command.setPersonId(creatorId);
		
		return command;
	}
	
	private void performSearchForTopics(BatchCommandTool batcher, HistoryToken token){
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 
		searchPresenter.setPostId(command.getThreadId());
		view.getSummaryDetail().setText("Searching for content matching "+phrase+"...");
		command.setPostSearchType(PostSearchType.TOPICS); // NOT_REPLIES was broken, check PostSearchDao before using

//		SearchPostsCommand command = new SearchPostsCommand();
//		DateRangeLite dateRange = new DateRangeLite(lastToken);
//		command.setStartDate(dateRange.getStartDate());
//		command.setEndDate(dateRange.getEndDate());
//		command.setPhrase(phrase);
//		//command.setTitleSearch(true);
//		command.setPostSearchType(PostSearchType.TOPICS); // NOT_REPLIES was broken, check PostSearchDao before using
//		command.setPageNumber(pageNumber);
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				PaginatedList<GPost> results = result.getResults();
				postCollection.setPostList(results.getList());
				
				view.refreshResults(postCollection.getView().getWidget());
				view.getSummaryDetail().setText(results.toString());
				
				//final HistoryToken topicToken = buildHistoryToken(phrase, SEARCH_MODE_VALUE_TOPICS);
				final HistoryToken topicToken = buildHistoryToken(phrase);
				showPagination(results, topicToken);
			}
		};
		batcher.add(command, callback);
	}
	
	private void performSearchForTags(BatchCommandTool batcher, HistoryToken token){
		view.getTagResultsTarget().clear();
		String phrase = token.getParameter(SEARCH_PHRASE_KEY);
		List<String> tagIds = token.getParameterList(SEARCH_TAG_ID_KEY);
		
		SearchTagsCommand command = new SearchTagsCommand(tagIds);
		DateRangeLite dateRange = new DateRangeLite(token);
		command.setStartDate(dateRange.getStartDate());
		command.setEndDate(dateRange.getEndDate());
		command.setPhrase(phrase);
		
		
		CommandResultCallback<SearchTagsCommandResult> callback = new CommandResultCallback<SearchTagsCommandResult>(){
			@Override
			public void onSuccess(SearchTagsCommandResult result) {
				PaginatedList<GTag> results = result.getResults();
				TagCloudPresenter tagResultPresenter = injector.getTagCloudPresenter();
				if(results.getList().size() > 0){
					tagResultPresenter.setTagList(results.getList());
					view.getTagResultsTarget().add(tagResultPresenter.getWidget());
				}
				else{
					/*shrug*/
				}
			}
		};
				
		batcher.add(command, callback);
	}
	
	private void performSearchInConversation(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 
		searchPresenter.setPostId(command.getThreadId());
		
		view.getSummaryDetail().setText("Searching comments for "+phrase+"...");
		
//		SearchPostsCommand command = new SearchPostsCommand();
//		DateRangeLite dateRange = new DateRangeLite(lastToken);
//		command.setStartDate(dateRange.getStartDate());
//		command.setEndDate(dateRange.getEndDate());
//		command.setPhrase(phrase);
//		command.setPageNumber(pageNumber);
//		command.setTagIdList(tagIds);
//		command.setThreadId(threadId);
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result);
			}
		};
		getService().execute(command, callback);
	}
	
	private void performSearchInTopic(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 
		searchPresenter.setPostId(command.getRootId());
		
		view.getSummaryDetail().setText("Searching comments for "+phrase+"...");
		
//		SearchPostsCommand command = new SearchPostsCommand();
//		DateRangeLite dateRange = new DateRangeLite(lastToken);
//		command.setStartDate(dateRange.getStartDate());
//		command.setEndDate(dateRange.getEndDate());
//		command.setPhrase(phrase);
//		command.setPageNumber(pageNumber);
//		command.setTagIdList(tagIds);
//		command.setRootId(rootId);
		
		command.setPostSearchType(PostSearchType.ALL);// 6/21/2010 i added this.  I was guessing.  Search from a thread didnt work without this
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result);
			}
		};
		getService().execute(command, callback);
		
	}
	
	private void showPagination(PaginatedList<GPost> results, final HistoryToken topicToken) {
		if(results.calculateNumberOfPages() > 1){
			paginationPresenter.initialize(topicToken, results);
			view.paginationTarget().add(paginationPresenter.getWidget());
		}
	}

	private HistoryToken buildHistoryToken(final String phrase) {
		final HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		token.setParameter(SEARCH_MODE_KEY,lastToken.getParameter(SEARCH_MODE_KEY)); //For some reason this method used to take this as an arguement?! 6/21/2010
		if(StringUtil.notEmpty(phrase))
			token.setParameter(SEARCH_PHRASE_KEY,phrase);
		if(lastToken.hasParameter(SEARCH_START_DATE))
			token.setParameter(HistoryConstants.SEARCH_START_DATE, lastToken.getParameter(SEARCH_START_DATE));
		if(lastToken.hasParameter(SEARCH_END_DATE))
			token.setParameter(HistoryConstants.SEARCH_END_DATE, lastToken.getParameter(SEARCH_END_DATE));
		if(lastToken.hasParameter(HistoryConstants.SEARCH_CREATOR_ID_KEY))
			token.setParameter(HistoryConstants.SEARCH_CREATOR_ID_KEY, lastToken.getParameter(SEARCH_CREATOR_ID_KEY));
		if(lastToken.hasParameter(HistoryConstants.ROOT_ID_KEY))
			token.setParameter(HistoryConstants.ROOT_ID_KEY, lastToken.getParameter(ROOT_ID_KEY));
		return token;
	}
	
//	private HistoryToken buildHistoryToken(final String phrase, final String mode) {
//		HistoryToken token = new HistoryToken();
//		token.load(lastToken);
//		token.removeParameter(PAGE_NUMBER_KEY);
//		return token;
//	}
	
	
	
	private void addSearchResultsToView(final String phrase, SearchPostsCommandResult result) {
		PaginatedList<GPost> results = result.getResults();
		postCollection.setPostList(results.getList());
		view.refreshResults(postCollection.getView().getWidget());
		view.getSummaryDetail().setText(results.toString());
		
		//final HistoryToken topicToken = buildHistoryToken(phrase, SEARCH_MODE_VALUE_COMMENTS);
		final HistoryToken topicToken = buildHistoryToken(phrase);
		
		showPagination(results, topicToken);
	}

}
