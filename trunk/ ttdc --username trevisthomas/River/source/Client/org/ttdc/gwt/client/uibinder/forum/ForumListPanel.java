package org.ttdc.gwt.client.uibinder.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ForumListPanel extends BasePageComposite implements PostEventListener, MessageEventListener{
	interface MyUiBinder extends UiBinder<Widget, ForumListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField (provided = true) Widget pageFooterElement;
	@UiField TabPanel topicTabPanelElement;
	private VerticalPanel forumListPanel;
	
	private Map<String,ForumListItemPanel> forumMap = new HashMap<String,ForumListItemPanel>();
	private final StandardPageHeaderPanel pageHeaderPanel;
	private HistoryToken token;
	private String forumId;
	
	@Inject
	public ForumListPanel(Injector injector) {
		this.injector = injector;
		pageFooterElement = injector.createStandardFooter().getWidget();
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
    	forumListPanel = new VerticalPanel();
    	forumListPanel.setStyleName("tt-fill");
		
    	initWidget(binder.createAndBindUi(this));
		
		topicTabPanelElement.add(forumListPanel, "Forums");
		topicTabPanelElement.selectTab(0);
		
		EventBus.getInstance().addListener((PostEventListener)this);
		EventBus.getInstance().addListener((MessageEventListener)this);
	}
	
	@Override
	public void onShow(HistoryToken token) {
		this.token = token;
		forumId = token.getParameter(HistoryConstants.FORUM_ID_KEY);
		createForumList();
	}

	private void updatePageTitle(HistoryToken token) {
		if(token.hasParameter(HistoryConstants.FORUM_ID_KEY)){
			GForum forum = forumMap.get(forumId).getForum();
			if(forum != null){
				pageHeaderPanel.init("Forum: " + forum.getValue(), "browse the "+forum.getMass() + " topics contained within");
			}
			else{
				pageHeaderPanel.init("Forums", "choose a forum to view its topics");
			}
		}
		else{
			pageHeaderPanel.init("Forums", "choose a forum to view its topics");
		}
		pageHeaderPanel.getSearchBoxPresenter().init(token);
	}

	private void createForumList() {
		if(!PresenterHelpers.isWidgetEmpty(forumListPanel)){
			return;
		}
		ForumCommand cmd = new ForumCommand();
		CommandResultCallback<GenericListCommandResult<GForum>> callback = buildForumListCallback();
		injector.getService().execute(cmd, callback);
		
	}

	private CommandResultCallback<GenericListCommandResult<GForum>> buildForumListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GForum>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GForum> result) {
				loadForumList(result.getList());
				updatePageTitle(token);
			}
		};
	}

	private void loadForumList(List<GForum> list) {
		for(GForum forum : list){
			ForumListItemPanel item = injector.createForumListItemPanel();
			item.init(forum);
			forumMap.put(forum.getTagId(),item);
			forumListPanel.add(item);
			
			if(forum.getTagId().equals(forumId)){
				item.expand();
			}
		}
	}
	

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW_FORCE_REFRESH)){
			EventBus.reload();
		}
		else if(postEvent.is(PostEventType.NEW)){
			//EventBus.reload(); 
		}
	}
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.COLAPSE_FORUM_TOPICS)){
			for(ForumListItemPanel forumView : forumMap.values()){
				forumView.colapse();
			}
			//pageFooterElement.getElement().scrollIntoView();
		}
	}

	
}
