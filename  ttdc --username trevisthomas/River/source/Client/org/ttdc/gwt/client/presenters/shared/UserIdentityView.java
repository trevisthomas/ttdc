package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserIdentityView implements UserIdentityPresenter.View{
	private final FlowPanel main = new FlowPanel(); 
	private final Button loginButton = new Button("Go");
	private final SimplePanel logoutPanel = new SimplePanel();
	private final Grid fromGrid = new Grid(2,3);
	private final TextBox loginTextBox = new TextBox();
	private final PasswordTextBox passwordTextBox = new PasswordTextBox();
	private final SimplePanel userPanel = new SimplePanel();
	private final Anchor logoutLink = new Anchor("Logout");

	public UserIdentityView() {
		KeyUpHandler handler = clickLoginButtonOnEnterKeyUpHandler();
		loginTextBox.addKeyUpHandler(handler);
		passwordTextBox.addKeyUpHandler(handler);
		
	}
	
	private KeyUpHandler clickLoginButtonOnEnterKeyUpHandler() {
		return new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					loginButton.click();
				}
			}
		};
	}

	@Override
	public Widget getWidget() {
		return main;
		
		
	}

	@Override
	public void modeLogin(){
		main.clear();
		main.add(fromGrid);
		fromGrid.setWidget(0, 0, createLabel("Username"));
		fromGrid.setWidget(1, 0, loginTextBox);
		fromGrid.setWidget(0, 1, createLabel("Password"));
		fromGrid.setWidget(1, 1, passwordTextBox);
		fromGrid.setWidget(0, 2, createLabel(""));
		fromGrid.setWidget(1, 2, loginButton);
	}
	
	private Label createLabel(String text){
		Label label = new Label(text);
		label.setStyleName("tt-text-mini");
		label.addStyleName("tt-text-center");
		return label;
	}
	
	@Override
	public void modeLogout(){
		main.clear();
		main.add(userPanel);
		main.add(logoutLink);
	}
	
	@Override
	public HasClickHandlers loginButton() {
		return loginButton;
	}
	
	@Override
	public HasText loginTextBox() {
		return loginTextBox;
	}

	@Override
	public HasText passwordTextBox() {
		return passwordTextBox;
	}


	@Override
	public HasWidgets authenticatedUserPanel() {
		return userPanel;
	}

	@Override
	public HasWidgets logoutPanel() {
		return logoutPanel;
	}

	@Override
	public HasClickHandlers logoutButton() {
		return logoutLink;
	}

	@Override
	public void clear() {
		loginTextBox.setText("");
		passwordTextBox.setText("");
		userPanel.clear();
	}

}
