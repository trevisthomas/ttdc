package org.ttdc.gwt.client.presenters.admin;

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

public class AdminToolsView implements AdminToolsPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final DecoratedTabPanel tabPanel = new DecoratedTabPanel();
	private final SimplePanel imagePanel = new SimplePanel();
	private final SimplePanel usersPanel = new SimplePanel();
	private final SimplePanel templatePanel = new SimplePanel();
	private final SimplePanel headerPanel = new SimplePanel();
	private final SimplePanel messagePanel = new SimplePanel();
	private final SimplePanel stylePanel = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	    
	public AdminToolsView() {
		main.add(navigationPanel);
		main.add(headerPanel);
		main.add(messagePanel);
		tabPanel.setWidth("400px");
	    tabPanel.setAnimationEnabled(true);
	    tabPanel.add(imagePanel, "Image Management");
	    tabPanel.add(usersPanel, "Users");
	    tabPanel.add(templatePanel, "Template Editor");
	    tabPanel.add(stylePanel, "Style Manager");
	    
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
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}

	/*
	 * Trevis, be aware that calling History.newItem actually caues a history event to be
	 * fired.  Think about what that means.  You may want to do other things to make better use 
	 * history and ajax
	 * 
	 */
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_ADMIN_TOOLS);
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.ADMIN_IMAGE_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.ADMIN_USER_TAB);
				break; 
			case 2:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.ADMIN_USER_OBJECT_TEMPLATE_TAB);
				break;
			case 3:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.ADMIN_STYLE_TAB);
				break;	
		}
		History.newItem(token.toString());
	}
	
	//Trevis, consider moving this impl to a base class...
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
	public HasWidgets imagePanel() {
		return imagePanel;
	}
	
	@Override
	public HasWidgets usersPanel(){
		return usersPanel;
	}

	@Override
	public HasWidgets templatePanel() {
		return templatePanel;
	}
	
	@Override
	public HasWidgets headerPanel() {
		return headerPanel;
	}

	@Override
	public HasWidgets messagePanel() {
		return messagePanel;
	}
	
	@Override
	public HasWidgets stylePanel() {
		return stylePanel;
	}

	@Override
	public void displayImageManagementTab(){
		tabPanel.selectTab(0);
	}

	@Override
	public void displayUserAdminTab() {
		tabPanel.selectTab(1);
	}

	@Override
	public void displayTempalateAdminTab() {
		tabPanel.selectTab(2);
	}

	@Override
	public void displayStyleManagementTab() {
		tabPanel.selectTab(3);
	}
}
