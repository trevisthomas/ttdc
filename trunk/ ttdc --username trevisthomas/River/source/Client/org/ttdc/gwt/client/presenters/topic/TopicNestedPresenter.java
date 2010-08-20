package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.Mode;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TopicNestedPresenter extends BasePresenter<TopicNestedPresenter.View>{
	private HistoryToken lastToken = null;
	
	@Inject
	public TopicNestedPresenter(Injector injector) {
		super(injector, injector.getTopicNestedView());
	}
	
	public interface View extends BaseView{
		HasWidgets postsTarget();
		HasWidgets paginationTarget();
	}
	
	public void init(HistoryToken token){
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,-1);
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		BatchCommandTool batcher = new BatchCommandTool();
		
		TopicCommand starterCmd = new TopicCommand();
		starterCmd.setPostId(postId);
		starterCmd.setPageNumber(pageNumber);
		starterCmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY);
		CommandResultCallback<TopicCommandResult> starterListCallback = buildNestedListCallback(postId);
		batcher.add(starterCmd, starterListCallback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		lastToken = token;
		
		TopicHelpers.setSourcePostId(postId);
	}
	
	private CommandResultCallback<TopicCommandResult> buildNestedListCallback(final String postId) {
		view.paginationTarget().clear();
		view.postsTarget().clear();
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList(),Mode.NESTED_SUMMARY);
				
				view.postsTarget().add(postCollectionPresenter.getWidget());
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				HistoryToken token = TopicHelpers.buildNestedPageToken(postId);
				paginationPresenter.initialize(token, result.getResults());
				view.paginationTarget().add(paginationPresenter.getWidget());
				
				if(TopicHelpers.getPostComponent() != null){
					//The shameless hack below is to get the window to scroll to the bottom 
					//so that the following line can get the focused post scrolled to the top
					//of the window.
					((Widget)view.paginationTarget()).getElement().scrollIntoView();
					TopicHelpers.getPostComponent().getWidget().getElement().scrollIntoView();
				}
			}
		};
		return replyListCallback;
	}

}
