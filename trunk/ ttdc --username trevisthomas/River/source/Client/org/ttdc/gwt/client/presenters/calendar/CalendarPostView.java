package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarPostView implements CalendarPostPresenter.View{
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final FlowPanel headerPanel = new FlowPanel();
	private final SimplePanel postSummaryPanel = new SimplePanel();
	private final SimplePanel personPanel = new SimplePanel();
	private final SimplePanel threadPanel = new SimplePanel();
	private final Label entrySummaryLabel = new Label();
	
	private String datetime = null;

	public CalendarPostView() {
		mainPanel.add(headerPanel);
		mainPanel.add(postSummaryPanel);
		entrySummaryLabel.setStyleName("tt-calendar-post-summary");
	}
	
	@Override
	public Widget getWidget() {
		headerPanel.setStyleName("tt-inline");
		Widget w = personPanel.getWidget();
		w.setStyleName("tt-inline");
		headerPanel.add(w);
		
		w = new HTMLPanel("<b>&nbsp;:&nbsp;</b>");
		w.setStyleName("tt-inline");
		headerPanel.add(w);
		
		
		w = threadPanel.getWidget();
		w.setStyleName("tt-inline");
		headerPanel.add(w);
		
		if(datetime != null){
			w = new HTMLPanel(datetime);
			w.setStyleName("tt-inline");
			headerPanel.add(w);
		}
		return mainPanel;
	}

	@Override
	public HasWidgets personTarget() {
		return personPanel;
	}

	@Override
	public HasWidgets threadTarget() {
		return threadPanel;
	}

	//This is not in use yet
	@Override
	public HasText entrySummaryTarget() {
		if(!entrySummaryLabel.isAttached()){
			postSummaryPanel.add(entrySummaryLabel);
		}
		return entrySummaryLabel;
	}

	@Override
	public HasText getDateTarget() {
		return new FakeDateTarget();
	}
	
	/*
	 * 
	 * I do this fake thing so that the time can be set as text and the ui still feels like gwt, though
	 * this thing does something weird with the text
	 *
	 */
	private class FakeDateTarget implements HasText{
		@Override
		public String getText() {
			return datetime;
		}
		@Override
		public void setText(String text) {
			datetime = text;
		}
	}
}
