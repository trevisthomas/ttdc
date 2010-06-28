package org.ttdc.gwt.client.presenters.home;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class MoreLatestPresenter extends BasePresenter<MoreLatestPresenter.View>{
	public interface MoreLatestObserver {
		//HistoryToken getToken();
		void onMorePosts(final List<GPost> posts);
		//void showResult(PaginatedListCommandResult<GPost> result);
	}
	
	public interface View extends BaseView{
		HasClickHandlers moreButton();
		//HasWidgets panel();
		void setVisible(boolean visible);
	}

	private int pageNumber;
	private MoreLatestObserver observer;
	private PostListType listType;
	
	@Inject
	protected MoreLatestPresenter(Injector injector) {
		super(injector, injector.getMoreLatestView());
	}
		
	public void init(MoreLatestObserver observer, PostListType listType, PaginatedList<GPost> results){
		this.observer = observer;
		this.listType = listType;
		
		determineIfMoreAvailable(results);
		
		view.moreButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LatestPostsCommand cmd = new LatestPostsCommand();
				cmd.setAction(MoreLatestPresenter.this.listType);
				cmd.setPageNumber(pageNumber);
				CommandResultCallback<PaginatedListCommandResult<GPost>> callback = buildCallback();
				getService().execute(cmd, callback);
			}
		});
	}
	
	private CommandResultCallback<PaginatedListCommandResult<GPost>> buildCallback() {
		CommandResultCallback<PaginatedListCommandResult<GPost>> callback = new CommandResultCallback<PaginatedListCommandResult<GPost>>(){
			public void onSuccess(PaginatedListCommandResult<GPost> result) {
				determineIfMoreAvailable(result.getResults());
				observer.onMorePosts(result.getResults().getList());
			}
		};
		return callback;
	}

	
	private void determineIfMoreAvailable(PaginatedList<GPost> results) {
		if(results.calculateNumberOfPages() >= results.getCurrentPage() + 1){
			pageNumber = results.getCurrentPage();
			view.setVisible(true);
		}
		else{
			view.setVisible(false);
		}
	}
}
