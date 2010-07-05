package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * 
 * SEE User IdentityPanel!
 * 
 * @deprecated
 *
 */
public class UserIdentityPresenter extends BasePresenter<UserIdentityPresenter.View> implements PersonEventListener, MessageEventListener{
	public interface View extends BaseView{
		HasClickHandlers loginButton();
		HasWidgets logoutPanel();
		HasClickHandlers logoutButton();
		HasWidgets authenticatedUserPanel();
		void modeLogin();
		void modeLogout();
		void clear();
		void setLoginWidget(Widget w);
		void hideLoginPopup();
		HasWidgets accountCreatePanel();
		
	}
	
	@Inject
	public UserIdentityPresenter(final Injector injector) {
		super(injector,injector.getUserIdentityView());
		
		LoadCurrentUser.load(new Worker());
		
		view.logoutButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processLogout();
			}
		});
		
		view.setLoginWidget(injector.getLoginPresenter().getWidget()); 
		EventBus.getInstance().addListener((MessageEventListener)this);
		EventBus.getInstance().addListener((PersonEventListener)this);
	}
	
	private void init(GPerson person) {
		view.clear();
		if(person.isAnonymous()){
			view.modeLogin();
			
			HistoryToken token = new HistoryToken();
			token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_TOOLS);
			token.addParameter(HistoryConstants.TAB_KEY, HistoryConstants.USER_CREATE_ACCOUNT_TAB);
			HyperlinkPresenter createAccountPresenter = injector.getHyperlinkPresenter();
			createAccountPresenter.setToken(token, "create");
			view.accountCreatePanel().add(createAccountPresenter.getWidget());
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
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.VIEW_CHANGE)){
			view.hideLoginPopup();
		}
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			view.modeLogout();
			view.hideLoginPopup();
			init(event.getSource());
		}
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
