package org.ttdc.gwt.client.uibinder.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.presenters.users.CreateAccountPresenter;
import org.ttdc.gwt.client.presenters.users.LoginPresenter;
import org.ttdc.gwt.client.presenters.users.RequestPasswordResetPresenter;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.post.NestedPostPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserToolsPanel extends BasePageComposite{
	interface MyUiBinder extends UiBinder<Widget, UserToolsPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private final SimplePanel loginPanel = new SimplePanel();
	private final SimplePanel createPanel = new SimplePanel();
	private final SimplePanel requestPasswordResetPanel = new SimplePanel();
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField TabPanel tabPanelElement;
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	HistoryToken token;
	
	@Inject
	public UserToolsPanel(Injector injector) {
		this.injector = injector;
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	tabPanelElement.setStyleName("tt-TabPanel-fullpage");
    	tabPanelElement.setAnimationEnabled(true);
    	tabPanelElement.add(loginPanel, "Login");
    	tabPanelElement.add(createPanel, "Create Account");
    	tabPanelElement.add(requestPasswordResetPanel, "Forgotten Password");
	    

    	tabPanelElement.selectTab(0); //Default (other wise it comes up with none shown, pretty weird)
	    
    	tabPanelElement.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!tabPanelElement.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectTabSelection(index);
			}
		});
		
		
		
	}

	@Override
	protected void onShow(HistoryToken token) {
		LoginPresenter loginPresenter = injector.getLoginPresenter();
		loginPanel.add(loginPresenter.getWidget());
		
		CreateAccountPresenter createAccountPresenter = injector.getCreateAccountPresenter();
		createPanel.add(createAccountPresenter.getWidget());
		createAccountPresenter.init();
		
		RequestPasswordResetPresenter requestPassowrdResetPresenter = injector.getRequestPasswordPresenter();
		requestPasswordResetPanel.add(requestPassowrdResetPresenter.getWidget());
		requestPassowrdResetPresenter.init();
				
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		
		CookieTool.clear(); //I added this here after removing it from the LoginPresenter
		
		if(StringUtil.notEmpty(tab)){
			if(HistoryConstants.USER_LOGIN_TAB.equals(tab)){
				displayLoginTab();
			}
			else if(HistoryConstants.USER_CREATE_ACCOUNT_TAB.equals(tab)){
				displayCreateAccountTab();
			}
			else if(HistoryConstants.USER_REQUEST_PASSWORD_RESET_TAB.equals(tab)){
				displayRequestPasswordResetTab();
			}
			else{
				displayLoginTab();
			}
		}
		else{
			displayLoginTab();
		}
		
	}
	
	public void displayCreateAccountTab() {
		tabPanelElement.selectTab(1);
	}

	public void displayLoginTab() {
		tabPanelElement.selectTab(0);
	}
	
	
	public void displayRequestPasswordResetTab() {
		tabPanelElement.selectTab(2);
	}

	/*
	 * Trevis, be aware that calling History.newItem actually caues a history event to be
	 * fired.  Think about what that means.  You may want to do other things to make better use 
	 * history and ajax.
	 * 
	 */
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_TOOLS);
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.USER_LOGIN_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.USER_CREATE_ACCOUNT_TAB);
				break; 
		}
		History.newItem(token.toString());
	}
}
