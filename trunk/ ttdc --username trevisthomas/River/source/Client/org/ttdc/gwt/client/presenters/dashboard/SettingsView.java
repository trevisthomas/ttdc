package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.presenters.util.MyListBox;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.form.FieldSet;

public class SettingsView implements SettingsPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final FieldSet styleFieldSet = new FieldSet();
	private final FieldSet filtersFieldSet = new FieldSet();
	private final HorizontalPanel stylePanel = new HorizontalPanel();
	private final Button updateStyleButton = new Button("Update Theme");
	private final MyListBox styleListBox = new MyListBox();
	private final VerticalPanel filterListPanel = new VerticalPanel();
	//private final HorizontalPanel nwsFilterPanel = new HorizontalPanel();
	private final CheckBox nwsCleckBox = new CheckBox();
	
	public SettingsView() {
		stylePanel.add(new Label("Choose Style"));
		stylePanel.add(styleListBox);
		stylePanel.add(updateStyleButton);
		
		styleFieldSet.add(stylePanel);
		
		filtersFieldSet.add(filterListPanel);
		filterListPanel.add(nwsCleckBox);
		nwsCleckBox.setText("Enable NWS content");
		
		main.add(styleFieldSet);
		main.add(filtersFieldSet);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public void addAvailableStyle(GStyle style) {
		styleListBox.addItem(style.getName(), style.getStyleId());		
	}

	@Override
	public void clearAvailableStyles() {
		styleListBox.clear();
	}

	@Override
	public String getSelectedStyleId() {
		return styleListBox.getSelectedValue();
	}
	
	@Override
	public void setSelectedStyleId(String styleId) {
		styleListBox.setSelectedValue(styleId);
	}

	@Override
	public HasValue<Boolean> enableNwsValue() {
		return nwsCleckBox;
	}

	@Override
	public HasClickHandlers nwsCheckBoxClick() {
		return nwsCleckBox;
	}

	@Override
	public HasClickHandlers updateStyleClick() {
		return updateStyleButton;
	}
}
