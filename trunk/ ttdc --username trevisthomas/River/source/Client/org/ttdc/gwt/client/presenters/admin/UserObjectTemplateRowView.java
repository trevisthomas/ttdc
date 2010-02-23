package org.ttdc.gwt.client.presenters.admin;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserObjectTemplateRowView implements UserObjectTemplateRowPresenter.View{
	private final VerticalPanel controlsWidget = new VerticalPanel();
	private final VerticalPanel displayNameWidget = new VerticalPanel();
	private final VerticalPanel imageWidget = new VerticalPanel();
	private final VerticalPanel urlPrefixWidget = new VerticalPanel();
	
	private final FlowPanel staticImagePanel = new FlowPanel();
	private final FlowPanel editImagePanel = new FlowPanel();
	
	private final FlowPanel staticUrlPrefixPanel = new FlowPanel();
	private final FlowPanel editUrlPrefixPanel = new FlowPanel();
	
	private final FlowPanel staticDisplayNamePanel = new FlowPanel();
	private final FlowPanel editDisplayNamePanel = new FlowPanel();
	
	private final FlowPanel staticControlsPanel = new FlowPanel();
	private final FlowPanel editControlsPanel = new FlowPanel();
	private final FlowPanel addControlsPanel = new FlowPanel();
	
	
	private final TextBox displayNameTextBox = new TextBox();
	private final TextBox urlPrefixTextBox = new TextBox();
	
	private final Label displayNameLabel = new Label();
	private final Label urlPrefixLabel = new Label();
	
	private final EditableLabel displayNameTextPair = new EditableLabel(displayNameLabel, displayNameTextBox);
	private final EditableLabel urlPrefixTextPair = new EditableLabel(urlPrefixLabel, urlPrefixTextBox);
	
	private final Button cancelButton = new Button("Cancel");
	private final Button editButton = new Button("Edit");
	private final Button deleteButton = new Button("Delete");
	private final Button updateButton = new Button("Update");
	private final Button addButton = new Button("Add");
	
	private EditableTableMode editableTableMode = EditableTableMode.VIEW;
	
	public UserObjectTemplateRowView() {
		controlsWidget.add(staticControlsPanel);
		controlsWidget.add(editControlsPanel);
		controlsWidget.add(addControlsPanel);
		
		displayNameWidget.add(staticDisplayNamePanel);
		displayNameWidget.add(editDisplayNamePanel);
		staticDisplayNamePanel.add(displayNameLabel);
		editDisplayNamePanel.add(displayNameTextBox);
		
		imageWidget.add(staticImagePanel);
		imageWidget.add(editImagePanel);
		
		urlPrefixWidget.add(staticUrlPrefixPanel);
		urlPrefixWidget.add(editUrlPrefixPanel);
		staticUrlPrefixPanel.add(urlPrefixLabel);
		editUrlPrefixPanel.add(urlPrefixTextBox);
		
		staticControlsPanel.add(editButton);
				
		editControlsPanel.add(updateButton);
		editControlsPanel.add(deleteButton);
		editControlsPanel.add(cancelButton);
		
		addControlsPanel.add(addButton);
		init();
	}
	
	@Override
	public void setMode(EditableTableMode editableTableMode) {
		this.editableTableMode = editableTableMode;
		init();
	}
		
	void init(){
		switch(editableTableMode){
		case ADD:
			staticControlsPanel.setVisible(false);
			staticDisplayNamePanel.setVisible(false);
			staticImagePanel.setVisible(false);
			staticUrlPrefixPanel.setVisible(false);
			
			addControlsPanel.setVisible(true);
			editControlsPanel.setVisible(false);
			editDisplayNamePanel.setVisible(true);
			editImagePanel.setVisible(true);
			editUrlPrefixPanel.setVisible(true);
			break;
		case VIEW:
			staticControlsPanel.setVisible(true);
			staticDisplayNamePanel.setVisible(true);
			staticImagePanel.setVisible(true);
			staticUrlPrefixPanel.setVisible(true);
			
			addControlsPanel.setVisible(false);
			editControlsPanel.setVisible(false);
			editDisplayNamePanel.setVisible(false);
			editImagePanel.setVisible(false);
			editUrlPrefixPanel.setVisible(false);
			break;
		case EDIT:
			staticControlsPanel.setVisible(false);
			staticDisplayNamePanel.setVisible(false);
			staticImagePanel.setVisible(false);
			staticUrlPrefixPanel.setVisible(false);
			
			addControlsPanel.setVisible(false);
			editControlsPanel.setVisible(true);
			editDisplayNamePanel.setVisible(true);
			editImagePanel.setVisible(true);
			editUrlPrefixPanel.setVisible(true);
			break;
		}
		
	}
	@Override
	public Widget getWidget() {
		throw new RuntimeException("Dont call get widget on RowView views");
	}
	
	@Override
	public Widget getControlsWidget() {
		return controlsWidget;
	}

	@Override
	public Widget getDisplayNameWidget() {
		return displayNameWidget;
	}

	@Override
	public Widget getImageWidget() {
		return imageWidget;
	}
	
	@Override
	public Widget getUrlPrefixWidget() {
		return urlPrefixWidget;
	}

	@Override
	public HasClickHandlers cancelButton() {
		return cancelButton;
	}

	@Override
	public HasClickHandlers deleteButton() {
		return deleteButton;
	}
	
	@Override
	public HasClickHandlers editButton() {
		return editButton;
	}
	
	@Override
	public HasClickHandlers updateButton() {
		return updateButton;
	}
	
	@Override
	public HasClickHandlers addButton() {
		return addButton;
	}

	@Override
	public HasText displayNameText() {
		return displayNameTextPair;
	}
	
	@Override
	public HasText urlPrefixText() {
		return urlPrefixTextPair;
	}
	
	@Override
	public HasWidgets imageUploadWidget() {
		return editImagePanel;
	}

	@Override
	public HasWidgets staticImageWidget() {
		return staticImagePanel;
	}

	@Override
	public void delete() {
		//This is a fake hack, kinda.
		controlsWidget.clear();
		displayNameWidget.clear();
		imageWidget.clear();
		urlPrefixWidget.clear();
	}

		
}
