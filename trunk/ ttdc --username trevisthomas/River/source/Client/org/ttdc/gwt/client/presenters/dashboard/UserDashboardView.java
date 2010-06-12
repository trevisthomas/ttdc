package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserDashboardView implements UserDashboardPresenter.View{
	private static final VerticalPanel main = new VerticalPanel();
	private static final SimplePanel messagePanel = new SimplePanel();
	private static final TabPanel tabPanel = new TabPanel();
	private static final SimplePanel profilePanel = new SimplePanel();
	private static final SimplePanel editProfilePanel = new SimplePanel();
	private static final SimplePanel resetPassword = new SimplePanel();
	private static final SimplePanel settings = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	public UserDashboardView() {
		main.add(navigationPanel);
		main.add(messagePanel);
		main.add(tabPanel);
		tabPanel.add(profilePanel,"Profile");
		tabPanel.add(editProfilePanel,"Edit Profile");
		tabPanel.add(resetPassword,"Reset Password");
		tabPanel.add(settings,"Settings");
		
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
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}
	
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_DASHBOARD);
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_PROFILE_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_EDIT_PROFILE_TAB);
				break;
			case 2:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_SETTINGS_TAB);
				break; 
			case 3:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_PASSWORD_TAB);
				break; 	
				
		}
		History.newItem(token.toString(),false);
	}

	@Override
	public HasWidgets messagePanel() {
		return messagePanel;
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
	public void displayProfileTab() {
		tabPanel.selectTab(0);
	}
	
	@Override
	public void displayEditProfileTab() {
		tabPanel.selectTab(1);
	}
	
	@Override
	public void displayPasswordTab() {
		tabPanel.selectTab(3);
	}

	@Override
	public void displaySettingsTab() {
		tabPanel.selectTab(2);
	}
	
	@Override
	public HasWidgets profilePanel() {
		return profilePanel;
	}
	
	@Override
	public HasWidgets editProfilePanel() {
		return editProfilePanel;
	}

	@Override
	public HasWidgets passwordPanel() {
		return resetPassword;
	}

	@Override
	public HasWidgets settingsPanel() {
		return settings;
	}
	
}
