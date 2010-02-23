package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagRemoveView implements TagRemovePresenter.View{

	private final Label tagName = new Label();
	private final Button removeButton = new Button();
	private final Grid mainPanel = new Grid(1,2);
	
	public TagRemoveView() {
		mainPanel.setWidget(0, 0, tagName);
		mainPanel.setWidget(0, 1, removeButton);
		mainPanel.addStyleName("tt-hyperLinkView");
	}
	
	public HasClickHandlers getRemoveTagClickHandler() {
		return removeButton;
	}

	public HasText getTextTarget() {
		return tagName;
	}

	public Widget getWidget() {
		return mainPanel;
	}
	
}
