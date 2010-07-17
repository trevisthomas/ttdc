package org.ttdc.gwt.client.uibinder.search;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_CREATOR_ID_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_PHRASE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_TAG_ID_KEY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.DefaultMessageTextBox;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;



public class SearchBoxPanel extends Composite implements MessageEventListener, DefaultMessageTextBox.EnterKeyPressedListener{
	interface MyUiBinder extends UiBinder<Widget, SearchBoxPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) FocusPanel refineSearchElement = new ClickableIconPanel("tt-clickable-icon-prev");
    @UiField(provided = true) DefaultMessageTextBox searchPhraseElement = new DefaultMessageTextBox("initializing...");
    @UiField(provided = true) FocusPanel goElement = new ClickableIconPanel("tt-clickable-icon-go"); 
    @UiField HTMLPanel parentElement; 
    
    private String rootId;
	private String threadId;
	private String postId;
	private List<String> tagIdList = new ArrayList<String>();
	private List<GTag> tagList = new ArrayList<GTag>();
	private final static PopupPanel controlsPopup = new PopupPanel(false);
	private final RefineSearchPanel refineSearchPanel;
	
	private String threadTitle;
	private String tagTitles;
	
    
    @Inject
    public SearchBoxPanel(Injector injector) { 
    	this.injector = injector;
    	
    	refineSearchPanel = injector.createRefineSearchPanel();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	searchPhraseElement.setDefaultMessage("Enter phrase to perform search");
		
		EventBus.getInstance().addListener(this);
		
		searchPhraseElement.setStyleName("tt-textbox-search");
		refineSearchPanel.setStyleName("tt-search-panel-adv");
		
		controlsPopup.clear();
		controlsPopup.add(refineSearchPanel);
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }

    @UiHandler("goElement")
    public void onClickGo(ClickEvent event){
    	onEnterKeyPressed();
    }
    
    @Override
	public void onEnterKeyPressed() {
		performSearch();
	}
    
    @UiHandler("refineSearchElement")
    public void onClickRefineSearch(ClickEvent event) {
		if(controlsPopup.isShowing()){
			controlsPopup.hide();
		}
		else{
			//Trevis, in the old one it set itself relative to the whole search box, but i didn't have it built yet 
            int left = parentElement.getAbsoluteLeft();
            int top = parentElement.getAbsoluteTop() + parentElement.getOffsetHeight() - 1;
            controlsPopup.setPopupPosition(left, top);

            // Show the popup
			controlsPopup.show();	
		}
	}
    
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.VIEW_CHANGE)){
			hidePopup();
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
		refineSearchPanel.init(token);
		
		BatchCommandTool batcher = new BatchCommandTool();

		threadTitle = "";
		tagTitles = "";
		searchPhraseElement.setActiveText(token.getParameter(SEARCH_PHRASE_KEY));
		
		PostCrudCommand postCmd = new PostCrudCommand();
		if(postId != null){
			postCmd.setPostId(postId);
			batcher.add(postCmd,buildPostCallback());
		}
		
		tagIdList.addAll(token.getParameterList(SEARCH_TAG_ID_KEY));
		
		if(tagIdList.size() > 0){
			SearchTagsCommand command = new SearchTagsCommand(tagIdList);
			batcher.add(command,buildTagListCallback());
		}
		if(tagList.size() > 0){
			extractTagTitles(tagList);
		}
		
		setDefaultMessage();//Will be over ridden if there is more info to show
		
		injector.getService().execute(batcher.getActionList(), batcher);
	}
	
	/**
	 * After setting rootId, threadId's and Filters call init to prep the search box.
	 * 
	 */
	public void init(){
		init(new HistoryToken());
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
	}

	private CommandResultCallback<PostCommandResult> buildPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				threadTitle = post.getTitle();		
				
				if(post.isRootPost()){
					//Nothing to do
					rootId = post.getPostId();
				}
				else if(post.isThreadPost()){
					threadId = post.getPostId();
				}
				else{
					threadId = post.getThread().getPostId();
				}
				
				setDefaultMessage();
			}
		};
		return rootPostCallback;
	}
	

	private void setDefaultMessage() {
		String msg = "";
		
		if(StringUtil.notEmpty(searchPhraseElement.getActiveText())){
			msg += searchPhraseElement.getActiveText();
		}
		
		if(tagTitles.length() > 0){
			if(threadTitle.length() > 0)
				msg += "Search in "+threadTitle+" for posts tagged "+tagTitles;
			else
				msg += "Search in posts tagged "+tagTitles;
		}
		else if(StringUtil.notEmpty(threadTitle)){
			msg += "Search in "+threadTitle+".";
		}
		
		msg += refineSearchPanel.getDateRange().toString();
		
		if(StringUtil.notEmpty(msg)){
			searchPhraseElement.setDefaultMessage(msg);
		}
		
		
	}
	
	private void performSearch(){
		String phrase = searchPhraseElement.getActiveText();
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
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_VALUE_TOPICS);
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
	
	public List<GTag> getTagList() {
		return tagList;
	}

	public void setTagList(List<GTag> tagList) {
		if(tagList != null)
			this.tagList = tagList;
	}
}

