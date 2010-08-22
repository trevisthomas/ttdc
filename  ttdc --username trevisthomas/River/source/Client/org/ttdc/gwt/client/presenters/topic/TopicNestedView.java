package org.ttdc.gwt.client.presenters.topic;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@Deprecated 
/**
 * see TopicNestedPanel 
 */

public class TopicNestedView implements TopicNestedPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel postTarget = new SimplePanel();
	private final SimplePanel pagination = new SimplePanel();
		
	public TopicNestedView() {
		main.add(postTarget);
		main.add(pagination);
	}
	
	@Override
	public HasWidgets postsTarget() {
		return postTarget;
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets paginationTarget() {
		return pagination;
	}
}
