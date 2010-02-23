package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HomeView implements HomePresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final Grid bodyTable = new Grid(1,3);
	private final TabPanel centerTabPanel = new TabPanel();
	private final TabPanel rightTabPanel = new TabPanel();
	private final VerticalPanel modulePanel = new VerticalPanel(); 
	private final SimplePanel messagePannel = new SimplePanel();
	
	private final SimplePanel conversationPanel = new SimplePanel();
	private final SimplePanel nestedPanel = new SimplePanel();
	private final SimplePanel flatPanel = new SimplePanel();
	private final SimplePanel threadPanel = new SimplePanel();
	private HistoryToken token = new HistoryToken();
	
	private boolean fireHistoryEvent = true;
	
	public HomeView() {
		main.add(messagePannel);
		main.add(bodyTable);
		bodyTable.setWidget(0, 0, modulePanel);
		bodyTable.setWidget(0, 1, centerTabPanel);
		bodyTable.setWidget(0, 2, rightTabPanel);
		
		centerTabPanel.add(nestedPanel, "Nested");
		centerTabPanel.add(flatPanel, "Flat");
		rightTabPanel.add(conversationPanel,"Conversations");
		rightTabPanel.add(threadPanel,"Threads");
		
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_HOME);
		
		centerTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!centerTabPanel.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectCenterTabSelection(index);
			}
		});
		
		rightTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!rightTabPanel.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectRightTabSelection(index);
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
	

	private void updateHistoryToReflectCenterTabSelection(int index) {
		switch (index){
			case INDEX_NESTED:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_NESTED_TAB);
				break;
			case INDEX_FLAT:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_FLAT_TAB);
				break;
		}
		History.newItem(token.toString(),fireHistoryEvent);
	}
	
	private void updateHistoryToReflectRightTabSelection(int index) {
		switch (index){
			case INDEX_CONVERSATION:
				token.setParameter(HistoryConstants.TAB2_KEY, HistoryConstants.HOME_CONVERSATION_TAB);
				break;
			case INDEX_THREAD:
				token.setParameter(HistoryConstants.TAB2_KEY, HistoryConstants.HOME_THREAD_TAB);
				break;
		}
		History.newItem(token.toString(),fireHistoryEvent);
	}
	
	final static int INDEX_FLAT = 1;
	final static int INDEX_THREAD = 1;
	final static int INDEX_NESTED = 0;
	final static int INDEX_CONVERSATION = 0;

	@Override
	public void displayTabs(TabType[] tabs) {
		fireHistoryEvent = false;
		if(tabs[0].equals(TabType.FLAT)){
			centerTabPanel.selectTab(INDEX_FLAT);
		}else{
			centerTabPanel.selectTab(INDEX_NESTED);
		}
		
		if(tabs[1].equals(TabType.THREAD)){
			rightTabPanel.selectTab(INDEX_THREAD);
		}else{
			rightTabPanel.selectTab(INDEX_CONVERSATION);
		}
		fireHistoryEvent = true;
	}
	
	@Override
	public void displayFlatTab() {
		centerTabPanel.selectTab(1);		
	}

	@Override
	public void displayNestedTab() {
		centerTabPanel.selectTab(0);
	}
	
	@Override
	public void displayConversationTab() {
		rightTabPanel.selectTab(1);
	}
	
	@Override
	public void displayThreadTab() {
		rightTabPanel.selectTab(0);
	}

	@Override
	public HasWidgets messagePanel() {
		return messagePannel;
	}
	
	@Override
	public HasWidgets conversationPanel() {
		return conversationPanel;
	}

	@Override
	public HasWidgets flatPanel() {
		return flatPanel;
	}
	@Override
	public HasWidgets nestedPanel() {
		return nestedPanel;
	}

	@Override
	public HasWidgets threadPanel() {
		return threadPanel;
	}

	@Override
	public HasWidgets modulePanel() {
		return modulePanel;
	}


	
}
