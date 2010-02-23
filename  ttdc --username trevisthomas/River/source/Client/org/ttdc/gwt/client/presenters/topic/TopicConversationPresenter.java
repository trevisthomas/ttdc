package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TopicConversationPresenter extends BasePresenter<TopicConversationPresenter.View>{
	private HistoryToken lastToken = null;
	
	@Inject
	public TopicConversationPresenter(Injector injector) {
		super(injector, injector.getTopicConversationView());
	}
	public interface View extends BaseView{
		//HasWidgets conversationsTarget();
		HasWidgets repliesTarget();
		HasWidgets rootTarget();
		HasText threadTitle();
		HasWidgets searchTarget();
		HasWidgets topicRootLink();
	}

	String postId;
	public void init(HistoryToken token){
		boolean update = false;
		if(TopicHelpers.compareHistoryKeyValues(HistoryConstants.POST_ID_KEY, lastToken, token)){
			update = true;
		}
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,1);
		postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		BatchCommandTool batcher = new BatchCommandTool();
		
		if(!update){
			PostCrudCommand postCmd = new PostCrudCommand();
			postCmd.setPostId(postId);
			postCmd.setLoadThreadAncestor(true);
			CommandResultCallback<PostCommandResult> rootReplyCallback = buildTopicRootCallback();
			batcher.add(postCmd, rootReplyCallback);
		}
		
		TopicCommand starterCmd = new TopicCommand();
		starterCmd.setPostId(postId);
		starterCmd.setPageNumber(pageNumber);
		starterCmd.setType(TopicCommandType.CONVERSATION);
		CommandResultCallback<TopicCommandResult> starterListCallback = buildConversationStarterListCallback(postId);
		batcher.add(starterCmd, starterListCallback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		lastToken = token;
	}
	
	private CommandResultCallback<PostCommandResult> buildTopicRootCallback() {
		view.searchTarget().clear();
		view.rootTarget().clear();
		view.topicRootLink().clear();
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				PostPresenter postPresenter = injector.getPostPresenter();
				
				postPresenter.setPost(result.getPost());
				view.threadTitle().setText(result.getPost().getTitle());
				view.rootTarget().add(postPresenter.getWidget());
				showSearchWithResults(result.getPost());
				
				if(!result.getPost().isRootPost()){
					HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
					linkPresenter.setPost(result.getPost().getRoot());
					view.topicRootLink().add(linkPresenter.getWidget());
				}
				else{
					view.topicRootLink().clear();
				}
				
			}
		};
		return rootPostCallback;
	}

	private CommandResultCallback<TopicCommandResult> buildConversationStarterListCallback(final String postId) {
		view.repliesTarget().clear();
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList());
				
				view.repliesTarget().add(postCollectionPresenter.getWidget());
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				HistoryToken token = TopicHelpers.buildConversationPageToken(postId);
				paginationPresenter.initialize(token, result.getResults());
				view.repliesTarget().add(paginationPresenter.getWidget());
				
				
			}
		};
		return replyListCallback;
	}
	
	public void showSearchWithResults(GPost post) {
		SearchBoxPresenter searchPresenter = injector.getSearchBoxPresenter();
		searchPresenter.setPostId(post.getPostId());
		searchPresenter.init();
		view.searchTarget().add(searchPresenter.getWidget());
	}

}
