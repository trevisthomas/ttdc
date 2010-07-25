package org.ttdc.gwt.client.uibinder.shared;

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
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserIdentityPanel extends Composite implements PersonEventListener, MessageEventListener{
	interface MyUiBinder extends UiBinder<Widget, UserIdentityPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    @UiField FlowPanel anonymousPanel; 
    @UiField FlowPanel userPanel;
    
    @UiField (provided = true) Anchor loginElement = new Anchor("login");
    @UiField (provided = true) Widget createElement;
    @UiField (provided = true) HTML welcomeMessageElement = new HTML("sup, ");
    @UiField (provided = true) Widget userElement;
    @UiField (provided = true) Anchor logoutElement = new Anchor("logout");
    
    private final PopupPanel loginPopup = new PopupPanel(true);
    private final HyperlinkPresenter personLink;
    private Injector injector;
    
    @Inject
	public UserIdentityPanel(final Injector injector) {
    	this.injector = injector;
    	GPerson person = ConnectionId.getInstance().getCurrentUser();
    	
//		loginPopup.add(injector.getLoginPresenter().getWidget());

		EventBus.getInstance().addListener((MessageEventListener)this);
		EventBus.getInstance().addListener((PersonEventListener)this);
		
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_TOOLS);
		token.addParameter(HistoryConstants.TAB_KEY, HistoryConstants.USER_CREATE_ACCOUNT_TAB);
		HyperlinkPresenter createAccountPresenter = injector.getHyperlinkPresenter();
		createAccountPresenter.setToken(token, "create");
		createElement = createAccountPresenter.getWidget();
		createElement.setStyleName("tt-inline");
		
		personLink = injector.getHyperlinkPresenter();
		userElement = personLink.getWidget();
		
		welcomeMessageElement.setStyleName("tt-inline");
		
		initWidget(binder.createAndBindUi(this));
		init(person);

	}
    
    @UiHandler("logoutElement")
    public void onClickLogout(ClickEvent event) {
		processLogout();
	}
	
    @UiHandler("loginElement")
    public void onClickLogin(ClickEvent event){
    	if(loginPopup.isShowing()){
			loginPopup.hide();
		}
		else{
			// Reposition the popup relative to the button
            int left = loginElement.getAbsoluteLeft();
            int top = loginElement.getAbsoluteTop() + loginElement.getOffsetHeight() - 1;
            loginPopup.setPopupPosition(left, top);
            // Show the popup
            loginPopup.show();	
		}
    }
    
    
	private void init(GPerson person) {
		loginPopup.clear();
		loginPopup.add(injector.getLoginPresenter().getWidget());
		//view.clear();
		if(person.isAnonymous()){
			modeLogin();
		}
		else{
			personLink.setPerson(person);
			modeLogout();
		}
	}
	
	private void modeLogout() {
		anonymousPanel.setVisible(false);
		userPanel.setVisible(true);
	}

	private void modeLogin() {
		anonymousPanel.setVisible(true);
		userPanel.setVisible(false);
	}

	
	//TODO: move this to LoginPresenter to have centralized handling.
	private void processLogout(){
		injector.getLoginPresenter().logout();
	}
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.VIEW_CHANGE)){
			hideLoginPopup();
		}
	}
	
	private void hideLoginPopup() {
		if(loginPopup.isShowing()){
			loginPopup.hide();
		}
	}

	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			modeLogout();
			hideLoginPopup();
			init(event.getSource());
		}
	}
}
