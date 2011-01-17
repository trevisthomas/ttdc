package org.ttdc.gwt.client.uibinder.comment;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.presenters.util.MyListBox;
import org.ttdc.gwt.client.uibinder.comment.CommentEditorPanel.Mode;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.inject.Inject;

public class ReparentPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ReparentPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	@UiField(provided = true) ClickableIconPanel reparentButtonElement = createGraphicButton("Reparent");
	@UiField(provided = true) ClickableIconPanel cancelButtonElement = createGraphicButton("Cancel");
	@UiField Label topicLabelElement;
	@UiField SimplePanel topicSuggestionHolderElement;
	@UiField Label parentInfoElement;
	
	private SuggestBox parentSuggestionBox;
	private SugestionOracle parentSuggestionOracle;
	
	private Injector injector;
	private GPost post;
	private GPost parent = null;
	
	private ClickableIconPanel createGraphicButton(String text) {
		ClickableIconPanel button = new ClickableIconPanel("tt-graphic-button-normal","tt-graphic-button-down");
		button.addStyleName("tt-comment-button");
		button.add(new Label(text));
		return button;
	}
	
	@Inject
	public ReparentPanel(Injector injector) {
		this.injector = injector;
				
		initWidget(binder.createAndBindUi(this));
		
//		topicFieldsElement.setVisible(false);
//		EventBus.getInstance().addListener(this);
		
		//topicLabelElement.setVisible(false);
		
		topicSuggestionHolderElement.add(parentSuggestionBox);
		
	}
	
	@UiHandler("cancelButtonElement")
	public void onClickCancelButton(ClickEvent e){
		close();
	}
	
	@UiHandler("reparentButtonElement")
	public void onClickReparentButton(ClickEvent e){
		//Perform reparent
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId(post.getPostId());
		if(parent == null){
			throw new RuntimeException("New parent has not been selected");
		}
		cmd.setParentId(parent.getPostId());
		cmd.setAction(PostActionType.REPARENT);
		
		injector.getService().execute(cmd, callbackReparentAction());
	}
	
	
	public void close(){
		removeFromParent();
	}
	
	public void init(final GPost post){
		this.post = post;
		setupParentSuggestionOracle();
	}
	
	private void setupParentSuggestionOracle() {
		parentSuggestionOracle = injector.getTagSugestionOracle();
		parentSuggestionBox = parentSuggestionOracle.createSuggestBoxForPostSearch(); //No create option
		topicLabelElement.setVisible(true);
		topicSuggestionHolderElement.setVisible(true);
		topicSuggestionHolderElement.clear();
		topicSuggestionHolderElement.add(parentSuggestionBox);
		
		parentSuggestionBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				SuggestionObject suggestion = parentSuggestionOracle.getCurrentSuggestion();
				if(suggestion != null){
					GPost parent = suggestion.getPost();
					if(!parent.getPostId().trim().isEmpty()){
						PostCrudCommand cmd = new PostCrudCommand();
						cmd.setPostId(parent.getPostId());
						parent=null;
						injector.getService().execute(cmd, callbackParentPostSelected());
					}
					else{
						parent = null;
						//view.setReviewable(false);
//						init(Mode.CREATE, null); //To reset display for no parent
//						configureForTopicCreation(parent);
					}
				}
				else{
					throw new RuntimeException("Suggestion came back null. Bad juju, this shouldnt happen.");
				}
			}
		});
	}
	
	private CommandResultCallback<PostCommandResult> callbackParentPostSelected() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				parent = result.getPost();
				
				parentInfoElement.setText("Note: " + parent.getMass()+ " comments in " + parent.getReplyCount() + " conversations already exit on this topic.");
				
//				init(mode, parent);
			}
		};
		return rootPostCallback;
	}
	
	
	private AsyncCallback<PostCommandResult> callbackReparentAction() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				close();
				
//				
//				parent = result.getPost();
//				
//				parentInfoElement.setText("Note: " + parent.getMass()+ " comments in " + parent.getReplyCount() + " conversations already exit on this topic.");
				
//				init(mode, parent);
			}
			
		};
		return callback;
	}


}
