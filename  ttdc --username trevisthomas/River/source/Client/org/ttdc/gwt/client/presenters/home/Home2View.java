package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.uibinder.Demo;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Home2View implements Home2Presenter.View{
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
	private final SimplePanel searchPanel = new SimplePanel();
	private final SimplePanel loginPanel = new SimplePanel();
	private final Button commentButton = new Button("Comment");
	private final Button movieButton = new Button("Movie");
	//private final DisclosurePanel commentPanel = new DisclosurePanel("Add Comment");
	private final SimplePanel commentPanel = new SimplePanel();
	private HistoryToken token = new HistoryToken();
	private final HorizontalPanel searchAndReplyPanel = new HorizontalPanel();
	private final SimplePanel siteUpdatePanel = new SimplePanel();
	
	private boolean fireHistoryEvent = true;
	
	public Home2View() {
		main.add(new Demo());
		main.add(loginPanel);
		main.add(messagePannel);
		main.add(siteUpdatePanel);
		main.add(searchAndReplyPanel);
		main.add(commentPanel);
		main.add(bodyTable);
		bodyTable.setWidget(0, 0, modulePanel);
		bodyTable.setWidget(0, 1, centerTabPanel);
		bodyTable.setWidget(0, 2, rightTabPanel);
		searchAndReplyPanel.add(searchPanel);
		searchAndReplyPanel.add(commentButton);
		searchAndReplyPanel.add(movieButton);
		
		centerTabPanel.add(nestedPanel, "Nested");
		centerTabPanel.add(flatPanel, "Flat");
		centerTabPanel.add(conversationPanel,"Conversations");
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
	}
	
	
	@Override
	public void show() {
		RootPanel.get("content").clear();
		Label label = new Label(ConnectionId.getInstance().getCurrentUser().getLogin()+ "  " +ConnectionId.getInstance().getConnectionId());
		RootPanel.get("content").add(label);
		RootPanel.get("content").add(getWidget());
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasClickHandlers commentButton() {
		return commentButton;
	}
	
	@Override
	public HasClickHandlers movieButton(){
		return movieButton;
	}

	private void updateHistoryToReflectCenterTabSelection(int index) {
		switch (index){
			case INDEX_NESTED:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_NESTED_TAB);
				break;
			case INDEX_FLAT:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_FLAT_TAB);
				break;
			case INDEX_CONVERSATION:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_CONVERSATION_TAB);
				break;				
		}
		History.newItem(token.toString(),fireHistoryEvent);
	}
	
	final static int INDEX_FLAT = 1;
	final static int INDEX_THREAD = 0;
	final static int INDEX_NESTED = 0;
	final static int INDEX_CONVERSATION = 2;

	@Override
	public void displayTab(TabType selected) {
		fireHistoryEvent = false;
		if(selected.equals(TabType.FLAT)){
			centerTabPanel.selectTab(INDEX_FLAT);
		}else if(selected.equals(TabType.NESTED)){
			centerTabPanel.selectTab(INDEX_NESTED);
		}
		else{
			centerTabPanel.selectTab(INDEX_CONVERSATION);
		}
		
		rightTabPanel.selectTab(INDEX_THREAD);
		fireHistoryEvent = true;
	}
	
//	@Override
//	public void displayFlatTab() {
//		centerTabPanel.selectTab(1);		
//	}
//
//	@Override
//	public void displayNestedTab() {
//		centerTabPanel.selectTab(0);
//	}
//	
//	@Override
//	public void displayConversationTab() {
//		rightTabPanel.selectTab(1);
//	}
//	
//	@Override
//	public void displayThreadTab() {
//		rightTabPanel.selectTab(0);
//	}

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


	@Override
	public HasWidgets searhcPanel() {
		return searchPanel;
	}

	@Override
	public HasWidgets loginPanel() {
		return loginPanel;
	}
	
	@Override
	public HasWidgets commentPanel() {
		return commentPanel;
	}

	@Override
	public HasWidgets siteUpdatePanel() {
		return siteUpdatePanel;
	}
	
}
