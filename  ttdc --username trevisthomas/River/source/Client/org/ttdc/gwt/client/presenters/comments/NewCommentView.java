package org.ttdc.gwt.client.presenters.comments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
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
	private final CommentToolbar toolbar = new CommentToolbar(textArea, embedTargetPlaceholder);
	private final CheckBox deletedCheckbox = new CheckBox("Deleted");
	private final CheckBox reviewCheckbox = new CheckBox("Review");
	private final CheckBox infCheckbox = new CheckBox("Informative");
	private final CheckBox nwsCheckbox = new CheckBox("Not Work Safe");
	private final CheckBox privateCheckbox = new CheckBox("Private");
	private final CheckBox lockedCheckbox = new CheckBox("Locked");
	private final Grid tagGrid = new Grid(1,3);
	private final FlowPanel tagsPanel = new FlowPanel();
	private final SimplePanel tagSelectorPanel = new SimplePanel();
	private final Button addTagButton = new Button("Add");
	private final SimplePanel ratingPanel = new SimplePanel();
	private final Button cancelButton = new Button("Cancel");
	
	private final FlowPanel checkboxPanel = new FlowPanel();
	
	private final Button previewButton = new Button("Preview");
	private final SimplePanel show = new SimplePanel();
	
	private final FlowPanel controlPanel = new FlowPanel(); 
	
	public NewCommentView() {
		main.add(messagePanel);
		main.add(ratingPanel);
		main.add(replyToPanel);
		main.add(toolbar);
		main.add(textArea);
		main.add(checkboxPanel);
		controlPanel.add(addCommentButton);
		controlPanel.add(previewButton);
		controlPanel.add(cancelButton);
		main.add(controlPanel);
		
		checkboxPanel.add(infCheckbox);
		checkboxPanel.add(nwsCheckbox);
		
		
		//The embed target may need to be dynamic.  Just remember that the back end wil need
		//to know this value in order to swap it for the real one. So if placeholder becomes dynamic
		//you're gonna need to send it up stream.
		main.add(new HTML("<center><span id=\""+embedTargetPlaceholder+"\"></span></center>"));
		
		tagGrid.setWidget(0, 0, tagsPanel);
		tagGrid.setWidget(0, 1, tagSelectorPanel);
		tagGrid.setWidget(0, 2, addTagButton);
		main.add(tagGrid);
		
		main.add(show);
		previewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				show.clear();
				//show.add(new HTML(textArea.getHTML()));
				show.add(new HTML(toolbar.getHTML()));
				
			}
		});
		
		main.setStyleName("tt-comment-editor");
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
	}
	
	
	@Override
	public HasClickHandlers addTagClickHandler() {
		return addTagButton;
	}
	
	@Override
	public HasWidgets tagSelectorPanel() {
		return tagSelectorPanel;
	}
	
	@Override
	public HasWidgets tagsPanel() {
		return tagsPanel;
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
		//return textArea;
		return toolbar; // A wild hack
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

	@Override
	public HasValue<Boolean> getDeletedCheckbox() {
		return deletedCheckbox;
	}

	@Override
	public HasValue<Boolean> getInfCheckbox() {
		return infCheckbox;
	}

	@Override
	public HasValue<Boolean> getLockedCheckbox() {
		return lockedCheckbox;
	}

	@Override
	public HasValue<Boolean> getNwsCheckbox() {
		return nwsCheckbox;
	}

	@Override
	public HasValue<Boolean> getPrivateCheckbox() {
		return privateCheckbox;
	}

	@Override
	public HasValue<Boolean> getReviewCheckbox() {
		return reviewCheckbox;
	}

	@Override
	public void setForAdmin(boolean enabled) {
		if(enabled){
			checkboxPanel.add(lockedCheckbox);
			checkboxPanel.add(deletedCheckbox);
		}
		else{
			checkboxPanel.remove(lockedCheckbox);
			checkboxPanel.remove(deletedCheckbox);
			lockedCheckbox.setValue(false);
			deletedCheckbox.setValue(false);
		}
	}
	
	@Override
	public void setForPrivate(boolean enabled) {
		if(enabled){
			checkboxPanel.add(privateCheckbox);
		}
		else{
			checkboxPanel.remove(privateCheckbox);
			privateCheckbox.setValue(false);
		}
	}

	@Override
	public void setReviewable(boolean enabled) {
		reviewCheckbox.setVisible(enabled);
		if(enabled){
			checkboxPanel.insert(reviewCheckbox, 0);
		}
		else{
			checkboxPanel.remove(reviewCheckbox);
			reviewCheckbox.setValue(false);
		}
	}


	@Override
	public HasWidgets ratingPanel() {
		return ratingPanel;
	}
	
	@Override
	public void close() {
		main.clear();
		main.setVisible(false);
	}
	
}
