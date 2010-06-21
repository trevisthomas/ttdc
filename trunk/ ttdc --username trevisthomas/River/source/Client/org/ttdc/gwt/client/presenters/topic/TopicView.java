package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.presenters.home.TabType;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicView implements TopicPresenter.View{
	private final Label title = new Label();
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final SimplePanel topicPanel = new SimplePanel();
	private final SimplePanel messagesPanel = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	private final Grid bodyTable = new Grid(1,2);
	private final TabPanel replyTabPanel = new TabPanel();
	private final SimplePanel flatPanel = new SimplePanel();
	private final SimplePanel nestedPanel = new SimplePanel(); 
	private final SimplePanel searchTarget = new SimplePanel();

	public final static int INDEX_FLAT = 0;
	public final static int INDEX_NESTED = 1;
	
	
	public TopicView() {
		mainPanel.add(navigationPanel);
		mainPanel.add(messagesPanel);
		mainPanel.add(searchTarget);
		mainPanel.add(title);
		mainPanel.add(bodyTable);
		bodyTable.setWidget(0, 0, topicPanel);
		bodyTable.setWidget(0, 1, replyTabPanel);
		
		replyTabPanel.add(flatPanel, "Flat");
		replyTabPanel.add(nestedPanel, "Nested");
	}
	
	@Override
	public void displayTab(TabType selected) {
		switch(selected){
			case FLAT:
				replyTabPanel.selectTab(INDEX_FLAT);
				break;
			case NESTED:
				replyTabPanel.selectTab(INDEX_NESTED);
				break;
		}
		
	}
	
	@Override
	public void addTabSelectionHandler(SelectionHandler<Integer> handler){
		replyTabPanel.addSelectionHandler(handler);
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}
	
	@Override
	public HasWidgets messagePanel() {
		return messagesPanel;
	}
	
	@Override
	public HasWidgets topicTarget() {
		return topicPanel;
	}

	@Override
	public Widget getWidget() {
		return mainPanel;
	}
	
	@Override
	public HasWidgets flatPanel(){
		return flatPanel;
	}
	
	@Override
	public HasWidgets nestedPanel(){
		return nestedPanel;
	}
	
	@Override
	public HasText topicTitle(){
		return title;
	}
	
	@Override
	public HasWidgets searchTarget() {
		return searchTarget;
	}

	@Override
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(mainPanel);
	}
	
}
