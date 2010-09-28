package org.ttdc.gwt.client.presenters.home;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ForumTopicListCommand;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

public class FlatPresenter extends BasePresenter<FlatPresenter.View> implements MoreLatestPresenter.MoreLatestObserver{
	public interface View extends BaseView{
		HasWidgets postPanel();
		HasWidgets postFooterPanel();
	}
	
	private PostCollectionPresenter postCollection;
	private static PaginatedListCommandResult<GPost> resultCache = null;
	private String forumId = null;
	private HistoryToken token;

	@Inject
	public FlatPresenter(Injector injector){
		super(injector,injector.getFlatView());
	}

	public void init(HistoryToken token) {
		this.token = token;
		this.forumId = token.getParameter(HistoryConstants.FORUM_ID_KEY);
		if(forumId != null){
			init();
			postCollection.setListenForLocalNew(false);
		}
	}
	
	public void init(){
		view.postPanel().clear();
		view.postPanel().add(injector.getWaitPresenter().getWidget());
		postCollection = injector.getPostCollectionPresenter();
		if(resultCache == null){
			refresh();
		}
		else{
			showResult(resultCache);
		}
	}
	
	public void refresh(){
		if(forumId == null){
			LatestPostsCommand cmd = new LatestPostsCommand();
			cmd.setAction(PostListType.LATEST_FLAT);
			CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
			getService().execute(cmd, callback);	
		}
		else{
			ForumTopicListCommand cmd = new ForumTopicListCommand();
			cmd.setAction(ForumActionType.LOAD_TOPIC_PAGE);
			cmd.setForumId(forumId);
			cmd.setCurrentPage(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY, 1));
			getService().execute(cmd, buildCallback());
		}
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				if(forumId == null){
					resultCache = result;
				}
				else{
					resultCache = null;
				}
				showResult(result);
			}
		};
		return callback;
	}
	
	private void showResult(PaginatedListCommandResult<GPost> result) {
		PaginatedList<GPost> results = result.getResults();
		view.postPanel().clear();
		if(results.getList().size() == 0){
			view.postPanel().add(new Label(results.toString()));
		}
		else{
			postCollection.setPostList(results.getList(), Mode.FLAT);
			view.postPanel().add(postCollection.getWidget());
			if(forumId == null){
				setupMorePresenter(result);
			}
			else{
				setupPaginator(result);
			}
		}
	}

	private void setupPaginator(PaginatedListCommandResult<GPost> result) {
		PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
		HistoryToken token = TopicHelpers.buildForumPageToken(forumId);
		paginationPresenter.initialize(token, result.getResults());
		view.postFooterPanel().clear();
		view.postFooterPanel().add(paginationPresenter.getWidget());
	}

	private void setupMorePresenter(PaginatedListCommandResult<GPost> result) {
		MoreLatestPresenter morePresenter = injector.getMoreLatestPresenter();
		morePresenter.init(FlatPresenter.this, PostListType.LATEST_FLAT, result.getResults());
		view.postFooterPanel().clear();
		view.postFooterPanel().add(morePresenter.getWidget());
	}
	
	@Override
	public void onMorePosts(List<GPost> posts) {
		postCollection.addPostsToPostList(posts, Mode.FLAT);
	}


}
