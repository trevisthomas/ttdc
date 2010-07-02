package org.ttdc.gwt.client.presenters.users;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter;
import org.ttdc.gwt.client.presenters.home.MoreLatestPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter;
import org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.TextPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class PublicUserProfilePresenter extends BasePagePresenter<PublicUserProfilePresenter.View> {
	
	public interface View extends BasePageView{
		HasWidgets profile();
		
		HasText bioText();
		HasWidgets bestMoviesPanel();
		HasWidgets worstMoviesPanel();
		HasWidgets latestConversationsPanel();
		HasWidgets latestPostsPanel();
		HasWidgets latestReviewsPanel();
		
		HasWidgets latestConversationsFooterPanel();
		HasWidgets latestPostsFooterPanel();
		HasWidgets latestReviewsFooterPanel();
		
		void displayBioTab();
		void displayBestMoviesTab();
		void displayWorstMoviesTab();
		void displayLatestConversationsTab();
		void displayLatestPostsTab();
		void displayLatestReviewsTab();
		
		void setPersonId(String personId);
		
		void clear();
	}
	
	private GPerson person;
	private HistoryToken lastToken = null;
	
	@Inject
	public PublicUserProfilePresenter(Injector injector) {
		super(injector,injector.getPublicUserProfileView());
	}

	@Override
	public void show(HistoryToken token) {
		if(lastToken == null || !lastToken.isParameterEq(HistoryConstants.PERSON_ID, token.getParameter(HistoryConstants.PERSON_ID))){
			view.clear();
			String personId = token.getParameter(HistoryConstants.PERSON_ID);
			PersonCommand cmd = new PersonCommand(personId,PersonStatusType.LOAD);
			injector.getService().execute(cmd, buildCallback());
			view.show();
		}
		else{
			initializeTabs(token);
		}
		lastToken = token;
	}

	private void initializeTabs(HistoryToken token) {
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		if(StringUtil.notEmpty(tab)){
			if(HistoryConstants.PROFILE_BIO_TAB.equals(tab)){
				view.displayBioTab();
				buildBioTab();
			}
			else if(HistoryConstants.PROFILE_BEST_MOVIES_TAB.equals(tab)){
				view.displayBestMoviesTab();
				buildBestMoviesTab();
			}
			else if(HistoryConstants.PROFILE_WORST_MOVIES_TAB.equals(tab)){
				view.displayWorstMoviesTab();
				buildWorstMoviesTab();
			}
			else if(HistoryConstants.PROFILE_CONVERSATIONS_TAB.equals(tab)){
				view.displayLatestConversationsTab();
				buildLatestConversationsTab();
			}
			else if(HistoryConstants.PROFILE_POSTS_TAB.equals(tab)){
				view.displayLatestPostsTab();
				buildLatestPostsTab();
			}
			else if(HistoryConstants.PROFILE_REVIEWS_TAB.equals(tab)){
				view.displayLatestReviewsTab();
				buildLatestReviewsTab();
			}
			else{
				view.displayBioTab();
			}
		}
		else{
			view.displayBioTab();
		}
	}
	
	CommandResultCallback<GenericCommandResult<GPerson>> buildCallback(){
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				ProfilePresenter userProfilePresenter = injector.getUserProfilePresenter();
				person = result.getObject();
				userProfilePresenter.init(person);
				view.profile().add(userProfilePresenter.getWidget());
				view.setPersonId(person.getPersonId());
				initializeTabs(lastToken);
			}
		};
	}
	
	public void buildBioTab() {
		if(StringUtil.notEmpty(person.getBio().trim())){
			view.bioText().setText(person.getBio());
		}
		else{
			view.bioText().setText("&lt;missing bio&gt;");
		}
		
	}
	
	public void buildBestMoviesTab() {
		MovieListCommand cmd = new MovieListCommand();
		cmd.setPersonId(person.getPersonId());
		cmd.setSortBy(SortBy.BY_RATING);
		cmd.setSortDirection(SortDirection.ASC);
		if(PresenterHelpers.isWidgetEmpty(view.bestMoviesPanel()))
			injector.getService().execute(cmd, buildMovieListCallback(view.bestMoviesPanel(),person.getLogin()+ " hasn't rated any movies."));
	}


	public void buildWorstMoviesTab() {
		MovieListCommand cmd = new MovieListCommand();
		cmd.setPersonId(person.getPersonId());
		cmd.setSortBy(SortBy.BY_RATING);
		cmd.setSortDirection(SortDirection.DESC);
		if(PresenterHelpers.isWidgetEmpty(view.worstMoviesPanel()))
			injector.getService().execute(cmd, buildMovieListCallback(view.worstMoviesPanel(),person.getLogin()+ " hasn't rated any movies."));
	}

	public void buildLatestConversationsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		//cmd.setConversationsOnly(true);
		cmd.setPostSearchType(PostSearchType.CONVERSATIONS);
		cmd.setPersonId(person.getPersonId());
		cmd.setPageSize(10);
		if(PresenterHelpers.isWidgetEmpty(view.latestConversationsPanel()))
			injector.getService().execute(cmd, buildPostListCallback(cmd,view.latestConversationsPanel(), 
					view.latestConversationsFooterPanel(),person.getLogin()+ " hasn't started any conversations."));
	}

	public void buildLatestPostsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setPostSearchType(PostSearchType.REPLIES);
		cmd.setPersonId(person.getPersonId());
		cmd.setNonReviewsOnly(true);
		cmd.setPageSize(10);	
		if(PresenterHelpers.isWidgetEmpty(view.latestPostsPanel()))
			injector.getService().execute(cmd, buildPostListCallback(cmd,view.latestPostsPanel(), 
					view.latestPostsFooterPanel(),person.getLogin()+ " hasn't made any comments."));
	}


	public void buildLatestReviewsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setPostSearchType(PostSearchType.CONVERSATIONS);
		cmd.setPersonId(person.getPersonId());
		cmd.setReviewsOnly(true);
		cmd.setPageSize(10);	
		if(PresenterHelpers.isWidgetEmpty(view.latestReviewsPanel()))
			injector.getService().execute(cmd, buildPostListCallback(cmd,view.latestReviewsPanel(), 
					view.latestReviewsFooterPanel(),person.getLogin()+ " hasn't left any reviews."));
	}

	private CommandResultCallback<SearchPostsCommandResult> buildPostListCallback(final SearchPostsCommand cmd, 
			final HasWidgets target,
			final HasWidgets moreTarget, 
			final String noResutsMessage) {
		target.add(injector.getWaitPresenter().getWidget());
		CommandResultCallback<SearchPostsCommandResult> replyListCallback = new MyCommandCallback(cmd,target,moreTarget,noResutsMessage);
		return replyListCallback;
	}
	
	
	private class MyCommandCallback extends CommandResultCallback<SearchPostsCommandResult>{
		SearchPostsCommand cmd;
		HasWidgets target; 
		String noResutsMessage;
		HasWidgets moreTarget;
		
		public MyCommandCallback(final SearchPostsCommand cmd, final HasWidgets target, final HasWidgets moreTarget, final String noResutsMessage) {
			this.cmd = cmd;
			this.target = target;
			this.noResutsMessage = noResutsMessage;
			this.moreTarget = moreTarget;
		}
		@Override
		public void onSuccess(SearchPostsCommandResult result) {
			target.clear();
			PostCollectionPresenter postCollection = injector.getPostCollectionPresenter();
			if(!result.getResults().isEmpty()){
				postCollection.setPostList(result.getResults().getList());
				target.add(postCollection.getWidget());
				MoreSearchPresenter moreSearchPresenter = injector.getMoreSearchPresenter();
				moreSearchPresenter.init(new MyMoreObserver(postCollection), result.getResults(), cmd);
				moreTarget.clear();
				moreTarget.add(moreSearchPresenter.getWidget());
			}
			else{
				TextPresenter textPresenter = injector.getTextPresenter();
				textPresenter.setText(noResutsMessage);
				target.add(textPresenter.getWidget());
			}
		}
	}
	
	CommandResultCallback<SearchPostsCommandResult> buildMovieListCallback(final HasWidgets target,final String noResutsMessage) {
		target.add(injector.getWaitPresenter().getWidget());
		CommandResultCallback<SearchPostsCommandResult> replyListCallback = new CommandResultCallback<SearchPostsCommandResult>(){
			@Override
			public void onSuccess(SearchPostsCommandResult result) {
				target.clear();
				GenericTabularFlowPresenter gfp = injector.getGenericTabularFlowPresenter();
				if(!result.getResults().isEmpty()){
					for(GPost post : result.getResults().getList()){
						MovieCoverWithRatingPresenter coverPresenter = injector.getMovieCoverWithRatingPresenter();
						coverPresenter.init(post,person.getPersonId());
						gfp.stackWidget(coverPresenter.getWidget());
					}
					target.add(gfp.getWidget());
				}
				else{
					TextPresenter textPresenter = injector.getTextPresenter();
					textPresenter.setText(noResutsMessage);
					target.add(textPresenter.getWidget());
				}
			}
		};
		return replyListCallback;
	}

	class MyMoreObserver implements MoreSearchPresenter.MoreSearchObserver{
		private PostCollectionPresenter postCollection;
		public MyMoreObserver(PostCollectionPresenter postCollection) {
			this.postCollection = postCollection;
		}
		@Override
		public void onMorePosts(List<GPost> posts) {
			postCollection.addPostsToPostList(posts, Mode.FLAT);
		}
	}

}
