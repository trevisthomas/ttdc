package org.ttdc.gwt.client.presenters.users;

import java.util.Date;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class CreateAccountView implements CreateAccountPresenter.View{
	private final Grid grid  = new Grid(10,2);
	private final Button createButton = new Button("Create Account");
	private final TextArea bio = new TextArea();
	private final TextBox login = new TextBox();
	private final TextBox password = new PasswordTextBox();
	private final TextBox vPassword = new PasswordTextBox();
	private final TextBox email = new TextBox();
	private final TextBox vEmail = new TextBox();
	private final DateBox birthday = new DateBox();
	private final TextBox name = new TextBox();
	private final FlowPanel questionTable = new FlowPanel();
	private final HTML val1 = new HTML();
	private final HTML val2 = new HTML();
	private final TextBox sum = new TextBox();
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel successPanel = new SimplePanel();
	
	public CreateAccountView() {
		
		DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd/yyyy");
		birthday.setFormat(new DateBox.DefaultFormat(dateFormat));
		
		grid.setWidget(0, 0, new Label("Login"));
		grid.setWidget(0, 1, login);
		grid.setWidget(1, 0, new Label("Password"));
		grid.setWidget(1, 1, password);
		grid.setWidget(2, 0, new Label("Verify Password"));
		grid.setWidget(2, 1, vPassword);
		grid.setWidget(3, 0, new Label("Email"));
		grid.setWidget(3, 1, email);
		grid.setWidget(4, 0, new Label("Verify Email"));
		grid.setWidget(4, 1, vEmail);
		grid.setWidget(5, 0, new Label("Birthday"));
		grid.setWidget(5, 1, birthday);
		grid.setWidget(6, 0, new Label("Name"));
		grid.setWidget(6, 1, name);
		grid.setWidget(7, 0, new Label("Hm?"));
		grid.setWidget(7, 1, questionTable);
		grid.setWidget(8, 0, new Label("Bio"));
		grid.setWidget(8, 1, bio);
		
		val1.addStyleName("tt-inline");
		val2.addStyleName("tt-inline");
		sum.setWidth("20px");
		
		questionTable.add(val1);
		HTML plus = new HTML(" + ");
		plus.addStyleName("tt-inline");
		questionTable.add(plus);
		questionTable.add(val2);
		
		HTML equals = new HTML(" = ");
		equals.addStyleName("tt-inline");
		questionTable.add(equals);
		questionTable.add(sum);
		
		grid.setWidget(9, 1, createButton);
		
		main.add(grid);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasClickHandlers createAccountClickHandler() {
		return createButton;
	}

	@Override
	public HasText bioTextBox() {
		return bio;
	}

	@Override
	public HasValue<Date> birthdayDateBox() {
		return birthday;
	}

	@Override
	public HasText emailTextBox() {
		return email;
	}

	@Override
	public HasText loginTextBox() {
		return login;
	}

	@Override
	public HasText nameTextBox() {
		return name;
	}

	@Override
	public HasText passwordTextBox() {
		return password;
	}

	@Override
	public HasText passwordVerifyTextBox() {
		return vPassword;
	}

	@Override
	public HasText sumTextBox() {
		return sum;
	}

	@Override
	public HasText valOneTextBox() {
		return val1;
	}

	@Override
	public HasText valTwoTextBox() {
		return val2;
	}

	@Override
	public HasText emailVerifyTextBox() {
		return vEmail;
	}

	@Override
	public void showSuccess(String successMessage) {
		successPanel.add(new Label(successMessage));		
		main.clear();
		main.add(successPanel);
	}
	
}	
