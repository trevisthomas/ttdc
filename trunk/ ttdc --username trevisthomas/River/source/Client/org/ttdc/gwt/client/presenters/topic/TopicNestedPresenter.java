package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter;
import org.ttdc.gwt.client.presenters.post.SearchWithinSubsetPresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.post.PostPanel;
import org.ttdc.gwt.client.uibinder.post.ReviewSummaryListPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TopicNestedPresenter extends BasePresenter<TopicNestedPresenter.View>{
	private HistoryToken lastToken = null;
	
	@Inject
	public TopicNestedPresenter(Injector injector) {
		super(injector, injector.getTopicNestedView());
	}
	
	public interface View extends BaseView{
		HasWidgets postsTarget();
		HasText threadTitle();
		HasWidgets paginationTarget();
		HasWidgets rootTarget();
		HasWidgets searchTarget();
	}
	
	public void init(HistoryToken token){
		boolean update = false;
		if(TopicHelpers.compareHistoryKeyValues(HistoryConstants.POST_ID_KEY, lastToken, token)){
			update = true;
		}
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,1);
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		BatchCommandTool batcher = new BatchCommandTool();
		
		if(!update){
			PostCrudCommand postCmd = new PostCrudCommand();
			postCmd.setPostId(postId);
			postCmd.setLoadRootAncestor(true);
			CommandResultCallback<PostCommandResult> rootReplyCallback = buildTopicRootCallback();
			batcher.add(postCmd, rootReplyCallback);
		}
		
		TopicCommand starterCmd = new TopicCommand();
		starterCmd.setPostId(postId);
		starterCmd.setPageNumber(pageNumber);
		starterCmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY);
		CommandResultCallback<TopicCommandResult> starterListCallback = buildNestedListCallback(postId);
		batcher.add(starterCmd, starterListCallback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		lastToken = token;
	}
	
	
	private CommandResultCallback<TopicCommandResult> buildNestedListCallback(final String postId) {
		view.paginationTarget().clear();
		view.postsTarget().clear();
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList(),PostPresenter.Mode.NESTED_SUMMARY);
				
				view.postsTarget().add(postCollectionPresenter.getWidget());
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				HistoryToken token = TopicHelpers.buildNestedPageToken(postId);
				paginationPresenter.initialize(token, result.getResults());
				view.paginationTarget().add(paginationPresenter.getWidget());
			}
		};
		return replyListCallback;
	}


	private CommandResultCallback<PostCommandResult> buildTopicRootCallback() {
		view.searchTarget().clear();
		view.rootTarget().clear();
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				
				if(post.isMovie()){
					ReviewSummaryListPanel reviewSummaryListPanel = injector.createReviewSummaryListPanel();
					reviewSummaryListPanel.init(post);
					view.rootTarget().add(reviewSummaryListPanel);
				}
				else{
					PostPanel postPanel = injector.createPostPanel();
					postPanel.setPost(post);
					view.threadTitle().setText(result.getPost().getTitle());
					view.rootTarget().add(postPanel.getWidget());
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
