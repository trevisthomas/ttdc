package org.ttdc.gwt.client.presenters.comments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewCommentView implements NewCommentPresenter.View{
	private String embedTargetPlaceholder = "EmbedTarget_PLACEHOLDER";
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel messagePanel = new SimplePanel();
	private final SimplePanel replyToPanel = new SimplePanel();
	private final TextBox loginTextBox = new TextBox();
	private final PasswordTextBox passwordTextBox = new PasswordTextBox();
	private final Button addCommentButton = new Button("Add");
	private final RichTextArea textArea = new RichTextArea();
	private final RichTextToolbar toolbar = new RichTextToolbar(textArea, embedTargetPlaceholder);
	private final Button previewButton = new Button("Preview");
	private final SimplePanel show = new SimplePanel();
	
	
	
	private final FlowPanel controlPanel = new FlowPanel(); 
	
	public NewCommentView() {
		main.add(messagePanel);
		main.add(replyToPanel);
		main.add(toolbar);
		main.add(textArea);
		controlPanel.add(addCommentButton);
		controlPanel.add(previewButton);
		main.add(controlPanel);
		
		//The embed target may need to be dynamic.  Just remember that the back end wil need
		//to know this value in order to swap it for the real one. So if placeholder becomes dynamic
		//you're gonna need to send it up stream.
		main.add(new HTML("<center><span id=\""+embedTargetPlaceholder+"\"></span></center>"));
		
		main.add(show);
		previewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				show.clear();
				show.add(new HTML(textArea.getHTML()));
			}
		});
	}
	
	@Override
	public void setEmbedTargetPlaceholder(String embedTargetPlaceholder) {
		this.embedTargetPlaceholder = embedTargetPlaceholder;
	}


	@Override
	public HasClickHandlers getAddCommentClickHandlers() {
		return addCommentButton;
	}

	@Override
	public HasHTML getCommentBody() {
		return textArea;
	}

	@Override
	public HasText getPassword() {
		return passwordTextBox;
	}

	@Override
	public HasText getUserName() {
		return loginTextBox;
	}

	@Override
	public HasWidgets replyToPanel() {
		return replyToPanel;
	}

	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasWidgets getMessagePanel() {
		return messagePanel;
	}
}
