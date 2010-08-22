package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.home.TabType;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.uibinder.post.PostPanel;
import org.ttdc.gwt.client.uibinder.post.ReviewSummaryListPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

@Deprecated 
/**
 * See NestedPostPanel and TopicPanel
 */
public class TopicPresenter extends BasePagePresenter<TopicPresenter.View> implements PostEventListener{
	private HistoryToken token;
	boolean fireHistoryEvent = false;
	private TopicNestedPresenter nestedPresenter;
	private TopicFlatPresenter flatPresenter;
	
	@Inject
	public TopicPresenter(Injector injector) {
		super(injector, injector.getTopicView());
		EventBus.getInstance().addListener(this);
		
		
	}

	public interface View extends BasePageView{
		HasWidgets topicTarget();
		HasWidgets flatPanel();
		HasWidgets nestedPanel();
		HasText topicTitle();
		HasWidgets searchTarget();
		void addTabSelectionHandler(SelectionHandler<Integer> handler);
		void displayTab(TabType selected);
	}

	@Override
	public void show(HistoryToken token) {
		this.token = token;
		showTopic(token);

		//If there is a specific one on the history list use it, 
		//If not use the for the specific user
		//Else, use a canned default
		String topicView = token.getParameter(HistoryConstants.TAB_KEY);
		if(HistoryConstants.TOPIC_FLAT_TAB.equals(topicView)){
			processDisplayTab(TabType.FLAT);
			processRequestForFlatTab();
		}
		else if(HistoryConstants.TOPIC_NESTED_TAB.equals(topicView)){
			processDisplayTab(TabType.NESTED);
			processRequestForNestedTab();
		}
		else{
			processDisplayTab(TabType.NESTED);// Default. (TODO: Should probably get this from user profile)
			processRequestForNestedTab();
		}
		view.show();
		
		view.addTabSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int index = event.getSelectedItem();
				updateHistoryToReflectTabSelection(index);
			}
		});
	}
	
	private void updateHistoryToReflectTabSelection(int index) {
		token.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1); 
		switch (index){
			case TopicView.INDEX_NESTED:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.TOPIC_NESTED_TAB);
				processRequestForNestedTab();
				break;
			case TopicView.INDEX_FLAT:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.TOPIC_FLAT_TAB);
				processRequestForFlatTab();
				break;
		}
		History.newItem(token.toString(), fireHistoryEvent);
	}

	private void processRequestForFlatTab() {
		if(flatPresenter == null)
			createTopicFlatPresenter(token);
	}

	private void processRequestForNestedTab() {
		if(nestedPresenter == null)
			createTopicNestedPresenter(token);
	}

	private void showTopic(HistoryToken token) {
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(postId);
		postCmd.setLoadRootAncestor(true);
		CommandResultCallback<PostCommandResult> rootReplyCallback = buildTopicRootCallback();
		injector.getService().execute(postCmd, rootReplyCallback);
		
	}

	
	
	private void createTopicNestedPresenter(HistoryToken token) {
		nestedPresenter = injector.getTopicNestedPresenter();
		nestedPresenter.init(token);
		view.nestedPanel().clear();
		view.nestedPanel().add(nestedPresenter.getWidget());
	}

	
	private void createTopicFlatPresenter(HistoryToken token) {
		flatPresenter = injector.getTopicFlatPreseter();
		flatPresenter.init(token);
		view.flatPanel().clear();
		view.flatPanel().add(flatPresenter.getWidget());
	}

	private void processDisplayTab(TabType type){
		view.displayTab(type);
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
			EventBus.reload();
		}
	}
	
	private CommandResultCallback<PostCommandResult> buildTopicRootCallback() {
		view.searchTarget().clear();
		view.topicTarget().clear();
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				
				if(post.isMovie()){
					ReviewSummaryListPanel reviewSummaryListPanel = injector.createReviewSummaryListPanel();
					reviewSummaryListPanel.init(post);
					view.topicTarget().add(reviewSummaryListPanel);
				}
				else{
					PostPanel postPanel = injector.createPostPanel();
					postPanel.setPost(post);
					view.topicTitle().setText(result.getPost().getTitle());
					view.topicTarget().add(postPanel.getWidget());
				}
				showSearchWithResults(post);
			}
		};
		return rootPostCallback;
	}
	
	public void showSearchWithResults(GPost post) {
		SearchBoxPresenter searchPresenter = injector.getSearchBoxPresenter();
		searchPresenter.setPostId(post.getPostId());
		searchPresenter.init();
		view.searchTarget().add(searchPresenter.getWidget());
	}
}
