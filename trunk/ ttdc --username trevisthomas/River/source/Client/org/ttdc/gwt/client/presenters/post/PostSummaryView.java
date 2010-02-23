package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PostSummaryView implements PostSummaryPresenter.View{
	
	private final FlowPanel mainPanel = new FlowPanel();
	private final SimplePanel personPanel = new SimplePanel();
	private final Label entrySummaryLabel = new Label();
	private final FlowPanel spacerPanel = new FlowPanel();
	
	public PostSummaryView() {
		personPanel.setStyleName("tt-inline");
		entrySummaryLabel.setStyleName("tt-post-summary-text");
		entrySummaryLabel.addStyleName("tt-inline");
		spacerPanel.setStyleName("tt-post-summary-spacer");
		mainPanel.add(spacerPanel);
		mainPanel.add(entrySummaryLabel);
		mainPanel.add(personPanel);
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public HasWidgets personTarget() {
		return personPanel;
	}

	@Override
	public HasText entrySummaryTarget() {
		return entrySummaryLabel;
	}

	@Override
	public HasClickHandlers toggleTarget() {
		return entrySummaryLabel;
	}

	@Override
	public void setTabCount(int tabCount) {
		spacerPanel.clear();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i <= tabCount ; i++){
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		if(sb.length() > 0)
			spacerPanel.add(new HTMLPanel(sb.toString()));
	}

	/**
	 * This method replaces the current view's main panel widget with the provided widget
	 * This was created for post expand contract functionality.
	 */
	@Override
	public void replaceMeWith(Widget w) {
		mainPanel.clear();
		mainPanel.add(spacerPanel);
		mainPanel.add(w);
	}

	@Override
	public void revert() {
		if(!entrySummaryLabel.isAttached()){
			mainPanel.clear();
			mainPanel.add(spacerPanel);
			mainPanel.add(entrySummaryLabel);
			mainPanel.add(personPanel);
		}
	}


}
