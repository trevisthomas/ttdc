package org.ttdc.gwt.client.presenters.home;

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

public class NestedPresenter extends BasePresenter<NestedPresenter.View>{
	public interface View extends BaseView{
		HasWidgets postPanel();
		HasWidgets postFooterPanel();
	}
	
	private PostCollectionPresenter postCollection;
	private static PaginatedListCommandResult<GPost> resultCache;
	@Inject
	public NestedPresenter(Injector injector){
		super(injector,injector.getNestedView());
	}

	public void init(){
		view.postPanel().add(injector.getWaitPresenter().getWidget());
		postCollection = injector.getPostCollectionPresenter();
		
		if(resultCache == null){
			refresh();
		}
		else{
			addResults(resultCache);
		}
	}
	
	public void refresh() {
		LatestPostsCommand cmd = new LatestPostsCommand();
		cmd.setAction(PostListType.LATEST_NESTED);
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
		getService().execute(cmd, callback);
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				resultCache = result;
				addResults(result);
			}
		};
		return callback;
	}
	
	private void addResults(PaginatedListCommandResult<GPost> result) {
		PaginatedList<GPost> results = result.getResults();
		postCollection.setPostList(results.getList(), Mode.NESTED_SUMMARY);
		view.postPanel().clear();
		view.postPanel().add(postCollection.getWidget());
	}

	
}
