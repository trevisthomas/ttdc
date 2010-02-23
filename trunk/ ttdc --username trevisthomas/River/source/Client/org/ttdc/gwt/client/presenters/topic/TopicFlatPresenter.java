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

public class TopicFlatPresenter extends BasePresenter<TopicFlatPresenter.View>{
	@Inject
	public TopicFlatPresenter(Injector injector) {
		super(injector, injector.getTopicFlatView());
	}

	public interface View extends BaseView{
		HasWidgets rootTarget();
		HasWidgets postsTarget();
		HasText threadTitle();
		HasWidgets searchTarget();
	}
	String rootId;
	public void init(HistoryToken token){
		rootId = token.getParameter(HistoryConstants.POST_ID_KEY);
		BatchCommandTool batcher = new BatchCommandTool();
		
		TopicCommand topicCmd = new TopicCommand();
		topicCmd.setPostId(rootId);
		topicCmd.setPageNumber(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,1));
		topicCmd.setType(TopicCommandType.FLAT);
		CommandResultCallback<TopicCommandResult> replyListCallback = buildTopicReplyListCallback(rootId);
		batcher.add(topicCmd, replyListCallback);
		
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(rootId);
		postCmd.setLoadRootAncestor(true);
		CommandResultCallback<PostCommandResult> rootPostCallback = buildTopicRootCallback();
		batcher.add(postCmd, rootPostCallback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
	}

	private CommandResultCallback<PostCommandResult> buildTopicRootCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				PostPresenter postPresenter = injector.getPostPresenter();
				postPresenter.setPost(result.getPost());
				view.threadTitle().setText(result.getPost().getTitle());
				view.rootTarget().add(postPresenter.getWidget());
				showSearchWithResults(result.getPost());
			}
		};
		return rootPostCallback;
	}

	private CommandResultCallback<TopicCommandResult> buildTopicReplyListCallback(final String rootId) {
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList());
				view.postsTarget().add(postCollectionPresenter.getWidget());
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				HistoryToken token = TopicHelpers.buildFlatPageToken(rootId);
				paginationPresenter.initialize(token, result.getResults());
				view.postsTarget().add(paginationPresenter.getWidget());
			}
		};
		return replyListCallback;
	}
	
	public void showSearchWithResults(GPost post) {
		SearchBoxPresenter searchPresenter = injector.getSearchBoxPresenter();
		searchPresenter.setPostId(rootId);
		searchPresenter.init();
		view.searchTarget().add(searchPresenter.getWidget());
	}
}
