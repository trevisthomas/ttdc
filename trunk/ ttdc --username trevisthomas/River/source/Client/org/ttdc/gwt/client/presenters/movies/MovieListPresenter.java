package org.ttdc.gwt.client.presenters.movies;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MovieListPresenter extends BasePagePresenter<MovieListPresenter.View>{
	private String personId = null; 
	private boolean speedRate = false;
	private final List<MovieRatingPresenter> ratingPresenterList = new ArrayList<MovieRatingPresenter>();
	
	@Inject
	public MovieListPresenter(Injector injector) {
		super(injector, injector.getMovieListView());
	}

	public interface View extends BasePageView{
		HasWidgets paginator();
		
		HasWidgets releaseYearColumnHeader();
		HasWidgets ratingColumnHeader();
		HasWidgets titleColumnHeader();
		
		HasClickHandlers releaseYearSortClickHandler();
		HasClickHandlers ratingSortClickHandler();
		HasClickHandlers titleSortClickHandler();
		HasClickHandlers speedRateClickHandler();
		HasClickHandlers exitSpeedRateClickHandler();
		
		void addMovie(String year, Widget titleLink, Widget imdbLink, Widget rating);
		String getSelectedPersonId();
		void setSelectedPersonId(String personId);
		void addPerson(String login, String personId, int reviewCount);
		HasClickHandlers goButton();

		void enableExitSpeedRateButton();
		void enableSpeedRateButton();

		
	}

	@Override
	public void show(HistoryToken token) {
		final int pageNumber = token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,1);
		final String sort = token.getParameter(HistoryConstants.SORT_KEY, HistoryConstants.MOVIES_SORT_BY_TITLE);
		final String direction = token.getParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_ASC);
		personId = token.getParameter(HistoryConstants.PERSON_ID);
		
		final String viewMode  = token.getParameter(HistoryConstants.MOVIES_LIST_MODE);
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		
		token.setParameter(HistoryConstants.SORT_KEY, sort);
		token.setParameter(HistoryConstants.SORT_DIRECTION_KEY, direction);
		
		setupHeaders(token);
		
		BatchCommandTool batcher = new BatchCommandTool();

		MovieListCommand cmd = new MovieListCommand();
		if(StringUtil.notEmpty(personId))
			cmd.setPersonId(personId);
		
		cmd.setPageNumber(pageNumber);
		
		speedRate = false;
		if(HistoryConstants.MOVIES_LIST_MODE_SPEEDRATE.equals(viewMode) && !user.isAnonymous()){
			speedRate = true;
			cmd.setPersonId(personId);
			cmd.setSpeedRate(true);
			view.enableExitSpeedRateButton();
		} else if(!user.isAnonymous()){
			view.enableSpeedRateButton();
		}
		
		if(HistoryConstants.SORT_ASC.equals(direction))
			cmd.setSortDirection(SortDirection.ASC);
		else
			cmd.setSortDirection(SortDirection.DESC);
		
		if(HistoryConstants.MOVIES_SORT_BY_RATING.equals(sort))
			cmd.setSortBy(SortBy.BY_RATING);
		else if(HistoryConstants.MOVIES_SORT_BY_TITLE.equals(sort))
			cmd.setSortBy(SortBy.BY_TITLE);
		else if(HistoryConstants.MOVIES_SORT_BY_RELEASE_YEAR.equals(sort))
			cmd.setSortBy(SortBy.BY_RELEASE_YEAR);
		else
			throw new RuntimeException("Bad sort field");
		
		PersonListCommand personListCmd = new PersonListCommand(PersonListType.MOVIE_REVIEWERS);
		CommandResultCallback<PersonListCommandResult> personListCallback = buildReviewerListCallback();
		batcher.add(personListCmd, personListCallback);
		view.goButton().addClickHandler(personSelectedClickHandler(token));
		
		CommandResultCallback<SearchPostsCommandResult> callback = buildMovieListCallback(token);
		batcher.add(cmd, callback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		view.speedRateClickHandler().addClickHandler(speedRateClickHandler(token));
		view.exitSpeedRateClickHandler().addClickHandler(exitSpeedRateClickHandler(token));
		
		view.show();
	}

	private ClickHandler speedRateClickHandler(final HistoryToken token){
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = new HistoryToken();
				newToken.load(token);
//				newToken.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
//				newToken.removeParameter(HistoryConstants.PERSON_ID);
				newToken.setParameter(HistoryConstants.MOVIES_LIST_MODE, HistoryConstants.MOVIES_LIST_MODE_SPEEDRATE);
				EventBus.fireHistoryToken(newToken);
//				enableSpeedRateMode();
			}

			
		};
	}
	private ClickHandler personSelectedClickHandler(final HistoryToken token) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = new HistoryToken();
				newToken.load(token);
				newToken.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
				newToken.setParameter(HistoryConstants.PERSON_ID, view.getSelectedPersonId());
				EventBus.fireHistoryToken(newToken);
			}
		};
	}
	
	private ClickHandler exitSpeedRateClickHandler(final HistoryToken token){
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = new HistoryToken();
				newToken.load(token);
//				newToken.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
//				newToken.removeParameter(HistoryConstants.PERSON_ID);
				newToken.removeParameter(HistoryConstants.MOVIES_LIST_MODE);
				EventBus.fireHistoryToken(newToken);
				
			}
		};
	}
	
//	private void enableSpeedRateMode() {
//		for(MovieRatingPresenter ratingPresenter : ratingPresenterList){
//			GPerson user = ConnectionId.getInstance().getCurrentUser();
//			if(!user.isAnonymous())
//				ratingPresenter.reInititalize(user.getPersonId());
//			else
//				throw new RuntimeException("Anonymous users can't rate");
//		}
//	}
	
	private void setupHeaders(final HistoryToken token){
//		HyperlinkPresenter yearSortPresenter = injector.getHyperlinkPresenter();
//		HistoryToken yearToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RELEASE_YEAR, token);
//		yearSortPresenter.setToken(yearToken, "Year");
//		
//		HyperlinkPresenter ratingSortPresenter = injector.getHyperlinkPresenter();
//		HistoryToken ratingToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RATING, token);
//		ratingSortPresenter.setToken(ratingToken, "Rating");
//		
//		HyperlinkPresenter titleSortPresenter = injector.getHyperlinkPresenter();
//		HistoryToken titleToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_TITLE, token);
//		titleSortPresenter.setToken(titleToken, "Title");
//		
//		view.releaseYearColumnHeader().add(yearSortPresenter.getWidget());
//		view.ratingColumnHeader().add(ratingSortPresenter.getWidget());
//		view.titleColumnHeader().add(titleSortPresenter.getWidget());
		
		view.ratingSortClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RATING, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.releaseYearSortClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RELEASE_YEAR, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.titleSortClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_TITLE, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
	}

	private CommandResultCallback<PersonListCommandResult> buildReviewerListCallback() {
		CommandResultCallback<PersonListCommandResult> replyListCallback = new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				for(GPerson person : result.getPersonList()){
					view.addPerson(person.getLogin(), person.getPersonId(), Integer.parseInt(person.getValue()));
				}
				if(StringUtil.notEmpty(personId))
					view.setSelectedPersonId(personId);
			}
		};
		return replyListCallback;
	}
	
	private CommandResultCallback<SearchPostsCommandResult> buildMovieListCallback(final HistoryToken token) {
		CommandResultCallback<SearchPostsCommandResult> replyListCallback = new CommandResultCallback<SearchPostsCommandResult>(){
			@Override
			public void onSuccess(SearchPostsCommandResult result) {
				ratingPresenterList.clear();
				for(GPost post : result.getResults().getList()){
					MovieRatingPresenter ratingPresenter = injector.getMovieRatingPresenter();
					ratingPresenterList.add(ratingPresenter);
					ratingPresenter.setAutohide(false);
					//PresenterHelpers.initializeMovieRatingPresenter(ratingPresenter, post, personId);
					//PresenterHelpers.initializeMovieRatingPresenter(ratingPresenter, post, null);
					GPerson user = ConnectionId.getInstance().getCurrentUser();
					if(speedRate && !user.isAnonymous()){
						ratingPresenter.initializeMovieRatingPresenter(post, user.getPersonId());
					}
					else
						ratingPresenter.initializeMovieRatingPresenter(post, personId);
					HyperlinkPresenter urlLinkPresenter = createPostUrlPresenter(post);
					HyperlinkPresenter titlePresenter = injector.getHyperlinkPresenter();
					titlePresenter.setPost(post);
					String year = ""+post.getPublishYear();
					view.addMovie(year, titlePresenter.getWidget(), urlLinkPresenter.getWidget(), ratingPresenter.getWidget());
				}
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				paginationPresenter.initialize(token, result.getResults());
				view.paginator().add(paginationPresenter.getWidget());
			}
		};
		return replyListCallback;
	}
	
	
	private HyperlinkPresenter createPostUrlPresenter(GPost post) {
		HyperlinkPresenter urlLinkPresenter = injector.getHyperlinkPresenter();
		if(StringUtil.notEmpty(post.getUrl())){
			urlLinkPresenter.setUrl(post.getUrl());
			urlLinkPresenter.setText("[IMDB]");
		}
		return urlLinkPresenter;
	}

	
}

