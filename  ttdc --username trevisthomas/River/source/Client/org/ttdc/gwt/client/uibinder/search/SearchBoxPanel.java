package org.ttdc.gwt.client.uibinder.search;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_CREATOR_ID_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_PHRASE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_TAG_ID_KEY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.MySuggestBox;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.DefaultMessageTextBox;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.comment.CommentEditorPanel;
import org.ttdc.gwt.client.uibinder.post.NewMoviePanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;



public class SearchBoxPanel extends Composite implements MessageEventListener, DefaultMessageTextBox.EnterKeyPressedListener{
	interface MyUiBinder extends UiBinder<Widget, SearchBoxPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) FocusPanel refineSearchElement = new ClickableIconPanel("tt-graphic-button-r","tt-graphic-button-normal","tt-graphic-button-down");
    //@UiField(provided = true) DefaultMessageTextBox searchPhraseElement = new DefaultMessageTextBox("initializing...");
    @UiField(provided = true) DefaultMessageTextBox searchPhraseElement = new DefaultMessageTextBox("initializing...");
    @UiField(provided = true) FocusPanel goElement = new ClickableIconPanel("tt-graphic-button-l","tt-graphic-button-normal","tt-graphic-button-down");
    @UiField(provided = true) FocusPanel clearCriteriaElement = new ClickableIconPanel("tt-graphic-button-r","tt-graphic-button-normal","tt-graphic-button-down");
//    @UiField(provided = true) FocusPanel commentElement = new ClickableIconPanel("tt-clickable-icon-comment");
//    @UiField(provided = true) FocusPanel movieElement = new ClickableIconPanel("tt-clickable-icon-movie");
    @UiField Anchor commentElement;
	@UiField Anchor movieElement;    
	@UiField Anchor markReadElement;
    @UiField TableElement parentElement; 
    
    //private DefaultMessageTextBox standardSearchBox = new DefaultMessageTextBox("initializing...");
    
    @UiField (provided=true) SuggestBox suggestSearchPhraseElement;
    private MySuggestBox suggestSearchBox;
    
	private SugestionOracle suggestionOracle;
    
    private String rootId;
	private String threadId;
	private String postId;
	private List<String> tagIdList = new ArrayList<String>();
//	private List<GTag> tagList = new ArrayList<GTag>();
	private final static PopupPanel controlsPopup = new PopupPanel(false);
	private final RefineSearchPanel refineSearchPanel;
	
	
	private String threadTitle;
	private String tagTitles;
	private Object activeWidget;
	
	private SearchDetailListenerSmartCollection searchDetailListenerCollection;
    private final static String DEFAULT_SEARCH_MSG = "Enter phrase to perform search";
    
    boolean ignoreAutoComplete = false;
    boolean suggest = true;
    
    @Inject
    public SearchBoxPanel(Injector injector) { 
    	this.injector = injector;
    	
    	searchDetailListenerCollection = new SearchDetailListenerSmartCollection();
    	
    	refineSearchPanel = injector.createRefineSearchPanel();
    	
    	suggestionOracle = injector.getTagSugestionOracle();
    	suggestSearchBox = (MySuggestBox)suggestionOracle.createSuggestBoxForPostSearch();
    	suggestSearchPhraseElement = suggestSearchBox;
    	
    	suggestSearchBox.setStyleName("tt-textbox-search");
    	suggestSearchBox.setDefaultMessage(DEFAULT_SEARCH_MSG);
    	
    	
    	initWidget(binder.createAndBindUi(this));
    	searchPhraseElement.setStyleName("tt-textbox-search");
    	searchPhraseElement.setDefaultMessage(DEFAULT_SEARCH_MSG);
    	searchPhraseElement.addEnterKeyPressedListener(this);
    	
    	
		
		EventBus.getInstance().addListener(this);
		refineSearchPanel.setSearchDetailListenerCollection(searchDetailListenerCollection);
		
		refineSearchPanel.setStyleName("tt-search-panel-adv");
		
		controlsPopup.setStyleName("tt-search-popup");
		
		//new Label("Advanced")
		HTMLPanel downArrow = new HTMLPanel("&nbsp;");
		downArrow.setStyleName("tt-icon-tall tt-icon-tall-expand-arrow");
		refineSearchElement.add(downArrow);
		
		clearCriteriaElement.add(new Label("Clear"));
//		commentElement.add(new Label("Comment"));
//		movieElement.add(new Label("Movie"));
		goElement.add(new Label("Search"));
		
		commentElement.setText("comment");
    	movieElement.setText("movie");
    	markReadElement.setText("mark read");
    	//readElement.setText("mark read");
    	commentElement.addStyleName("tt-cursor-pointer");
    	movieElement.addStyleName("tt-cursor-pointer");
    	markReadElement.addStyleName("tt-cursor-pointer");
    	
    	suggestSearchBox.setVisible(false);
		
    	if(ConnectionId.isAnonymous()){
    		markReadElement.setVisible(false);
    	}
    	setupSuggestionHandler();
	}
    
    
    
    private void setupSuggestionHandler() {
    	ignoreAutoComplete = false;
    	suggestSearchBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				if(ignoreAutoComplete)
					return;
				SuggestionObject suggestion = suggestionOracle.getCurrentSuggestion();
				if(suggestion != null){
					GPost post = suggestion.getPost();
					if(!post.getPostId().trim().isEmpty()){
						HistoryToken token = new HistoryToken();
						token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
						token.addParameter(HistoryConstants.POST_ID_KEY, post.getPostId());
						
						EventBus.fireHistoryToken(token);
					}
					else{
						performSearch(post.getTitle());
					}
				}
				else{
					throw new RuntimeException("Suggestion came back null. Bad juju, this shouldnt happen.");
				}
			}
		});
    	
    	suggestSearchPhraseElement.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					ignoreAutoComplete = true;
					suggestionOracle.killdashnine();
					performSearch(suggestSearchBox.getActiveText());
				}
			}
		});
	}

	@Override
    public Widget getWidget() {
    	return this;
    }

    @UiHandler("goElement")
    public void onClickGo(ClickEvent event){
    	onEnterKeyPressed();
    }
    
    @UiHandler("clearCriteriaElement")
    public void onClearCriteria(ClickEvent event){
    	clearCriteria();
    }
    
    private void clearCriteria() {
    	postId = null;
    	rootId = null;
    	threadId = null;
    	postId = null;
    	threadTitle = null;
    	tagTitles = null;
    	tagIdList = new ArrayList<String>();
    	searchPhraseElement.setDefaultMessage(DEFAULT_SEARCH_MSG);
    	suggestSearchBox.setDefaultMessage(DEFAULT_SEARCH_MSG);
    	suggest = true;
    	init(new HistoryToken());		
	}

	@Override
	public void onEnterKeyPressed() {
		performSearch(searchPhraseElement.getActiveText());
	}
    
    
    
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.VIEW_CHANGE)){
			hidePopup();
		}
		else if(event.is(MessageEventType.SEARCH_CRITERIA_UPDATED)){
			setDefaultMessage();
		}
	}
	
	public void hidePopup() {
		if(controlsPopup.isShowing()){
			controlsPopup.hide();
		}	
	}
	
	public void init(Date startDate, Date endDate) {
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.SEARCH_START_DATE, startDate.getTime());
		token.setParameter(HistoryConstants.SEARCH_END_DATE, endDate.getTime());
		
		init(token);
	}
	
	public void init(HistoryToken token){
		if(token.hasParameter(HistoryConstants.SEARCH_START_DATE) || token.hasParameter(HistoryConstants.SEARCH_CREATOR_ID_KEY)){
			suggest = false;
		}
		refineSearchPanel.init(token);
		searchDetailListenerCollection.setDateRange(refineSearchPanel.getDateRange());
		
		BatchCommandTool batcher = new BatchCommandTool();

		threadTitle = "";
		tagTitles = "";
		String phrase = token.getParameter(SEARCH_PHRASE_KEY);
		searchPhraseElement.setActiveText(phrase);
		searchDetailListenerCollection.setPhrase(phrase);
		PostCrudCommand postCmd = new PostCrudCommand();
		
		if(postId == null){
			if(token.hasParameter(HistoryConstants.ROOT_ID_KEY)){
				postId = token.getParameter(HistoryConstants.ROOT_ID_KEY);
			}
			else if(token.hasParameter(HistoryConstants.THREAD_ID_KEY)){
				postId = token.getParameter(HistoryConstants.THREAD_ID_KEY);
			}
			else if(token.hasParameter(HistoryConstants.POST_ID_KEY)){
				postId = token.getParameter(HistoryConstants.POST_ID_KEY);
			}
		}
		
		if(postId != null){
			suggest = false;
			postCmd.setPostId(postId);
			batcher.add(postCmd,buildPostCallback());
		}
		else{
			searchDetailListenerCollection.setThreadTitle("");
		}
		
		tagIdList.addAll(token.getParameterList(SEARCH_TAG_ID_KEY));
		
		if(tagIdList.size() > 0){
			suggest = false;
			SearchTagsCommand command = new SearchTagsCommand(tagIdList);
			batcher.add(command,buildTagListCallback());
		}
		else{
			searchDetailListenerCollection.setTags("");
		}
//		if(tagList.size() > 0){
//			extractTagTitles(tagList);
//		}
		
		setDefaultMessage();//Will be over ridden if there is more info to show
		
		injector.getService().execute(batcher.getActionList(), batcher);
		
		setupPopups();
		
		
		suggestSearchBox.setActiveText(phrase);
		suggestSearchBox.setVisible(suggest);
		searchPhraseElement.setVisible(!suggest);
		
	}
	private CommentEditorPanel commentEditorPanel;
	private NewMoviePanel newMoviePanel;
	
	private void setupPopups() {
		commentEditorPanel = injector.createCommentEditorPanel();
		commentEditorPanel.addCancelClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				performCancel();
			}
		});
		
		commentEditorPanel.init(CommentEditorPanel.Mode.CREATE, null);
		
		newMoviePanel = injector.createNewMoviePanel();
		newMoviePanel.addCancelClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				performCancel();
			}
		});
		newMoviePanel.init();
		
	}

	/**
	 * After setting rootId, threadId's and Filters call init to prep the search box.
	 * 
	 */
	public void init(){
		init(new HistoryToken());
	}
	
	public void init(GPerson person){
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.SEARCH_CREATOR_ID_KEY, person.getPersonId());
		init(token);
	}
	
	private void performCancel() {
		if(controlsPopup.isShowing()){
			controlsPopup.hide();
			activeWidget = null;
		}
		setupPopups();
	}
	
	
	@UiHandler("movieElement")
	public void onClickMovieEditor(ClickEvent event){
		if(activeWidget == newMoviePanel){
			performCancel();
		}
		else{
			showPopup(newMoviePanel);	
		}
	}
	
	@UiHandler("commentElement")
	public void onClickCommentEditor(ClickEvent event){
		if(activeWidget == commentEditorPanel){
			performCancel();
		}
		else{
			showPopup(commentEditorPanel);	
		}
	}
	
	@UiHandler("markReadElement")
	public void onClickMarkRead(ClickEvent event){
		PersonCommand cmd = new PersonCommand(ConnectionId.getInstance().getCurrentUser().getPersonId(),
				PersonStatusType.MARK_SITE_READ);
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, createStatusUpdateCallback());
	}
	//Duplicated in HomePanel
	private CommandResultCallback<GenericCommandResult<GPerson>> createStatusUpdateCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				//EventBus.reload();//TODO: probably should come up with a way to just refresh the parts i care about!
				EventBus.fireEvent(new MessageEvent(MessageEventType.MARK_SITE_READ, "Nothing"));
			}
		};
	}
	
	@UiHandler("refineSearchElement")
    public void onClickRefineSearch(ClickEvent event) {
		if(activeWidget == refineSearchPanel){
			performCancel();
		}
		else{
			showPopup(refineSearchPanel);	
		}
	}

	private void showPopup(Widget panel) {
		activeWidget = panel;
		controlsPopup.clear();
		controlsPopup.add(panel);
		//Trevis, in the old one it set itself relative to the whole search box, but i didn't have it built yet 
		int left = parentElement.getAbsoluteLeft();
		int top = parentElement.getAbsoluteTop() + parentElement.getOffsetHeight() - 1;
		String width = parentElement.getWidth();
		int w = parentElement.getOffsetWidth();
		panel.setWidth(w-32+"px");// the -32 is a hack to compensate for padding, borders and what not 
		controlsPopup.setPopupPosition(left, top);

		// Show the popup
		controlsPopup.show();
	}
	
	private CommandResultCallback<SearchTagsCommandResult> buildTagListCallback(){
		CommandResultCallback<SearchTagsCommandResult> callback = new CommandResultCallback<SearchTagsCommandResult>(){
			@Override
			public void onSuccess(SearchTagsCommandResult result) {
				List<GTag> list = result.getResults().getList();
				extractTagTitles(list);
				setDefaultMessage();
				tagIdList = new ArrayList<String>();
				for(GTag tag : list){
					tagIdList.add(tag.getTagId());
				}
			}
		};
		return callback;
	}
	
	private void extractTagTitles(List<GTag> list) {
		StringBuilder sb = new StringBuilder();
		for(GTag tag : list){
			if(sb.length() > 0)
				sb.append(",");
			sb.append(tag.getValue());
		}
		tagTitles = sb.toString();
		searchDetailListenerCollection.setTags(tagTitles);
	}

	private CommandResultCallback<PostCommandResult> buildPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				threadTitle = post.getTitle();		
				
				searchDetailListenerCollection.setThreadTitle(threadTitle);
				
				rootId = post.getRoot().getPostId();
				
//				This code was for allowing sub conversation searching which i am removing for now
//				if(post.isRootPost()){
//					//Nothing to do
//					rootId = post.getPostId();
//				}
//				else if(post.isThreadPost()){
//					threadId = post.getPostId();
//				}
//				else{
//					threadId = post.getThread().getPostId();
//				}
				
				setDefaultMessage();
			}
		};
		return rootPostCallback;
	}
	

	private void setDefaultMessage() {
		String msg = "";
		
		if(StringUtil.notEmpty(searchPhraseElement.getActiveText())){
			msg += " for " + searchPhraseElement.getActiveText();
		}
		
		if(tagTitles != null && tagTitles.length() > 0){
			if(threadTitle.length() > 0)
				msg += "in "+threadTitle+" for posts tagged "+tagTitles;
			else
				msg += "in posts tagged "+tagTitles;
			
		}
		else if(StringUtil.notEmpty(threadTitle)){
			msg += " in "+threadTitle+".";
		}
		
		msg += refineSearchPanel.getDateRange().toString();
		
		if(StringUtil.notEmpty(refineSearchPanel.getPersonName())){
			if(threadTitle.length() == 0){
				msg = "search for content";
			}
			msg += " by " + refineSearchPanel.getPersonName();
		}
		
		if(StringUtil.notEmpty(msg)){
			searchPhraseElement.setDefaultMessage(msg);
		}
	}
	
	
	private void performSearch(final String phrase){
		
		HistoryToken token = new HistoryToken();
		
		String creatorId = refineSearchPanel.getSelectedCreatorId();
		
		refineSearchPanel.addSelectedDateRangeToToken(token);
	
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		if(tagIdList.size() > 0){
			for(String tagId : tagIdList){
				token.addParameter(SEARCH_TAG_ID_KEY, tagId);
			}
		}

		if(StringUtil.notEmpty(creatorId)){
			token.addParameter(SEARCH_CREATOR_ID_KEY, creatorId);
		}
		
		if(StringUtil.notEmpty(rootId)){
			token.addParameter(HistoryConstants.ROOT_ID_KEY, rootId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_ROOT);
		}
		else if(StringUtil.notEmpty(threadId)){
			token.addParameter(HistoryConstants.THREAD_ID_KEY, threadId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_THREAD);
		}
		else{
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_VALUE_ALL);
		}
		
		if(StringUtil.notEmpty(phrase)){
			token.setParameter(SEARCH_PHRASE_KEY, phrase);
		}
				
		EventBus.fireHistoryToken(token);
		
	}


	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public void setPhrase(String phrase) {
		searchPhraseElement.setActiveText(phrase);
	}

	public List<String> getTagIdList() {
		return tagIdList;
	}
	
	public void setTagIdList(List<String> tagIdList) {
		if(tagIdList != null)
			this.tagIdList = tagIdList;
	}
	
	public void addTagIdFilter(String tagId){
		tagIdList.add(tagId);
	}
	
	public void removeTagIdFilter(String tagId){
		tagIdList.remove(tagId);
	}
	
	public void addSearchDetailListener(SearchDetailListener listener) {
		searchDetailListenerCollection.addListener(listener);
	}
}

