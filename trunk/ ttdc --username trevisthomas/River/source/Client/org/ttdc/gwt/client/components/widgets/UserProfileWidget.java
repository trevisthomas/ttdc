package org.ttdc.gwt.client.components.widgets;

import org.ttdc.gwt.client.beans.GPerson;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class UserProfileWidget extends Composite{
	private UserProfileWidget(){
		
	}
	
	public static UserProfileWidget createInstance(GPerson person){
		final UserProfileWidget instance = new UserProfileWidget();
		final HorizontalPanel mainPanel = new HorizontalPanel();
		final SimplePanel avatarPanel = new SimplePanel();
		 
		mainPanel.add(avatarPanel);
		mainPanel.add(instance.buildPersonDetailGrid(person));
		instance.initWidget(mainPanel);
		return instance;
	}
	
	private Grid buildPersonDetailGrid(GPerson person){
		Grid personDetailGrid = new Grid(5,2);
		personDetailGrid.setText(0, 0, "Login");
		personDetailGrid.setText(1, 0, "Name");
		personDetailGrid.setText(2, 0, "EmailHelper");
		personDetailGrid.setText(3, 0, "Bio");
		
		personDetailGrid.setText(0, 1, person.getLogin());
		personDetailGrid.setText(1, 1, person.getName());
		personDetailGrid.setHTML(2, 1, person.getEmail()); //setWidget
		personDetailGrid.setHTML(3, 1, person.getBio());
		
		return personDetailGrid;
	}
	
}
