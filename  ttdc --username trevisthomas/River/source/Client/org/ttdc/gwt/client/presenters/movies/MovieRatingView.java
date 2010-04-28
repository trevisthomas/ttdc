package org.ttdc.gwt.client.presenters.movies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MovieRatingView implements MovieRatingPresenter.View{
	private final HorizontalPanel main = new HorizontalPanel();
	private final Label temp = new Label();
	private final String CSS_STAR_RIGHT_PREFIX  = "tt-star-right-";
	private final String CSS_STAR_LEFT_PREFIX  = "tt-star-left-";
	private boolean interactiveMode = false;
	private RatableContentProcessor rateMovie;
	List<HalfStarPanel> starList = new ArrayList<HalfStarPanel>();
	
	public MovieRatingView() {
		main.add(temp);
		main.setStyleName("tt-rating-widget");
		
	}
	
	@Override
	public void setRating(String rating) {
		main.clear();
		float r = Float.parseFloat(rating);
		//HalfStarPanel hstar; 
		createHalfStarPanel(r, 0.5f, CSS_STAR_LEFT_PREFIX);
		createHalfStarPanel(r, 1.0f, CSS_STAR_RIGHT_PREFIX);
		createHalfStarPanel(r, 1.5f, CSS_STAR_LEFT_PREFIX);
		createHalfStarPanel(r, 2.0f, CSS_STAR_RIGHT_PREFIX);
		createHalfStarPanel(r, 2.5f, CSS_STAR_LEFT_PREFIX);
		createHalfStarPanel(r, 3.0f, CSS_STAR_RIGHT_PREFIX);
		createHalfStarPanel(r, 3.5f, CSS_STAR_LEFT_PREFIX);
		createHalfStarPanel(r, 4.0f, CSS_STAR_RIGHT_PREFIX);
		createHalfStarPanel(r, 4.5f, CSS_STAR_LEFT_PREFIX);
		createHalfStarPanel(r, 5.0f, CSS_STAR_RIGHT_PREFIX);
	}
	
	

	@Override
	public void initVoteMode(RatableContentProcessor rateMovie){
		interactiveMode = true;
		setRating("0.0");
		this.rateMovie = rateMovie;
	}
	
	private HalfStarPanel createHalfStarPanel(float ratingValue, float halfStarValue, String cssPrefix) {
		HalfStarPanel hstar;
		hstar = new HalfStarPanel(halfStarValue, cssPrefix);
		main.add(hstar);
		if(ratingValue >= halfStarValue){
			hstar.setFilled();
		}
		
		starList.add(hstar);
		return hstar;
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	private class HalfStarPanel extends Label{
		private float value;
		private boolean active = false;
		private String cssPrefix;
		
		HalfStarPanel(float value, String cssPrefix){
			this.value = value;
			this.cssPrefix = cssPrefix;
			setClear();
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
							if(hStarPanel.value <= HalfStarPanel.this.value)
								hStarPanel.setFilled();
							else
								hStarPanel.setClear();
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
					}
				});
			}
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
			// TODO Auto-generated method stub
			return super.addClickHandler(handler);
		}
	}
}
