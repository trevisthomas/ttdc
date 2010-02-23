package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserToolsView implements UserToolsPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final DecoratedTabPanel tabPanel = new DecoratedTabPanel();
	private final SimplePanel loginPanel = new SimplePanel();
	private final SimplePanel createPanel = new SimplePanel();
	private final SimplePanel messagesPanel = new SimplePanel();
	private final SimplePanel requestPasswordResetPanel = new SimplePanel();
	    
	public UserToolsView() {
		main.add(messagesPanel);
		
		tabPanel.setWidth("100%");
	    tabPanel.setAnimationEnabled(true);
	    tabPanel.add(loginPanel, "Login");
	    tabPanel.add(createPanel, "Create Account");
	    tabPanel.add(requestPasswordResetPanel, "Forgotten Password");
	    

	    main.add(tabPanel);
	    
	    tabPanel.selectTab(0); //Default (other wise it comes up with none shown, pretty weird)
	    
	    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!tabPanel.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectTabSelection(index);
			}
		});
	}
	@Override
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(getWidget());
	}

	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasWidgets createAccountPanel() {
		return createPanel;
	}

	@Override
	public HasWidgets loginPanel() {
		return loginPanel;
	}
	
	@Override
	public HasWidgets requestPasswordResetPanel() {
		return requestPasswordResetPanel;
	}

	@Override
	public void displayCreateAccountTab() {
		tabPanel.selectTab(1);
	}

	@Override
	public void displayLoginTab() {
		tabPanel.selectTab(0);
	}
	
	@Override
	public void displayRequestPasswordResetTab() {
		tabPanel.selectTab(2);
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
	@Override
	public HasWidgets messagePanel() {
		return messagesPanel;
	}
	
	
	
}
