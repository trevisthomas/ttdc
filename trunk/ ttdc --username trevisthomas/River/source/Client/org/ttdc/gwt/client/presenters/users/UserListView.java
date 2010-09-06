package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@Deprecated
public class UserListView implements UserListPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final FlexTable table = new FlexTable();
	private final SimplePanel paginator = new SimplePanel();
	private final SimplePanel messagePanel = new SimplePanel();
	
	private final FocusPanel loginHeader = new FocusPanel();
	private final FocusPanel hitCountHeader = new FocusPanel();
	private final FocusPanel memberSinceHeader = new FocusPanel();
	private final FocusPanel emailHeader = new FocusPanel();
	private final FocusPanel nameHeader = new FocusPanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	private int row = 0;
	public UserListView() {
		main.add(navigationPanel);
		main.add(messagePanel);
		main.add(table);
		main.add(paginator);
		
		initHeader();
		
		loginHeader.add(new Label("Login"));
		hitCountHeader.add(new Label("Hit Count"));
		memberSinceHeader.add(new Label("Last Accessed"));
		emailHeader.add(new Label("Email"));
		nameHeader.add(new Label("Name"));
		
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}

	@Override
	public void addRow(UserRowPresenter rowPresenter) {
		row++;
		
		table.setWidget(row, 0, rowPresenter.getLoginWidget());
		table.setWidget(row, 1, rowPresenter.getNameWidget());
		table.setWidget(row, 2, rowPresenter.getHitsWidget());
		table.setWidget(row, 3, rowPresenter.getMemberSinceWidget());
		table.setWidget(row, 4, rowPresenter.getEmailWidget());
	}

	private void initHeader() {
		row = 0;
		table.clear();
		table.setWidget(0, 0, loginHeader);
		table.setWidget(0, 1, nameHeader);
		table.setWidget(0, 2, hitCountHeader);
		table.setWidget(0, 3, memberSinceHeader);
		table.setWidget(0, 4, emailHeader);
	}

	@Override
	public HasWidgets paginatorPanel() {
		return paginator;
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
	public HasClickHandlers emailHeaderClickHandlers() {
		return emailHeader;
	}

	@Override
	public HasClickHandlers hitCountHeaderClickHandlers() {
		return hitCountHeader;
	}

	@Override
	public HasClickHandlers loginHeaderClickHandlers() {
		return loginHeader;
	}

	@Override
	public HasClickHandlers memberSinceHeaderClickHandlers() {
		return memberSinceHeader;
	}

	@Override
	public HasClickHandlers nameHeaderClickHandlers() {
		return nameHeader;
	}

}
