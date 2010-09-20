package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter.Mode;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@Deprecated
public class NewCommentView implements NewCommentPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel messagePanel = new SimplePanel();
	private final SimplePanel replyToPanel = new SimplePanel();
	private final HorizontalPanel topicTitlePanel = new HorizontalPanel();
	private final TextBox loginTextBox = new TextBox();
	private final PasswordTextBox passwordTextBox = new PasswordTextBox();
	private final Button addCommentButton = new Button("Add");
	private final Button editCommentButton = new Button("Edit");
	private final MyRichTextArea textArea = new MyRichTextArea();
	private final CommentToolbar toolbar = new CommentToolbar(textArea);
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
	private final SimplePanel outer = new SimplePanel();
	private final HorizontalPanel loginPanel = new HorizontalPanel();
	private SuggestBox parentSuggestionBox;
	
	private final FlowPanel checkboxPanel = new FlowPanel();
	
	private final Button previewButton = new Button("Preview");
	private final SimplePanel show = new SimplePanel();
	
	private final FlowPanel controlPanel = new FlowPanel(); 
	
	private final static String ID_PREFIX = "RTA";
	private static int idCounter = 1;
	
	private String myId;
	
	private boolean enableCloseHandler = true;
	public NewCommentView() {
		
		outer.add(main);
		main.add(messagePanel);
		main.add(loginPanel);
		main.add(ratingPanel);
		
		topicTitlePanel.add(new Label("Topic: "));
		topicTitlePanel.add(replyToPanel);
		topicTitlePanel.setVisible(false);
		main.add(topicTitlePanel);
		
		main.add(toolbar);
		main.add(textArea);
		main.add(checkboxPanel);
		controlPanel.add(editCommentButton);
		controlPanel.add(addCommentButton);
		controlPanel.add(previewButton);
		controlPanel.add(cancelButton);
		main.add(controlPanel);
		
		checkboxPanel.add(infCheckbox);
		checkboxPanel.add(nwsCheckbox);
		
		loginPanel.setVisible(false);
		loginPanel.add(new Label("Login: "));
		loginPanel.add(loginTextBox);
		loginPanel.add(new Label("Password: "));
		loginPanel.add(passwordTextBox);
		
		textArea.setStyleName("tt-rich-text-iframe");
		
		
		//The embed target may need to be dynamic.  Just remember that the back end wil need
		//to know this value in order to swap it for the real one. So if placeholder becomes dynamic
		//you're gonna need to send it up stream.
		//main.add(new HTML("<center><span id=\""+embedTargetPlaceholder+"\"></span></center>"));
		
//		tagGrid.setWidget(0, 0, tagsPanel);
//		tagGrid.setWidget(0, 1, tagSelectorPanel);
//		tagGrid.setWidget(0, 2, addTagButton);
//		main.add(tagGrid);
		
		main.add(show);
		previewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				show.clear();
				//show.add(new HTML(textArea.getHTML()));
				HTML markup = new HTML(toolbar.getHTML());
				markup.setStyleName("tt-post tt-rich-text-iframe");
				show.add(markup);
				
			}
		});
		
		outer.setStyleName("tt-comment-editor");
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(enableCloseHandler){
					outer.setVisible(false);
				}
			}
		});
		
		myId = ID_PREFIX+idCounter++;
		
		//textArea.getElement().setId(myId);
		
		textArea.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				//applyCss(myId);
				show.clear();
			}
		});
		//textArea.setStyleName("tt-text-normal");
//		textArea.addKeyUpHandler(new KeyUpHandler() {
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				//grow();
//				textArea.setHeight("400px");
//			}
//		});
		
		toolbar.setStyleName("tt-comment-toolbar");
		controlPanel.setStyleName("tt-comment-editor-buttons");
		
		configureForTopicCreation(true);
	}
	
//	@Override
//	public void grow(){
//		loginPanel.setVisible(true);
//		ratingPanel.setVisible(true);
//		replyToPanel.setVisible(true);
//		toolbar.setVisible(true);
//		//main.add(textArea);
//		checkboxPanel.setVisible(true);
//		controlPanel.setVisible(true);
//		textArea.setHeight("400px");
//	}
//	
//	@Override
//	public void shrink(){
//		loginPanel.setVisible(false);
//		ratingPanel.setVisible(false);
//		replyToPanel.setVisible(false);
//		toolbar.setVisible(false);
//		//main.add(textArea);
//		checkboxPanel.setVisible(false);
//		controlPanel.setVisible(false);
//		textArea.setHeight("40px");
//	}
//	
	private class MyRichTextArea extends RichTextArea{
		public MyRichTextArea() {
			
		}
		@Override
		protected void onLoad() {
			getElement().setId(myId);
			applyCss(myId);
		}
	}
	
	@Override
	public HasClickHandlers getEditCommentClickHandlers() {
		return editCommentButton;
	}
	
	@Override
	public void setMode(Mode mode) {
		switch(mode){
			case CREATE:{
				editCommentButton.setVisible(false);
				addCommentButton.setVisible(true);
				break;
			}
			case EDIT:{
				addCommentButton.setVisible(false);
				editCommentButton.setVisible(true);
				break;
			}
		}
		
	}
	
	@Override
	public void configureForTopicCreation(boolean b) {
		if(b){
			//Creating a new topic
			addCommentButton.setText("Create");
			addCommentButton.setTitle("Create a new review or conversation");
		}
		else{
			//Replying to an existing parent
			addCommentButton.setText("Reply");
			addCommentButton.setTitle("Reply to the selected topic");
		}
	}
	
	@Override
	public void showLoginFields() {
		loginPanel.setVisible(true);
	}

	
//	public static native void applyCss(String id) /*-{
//		$doc.getElementById(id).contentWindow.document.body.className="tt-rich-text-area";
//		
//		var element = $doc.getElementById('mainCss');
//		
//		var cssLink = document.createElement("link") 
//		cssLink.href = element.href; 
//		cssLink .rel = "stylesheet"; 
//		cssLink .type = "text/css";
//		
//		$doc.getElementById(id).contentWindow.document.getElementsByTagName("head")[0].appendChild(cssLink);
//
//		//alert(element.href);
//		
//	}-*/;
//	
	/*
	 * NOTE the timeout delay before completing execution.  I found a tip about this
	 * in the gwt source code for RichTextAreaImplStandard
	 */
	public static native void applyCss(String id) /*-{
		setTimeout($entry(function() {
			$doc.getElementById(id).contentWindow.document.body.className="tt-rich-text-area";
			
			var element = $doc.getElementById('mainCss');
			
			var cssLink = document.createElement("link") 
			cssLink.href = element.href; 
			cssLink.rel = "stylesheet"; 
			cssLink.type = "text/css";
			
			var cssShackagLink = document.createElement("link") 
			cssShackagLink.href = "/css/shacktags.css"; 
			cssShackagLink.rel = "stylesheet"; 
			cssShackagLink.type = "text/css";
			
			$doc.getElementById(id).contentWindow.document.getElementsByTagName("head")[0].appendChild(cssLink);
			$doc.getElementById(id).contentWindow.document.getElementsByTagName("head")[0].appendChild(cssShackagLink);
    	}), 5);
	}-*/;
	private final static int TEXTAREA_LINE_HEIGHT = 13;
//	private void grow() {
//		Element element = textArea.getElement();
//		
//		  int newHeight = element.getScrollHeight();
//		  int currentHeight = element.getClientHeight();
//		  if (newHeight > currentHeight + 10) {
//			  //textArea.setHeight(newHeight + 5 * TEXTAREA_LINE_HEIGHT + "px");
//			  textArea.setHeight(newHeight+"px");
//		  }
//	}
	
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
	public void resetEditableFields() {
		textArea.setText("");
//		if(parentSuggestionBox != null)
//			parentSuggestionBox.setText(""); //probably already cleared through the oracle
	}
	
	
//	@Override
//	public void setEmbedTargetPlaceholder(String embedTargetPlaceholder) {
//		this.embedTargetPlaceholder = embedTargetPlaceholder;
//	}


	@Override
	public HasClickHandlers getAddCommentClickHandlers() {
		return addCommentButton;
	}
	
	@Override
	public HasClickHandlers getCancelClickHandlers() {
		return cancelButton;
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
	public void installParentSuggestionBox(SuggestBox parentSuggestionBox) {
		this.parentSuggestionBox = parentSuggestionBox;
		topicTitlePanel.setVisible(true);
		replyToPanel.clear();
		replyToPanel.add(parentSuggestionBox);
	}
	
	@Override
	public Widget getWidget() {
		return outer;
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
		
		cancelButton.click();
//		main.clear();
		//outer.setVisible(false);
		//outer.removeFromParent();		
		//main.removeFromParent();
	}

	@Override
	public boolean isEnableCloseHandler() {
		return enableCloseHandler;
	}

	@Override
	public void setEnableCloseHandler(boolean enableCloseHandler) {
		this.enableCloseHandler = enableCloseHandler;
	}
	
	
	
}
