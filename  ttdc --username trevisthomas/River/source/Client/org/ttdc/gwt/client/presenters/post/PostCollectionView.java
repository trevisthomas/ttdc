package org.ttdc.gwt.client.presenters.post;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PostCollectionView implements PostCollectionPresenter.View{
	private final Button expandToggle = new Button();
	private final VerticalPanel postPanel = new VerticalPanel();
	
	public HasClickHandlers getToggleExpandHandler() {
		return expandToggle;
	}

	public Widget getWidget() {
		return postPanel;
	}
	
	public PostCollectionView() {
		//postPanel.addStyleName("tt-post-container");
	}

	public void setExpanded(boolean expanded) {
		// TODO Auto-generated method stub
		
	}

	public HasWidgets getPostWidgets() {
		return postPanel;
	}
}
