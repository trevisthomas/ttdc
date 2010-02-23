package org.ttdc.gwt.client.components.widgets.login;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public final class LoginWidget extends Composite implements PersonEventListener {
	private final HorizontalPanel root = new HorizontalPanel();
	private final TextBox loginTextBox = new TextBox();
	private final TextBox passwordTextBox = new PasswordTextBox();
	private final Label loginLabel = new Label("Login");
	private final Label passwordLabel = new Label("Password");
	private final Button loginButton = new Button("Login");
	private final Button logoutButton = new Button("Logout",new LogoutEventHandler(this));
	private final RpcServiceAsync service = GWT.create(RpcService.class);
		
	public static LoginWidget createInstance(){
		LoginWidget widget = new LoginWidget();
		return widget;
	}
	
	
	private LoginWidget(){
		EventBus.getInstance().addListener(this);
		LoginEventHandler handler = new LoginEventHandler(this);
		passwordTextBox.addKeyUpHandler(handler);
		loginTextBox.addKeyUpHandler(handler);
		loginButton.addClickHandler(handler);
		
		loadUserFromSession();
		
		initWidget(root);
	}
	
	private void loadUserFromSession() {
		//TODO: pass in the person id from the cookie if one exists
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				changeCurrentUserTo(result.getPerson());
			}
		};
		service.login(CookieTool.readGuid(),CookieTool.readPwd(), callback);
		
	}
	
	private void buildLoginView(){
		root.clear();
		root.add(loginLabel);
		root.add(loginTextBox);
		root.add(passwordLabel);
		root.add(passwordTextBox);
		root.add(loginButton);
	}
	
	private void buildLogoutView(GPerson person){
		root.clear();
		root.add(new Label("Hi, "+person.getLogin()));
		root.add(logoutButton);
	}
	
	public void changeCurrentUserTo(GPerson person) {
		PersonEvent personEvent = new PersonEvent(PersonEventType.USER_CHANGED, person);
		EventBus.getInstance().fireEvent(personEvent);
	}
	
	
	public void onPersonEvent(PersonEvent event) {
		if(event.getType() == PersonEventType.USER_CHANGED){
			GPerson person = event.getSource();
			if(person.isAnonymous()){
				buildLoginView();
			}
			else {
				loginTextBox.setText("");
				passwordTextBox.setText("");
				buildLogoutView(person);
			}
		}
	}
	
	public String getLoginText() {
		return loginTextBox.getText();
	}
	public void setLoginText(String loginText) {
		loginTextBox.setText(loginText);
	}
	public String getPasswordText() {
		return passwordTextBox.getText();
	}
	public void setPasswordText(String passwordText) {
		loginTextBox.setText(passwordText);
	}
	
}
