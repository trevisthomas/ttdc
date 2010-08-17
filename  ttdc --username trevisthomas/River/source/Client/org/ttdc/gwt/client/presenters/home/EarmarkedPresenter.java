package org.ttdc.gwt.client.presenters.home;


import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventListener;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.client.constants.TagConstants;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class EarmarkedPresenter extends BasePresenter<EarmarkedPresenter.View> implements TagEventListener{
	public interface View extends BaseView{
		HasWidgets postPanel();
		HasWidgets postFooterPanel();
	}
	
	private PostCollectionPresenter postCollection;
	private static PaginatedListCommandResult<GPost> resultCache = null;
	
	@Inject
	public EarmarkedPresenter(Injector injector){
		super(injector,injector.getEarmarkedView());
		EventBus.getInstance().addListener(this);
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
		cmd.setAction(PostListType.LATEST_EARMARKS);
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
		getService().execute(cmd, callback);	
	}

	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				resultCache = result;
				showResult(result);
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
	public void onTagEvent(TagEvent event) {
		if(event.getSource().getTag().getType().equals(TagConstants.TYPE_EARMARK)){
			refresh();
		}
	}
}
