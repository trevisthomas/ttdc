package org.ttdc.gwt.client.presenters.topic;


import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicFlatView implements TopicFlatPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final VerticalPanel postPanel = new VerticalPanel();
	
	public TopicFlatView() {
		main.add(postPanel);
	}
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets postsTarget() {
		return postPanel;
	}
}
