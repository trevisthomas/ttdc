package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.shared.PageSizeComponent;
import org.ttdc.gwt.client.uibinder.shared.PageType;
import org.ttdc.gwt.client.uibinder.shared.PaginationNanoPanel;
import org.ttdc.gwt.client.uibinder.shared.PaginationPanel;
import org.ttdc.gwt.client.uibinder.shared.SortOrderComponent;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class NestedPostPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, NestedPostPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField HTMLPanel contentPanelElement;
	@UiField SimplePanel pageSizeElement;
	@UiField SimplePanel sortOrderElement;
	
	@UiField SimplePanel postContainerElement;
	@UiField SimplePanel paginationElement;
	@UiField(provided = true) Widget paginationNanoElement;
	@UiField Label pageSummaryLabel;
	private final PaginationNanoPanel paginationNanoPanel;
	private PageSizeComponent pageSizeComponent; 
	private SortOrderComponent sortOrderComponent;
	
	private PaginationPanel paginationPanel;
	
	
	private HistoryToken lastToken = null;
	private GPost rootPost;
	
	@Inject
	public NestedPostPanel(Injector injector) {
		//paginationElement.add(paginationPanel);
		
		this.injector = injector;
		sortOrderComponent = injector.createSortOrderComponent();
		pageSizeComponent = injector.createPageSizeComponent();
		paginationNanoPanel = injector.createPaginationNanoPanel();
    	paginationNanoElement = paginationNanoPanel.getWidget();
    	
    	initWidget(binder.createAndBindUi(this));

    	pageSizeElement.add(pageSizeComponent);
    	sortOrderElement.add(sortOrderComponent);
	}
	
	public void init(HistoryToken token){
		BatchCommandTool batcher = new BatchCommandTool();
		init(token, batcher);
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
	}
	public void init(HistoryToken token, BatchCommandTool batcher){
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,-1);
		final String postId = token.getParameter(HistoryConstants.POST_ID_KEY);
		pageSizeComponent.init(token, PageType.TOPIC);
		sortOrderComponent.init(token, PageType.TOPIC);
		
		TopicCommand starterCmd = new TopicCommand();
		starterCmd.setPostId(postId);
		starterCmd.setPageNumber(pageNumber);
		
		starterCmd.setPageSize(pageSizeComponent.getRecordsPerPage());
		starterCmd.setSortOrder(sortOrderComponent.getSortOrder());
		pageSummaryLabel.setText("");
		
		starterCmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY);
		CommandResultCallback<TopicCommandResult> starterListCallback = buildNestedListCallback(postId);
		batcher.add(starterCmd, starterListCallback);
		
		lastToken = token;
		
		TopicHelpers.setSourcePostId(postId);
	}
	
	
	private CommandResultCallback<TopicCommandResult> buildNestedListCallback(final String postId) {
		paginationElement.clear();
		postContainerElement.clear();
		
		postContainerElement.add(injector.getWaitPresenter().getWidget());
		
		CommandResultCallback<TopicCommandResult> replyListCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				pageSummaryLabel.setText("page " + result.getResults().getCurrentPage() + " of " + result.getResults().calculateNumberOfPages());
				postContainerElement.clear();
				PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
				postCollectionPresenter.setPostList(result.getResults().getList(),Mode.GROUPED);
				
				postContainerElement.add(postCollectionPresenter.getWidget());
				
				paginationPanel = injector.createPaginationPanel();
				paginationPanel.initialize(lastToken, result.getResults());
				paginationElement.add(paginationPanel.getWidget());
				paginationNanoPanel.init(paginationPanel, result.getResults());
				
				if(result.getResults().getList().size() > 0){
					rootPost = result.getResults().getList().get(0).getRoot();
					sortOrderComponent.setRootPost(rootPost);
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
