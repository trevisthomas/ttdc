package org.ttdc.gwt.client.components.widgets.login;

import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

final class LoginEventHandler implements ClickHandler, KeyUpHandler{
	private final LoginWidget loginWidget;
	private final RpcServiceAsync service = GWT.create(RpcService.class);
	public LoginEventHandler(LoginWidget loginWidget){
		this.loginWidget = loginWidget;
	}
	
	public void onClick(ClickEvent event) {
		doLogin();
	}
	
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doLogin();
		}
	}

	private void doLogin() {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				loginWidget.changeCurrentUserTo(result.getPerson());
			}
		};
		service.authenticate(loginWidget.getLoginText(), loginWidget.getPasswordText(), callback);
	}
	
}
