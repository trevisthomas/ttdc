package org.ttdc.gwt.client.uibinder.movies;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.client.uibinder.shared.UiHelpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MovieListPanel extends BasePageComposite{
	interface MyUiBinder extends UiBinder<Widget, MovieListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField FlexTable tableElement;
	@UiField SimplePanel paginatorElement;
	@UiField FlowPanel personFilterElement;
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	private String personId = null; 
	private boolean speedRate = false;
	private final List<MovieRatingPresenter> ratingPresenterList = new ArrayList<MovieRatingPresenter>();
	
	private final SimplePanel imdb = new SimplePanel();
	private final FocusPanel titleHeaderPanel = UiHelpers.createTableHeaderPanel("Title");
	private final FocusPanel releaseYearHeaderPanel = UiHelpers.createTableHeaderPanel("Release Year");
	private final FocusPanel ratingHeaderPanel = UiHelpers.createTableHeaderPanel("Average Rating");
	
	private final ListBox reviewers = new ListBox(false);
	private final Button goButton = new Button();
	
	private final Label ratingLabel = (Label)ratingHeaderPanel.getWidget();
	private final Button speedRateButton = new Button("Speed Rate");
	private final Button exitSpeedRateButton = new Button("End Speed Rate");
	
	private HistoryToken token;
	
	int row = 0;
	
	@Inject
	public MovieListPanel(Injector injector) {
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
    	imdb.add(new Label("IMDB"));
    	
		initWidget(binder.createAndBindUi(this));
		
		reviewers.addItem("-- Reviewers --","-1");
		
		goButton.setText("Go");
		personFilterElement.add(reviewers);
		personFilterElement.add(goButton);
		
		
		tableElement.setWidget(0, 0, releaseYearHeaderPanel);
		tableElement.setWidget(0, 1, imdb);
		tableElement.setWidget(0, 2, titleHeaderPanel);
		tableElement.setWidget(0, 3, ratingHeaderPanel);
		
	}
	
	@Override
	protected void onShow(HistoryToken token) {
		this.token = token;
		
		String subtitle = "";
		
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
		if(StringUtil.notEmpty(personId)){
			cmd.setPersonId(personId);
		}
		
		cmd.setPageNumber(pageNumber);
		
		speedRate = false;
		if(HistoryConstants.MOVIES_LIST_MODE_SPEEDRATE.equals(viewMode) && !user.isAnonymous()){
			speedRate = true;
			cmd.setPersonId(personId);
			cmd.setSpeedRate(true);
			enableExitSpeedRateButton();
			subtitle = "Unrated movies are ready to be rated!";
		} else if(!user.isAnonymous()){
			enableSpeedRateButton();
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
		goButton.addClickHandler(personSelectedClickHandler());
		
		CommandResultCallback<SearchPostsCommandResult> callback = buildMovieListCallback(token);
		batcher.add(cmd, callback);
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
		speedRateButton.addClickHandler(speedRateClickHandler());
		exitSpeedRateButton.addClickHandler(exitSpeedRateClickHandler());
		
		pageHeaderPanel.init("Movies", subtitle);
		pageHeaderPanel.getSearchBoxPresenter().init();
				
	}
	
	private void setPageSubTitle(String subtitle) {
		pageHeaderPanel.init("Movies", subtitle);
	}
	
	public void enableSpeedRateButton(){
		personFilterElement.add(speedRateButton);
		personFilterElement.remove(exitSpeedRateButton);
	}
	
	public void enableExitSpeedRateButton() {
		personFilterElement.add(exitSpeedRateButton);
		personFilterElement.remove(speedRateButton);
	}
	
	public void addMovie(String year, Widget titleLink, Widget imdbLink, Widget rating) {
		//int row = movieTable.getRowCount() - 1;
		row++;
		
		tableElement.setWidget(row, 0, new Label(year));
		tableElement.setWidget(row, 1, imdbLink);
		tableElement.setWidget(row, 2, titleLink);
		tableElement.setWidget(row, 3, rating);
	}



	public String getSelectedPersonId() {
		int ndx = reviewers.getSelectedIndex();
		String personId = null;
		if(ndx >= 1)
			personId = reviewers.getValue(ndx);
			
		return personId;
	}
	
	
	private void setSelectedPersonId(String personId) {
		int index = 0;
		for(int i = 0 ; i < reviewers.getItemCount() ; i++){
			if(reviewers.getValue(i).equals(personId)){
				ratingLabel.setText(extractUserName(i)+"'s Rating");
				setPageSubTitle(extractUserName(i) + "'s movie ratings and reviews");
				index = i;
			}
		}
		reviewers.setSelectedIndex(index);
	}

	//This method strips off the review count from the dropdown text created in addPerson
	private String extractUserName(int i) {
		String str = reviewers.getItemText(i);
		return str.substring(0,str.lastIndexOf('(')).trim();
	}
	
	private void addPerson(String login, String personId, int reviewCount) {
		reviewers.addItem(login + " ("+reviewCount+")",personId);
		
	}	
	
	private ClickHandler speedRateClickHandler(){
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
	private ClickHandler personSelectedClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = new HistoryToken();
				newToken.load(token);
				newToken.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
				if(getSelectedPersonId() != null)
					newToken.setParameter(HistoryConstants.PERSON_ID, getSelectedPersonId());
				else
					newToken.removeParameter(HistoryConstants.PERSON_ID);
				EventBus.fireHistoryToken(newToken);
			}
		};
	}
	
	private ClickHandler exitSpeedRateClickHandler(){
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
		
		ratingHeaderPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RATING, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		releaseYearHeaderPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.MOVIES_SORT_BY_RELEASE_YEAR, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		titleHeaderPanel.addClickHandler(new ClickHandler() {
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
					addPerson(person.getLogin(), person.getPersonId(), Integer.parseInt(person.getValue()));
				}
				if(StringUtil.notEmpty(personId)){
					setSelectedPersonId(personId);
				}
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
					addMovie(year, titlePresenter.getWidget(), urlLinkPresenter.getWidget(), ratingPresenter.getWidget());
				}
				
				PaginationPresenter paginationPresenter = injector.getPaginationPresenter();
				paginationPresenter.initialize(token, result.getResults());
				paginatorElement.clear();
				paginatorElement.add(paginationPresenter.getWidget());
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
