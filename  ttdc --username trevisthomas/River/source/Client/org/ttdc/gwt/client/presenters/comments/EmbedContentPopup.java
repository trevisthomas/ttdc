package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;



public class EmbedContentPopup extends DialogBox{
	private final Grid main = new Grid(4,2);
	private final TextBox name = new TextBox();
	private final TextBox directLink = new TextBox();
	private final TextArea embededLink = new TextArea();
	private final Label nameLabel = new Label("Name");
	private final Label directLinkLabel = new Label("Direct");
	private final Label emdededLinkLabel = new Label("Embeded");
	private final Button okButton = new Button("Ok");
	private final Button cancelButton = new Button("Cancel");
	private final FlowPanel buttonPanel = new FlowPanel();
	
	private final RichTextToolbar toolbar;
	public EmbedContentPopup(RichTextToolbar toolbar, final String selectedText) {
		setWidget(main);
		this.toolbar = toolbar;
		
		name.setText(selectedText);
		//if(StringUtil.empty(selectedText)){
			main.setWidget(0, 0, nameLabel);
			main.setWidget(0, 1, name);
		//}
		main.setWidget(1, 0, directLinkLabel);
		main.setWidget(1, 1, directLink);
		main.setWidget(2, 0, emdededLinkLabel);
		main.setWidget(2, 1, embededLink);
		
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		main.setWidget(3, 1, buttonPanel);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EmbedContentPopup.this.hide();
			}
		});
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EmbedContentPopup.this.toolbar.performLinkEmbed(name.getText(), directLink.getText(),embededLink.getText());
				EmbedContentPopup.this.hide();
			}
		});
	}
}
