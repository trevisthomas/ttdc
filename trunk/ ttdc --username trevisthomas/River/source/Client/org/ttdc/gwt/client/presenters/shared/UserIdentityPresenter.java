package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.presenters.util.LoadCurrentUser;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class UserIdentityPresenter extends BasePresenter<UserIdentityPresenter.View>{
	public interface View extends BaseView{
		HasText loginTextBox();
		HasText passwordTextBox();
		HasClickHandlers loginButton();
		HasWidgets logoutPanel();
		HasClickHandlers logoutButton();
		HasWidgets authenticatedUserPanel();
		void modeLogin();
		void modeLogout();
		void clear();
	}
	
	@Inject
	public UserIdentityPresenter(final Injector injector) {
		super(injector,injector.getUserIdentityView());
		
		LoadCurrentUser.load(new Worker());
		
		view.loginButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RpcServiceAsync service = injector.getService();
				CommandResultCallback<PersonCommandResult> callback = loginCallback();
				service.authenticate(view.loginTextBox().getText(), view.passwordTextBox().getText(), callback);
			}
		});
		
		view.logoutButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processLogout();
			}
		});
	}
	
	private void init(GPerson person) {
		view.clear();
		if(person.isAnonymous()){
			view.modeLogin();
		}
		else{
			HyperlinkPresenter personLink = injector.getHyperlinkPresenter();
			personLink.setPerson(person);
			view.authenticatedUserPanel().add(personLink.getWidget());
			view.modeLogout();
		}
	}
	
	private void processLogout(){
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				GPerson person = result.getPerson();
				CookieTool.clear();
				ConnectionId.getInstance().setCurrentUser(person);
				PersonEvent personEvent = new PersonEvent(PersonEventType.USER_CHANGED, person);
				EventBus.fireEvent(personEvent);
				init(person);
			}
		};
		injector.getService().logout(callback);	
	}
	
	
	private CommandResultCallback<PersonCommandResult> loginCallback() {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				GPerson person = result.getPerson();
				ConnectionId.getInstance().setCurrentUser(person);
				
				//Consider what to do about cookies.
//				if(view.cookieMeCheckBox().getValue()){
					CookieTool.saveGuid(person.getPersonId());
					CookieTool.savePwd(person.getPassword());
//				}
				
				//This is how i notify others that the user has changed.
				PersonEvent personEvent = new PersonEvent(PersonEventType.USER_CHANGED, person);
				EventBus.fireEvent(personEvent);
				Window.alert(ConnectionId.getInstance().getConnectionId());

				init(person);
			}
		};
		return callback;
	}
	
	/**
	 * 
	 * A wacky little worker class to allow me to wait on an asynchronous call 
	 * to get the current user
	 *
	 */
	private class Worker implements LoadCurrentUser.CurrentUserResponse{
		@Override
		public void setCurrentUser(GPerson person) {
			init(person);
		}
	}
	
	
}
