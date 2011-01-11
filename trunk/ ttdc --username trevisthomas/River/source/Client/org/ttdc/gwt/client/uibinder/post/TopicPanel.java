package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TopicPanel extends BasePageComposite implements PostEventListener{
	interface MyUiBinder extends UiBinder<Widget, TopicPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField (provided = true) Widget pageFooterElement;
	@UiField SimplePanel rootPostElement;
	@UiField TabPanel postTabPanelElement;
	
	private SimplePanel threadsElement = new SimplePanel();
	
	private NestedPostPanel nestedPanel;
	private final StandardPageHeaderPanel pageHeaderPanel;
	HistoryToken token;
	
	@Inject
	public TopicPanel(Injector injector) {
		this.injector = injector;
		pageFooterElement = injector.createStandardFooter().getWidget();
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
		postTabPanelElement.add(threadsElement, "Conversations");
		postTabPanelElement.selectTab(0);
	}
	
	
	
	@Override
	public void onShow(HistoryToken token) {
		BatchCommandTool batcher = new BatchCommandTool();
		
		
		showTopic(token, batcher);
		createTopicNestedPresenter(token, batcher);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
	}

	private void showTopic(HistoryToken token, BatchCommandTool batcher) {
		this.token = token;
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(postId);
		postCmd.setLoadRootAncestor(true);
		CommandResultCallback<PostCommandResult> rootReplyCallback = buildTopicRootCallback();
		//injector.getService().execute(postCmd, rootReplyCallback);
		batcher.add(postCmd, rootReplyCallback);
	}

	
	
	private void createTopicNestedPresenter(HistoryToken token, BatchCommandTool batcher) {
		nestedPanel = injector.createNestedPostPanel();
		nestedPanel.init(token, batcher);
		
		threadsElement.clear();
		threadsElement.add(nestedPanel);
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
	
	private CommandResultCallback<PostCommandResult> buildTopicRootCallback() {
		rootPostElement.clear();
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				
				if(post.isMovie()){
					ReviewSummaryListPanel reviewSummaryListPanel = injector.createReviewSummaryListPanel();
					reviewSummaryListPanel.init(post);
					rootPostElement.add(reviewSummaryListPanel);
				}
				else{
					PostPanel postPanel = injector.createPostPanel();
					postPanel.setPost(post);
					rootPostElement.add(postPanel.getWidget());
				}
				//showSearchWithResults(post);
				pageHeaderPanel.init(result.getPost().getTitle(), "this view shows conversations on a topic");
				pageHeaderPanel.getSearchBoxPresenter().init(token);
			}
		};
		return rootPostCallback;
	}
	
	
	
	public void showSearchWithResults(GPost post) {
//		SearchBoxPresenter searchPresenter = injector.getSearchBoxPresenter();
//		searchPresenter.setPostId(post.getPostId());
//		searchPresenter.init();
//		view.searchTarget().add(searchPresenter.getWidget());
	}
	
}
