package org.ttdc.gwt.client.presenters.topic;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicView implements TopicPresenter.View{
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final SimplePanel topicPanel = new SimplePanel();
	private final SimplePanel messagesPanel = new SimplePanel();

	public TopicView() {
		mainPanel.add(messagesPanel);
		mainPanel.add(topicPanel);
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
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(mainPanel);
	}
	
}
