package org.ttdc.gwt.client.presenters.dashboard;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.form.FieldSet;

public class ProfileView implements ProfilePresenter.View{
	private VerticalPanel main = new VerticalPanel();
	private final Grid formatingTable = new Grid(1,2); 
	//private final Grid infoTable = new Grid(3,2);
	private final FlexTable infoTable = new FlexTable();
	private final FieldSet fieldSet = new FieldSet();
	
	private final SimplePanel avatar = new SimplePanel();
	
	private final Label bioText = new Label();
	private final Label emailText = new Label();
	private final Label nameText = new Label();
	private final Label loginText = new Label();
	private final SimplePanel webLinks = new SimplePanel();
	
	public ProfileView() {
		fieldSet.setTitle("Info");
		
		fieldSet.add(formatingTable);
		
		formatingTable.setWidget(0, 0, avatar);
		formatingTable.setWidget(0, 1, infoTable);
		
		infoTable.setWidget(0, 0, new Label("Login"));
		infoTable.setWidget(1, 0, new Label("Name"));
		infoTable.setWidget(2, 0, new Label("Email"));
		infoTable.setWidget(3, 0, webLinks);
		infoTable.getFlexCellFormatter().setColSpan(3, 0, 2);
		
		infoTable.setWidget(0, 1, loginText);
		infoTable.setWidget(1, 1, nameText);
		infoTable.setWidget(2, 1, emailText);
		
		main.add(fieldSet);
	
	}
	

	@Override
	public void clear() {
		avatar.clear();
		webLinks.clear();
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasWidgets avatar() {
		return avatar;
	}

	@Override
	public HasText bioText() {
		return bioText;
	}

	@Override
	public HasText emailText() {
		return emailText;
	}

	@Override
	public HasText loginText() {
		return loginText;
	}

	@Override
	public HasText nameText() {
		return nameText;
	}

	@Override
	public HasWidgets webLinks() {
		return webLinks;
	}

}
