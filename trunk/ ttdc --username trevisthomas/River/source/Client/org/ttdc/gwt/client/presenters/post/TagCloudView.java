package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class TagCloudView implements TagCloudPresenter.View{
	private FlowPanel flowPanel = new FlowPanel();
	
	public TagCloudView() {
		flowPanel.setStyleName("tt-cloud");
		//flowPanel.setStylePrimaryName(style)
	}
		
	@Override
	public HasWidgets getTagWidgets() {
		return flowPanel;
	}

	@Override
	public Widget getWidget() {
		return flowPanel;
	}
	
}
