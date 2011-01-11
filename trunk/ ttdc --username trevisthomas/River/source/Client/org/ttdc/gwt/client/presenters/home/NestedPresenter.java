package org.ttdc.gwt.client.presenters.home;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class NestedPresenter extends BasePresenter<NestedPresenter.View> implements MoreLatestPresenter.MoreLatestObserver{
	public interface View extends BaseView{
		HasWidgets postPanel();
		HasWidgets postFooterPanel();
	}
	
	private PostCollectionPresenter postCollection;
	private MoreLatestPresenter morePresenter;
	//private static PaginatedListCommandResult<GPost> resultCache;
	@Inject
	public NestedPresenter(Injector injector){
		super(injector,injector.getNestedView());
	}

	public void init(){
		view.postPanel().add(injector.getWaitPresenter().getWidget());
		postCollection = injector.getPostCollectionPresenter();
		refresh();
//		if(resultCache == null){
//			refresh();
//		}
//		else{
//			addResults(resultCache);
//		}
	}
	
	public void refresh() {
		LatestPostsCommand cmd = new LatestPostsCommand();
		//cmd.setAction(PostListType.LATEST_NESTED);
		cmd.setAction(PostListType.LATEST_GROUPED);
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
		getService().execute(cmd, callback);
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				//resultCache = result;
				EventBus.fireEvent(new MessageEvent(MessageEventType.REFRESH_COMPLETE, ""));
				addResults(result);
				setupMorePresenter(result);
			}
		};
		return callback;
	}
	
	private void addResults(PaginatedListCommandResult<GPost> result) {
		PaginatedList<GPost> results = result.getResults();
		//postCollection.setPostList(results.getList(), Mode.NESTED_SUMMARY);
		postCollection.setPostList(results.getList(), Mode.GROUPED);
		view.postPanel().clear();
		view.postPanel().add(postCollection.getWidget());
		setupMorePresenter(result);
	}
	
	
	private void setupMorePresenter(PaginatedListCommandResult<GPost> result) {
		morePresenter = injector.getMoreLatestPresenter();
		morePresenter.init(NestedPresenter.this, PostListType.LATEST_GROUPED, result.getResults());
		view.postFooterPanel().clear();
		view.postFooterPanel().add(morePresenter.getWidget());
	}

	@Override
	public void onMorePosts(List<GPost> posts) {
		postCollection.addPostsToPostList(posts, Mode.GROUPED);
		view.postFooterPanel().clear();
		view.postFooterPanel().add(morePresenter.getWidget());
	}
	
	@Override
	public void loadingMoreResults() {
		view.postFooterPanel().clear();
		view.postFooterPanel().add(injector.getWaitPresenter().getWidget());
	}
}
