package org.ttdc.gwt.client.presenters.admin;


import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserAdministrationView implements UserAdministrationPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final FlexTable userTable = new FlexTable();
	private final CheckBox filterActiveOnlyCheckBox = new CheckBox("Active Only");
	private final SimplePanel paginator = new SimplePanel();
	private final SimplePanel messagePanel = new SimplePanel();
	
	public UserAdministrationView() {
		main.add(filterActiveOnlyCheckBox);
		main.add(userTable);
		main.add(paginator);
		
		setupTableHeader();
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public void addPerson(Widget stateControls, Widget nameWidget, String fullName, String email,
			Widget priviledgeWidget, Widget loginAsUser) {
		int row = userTable.getRowCount();
		userTable.setWidget(row, 0, stateControls);
		userTable.setWidget(row, 1, nameWidget);
		userTable.setWidget(row, 2, new Label(fullName));
		userTable.setWidget(row, 3, new Label(email));
		userTable.setWidget(row, 4, priviledgeWidget);
		userTable.setWidget(row, 5, loginAsUser);
	}

	@Override
	public HasValue<Boolean> filterActiveOnly() {
		return filterActiveOnlyCheckBox;
	}
	
	@Override
	public HasValueChangeHandlers<Boolean> filterActiveOnlyValueChange() {
		return filterActiveOnlyCheckBox;
	}

	@Override
	public HasWidgets paginatorPanel() {
		return paginator;
	}

	@Override
	public void clearPersonTable() {
		userTable.clear();
		setupTableHeader();
	}

	private void setupTableHeader() {
		userTable.setWidget(0, 0, new Label("State"));
		userTable.setWidget(0, 1, new Label("Login"));
		userTable.setWidget(0, 2, new Label("Name"));
		userTable.setWidget(0, 3, new Label("Email"));
		userTable.setWidget(0, 4, new Label("Privileges"));
		userTable.setWidget(0, 5, new Label("Login As"));
	}
}
