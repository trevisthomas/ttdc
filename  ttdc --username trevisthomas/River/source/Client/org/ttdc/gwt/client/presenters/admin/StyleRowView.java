package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class StyleRowView implements StyleRowPresenter.View{
	private final VerticalPanel controlsWidget = new VerticalPanel();
	private final VerticalPanel descriptionWidget = new VerticalPanel();
	private final VerticalPanel cssFileNameWidget = new VerticalPanel();
	private final VerticalPanel displayNameWidget = new VerticalPanel();
	
	private final FlowPanel staticControlsPanel = new FlowPanel();
	private final FlowPanel editControlsPanel = new FlowPanel();
	private final FlowPanel addControlsPanel = new FlowPanel();
	
	private final FlowPanel staticDescriptionPanel = new FlowPanel();
	private final FlowPanel editDescriptionPanel = new FlowPanel();
	
	private final FlowPanel staticCssFileNamePanel = new FlowPanel();
	private final FlowPanel editCssFileNamePanel = new FlowPanel();
	
	private final FlowPanel staticDisplayNamePanel = new FlowPanel();
	private final FlowPanel editDisplayNamePanel = new FlowPanel();
	
	private final TextBox descriptionTextBox = new TextBox();
	private final TextBox cssFileNameTextBox = new TextBox();
	private final TextBox displayNameTextBox = new TextBox();
	
	
	private final Label descriptionLabel = new Label();
	private final Label cssFileNameLabel = new Label();
	private final Label displayNameLabel = new Label();
	
	private final EditableLabel descriptionTextBoxLabelPair = new EditableLabel(descriptionLabel, descriptionTextBox);
	private final EditableLabel cssFileNameTextBoxLabelPair = new EditableLabel(cssFileNameLabel, cssFileNameTextBox);
	private final EditableLabel displayNameTextBoxLabelPair = new EditableLabel(displayNameLabel, displayNameTextBox);
	
	private final Button cancelButton = new Button("Cancel");
	private final Button editButton = new Button("Edit");
	private final Button deleteButton = new Button("Delete");
	private final Button updateButton = new Button("Update");
	private final Button addButton = new Button("Add");
	
	private final CheckBox defaultStyleCheckBox = new CheckBox();
	
	private EditableTableMode editableTableMode = EditableTableMode.VIEW;
	
	public StyleRowView() {
		controlsWidget.add(staticControlsPanel);
		controlsWidget.add(editControlsPanel);
		controlsWidget.add(addControlsPanel);
		
		descriptionWidget.add(staticDescriptionPanel);
		descriptionWidget.add(editDescriptionPanel);
		
		cssFileNameWidget.add(staticCssFileNamePanel);
		cssFileNameWidget.add(editCssFileNamePanel);
		
		displayNameWidget.add(staticDisplayNamePanel);
		displayNameWidget.add(editDisplayNamePanel);
		
		staticControlsPanel.add(editButton);
		editControlsPanel.add(updateButton);
		editControlsPanel.add(deleteButton);
		editControlsPanel.add(cancelButton);
		addControlsPanel.add(addButton);
		
		staticDescriptionPanel.add(descriptionLabel);
		editDescriptionPanel.add(descriptionTextBox);
		
		staticCssFileNamePanel.add(cssFileNameLabel);
		editCssFileNamePanel.add(cssFileNameTextBox);
		
		staticDisplayNamePanel.add(displayNameLabel);
		editDisplayNamePanel.add(displayNameTextBox);
		
		init();
	}
	
	void init(){
		switch(editableTableMode){
		case ADD:
			staticControlsPanel.setVisible(false);
			staticDescriptionPanel.setVisible(false);
			staticCssFileNamePanel.setVisible(false);
			staticDisplayNamePanel.setVisible(false);
			
			addControlsPanel.setVisible(true);
			editControlsPanel.setVisible(false);
			editDescriptionPanel.setVisible(true);
			editCssFileNamePanel.setVisible(true);
			editDisplayNamePanel.setVisible(true);
			
			defaultStyleCheckBox.setVisible(false);
			break;
		case VIEW:
			staticControlsPanel.setVisible(true);
			staticDescriptionPanel.setVisible(true);
			staticCssFileNamePanel.setVisible(true);
			staticDisplayNamePanel.setVisible(true);
			
			addControlsPanel.setVisible(false);
			editControlsPanel.setVisible(false);
			editDescriptionPanel.setVisible(false);
			editCssFileNamePanel.setVisible(false);
			editDisplayNamePanel.setVisible(false);
			
			defaultStyleCheckBox.setVisible(true);
			break;
		case EDIT:
			staticControlsPanel.setVisible(false);
			staticDescriptionPanel.setVisible(false);
			staticCssFileNamePanel.setVisible(false);
			staticDisplayNamePanel.setVisible(false);
			
			addControlsPanel.setVisible(false);
			editControlsPanel.setVisible(true);
			editDescriptionPanel.setVisible(true);
			editCssFileNamePanel.setVisible(true);
			editDisplayNamePanel.setVisible(true);
			defaultStyleCheckBox.setVisible(true);
			break;
		}
	}

	@Override
	public HasClickHandlers addButton() {
		return addButton;
	}

	@Override
	public HasClickHandlers cancelButton() {
		return cancelButton;
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
	public HasClickHandlers deleteButton() {
		return deleteButton;
	}

	@Override
	public HasText cssFileNameText() {
		return cssFileNameTextBoxLabelPair;
	}
	
	@Override
	public HasText descriptionText() {
		return descriptionTextBoxLabelPair;
	}
	
	@Override
	public HasText styleNameText() {
		return displayNameTextBoxLabelPair;
	}

	
	@Override
	public Widget getActionsWidget() {
		return controlsWidget;
	}

	@Override
	public Widget getCssFileNameWidget() {
		return cssFileNameWidget;
	}

	@Override
	public Widget getDescriptonWidget() {
		return descriptionWidget;
	}

	@Override
	public Widget getStyleNameWidget() {
		return displayNameWidget;
	}

	@Override
	public Widget getDefaultStyleWidget() {
		return defaultStyleCheckBox;
	}
	
	@Override
	public HasValue<Boolean> defaultStyle() {
		return defaultStyleCheckBox;
	}
	
	@Override
	public HasClickHandlers defaultStyleCheckBox() {
		return defaultStyleCheckBox;
	}
	
	@Override
	public void setMode(EditableTableMode editableTableMode) {
		this.editableTableMode = editableTableMode;
		init();
	}

	@Override
	public Widget getWidget() {
		throw new RuntimeException("Dont call getWidget on a row view");
	}
	
	@Override
	public void delete() {
		//This is a fake hack, kinda.
		controlsWidget.clear();
		displayNameWidget.clear();
		descriptionWidget.clear();
		cssFileNameWidget.clear();
	}

}
