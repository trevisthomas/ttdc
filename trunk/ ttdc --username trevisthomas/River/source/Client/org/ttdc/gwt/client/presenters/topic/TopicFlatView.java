package org.ttdc.gwt.client.presenters.topic;


import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicFlatView implements TopicFlatPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final Grid postGrid = new Grid(1,2);
	private final VerticalPanel rootPanel = new VerticalPanel();
	private final VerticalPanel postPanel = new VerticalPanel();
	private final Label topicTitle = new Label(); 
	private final SimplePanel searchPanel = new SimplePanel();
	
	public TopicFlatView() {
		main.add(searchPanel);
		main.add(topicTitle);//Trevis, maybe this should be the fancy widget that shows counts and what not? 
		//(i mean the one that you intend to create for the nested view)
		postGrid.setWidget(0, 0, rootPanel);
		postGrid.setWidget(0, 1, postPanel);
		
		main.add(postGrid);
	}
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets postsTarget() {
		return postPanel;
	}

	@Override
	public HasWidgets rootTarget() {
		return rootPanel;
	}

	@Override
	public HasText threadTitle() {
		return topicTitle;
	}

	@Override
	public HasWidgets searchTarget() {
		return searchPanel;
	}
}
