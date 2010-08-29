package org.ttdc.gwt.client.presenters.comments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class LinkDialog  extends PopupRelative{
	private final Grid main = new Grid(3,2);
	private final TextBox name = new TextBox();
	private final TextBox directLink = new TextBox();
	private final Label nameLabel = new Label("Name");
	private final Label directLinkLabel = new Label("Direct");
	private final Button okButton = new Button("Ok");
	private final Button cancelButton = new Button("Cancel");
	private final FlowPanel buttonPanel = new FlowPanel();
	
	private final LinkDialogSource source;
	public LinkDialog(LinkDialogSource source, final String selectedText) {
		add(main);
		this.source = source;
		
		name.setText(selectedText);
		//if(StringUtil.empty(selectedText)){
			main.setWidget(0, 0, nameLabel);
			main.setWidget(0, 1, name);
		//}
		main.setWidget(1, 0, directLinkLabel);
		main.setWidget(1, 1, directLink);
		
		
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		main.setWidget(2, 1, buttonPanel);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LinkDialog.this.hide();
			}
		});
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LinkDialog.this.source.performLink(name.getText(), directLink.getText());
				LinkDialog.this.hide();
			}
		});
	}

}
