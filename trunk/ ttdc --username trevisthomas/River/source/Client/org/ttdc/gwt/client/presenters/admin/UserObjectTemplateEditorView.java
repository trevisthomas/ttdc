package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserObjectTemplateEditorView implements UserObjectTemplateEditorPresenter.View{
	private VerticalPanel main = new VerticalPanel();
	private FlexTable templateTable = new FlexTable();
	private Button addButton = new Button("Show Editor");
	private int row = 0;
	
	public UserObjectTemplateEditorView() {
		setupTableHeader();
		main.add(templateTable);
		main.add(addButton);
	}

	private void setupTableHeader() {
		templateTable.setWidget(0, 0, new Label("Icon"));
		templateTable.setWidget(0, 1, new Label("Url Prefix"));
		templateTable.setWidget(0, 2, new Label("Display Name"));
		templateTable.setWidget(0, 3, new Label("Actions"));
	}
	
	@Override
	public void addTemplate(Widget imageWidget, Widget urlPrefix, Widget displayName, Widget controlsWidget) {
		row++;
		templateTable.setWidget(row, 0, imageWidget);
		templateTable.setWidget(row, 1, urlPrefix);
		templateTable.setWidget(row, 2, displayName);
		templateTable.setWidget(row, 3, controlsWidget);
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public void clear() {
		templateTable.clear();
		setupTableHeader();
	}

	@Override
	public HasClickHandlers createAddRowClickHandler() {
		return addButton;
	}
		
}
