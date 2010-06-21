package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TopicFlatPresenter extends BasePresenter<TopicFlatPresenter.View>{
	@Inject
	public TopicFlatPresenter(Injector injector) {
		super(injector, injector.getTopicFlatView());
	}

	public interface View extends BaseView{
		HasWidgets postsTarget();
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
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
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
}
