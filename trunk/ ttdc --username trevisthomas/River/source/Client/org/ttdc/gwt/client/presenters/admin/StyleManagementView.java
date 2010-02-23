package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StyleManagementView implements StyleManagementPresenter.View{
	private VerticalPanel main = new VerticalPanel();
	private FlexTable styleTable = new FlexTable();
	private SimplePanel messagePanel = new SimplePanel();
	private Button addButton = new Button("Show Editor");
	private int row = 0;
	
	public StyleManagementView() {
		main.add(messagePanel);
		setupTableHeader();
		main.add(styleTable);
		main.add(addButton);
	}

	private void setupTableHeader() {
		styleTable.setWidget(0, 0, new Label("Style Name"));
		styleTable.setWidget(0, 1, new Label("Style Name"));
		styleTable.setWidget(0, 2, new Label("CSS File Name"));
		styleTable.setWidget(0, 3, new Label("Description"));
		styleTable.setWidget(0, 4, new Label("Actions"));
	}
	
	
	@Override
	public HasWidgets pageMessagesPanel() {
		return messagePanel;
	}


	@Override
	public void addStyle(Widget defaultStyle, Widget cssFileName, Widget displayName, Widget description, Widget controls) {
		row++;
		styleTable.setWidget(row, 0, defaultStyle);
		styleTable.setWidget(row, 1, displayName);
		styleTable.setWidget(row, 2, cssFileName);
		styleTable.setWidget(row, 3, description);
		styleTable.setWidget(row, 4, controls);
	}

	@Override
	public HasClickHandlers showAddRowClickHandler() {
		return addButton;
	}

	@Override
	public Widget getWidget() {
		return main;
	}
	
}
