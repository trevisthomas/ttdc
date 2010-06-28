package org.ttdc.gwt.client.presenters.home;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class FlatPresenter extends BasePresenter<FlatPresenter.View> implements MoreLatestPresenter.MoreLatestObserver{
	public interface View extends BaseView{
		HasWidgets postPanel();
		HasWidgets postFooterPanel();
	}
	
	private PostCollectionPresenter postCollection;
	private static PaginatedListCommandResult<GPost> resultCache = null;
	private int pageNumber = 1;
	
	@Inject
	public FlatPresenter(Injector injector){
		super(injector,injector.getFlatView());
	}

	public void init(){
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
		LatestPostsCommand cmd = new LatestPostsCommand();
		cmd.setAction(PostListType.LATEST_FLAT);
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
		getService().execute(cmd, callback);	
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				resultCache = result;
				showResult(result);
				MoreLatestPresenter morePresenter = injector.getMoreLatestPresenter();
				morePresenter.init(FlatPresenter.this, PostListType.LATEST_FLAT, result.getResults());
				view.postFooterPanel().clear();
				view.postFooterPanel().add(morePresenter.getWidget());
			}
		};
		return callback;
	}
	
	private void showResult(PaginatedListCommandResult<GPost> result) {
		PaginatedList<GPost> results = result.getResults();
		postCollection.setPostList(results.getList(), Mode.FLAT);
		view.postPanel().clear();
		view.postPanel().add(postCollection.getWidget());
	}

	@Override
	public void onMorePosts(List<GPost> posts) {
		// TODO Auto-generated method stub
		//Make an add method on postCollection
		//postCollection.setPostList(results.getList(), Mode.FLAT);
	}
}
