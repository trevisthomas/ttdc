package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RequestPasswordResetView implements RequestPasswordResetPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final Grid formGrid = new Grid(3,2);
	private final TextBox emailTextBox = new TextBox();
	private final TextBox loginTextBox = new TextBox();
	private final Button submitButton = new Button("Submit");
	
	public RequestPasswordResetView() {
		formGrid.setWidget(0, 0, new HTML("Email"));
		formGrid.setWidget(0, 1, emailTextBox);
		formGrid.setWidget(1, 0, new HTML("Login"));
		formGrid.setWidget(1, 1, loginTextBox);
		formGrid.setWidget(2, 1, submitButton);
		main.add(formGrid);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasText emailTextBox() {
		return emailTextBox;
	}

	@Override
	public HasText loginTextBox() {
		return loginTextBox;
	}

	@Override
	public HasClickHandlers submitClickHandler() {
		return submitButton;
	}

	@Override
	public void showSuccess(String message) {
		main.clear();
		main.add(new HTML(message));
	}

	
	

}
