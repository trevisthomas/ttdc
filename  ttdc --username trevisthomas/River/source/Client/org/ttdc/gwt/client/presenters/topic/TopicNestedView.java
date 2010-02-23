package org.ttdc.gwt.client.presenters.topic;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicNestedView implements TopicNestedPresenter.View{
	private final Label title = new Label();
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel postTarget = new SimplePanel();
	private final SimplePanel pagination = new SimplePanel();
	private final SimplePanel rootPostTarget = new SimplePanel();
	private final SimplePanel searchTarget = new SimplePanel();
	
	public TopicNestedView() {
		main.add(searchTarget);
		main.add(title);
		main.add(rootPostTarget);
		main.add(postTarget);
		main.add(pagination);
	}
	
	@Override
	public HasWidgets postsTarget() {
		return postTarget;
	}

	@Override
	public HasText threadTitle() {
		return title;
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets paginationTarget() {
		return pagination;
	}

	@Override
	public HasWidgets rootTarget() {
		return rootPostTarget;
	}

	@Override
	public HasWidgets searchTarget() {
		return searchTarget;
	}

}
