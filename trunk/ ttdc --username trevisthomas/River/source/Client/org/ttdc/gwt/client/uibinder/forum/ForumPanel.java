package org.ttdc.gwt.client.uibinder.forum;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.home.FlatPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.presenters.util.UnorderedListWidget;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ForumPanel extends BasePageComposite implements PostEventListener{
	interface MyUiBinder extends UiBinder<Widget, ForumPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField (provided = true) Widget pageFooterElement;
	@UiField UnorderedListWidget forumsElement;
	@UiField TabPanel topicTabPanelElement;
	
	private SimplePanel topicsTargetPanel = new SimplePanel();
	
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	private HistoryToken token;
	private FlatPresenter flatPresenter;
	
	@Inject
	public ForumPanel(Injector injector) {
		this.injector = injector;
		pageFooterElement = injector.createStandardFooter().getWidget();
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	flatPresenter = injector.getFlatPresenter();
    	
		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
		topicTabPanelElement.add(topicsTargetPanel, "Topics");
		topicTabPanelElement.selectTab(0);
	}
	
	
	
	@Override
	public void onShow(HistoryToken token) {
		createForumList();
		createTopicList(token);
	}

	private void createTopicList(HistoryToken token) {
		//Load the flat posts.
		this.token = token;
		
		pageHeaderPanel.init("Forums", "choose a forum to view its topics");
		pageHeaderPanel.getSearchBoxPresenter().init(token);
		
//		if(PresenterHelpers.isWidgetEmpty(topicsTargetPanel)){
//			topicsTargetPanel.add(flatPresenter.getWidget());
//		}
		flatPresenter.init(token);
		topicsTargetPanel.clear();
		topicsTargetPanel.add(flatPresenter.getWidget());
		
		
	}



	private void createForumList() {
		ForumCommand cmd = new ForumCommand();
		CommandResultCallback<GenericListCommandResult<GForum>> callback = buildForumListCallback();
		injector.getService().execute(cmd, callback);
		
	}

	private CommandResultCallback<GenericListCommandResult<GForum>> buildForumListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GForum>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GForum> result) {
				loadForumList(result.getList());
			}
		};
	}

	private void loadForumList(List<GForum> list) {
		List<Anchor> links = new ArrayList<Anchor>();
		for(GForum forum : list){
			ForumLink link = new ForumLink(forum);
			links.add(link);
		}
		forumsElement.loadAnchors(links, false);
	}
	
	
	private class ForumLink extends Anchor{
		private final GForum forum;
		private final HistoryToken token;
		public ForumLink(final GForum forum) {
			this.forum = forum;
			
			token = new HistoryToken();
			token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_FORUMS);
			token.addParameter(HistoryConstants.FORUM_ID_KEY, forum.getTagId());
			
			setText(forum.getValue() + " (" + forum.getMass() + ")");
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					History.newItem(token.toString(), false);	
					createTopicList(token);
				}
			});
		}
	}


	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW_FORCE_REFRESH)){
			EventBus.reload();
		}
		else if(postEvent.is(PostEventType.NEW)){
			//This is a bit hacky... i'm just gonna refresh everything for now
			//GPost gPost = postEvent.getSource();
			//if(postEvent.getSource().equals(post))
			//Trevis, figure out a way to determine what post this thread is showing!!! 
			//This implementation will refresh for any new post!
			
			//Decided to disable this. I'm wondering if this is the smoking gun, making random error popups.
			//EventBus.reload(); 
		}
	}
	

	
}
