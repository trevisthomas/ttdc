package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class NestedPostPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, NestedPostPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField Anchor sortByReplyDateElement;
	@UiField Anchor sortByThreadDateElement;
	@UiField SimplePanel postContainerElement;
	@UiField SimplePanel paginationElement;
	
	private HistoryToken lastToken = null;
	private GPost rootPost;
	
	@Inject
	public NestedPostPanel(Injector injector) {
		this.injector = injector;
		initWidget(binder.createAndBindUi(this));
		
		sortByReplyDateElement.addStyleName("tt-cursor-pointer");
		sortByThreadDateElement.addStyleName("tt-cursor-pointer");
	}
	
	@UiHandler("sortByReplyDateElement")
	public void onClickSortByReplyDate(ClickEvent event){
		prepTokenForSort(lastToken, HistoryConstants.SORT_BY_REPLY_DATE);
		init(lastToken);
	}
	
	@UiHandler("sortByThreadDateElement")
	public void onClickSortByCreateDate(ClickEvent event){
		prepTokenForSort(lastToken, HistoryConstants.SORT_BY_CREATE_DATE);
		init(lastToken);
	}

	/*
	 * Preparing the token for sort is all about removing things that i dont want to stick around through a resort
	 * like, the selected post that brought you to the thread, and the page number. 
	 */
	private void prepTokenForSort(HistoryToken token, String sortValue) {
		token.setParameter(HistoryConstants.SORT_KEY, sortValue);
		token.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
		if(rootPost != null){
			token.setParameter(HistoryConstants.POST_ID_KEY, rootPost.getPostId());
		}
		History.newItem(token.toString(), false);
	}
	public void init(HistoryToken token){
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,-1);
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		
		BatchCommandTool batcher = new BatchCommandTool();
		
		TopicCommand starterCmd = new TopicCommand();
		starterCmd.setPostId(postId);
		starterCmd.setPageNumber(pageNumber);
		if(token.isParameterEq(HistoryConstants.SORT_KEY, HistoryConstants.SORT_BY_CREATE_DATE)){
			starterCmd.setSortByDate(false);
		}
		else{
			starterCmd.setSortByDate(true);
		}
		starterCmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY);
		CommandResultCallback<TopicCommandResult> starterListCallback = buildNestedListCallback(postId);
		batcher.add(starterCmd, starterListCallback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		lastToken = token;
		
		TopicHelpers.setSourcePostId(postId);
	}
	
	
	
	private CommandResultCallback<TopicCommandResult> buildNestedListCallback(final String postId) {
		paginationElement.clear();
		postContainerElement.clear();
		
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList(),Mode.NESTED_SUMMARY);
				
				postContainerElement.add(postCollectionPresenter.getWidget());
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
//				HistoryToken token = TopicHelpers.buildNestedPageToken(postId);
//				token.addParameter(HistoryConstants.SORT_KEY, lastToken.)
				paginationPresenter.initialize(lastToken, result.getResults());
				paginationElement.add(paginationPresenter.getWidget());
				
				
				if(result.getResults().getList().size() > 0){
					rootPost = result.getResults().getList().get(0).getRoot();
				}
				
				if(TopicHelpers.getPostComponent() != null){
					//The shameless hack below is to get the window to scroll to the bottom 
					//so that the following line can get the focused post scrolled to the top
					//of the window.
					paginationElement.getElement().scrollIntoView();
					TopicHelpers.getPostComponent().getWidget().getElement().scrollIntoView();
					TopicHelpers.getPostComponent().expandPost();
				}
			}
		};
		return replyListCallback;
	}

}
