package org.ttdc.gwt.client.presenters.dashboard;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ResetPasswordView implements ResetPasswordPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final Grid formTable = new Grid(4,2);
	private final PasswordTextBox newPassword = new PasswordTextBox();
	private final PasswordTextBox verifyPassword = new PasswordTextBox();
	private final PasswordTextBox oldPassword = new PasswordTextBox();
	private final Button submitButton = new Button("Reset Password");
	
	public ResetPasswordView() {
		main.add(formTable);
		formTable.setWidget(0, 0, new Label("Old Password"));
		formTable.setWidget(0, 1, oldPassword);
		formTable.setWidget(1, 0, new Label("New Password"));
		formTable.setWidget(1, 1, newPassword);
		formTable.setWidget(2, 0, new Label("Verify New Password"));
		formTable.setWidget(2, 1, verifyPassword);
		formTable.setWidget(3, 1, submitButton);
	}
	
	@Override
	public Widget getWidget() {
		return main;
		
	}

	@Override
	public HasText newPasswordText() {
		return newPassword;
	}

	@Override
	public HasText oldPasswordText() {
		return oldPassword;
	}

	@Override
	public HasText verifyPasswordText() {
		return verifyPassword;
	}
	
	@Override
	public HasClickHandlers submitClickHandler() {
		return submitButton;
	}
}
