/**
 * 
 */
package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

class EditableLabel implements HasText{
	private TextBox textBox;
	private Label label;
	
	public EditableLabel(Label label, TextBox textBox) {
		this.textBox = textBox;
		this.label = label;
	}
	
	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public void setText(String text) {
		label.setText(text);
		textBox.setText(text);
	}
}