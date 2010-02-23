package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.PageMessagesPresenter;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class LoginPresenter extends BasePresenter<LoginPresenter.View> {
	public interface View extends BaseView{
		HasText loginTextBox();
		HasText passwordTextBox();
		HasClickHandlers loginButton();
		HasValue<Boolean> cookieMeCheckBox();
		HasWidgets messagesPanel();
	}
	
	private PageMessagesPresenter pageMessagesPresenter;
	
	@Inject
	public LoginPresenter(Injector injector) {
		super(injector,injector.getLoginView());
		pageMessagesPresenter = injector.getPageMessagesPresenter();
		view.messagesPanel().add(pageMessagesPresenter.getWidget());
		init();
		CookieTool.clear();
		logout();
	}
	
	private void logout() {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				ConnectionId.getInstance().setCurrentUser(result.getPerson());
			}
		};
		RpcServiceAsync service = injector.getService();
		service.logout(callback);
		
	}

	public void init() {
		view.loginButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RpcServiceAsync service = injector.getService();
				CommandResultCallback<PersonCommandResult> callback = loginCallback();
				service.authenticate(view.loginTextBox().getText(), view.passwordTextBox().getText(), callback);
			}
		});
	}
	
	private CommandResultCallback<PersonCommandResult> loginCallback() {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				GPerson person = result.getPerson();
				ConnectionId.getInstance().setCurrentUser(person);
				
				if(view.cookieMeCheckBox().getValue()){
					CookieTool.saveGuid(person.getPersonId());
					CookieTool.savePwd(person.getPassword());
				}

				EventBus.reloadHome();
				
				EventBus.fireMessage("Hi, "+person.getLogin());
			}
			@Override
			public void onFailure(Throwable caught) {
				//ConnectionId.getInstance().setCurrentUser(null);
				EventBus.fireErrorMessage(caught.getMessage());
				//CookieTool.clear();
			}
		};
		return callback;
	}

}
