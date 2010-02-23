package org.ttdc.gwt.client.components.widgets.login;

import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

final class LogoutEventHandler implements ClickHandler{
	private final RpcServiceAsync service = GWT.create(RpcService.class);
	
	private final LoginWidget loginWidget;
	public LogoutEventHandler(LoginWidget loginWidget){
		this.loginWidget = loginWidget;
	}
	public void onClick(ClickEvent event) {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				loginWidget.changeCurrentUserTo(result.getPerson());
				CookieTool.clear(); //I just added this on Dec 25
			}
		};
		service.logout(callback);
	}
	
}


