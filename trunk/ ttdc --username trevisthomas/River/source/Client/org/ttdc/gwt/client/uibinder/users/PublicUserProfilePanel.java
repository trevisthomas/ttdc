package org.ttdc.gwt.client.uibinder.users;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter;
import org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.TextPresenter;
import org.ttdc.gwt.client.presenters.users.MoreSearchPresenter;
import org.ttdc.gwt.client.presenters.util.HtmlLabel;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PublicUserProfilePanel  extends BasePageComposite {
	interface MyUiBinder extends UiBinder<Widget, PublicUserProfilePanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField TabPanel tabPanelElement;
	@UiField SimplePanel profileElement;
	
	private final HtmlLabel bioHtml = new HtmlLabel();
	private final SimplePanel bestMoviesPanel = new SimplePanel();
	private final SimplePanel worstMoviesPanel = new SimplePanel();
	private final VerticalPanel latestCoversationsTabPanel = new VerticalPanel();
	private final VerticalPanel latestPostsTabPanel = new VerticalPanel();
	private final VerticalPanel latestReviewsTabPanel = new VerticalPanel();
	

	private final SimplePanel latestConversationsPanel = new SimplePanel();
	private final SimplePanel latestPostsPanel = new SimplePanel();
	private final SimplePanel latestReviewsPanel = new SimplePanel();
		
	private final SimplePanel latestConversationsFooterPanel = new SimplePanel();
	private final SimplePanel latestPostsFooterPanel = new SimplePanel();
	private final SimplePanel latestReviewsFooterPanel = new SimplePanel();
	
	private final ProfilePresenter userProfilePresenter;
	
	//private String personId;
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	private HistoryToken token;
	private GPerson person;
	private HistoryToken lastToken = null;
	
	@Inject
	public PublicUserProfilePanel(Injector injector) {
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
		userProfilePresenter = injector.getUserProfilePresenter();
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
		initWidget(binder.createAndBindUi(this));
		
		latestCoversationsTabPanel.add(latestConversationsPanel);
		latestCoversationsTabPanel.add(latestConversationsFooterPanel);
		
		latestPostsTabPanel.add(latestPostsPanel);
		latestPostsTabPanel.add(latestPostsFooterPanel);
		
		latestReviewsTabPanel.add(latestReviewsPanel);
		latestReviewsTabPanel.add(latestReviewsFooterPanel);
		
		
		tabPanelElement.add(bioHtml, "Bio");
		tabPanelElement.add(bestMoviesPanel, "Best Movies");
		tabPanelElement.add(worstMoviesPanel, "Worst Movies");
		tabPanelElement.add(latestCoversationsTabPanel, "Conversations");
		tabPanelElement.add(latestReviewsTabPanel, "Reviews");
		tabPanelElement.add(latestPostsTabPanel, "Comments");
		
		tabPanelElement.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!tabPanelElement.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectTabSelection(index);
			}
		});
		
		tabPanelElement.addStyleName("tt-TabPanel-fullpage");
	}
	
	
	/*
	 * Trevis, be aware that calling History.newItem actually caues a history event to be
	 * fired.  Think about what that means.  You may want to do other things to make better use 
	 * history and ajax
	 * 
	 * (This functionality is now in more than one place, see also AdminToolsView)
	 * 
	 */
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_PROFILE);
		token.addParameter(HistoryConstants.PERSON_ID, person.getPersonId());
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_BIO_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_BEST_MOVIES_TAB);
				break; 
			case 2:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_WORST_MOVIES_TAB);
				break;
			case 3:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_CONVERSATIONS_TAB);
				break;	
			case 4:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_REVIEWS_TAB);
				break;	
			case 5:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_POSTS_TAB);	
				break;	
		}
		//History.newItem(token.toString(), false);
		History.newItem(token.toString());
	}
	
	@Override
	protected void onShow(HistoryToken token) {
		if(lastToken == null || !lastToken.isParameterEq(HistoryConstants.PERSON_ID, token.getParameter(HistoryConstants.PERSON_ID))){
			clear();
			String personId = token.getParameter(HistoryConstants.PERSON_ID);
			PersonCommand cmd = new PersonCommand(personId,PersonStatusType.LOAD);
			injector.getService().execute(cmd, buildCallback());
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
				displayBioTab();
				buildBioTab();
			}
			else if(HistoryConstants.PROFILE_BEST_MOVIES_TAB.equals(tab)){
				displayBestMoviesTab();
				buildBestMoviesTab();
			}
			else if(HistoryConstants.PROFILE_WORST_MOVIES_TAB.equals(tab)){
				displayWorstMoviesTab();
				buildWorstMoviesTab();
			}
			else if(HistoryConstants.PROFILE_CONVERSATIONS_TAB.equals(tab)){
				displayLatestConversationsTab();
				buildLatestConversationsTab();
			}
			else if(HistoryConstants.PROFILE_POSTS_TAB.equals(tab)){
				displayLatestPostsTab();
				buildLatestPostsTab();
			}
			else if(HistoryConstants.PROFILE_REVIEWS_TAB.equals(tab)){
				displayLatestReviewsTab();
				buildLatestReviewsTab();
			}
			else{
				displayBioTab();
			}
		}
		else{
			displayBioTab();
		}
	}
	
	CommandResultCallback<GenericCommandResult<GPerson>> buildCallback(){
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				person = result.getObject();
				userProfilePresenter.init(person);
				 	
				Widget w = userProfilePresenter.getWidget();
				w.addStyleName("tt-center");
				profileElement.add(w);
				initializeTabs(lastToken);
				
				pageHeaderPanel.init(person.getLogin(), "a profile of my history on ttdc");
				pageHeaderPanel.getSearchBoxPresenter().init(person);
			}
		};
	}
	
	public void buildBioTab() {
		if(StringUtil.notEmpty(person.getBio().trim())){
			bioHtml.setText(person.getBio());
		}
		else{
			bioHtml.setText("&lt;missing bio&gt;");
		}
		
	}
	
	public void buildBestMoviesTab() {
		MovieListCommand cmd = new MovieListCommand();
		cmd.setPersonId(person.getPersonId());
		cmd.setSortBy(SortBy.BY_RATING);
		cmd.setSortDirection(SortDirection.ASC);
		if(PresenterHelpers.isWidgetEmpty(bestMoviesPanel))
			injector.getService().execute(cmd, buildMovieListCallback(bestMoviesPanel,person.getLogin()+ " hasn't rated any movies."));
	}


	public void buildWorstMoviesTab() {
		MovieListCommand cmd = new MovieListCommand();
		cmd.setPersonId(person.getPersonId());
		cmd.setSortBy(SortBy.BY_RATING);
		cmd.setSortDirection(SortDirection.DESC);
		if(PresenterHelpers.isWidgetEmpty(worstMoviesPanel))
			injector.getService().execute(cmd, buildMovieListCallback(worstMoviesPanel,person.getLogin()+ " hasn't rated any movies."));
	}

	public void buildLatestConversationsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		//cmd.setConversationsOnly(true);
		cmd.setPostSearchType(PostSearchType.CONVERSATIONS);
		cmd.setPersonId(person.getPersonId());
		cmd.setPageSize(10);
		if(PresenterHelpers.isWidgetEmpty(latestConversationsPanel))
			injector.getService().execute(cmd, buildPostListCallback(cmd,latestConversationsPanel, 
					latestConversationsFooterPanel,person.getLogin()+ " hasn't started any conversations."));
	}

	public void buildLatestPostsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setPostSearchType(PostSearchType.REPLIES);
		cmd.setPersonId(person.getPersonId());
		cmd.setNonReviewsOnly(true);
		cmd.setPageSize(10);	
		if(PresenterHelpers.isWidgetEmpty(latestPostsPanel))
			injector.getService().execute(cmd, buildPostListCallback(cmd,latestPostsPanel, 
					latestPostsFooterPanel,person.getLogin()+ " hasn't made any comments."));
	}


	public void buildLatestReviewsTab() {
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setPostSearchType(PostSearchType.CONVERSATIONS);
		cmd.setPersonId(person.getPersonId());
		cmd.setReviewsOnly(true);
		cmd.setPageSize(10);	
		if(PresenterHelpers.isWidgetEmpty(latestReviewsPanel))
			injector.getService().execute(cmd, buildPostListCallback(cmd, latestReviewsPanel, 
					latestReviewsFooterPanel,person.getLogin()+ " hasn't left any reviews."));
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


	public void clear() {
		profileElement.clear();
		bioHtml.setText("");
		bestMoviesPanel.clear();
		worstMoviesPanel.clear();
		latestConversationsPanel.clear();
		latestPostsPanel.clear();
		latestReviewsPanel.clear();
	}
	
	public void displayBestMoviesTab() {
		tabPanelElement.selectTab(1);
	}

	public void displayBioTab() {
		tabPanelElement.selectTab(0);
	}

	public void displayLatestConversationsTab() {
		tabPanelElement.selectTab(3);
		
	}

	public void displayLatestPostsTab() {
		tabPanelElement.selectTab(5);
	}

	public void displayLatestReviewsTab() {
		tabPanelElement.selectTab(4);
	}

	public void displayWorstMoviesTab() {
		tabPanelElement.selectTab(2);
	}
}
