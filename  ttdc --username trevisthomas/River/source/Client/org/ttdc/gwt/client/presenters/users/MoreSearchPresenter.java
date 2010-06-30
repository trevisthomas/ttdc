package org.ttdc.gwt.client.presenters.users;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class MoreSearchPresenter extends BasePresenter<MoreSearchPresenter.View>{
	public interface MoreSearchObserver {
		void onMorePosts(final List<GPost> posts);
	}
	
	public interface View extends BaseView{
		HasClickHandlers moreButton();
		void setVisible(boolean visible);
	}

	private int pageNumber;
	private MoreSearchObserver observer;
	private SearchPostsCommand command;
	
	@Inject
	protected MoreSearchPresenter(Injector injector) {
		super(injector, injector.getMoreSearchView());
	}
		
	public void init(MoreSearchObserver observer, PaginatedList<GPost> results, SearchPostsCommand command){
		this.observer = observer;
		this.command = command;
		determineIfMoreAvailable(results);
		
		view.moreButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
//				SearchPostsCommand cmd = new SearchPostsCommand();
//				cmd.setPostSearchType(MoreSearchPresenter.this.searchType);
//				cmd.setPersonId(MoreSearchPresenter.this.personId);
//				cmd.setReviewsOnly(true);
//				cmd.setPageSize(10);
				
				MoreSearchPresenter.this.command.setPageNumber(pageNumber);
				getService().execute(MoreSearchPresenter.this.command, buildPostListCallback());
			}
		});
	}
	
	private CommandResultCallback<SearchPostsCommandResult> buildPostListCallback() {
		//target.add(injector.getWaitPresenter().getWidget());
		CommandResultCallback<SearchPostsCommandResult> replyListCallback = new CommandResultCallback<SearchPostsCommandResult>(){
			@Override
			public void onSuccess(SearchPostsCommandResult result) {
				determineIfMoreAvailable(result.getResults());
				observer.onMorePosts(result.getResults().getList());
			}
		};
		return replyListCallback;
	}
	
	private void determineIfMoreAvailable(PaginatedList<GPost> results) {
		if(results.calculateNumberOfPages() >= results.getCurrentPage() + 1){
			pageNumber = results.getCurrentPage() + 1;
			view.setVisible(true);
		}
		else{
			view.setVisible(false);
		}
	}
}
