package org.ttdc.gwt.client.uibinder.forum;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ForumTopicListCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ForumListItemPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, ForumListItemPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField Anchor forumTitleElement;
    @UiField SimplePanel forumSummaryElement;
    
    private GForum forum;
    private HistoryToken token;
    private String forumId;
    
        
    @Inject
    public ForumListItemPanel(Injector injector) { 
    	//Create stuff
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
    	
	}
    
    public void init(GForum forum){
    	this.forum = forum;
    	//setup token
    	forumTitleElement.setText(forum.getDisplayValue());
    	
    	token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_FORUMS);
		token.addParameter(HistoryConstants.FORUM_ID_KEY, forum.getTagId());
		
    }
    
    @UiHandler("forumTitleElement")
    void onClickShowThreads(ClickEvent event){
    	boolean expand = PresenterHelpers.isWidgetEmpty(forumSummaryElement);
    	MessageEvent colapseEvent = new MessageEvent(MessageEventType.COLAPSE_FORUM_TOPICS, "");
    	EventBus.fireEvent(colapseEvent);
    	if(expand){
    		expand();
    	}
    }

	public void colapse() {
		forumSummaryElement.clear();
	}
    
    private void createTopicList(HistoryToken token) {
    	this.forumId = token.getParameter(HistoryConstants.FORUM_ID_KEY);
		if(forumId != null){
			load();
		}
    }
    
    public void expand(){
    	forumTitleElement.getElement().scrollIntoView();
    	History.newItem(token.toString(), false);	
		createTopicList(token);
    }
    
    private void load() {
    	colapse();
    	forumSummaryElement.add(injector.getWaitPresenter().getWidget());
    	ForumTopicListCommand cmd = new ForumTopicListCommand();
		cmd.setAction(ForumActionType.LOAD_TOPIC_PAGE);
		cmd.setForumId(forumId);
		//cmd.setCurrentPage(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY, 1));
		cmd.setPageSize(-1);
		
		injector.getService().execute(cmd, buildCallback());
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
	//			if(forumId == null){
	//				resultCache = result;
	//			}
	//			else{
	//				resultCache = null;
	//			}
				showResult(result);
			}
		};
		return callback;
	}
	
	private void showResult(PaginatedListCommandResult<GPost> result) {
		PaginatedList<GPost> results = result.getResults();
		colapse();
		if(results.getList().size() == 0){
			forumSummaryElement.add(new Label(results.toString()));
		}
		else{
			VerticalPanel topics = new VerticalPanel();
			topics.setStyleName("tt-fill");
			
			for(GPost post : results.getList()){
				ForumPostPanel forumPostPanel = injector.createForumPostPanel();
				forumPostPanel.init(post);
				topics.add(forumPostPanel);
			}
			
			forumSummaryElement.add(topics);
		}
	}

	public GForum getForum() {
		return forum;
	}
}
