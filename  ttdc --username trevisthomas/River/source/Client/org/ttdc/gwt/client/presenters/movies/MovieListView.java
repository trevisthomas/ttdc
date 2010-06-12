package org.ttdc.gwt.client.presenters.movies;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MovieListView implements MovieListPresenter.View {
	private final VerticalPanel main = new VerticalPanel();
	
	private final SimplePanel paginator = new SimplePanel();
	
	private final SimplePanel releaseYear = new SimplePanel();
	private final SimplePanel title = new SimplePanel();
	private final SimplePanel rating = new SimplePanel();
	private final SimplePanel imdb = new SimplePanel();
	
	private final FocusPanel titleHeaderPanel = new FocusPanel();
	private final FocusPanel releaseYearHeaderPanel = new FocusPanel();
	private final FocusPanel ratingHeaderPanel = new FocusPanel();
	
	
	private final FlowPanel personFilter = new FlowPanel();
	private final Label imdbHeaderLabel = new Label("IMDB");
	private final ListBox reviewers = new ListBox(false);
	private final Button goButton = new Button();
	private final SimplePanel messagesPanel = new SimplePanel();
	private final Label ratingLabel = new Label("Average Rating");
	
	private final FlexTable movieTable = new FlexTable();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	int row = 0;
	
	public MovieListView() {
		main.add(navigationPanel);
		main.add(messagesPanel);
		reviewers.addItem("-- Reviewers --","-1");
		
		goButton.setText("Go");
		personFilter.add(reviewers);
		personFilter.add(goButton);
		main.add(personFilter);
		
		movieTable.setWidget(0, 0, releaseYearHeaderPanel);
		movieTable.setWidget(0, 1, imdb);
		movieTable.setWidget(0, 2, titleHeaderPanel);
		movieTable.setWidget(0, 3, ratingHeaderPanel);
		
		releaseYearHeaderPanel.add(new Label("Release Year"));
		titleHeaderPanel.add(new Label("Movie Title"));
		ratingHeaderPanel.add(ratingLabel);
		

		imdb.add(imdbHeaderLabel);
		
		main.add(movieTable);
		main.add(paginator);
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}
	
	@Override
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(main);		
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets paginator() {
		return paginator;
	}

	@Override
	public HasWidgets ratingColumnHeader() {
		return rating;
	}

	@Override
	public HasWidgets releaseYearColumnHeader() {
		return releaseYear;
	}

	@Override
	public HasWidgets titleColumnHeader() {
		return title;
	}

	@Override
	public void addMovie(String year, Widget titleLink, Widget imdbLink, Widget rating) {
		//int row = movieTable.getRowCount() - 1;
		row++;
		
		movieTable.setWidget(row, 0, new Label(year));
		movieTable.setWidget(row, 1, imdbLink);
		movieTable.setWidget(row, 2, titleLink);
		movieTable.setWidget(row, 3, rating);
	}



	@Override
	public String getSelectedPersonId() {
		int ndx = reviewers.getSelectedIndex();
		String personId = null;
		if(ndx > 1)
			personId = reviewers.getValue(ndx);
			
		return personId;
	}
	
	@Override
	public void setSelectedPersonId(String personId) {
		int index = 0;
		for(int i = 0 ; i < reviewers.getItemCount() ; i++){
			if(reviewers.getValue(i).equals(personId)){
				ratingLabel.setText(extractUserName(i)+"'s Rating");
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
	
	@Override
	public void addPerson(String login, String personId, int reviewCount) {
		reviewers.addItem(login + " ("+reviewCount+")",personId);
		
	}

	@Override
	public HasClickHandlers goButton() {
		return goButton;
	}

	@Override
	public HasWidgets messagePanel() {
		return messagesPanel;
	}

	@Override
	public HasClickHandlers ratingSortClickHandler() {
		return ratingHeaderPanel;
	}

	@Override
	public HasClickHandlers releaseYearSortClickHandler() {
		return releaseYearHeaderPanel;
	}

	@Override
	public HasClickHandlers titleSortClickHandler() {
		return titleHeaderPanel;
	}
}
