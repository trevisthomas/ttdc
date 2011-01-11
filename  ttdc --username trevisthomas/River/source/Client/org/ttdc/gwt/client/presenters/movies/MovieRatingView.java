package org.ttdc.gwt.client.presenters.movies;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MovieRatingView implements MovieRatingPresenter.View{
	//private final HorizontalPanel main = new HorizontalPanel();
	private final static String defaultMessage = "Click on a star to choose a rating.";
	private final FlowPanel starPanel = new FlowPanel();
	
	private final String CSS_STAR_RIGHT_PREFIX  = "tt-star-right-";
	private final String CSS_STAR_LEFT_PREFIX  = "tt-star-left-";
	private final Label descriptionLabel = new Label(defaultMessage);
	private boolean interactiveMode = false;
	private RatableContentProcessor rateMovie;
	private final Grid grid = new Grid(2,1);
	private List<HalfStarPanel> starList = new ArrayList<HalfStarPanel>();
	
	public MovieRatingView() {
		starPanel.setStyleName("tt-rating-widget");
		grid.setWidget(1, 0, starPanel);
		descriptionLabel.setStyleName("tt-rating-description");
		grid.setWidget(0, 0, descriptionLabel);
		grid.setStyleName("tt-rating-displayMode");
	}
	
	@Override
	public void setRating(String rating) {
		starPanel.clear();
		descriptionLabel.setVisible(false);
		float r = 0;
		if(rating != null)
			r = Float.parseFloat(rating);
		else
			return;
		
		createHalfStarPanel(r, 0.5f, CSS_STAR_LEFT_PREFIX," 1/2 star (epic fail)");
		createHalfStarPanel(r, 1.0f, CSS_STAR_RIGHT_PREFIX," 1 star (pretty awful)");
		createHalfStarPanel(r, 1.5f, CSS_STAR_LEFT_PREFIX," 1 and 1/2 stars (not good)");
		createHalfStarPanel(r, 2.0f, CSS_STAR_RIGHT_PREFIX," 2 stars (meh)");
		createHalfStarPanel(r, 2.5f, CSS_STAR_LEFT_PREFIX," 2 and 1/2 stars (alright i guess)");
		createHalfStarPanel(r, 3.0f, CSS_STAR_RIGHT_PREFIX," 3 stars (pretty good)");
		createHalfStarPanel(r, 3.5f, CSS_STAR_LEFT_PREFIX," 3 and 1/2 stars (really good)");
		createHalfStarPanel(r, 4.0f, CSS_STAR_RIGHT_PREFIX," 4 stars (excellent)");
		createHalfStarPanel(r, 4.5f, CSS_STAR_LEFT_PREFIX," 4 and 1/2 stars (among the best ever)");
		createHalfStarPanel(r, 5.0f, CSS_STAR_RIGHT_PREFIX," 5 stars (complete and utter perfection)");
	}
	
	@Override
	public void initShowMode(String rating){
		interactiveMode = false;
		setRating(rating);
		this.rateMovie = null;
		grid.setStyleName("tt-rating-displayMode");
		descriptionLabel.setVisible(false);
	}

	@Override
	public void initVoteMode(RatableContentProcessor rateMovie){
		grid.setStyleName("tt-rating-voteMode");
		interactiveMode = true;
		setRating("0.0");
		this.rateMovie = rateMovie;
		descriptionLabel.setVisible(true);
		descriptionLabel.setText(defaultMessage);
	}
	
	private HalfStarPanel createHalfStarPanel(float ratingValue, float halfStarValue, String cssPrefix, String description) {
		HalfStarPanel hstar;
		hstar = new HalfStarPanel(halfStarValue, cssPrefix, description);
		starPanel.add(hstar);
		if(ratingValue >= halfStarValue){
			hstar.setFilled();
		}
		
		starList.add(hstar);
		return hstar;
	}

	@Override
	public Widget getWidget() {
		return grid;
	}

	private class HalfStarPanel extends Label{
		private float value;
		private boolean active = false;
		private String cssPrefix;
		private final String description;
		
		HalfStarPanel(float value, String cssPrefix, String description){
			this.value = value;
			this.cssPrefix = cssPrefix;
			setClear();
			this.description = description;
			
			if(interactiveMode){
				addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						MovieRatingView.this.rateMovie.processRatingRequest(HalfStarPanel.this.value);
					}
				});
				addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						setFilled();
						for(HalfStarPanel hStarPanel : starList){
							if(hStarPanel.value <= HalfStarPanel.this.value){
								hStarPanel.setFilled();
								descriptionLabel.setText(hStarPanel.getDescription());
							}
							else{
								hStarPanel.setClear();
							}
						}
						
					}
				});
				addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						setClear();
						for(HalfStarPanel hStarPanel : starList){
							hStarPanel.setClear();
						}
						descriptionLabel.setText(defaultMessage);
					}
				});
			}
		}
		protected String getDescription() {
			return description;
		}
		public void setClear() {
			active = false;
			setStyleName(cssPrefix+"clear");
		}
		public void setFilled() {
			active = true;
			setStyleName(cssPrefix+"filled");
		}
		
		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return super.addClickHandler(handler);
		}
	}
}
