package org.ttdc.gwt.client.uibinder.search;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.PAGE_NUMBER_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.ROOT_ID_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_CREATOR_ID_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_END_DATE;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_IN_ROOT;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_IN_THREAD;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_VALUE_COMMENTS;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_VALUE_TOPICS;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_VALUE_ALL;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_PHRASE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_START_DATE;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_TAG_ID_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_MODE_TAG;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.TagCloudPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.util.DateRangeLite;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.PaginationNanoPanel;
import org.ttdc.gwt.client.uibinder.shared.PaginationPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SearchSortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchResultsPanel extends BasePageComposite implements SearchDetailListener{
	interface MyUiBinder extends UiBinder<Widget, SearchResultsPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    private final HyperlinkPresenter linkPresenter;
    private StandardPageHeaderPanel pageHeaderPanel;
    private Injector injector;
    
    @UiField(provided = true) Widget pageHeaderElement;
    @UiField (provided = true) Widget pageFooterElement;
    @UiField(provided = true) SimplePanel postListElement = new SimplePanel();
    //@UiField(provided = true) SimplePanel paginationElement = new SimplePanel();
    @UiField(provided = true) Widget paginationElement;
    @UiField(provided = true) Widget paginationNanoElement;
    @UiField Label searchSummaryDetailElement;
    @UiField Label pageResultMessageElement;
    @UiField(provided = true) Hyperlink sortByDateElement;
    @UiField(provided = true) Hyperlink sortByRelevanceElement;
    
    private final PaginationNanoPanel paginationNanoPanel;
    private final PostCollectionPresenter postCollection;
	private final PaginationPanel paginationPanel;
	private HistoryToken lastToken;
	private HyperlinkPresenter sortByDateLinkPresenter;
	private HyperlinkPresenter sortByRelevanceLinkPresenter;
    
    @Inject
    public SearchResultsPanel(Injector injector) { 
    	this.injector = injector;
    	pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	paginationNanoPanel = injector.createPaginationNanoPanel();
    	paginationNanoElement = paginationNanoPanel.getWidget();
    	
    	sortByDateLinkPresenter = injector.getHyperlinkPresenter();
    	
    	sortByRelevanceLinkPresenter = injector.getHyperlinkPresenter();
    	sortByDateElement = sortByDateLinkPresenter.getHyperlink();
    	sortByRelevanceElement = sortByRelevanceLinkPresenter.getHyperlink();
    	
    	postCollection = injector.getPostCollectionPresenter();
    	paginationPanel = injector.createPaginationPanel();
		linkPresenter = injector.getHyperlinkPresenter();
				
		paginationElement = paginationPanel;
		
		pageFooterElement = injector.createStandardFooter().getWidget();
		
    	initWidget(binder.createAndBindUi(this));
    	
    	searchSummaryDetailElement.setText("Loading...");
    	pageHeaderPanel.init("Search","By keyword, by tag, by date, by person");
	}
    
    
    @Override
    public Widget getWidget() {
    	return this;
    }
	
	
	@Override
	protected void onShow(HistoryToken token) {
		lastToken = token;
		//int pageNumber = Integer.parseInt(args.getParameter(PAGE_NUMBER_KEY,"1"));
//		String phrase = token.getParameter(SEARCH_PHRASE_KEY);
		
		
		HistoryToken tokenByDate = new HistoryToken();
		tokenByDate.load(lastToken);
		tokenByDate.addParameter(HistoryConstants.SORT_KEY, HistoryConstants.SORT_BYDATE);
		tokenByDate.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
		sortByDateLinkPresenter.setToken(tokenByDate, "date");
		
		HistoryToken tokenByRelevance = new HistoryToken();
		tokenByRelevance.load(lastToken);
		tokenByRelevance.removeParameter(HistoryConstants.SORT_KEY);
		tokenByRelevance.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
		sortByRelevanceLinkPresenter.setToken(tokenByRelevance, "relevance");
		
		//pageHeaderPanel.getSearchBoxPresenter().setPhrase(phrase); I think that init takes care of this.
		pageHeaderPanel.getSearchBoxPresenter().addSearchDetailListener(this);
		pageHeaderPanel.getSearchBoxPresenter().init(token);
		
		if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_COMMENTS)){
			performSearchForReplies(token);
		}
		else if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_TOPICS)){
			BatchCommandTool batcher = new BatchCommandTool();

			performSearchForCommentsOfType(batcher, token, PostSearchType.TOPICS);
			//performSearchForReplySummary(batcher, token);
			
			injector.getService().execute(batcher.getActionList(), batcher);
		}
		else if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_ALL)){
			BatchCommandTool batcher = new BatchCommandTool();
			
			performSearchForCommentsOfType(batcher, token, PostSearchType.ALL);
			//performSearchForReplySummary(batcher, token);
			
			injector.getService().execute(batcher.getActionList(), batcher);
		}
		else if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_ROOT)){
			performSearchInTopic(token);
		}
		else if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_THREAD)){
			performSearchInConversation(token);
		}
		else if(token.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_TAG)){
			BatchCommandTool batcher = new BatchCommandTool();
			performSearchAllComments(token);
			
			injector.getService().execute(batcher.getActionList(), batcher);
		}
		else{
			throw new RuntimeException("SearchResultsPresenter was told to do something that it didn't understand.");
			//TODO: just redirect them to the real search page.
		}
		//view.show()
	}
	
	private void performSearchForReplySummary(BatchCommandTool batcher, HistoryToken token){
		SearchPostsCommand command = createSearchPostsCommand(token);
		command.setPostSearchType(PostSearchType.ALL);//This was replies but... it's in flux
		
		
		final HistoryToken tokenToExpandResults = new HistoryToken();
		tokenToExpandResults.load(token);
		tokenToExpandResults.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		tokenToExpandResults.setParameter(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_COMMENTS);
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				if(result.getResults().getTotalResults() > 0){
					String linkMsg;
					if(result.getResults().getTotalResults() == 1){
						linkMsg = "View reply matching criteria. ";
					}
					else{
						linkMsg = "Browse " + result.getResults().getTotalResults() + " replies matching search. ";
					}
					linkPresenter.setToken(tokenToExpandResults, linkMsg);
					//expandSearchResultsElement.setVisible(true);
				}
				else{
					//expandSearchResultsElement.setVisible(false);
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
	
	private void performSearchForCommentsOfType(BatchCommandTool batcher, HistoryToken token, PostSearchType type){
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 

		//searchSummaryDetailElement.setText("TODO fix me! 'performSearchForTopics' Searching for content matching "+phrase+"...");
		
//		searchSummaryDetailElement.setText(createSearchMessage(command));
		
		setupSearchCommandForSort(token, command);
		
		command.setPostSearchType(type); // NOT_REPLIES was broken, check PostSearchDao before using

		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				PaginatedList<GPost> results = result.getResults();
				postCollection.setPostList(results.getList());
				
				addSearchResultsToView(phrase, result, "Comments");
			}
		};
		batcher.add(command, callback);
	}
	
	@Override
	public void onSearchDetail(SearchDetail detail) {
		String date = detail.getDateRange().toString();
		String creator = detail.getPerson();
		String tagTitles = detail.getTags();
		String threadTitle = detail.getThreadTitle();
		String phrase = detail.getPhrase();
		
		StringBuilder buff = new StringBuilder();
		if(StringUtil.notEmpty(phrase)){
			if(lastToken.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_ROOT)){
				buff.append("Searching for replies containing \'").append(phrase).append("\'");
			}
			else if(lastToken.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_COMMENTS)){
				buff.append("Searching for replies containing \'").append(phrase).append("\'");
			}
			else if(lastToken.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_VALUE_TOPICS)){
				buff.append("Searching tags, threads and conversations containing \'").append(phrase).append("\'");
			}
			else if(lastToken.isParameterEq(SEARCH_MODE_KEY,SEARCH_MODE_IN_THREAD)){
				buff.append("Searching for replies containing \'").append(phrase).append("\'");
			}
			else{
				buff.append("Searching for \'").append(phrase).append("\'");
			}
		}
		else{
			buff.append("Locating all content");
		}
		
		if(StringUtil.notEmpty(creator)){
			buff.append(" created by ").append(creator);
		}
		
		if(StringUtil.notEmpty(threadTitle)){
			buff.append(" in ").append(threadTitle);
		}
		
		if(StringUtil.notEmpty(tagTitles)){
			buff.append(", tagged ").append(tagTitles);
		}
		
		if(StringUtil.notEmpty(date)){
			buff.append(" added ").append(date);
		}
		
		buff.append(".");
		
		searchSummaryDetailElement.setText(buff.toString());
	}

	private void performSearchForReplies(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		command.setPostSearchType(PostSearchType.REPLIES);
		//searchSummaryDetailElement.setText("TODO fix me 'performSearchForComments'! Searching comments for "+command.getPhrase()+"...");
		final String phrase = command.getPhrase(); 
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result, "Replies");
			}
		};
				
		injector.getService().execute(command, callback);
	}
	
	
	private void performSearchInConversation(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 
		
	//	searchSummaryDetailElement.setText("TODO: fix this too, Searching comments for "+phrase+"...");
		command.setPostSearchType(PostSearchType.ALL);// 8/28/2010 
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result, "In conversation");
			}
		};
		injector.getService().execute(command, callback);
	}
	
	private void performSearchInTopic(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase(); 
		
	//	searchSummaryDetailElement.setText("TODO: fix this too, Searching comments for "+phrase+"...");
		
		command.setPostSearchType(PostSearchType.ALL);// 6/21/2010 i added this.  I was guessing.  Search from a thread didnt work without this
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result, "In topic");
			}
		};
		injector.getService().execute(command, callback);
		
	}
	
	private void performSearchAllComments(HistoryToken token) {
		SearchPostsCommand command = createSearchPostsCommand(token);
		final String phrase = command.getPhrase();
		
		setupSearchCommandForSort(token, command);
		
		command.setPostSearchType(PostSearchType.ALL);
		
		CommandResultCallback<SearchPostsCommandResult> callback = new CommandResultCallback<SearchPostsCommandResult>(){
			public void onSuccess(SearchPostsCommandResult result) {
				addSearchResultsToView(phrase, result, "Comments");
			}
		};
		injector.getService().execute(command, callback);
		
	}


	private void setupSearchCommandForSort(HistoryToken token,
			SearchPostsCommand command) {
		if(token.isParameterEq(HistoryConstants.SORT_KEY, HistoryConstants.SORT_BYDATE)){
			command.setSortOrder(SearchSortBy.BY_DATE);
		}
		else{
			command.setSortOrder(SearchSortBy.RELEVANCE);
		}
		command.setSortDirection(SortDirection.DESC);
	}
	
	private void showPagination(PaginatedList<GPost> results, final HistoryToken topicToken) {
		if(results.calculateNumberOfPages() > 1){
			paginationPanel.initialize(topicToken, results);
			paginationNanoPanel.init(paginationPanel, results);
		}
	}

	private HistoryToken buildHistoryToken(final String phrase) {
		final HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		token.setParameter(SEARCH_MODE_KEY,lastToken.getParameter(SEARCH_MODE_KEY)); //For some reason this method used to take this as an arguement?! 6/21/2010
		if(lastToken.getParameter(HistoryConstants.SORT_DIRECTION_KEY) != null)
			token.setParameter(HistoryConstants.SORT_DIRECTION_KEY, lastToken.getParameter(HistoryConstants.SORT_DIRECTION_KEY));
		if(lastToken.getParameter(HistoryConstants.SORT_KEY) != null)
			token.setParameter(HistoryConstants.SORT_KEY, lastToken.getParameter(HistoryConstants.SORT_KEY));
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
	
	private void addSearchResultsToView(final String phrase, final SearchPostsCommandResult result, final String label) {
		PaginatedList<GPost> results = result.getResults();
		postCollection.setPostList(results.getList());
		
		postListElement.clear();
		postListElement.add(postCollection.getView().getWidget());
		pageResultMessageElement.setText(results.toString());
		
		final HistoryToken topicToken = buildHistoryToken(phrase);
		showPagination(results, topicToken);
		
		searchSummaryDetailElement.setVisible(false);
	}
}
