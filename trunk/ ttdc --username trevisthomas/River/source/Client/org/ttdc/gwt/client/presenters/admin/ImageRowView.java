package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ImageRowView implements ImageRowPresenter.View{
	private final SimplePanel namePanel = new SimplePanel();
	private final FlowPanel controlPanel = new FlowPanel();
	private final Button deleteButton = new Button("Delete");
	private final Button updateButton = new Button("Update");
	private final TextBox nameTextBox = new TextBox();
	
	
	public ImageRowView() {
		controlPanel.add(updateButton);
		controlPanel.add(deleteButton);
		
		namePanel.add(nameTextBox);
	}
	
	@Override
	public Widget getWidget() {
		throw new RuntimeException("This view is not intended to be shown directly.");
	}
	
	@Override
	public HasClickHandlers deleteButton() {
		return deleteButton;
	}
	
	@Override
	public HasClickHandlers updateButton() {
		return updateButton;
	}

	@Override
	public Widget getControlsWidget() {
		return controlPanel;
	}

	@Override
	public Widget getNameWidget() {
		return namePanel;
	}

	@Override
	public HasText nameTextBox() {
		return nameTextBox;
	}

}
