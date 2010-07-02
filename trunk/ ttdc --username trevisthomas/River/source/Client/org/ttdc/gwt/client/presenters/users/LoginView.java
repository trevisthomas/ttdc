package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginView implements LoginPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final Grid formGrid = new Grid(3,2);
	private final TextBox login = new TextBox();
	private final PasswordTextBox password = new PasswordTextBox();
	private final CheckBox cookieMe = new CheckBox();
	private final Button loginButton = new Button("Login");
	private final SimplePanel messagesPanel = new SimplePanel();
	
	public LoginView() {
		cookieMe.setText("Remember Me?");
		
		main.add(messagesPanel);
		main.add(formGrid);
		main.add(loginButton);
		formGrid.setWidget(0, 0, new Label("Login"));
		formGrid.setWidget(0, 1, login);
		formGrid.setWidget(1, 0, new Label("Password"));
		formGrid.setWidget(1, 1, password);
		//formGrid.setWidget(, 0, new Label("Password"));
		formGrid.setWidget(2, 1, cookieMe);
		
		main.add(loginButton);
		KeyUpHandler handler = clickLoginButtonOnEnterKeyUpHandler();
		login.addKeyUpHandler(handler);
		password.addKeyUpHandler(handler);
		cookieMe.addKeyUpHandler(handler);
		
		
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
	public HasValue<Boolean> cookieMeCheckBox() {
		return cookieMe;
	}

	@Override
	public HasClickHandlers loginButton() {
		return loginButton;
	}

	@Override
	public HasText loginTextBox() {
		return login;
	}

	@Override
	public HasText passwordTextBox() {
		return password;
	}

	@Override
	public HasWidgets messagesPanel() {
		return messagesPanel;
	}
}
