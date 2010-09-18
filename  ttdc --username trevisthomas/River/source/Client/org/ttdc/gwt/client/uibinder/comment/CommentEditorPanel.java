package org.ttdc.gwt.client.uibinder.comment;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.comments.EmbedContentPopup;
import org.ttdc.gwt.client.presenters.comments.EmbedContentPopupSource;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.uibinder.comment.InsertTrevTagPopup.InsertTrevTagPopupSource;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;
import org.ttdc.gwt.shared.util.StringTools;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CommentEditorPanel extends Composite implements PersonEventListener, EmbedContentPopupSource, InsertTrevTagPopupSource{
	interface MyUiBinder extends UiBinder<Widget, CommentEditorPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	public enum Mode{EDIT,CREATE}
	private Mode mode = Mode.CREATE;
	private Injector injector;	
	private Map<String,String> embedMap = new HashMap<String,String>();
	
	@UiField(provided = true) ClickableIconPanel embedButtonElement = new ClickableIconPanel("tt-toolbar-icon tt-toolbar-icon-embed");
	@UiField(provided = true) ClickableIconPanel quoteButtonElement = new ClickableIconPanel("tt-toolbar-icon tt-toolbar-icon-quote");
	
	@UiField(provided = true) TextArea textAreaElement = new TextArea();
	@UiField SimplePanel topicSuggestionHolderElement;
	
	@UiField(provided = true) ClickableIconPanel postButtonElement = createGraphicButton("Post");
	@UiField(provided = true) ClickableIconPanel previewButtonElement = createGraphicButton("Preview");
	@UiField(provided = true) ClickableIconPanel cancelButtonElement = createGraphicButton("Cancel");
	@UiField(provided = true) ClickableIconPanel editButtonElement = createGraphicButton("Edit");
	
	@UiField CheckBox deletedCheckBoxElement;
	@UiField CheckBox reviewCheckBoxElement;
	@UiField CheckBox infCheckBoxElement;
	@UiField CheckBox nwsCheckBoxElement;
	@UiField CheckBox privateCheckBoxElement;
	@UiField CheckBox lockedCheckBoxElement;
	
	private GPost post = null;
	private SuggestBox parentSuggestionBox;
	private SugestionOracle parentSuggestionOracle;
	
	@Inject
	public CommentEditorPanel(Injector injector) {
		this.injector = injector;
		//Pre realization stuff
				
		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private ClickableIconPanel createGraphicButton(String text) {
		ClickableIconPanel button = new ClickableIconPanel("tt-graphic-button-normal","tt-graphic-button-down");
		button.addStyleName("tt-comment-button");
		button.add(new Label(text));
		return button;
	}

	public void init(Mode mode, GPost post) {
		this.mode = mode;
		this.post = post;
		
		GPerson person = ConnectionId.getInstance().getCurrentUser();
		
		if(Mode.EDIT.equals(mode)){
			editButtonElement.setVisible(true);
			postButtonElement.setVisible(false);
		}
		else{
			editButtonElement.setVisible(false);
			postButtonElement.setVisible(true);
		}
		
		configureLocked(person, post);
		configureDeleted(person, post);
		configureReviewCheckbox(person, post);
		configurePrivateCheckbox(person, post);
	}

	private void configureReviewCheckbox(GPerson person, GPost post) {
		if(post != null){
			boolean visible = false;
			boolean enabled = false;
			boolean checked = false;
			
			if(Mode.EDIT.equals(mode)){
				visible = post.getParent().isMovie();
				enabled = true;
				checked = post.isReview();
			}
			else{
				visible = post.isMovie();
				enabled = !post.isReviewedBy(person.getPersonId());
				checked = post.isMovie();
			}
			configureCheckBox(reviewCheckBoxElement, visible, enabled, checked);
		}
		else{
			configureCheckBox(reviewCheckBoxElement, false, false, false);
		}
	}
	private void configurePrivateCheckbox(GPerson person, GPost post) {
		if(person.isPrivateAccessAccount()){
			boolean enabled;
			boolean visible;
			boolean checked;
			if(Mode.EDIT.equals(mode)){
				enabled = true;
				visible = true;
				checked = post.isPrivate();
			}
			else{
				if(post != null){
					enabled = !post.isPrivate();
					visible = true;
					checked = post.isPrivate();
				}
				else{
					enabled = true;
					visible = true;
					checked = false;
				}
			}
			configureCheckBox(privateCheckBoxElement, visible, enabled, checked);
		}
		else{
			configureCheckBox(privateCheckBoxElement, false, false, false);
		}
	}
	
	private void configureDeleted(GPerson person, GPost post){
		if(person.isAdministrator()){
			boolean visible = true;
			boolean enabled = true;
			boolean checked;
			
			if(Mode.EDIT.equals(mode) && post != null){
				checked = post.isDeleted();
			}
			else{
				checked = false;
			}
			configureCheckBox(deletedCheckBoxElement, visible, enabled, checked);
		}
		else{
			configureCheckBox(deletedCheckBoxElement, false, false, false);
		}
	}
	
	private void configureLocked(GPerson person, GPost post){
		if(person.isAdministrator()){
			boolean visible = true;
			boolean enabled = true;
			boolean checked;
			
			if(Mode.EDIT.equals(mode) && post != null){
				checked = post.isLocked();
			}
			else{
				checked = false;
			}
			configureCheckBox(lockedCheckBoxElement, visible, enabled, checked);
		}
		else{
			configureCheckBox(lockedCheckBoxElement, false, false, false);
		}
	}
	
	private void configureCheckBox(CheckBox checkBox, boolean visible, boolean enabled, boolean checked){
		checkBox.setVisible(visible);
		checkBox.setValue(checked);
		checkBox.setEnabled(enabled);
	}
	
	public void close(){
		removeFromParent();
	}
	
	@UiHandler("postButtonElement")
	public void onClickPostButton(ClickEvent e){
//		String body = applyRealEmbeding(textAreaElement.getText());
//		Window.alert(body);
		
		executeCommand(PostActionType.CREATE);
	}
	
	private String applyRealEmbeding(String text) {
		String body = text;
		for(String key : embedMap.keySet()){
			String code = embedMap.get(key);
			body = body.replaceFirst(StringTools.escapeRexExSpecialCharacters(key), code);
		}
		body = StringTools.unescapeRexExSpecialCharacters(body);
		return body;
	}

	@UiHandler("cancelButtonElement")
	public void onClickCancelButton(ClickEvent e){
		close();
	}
	
	@UiHandler("embedButtonElement")
	public void onClickEmbedButton(ClickEvent e){
		EmbedContentPopup popup = new EmbedContentPopup(this, textAreaElement.getSelectedText());
		popup.showPositionRelativeTo(embedButtonElement);
	}
	
	@UiHandler("quoteButtonElement")
	public void onClickQuoteButton(ClickEvent e){
		String selected = textAreaElement.getSelectedText().trim();
		String open = "q[";
		String close = "]q";
		if(selected.length() == 0){
			InsertTrevTagPopup popup = new InsertTrevTagPopup(this, open, close, true);
			popup.showPositionRelativeTo(quoteButtonElement);
		}
		else{
			wrapSelection(open,close);
		}
	}
	 
	@Override
	public void performLinkEmbed(String selectedText, String directSource, String embedSource) {
		String realEmbed = "<a target=\"_blank\" href=\""+directSource+"\">"+selectedText+"</a><a href=\"javascript:tggle_embed7('"+embedSource+"');\">[view]</a>";
		String fakeEmbed = selectedText+" [view]";
		embedMap.put(fakeEmbed,realEmbed);		
		
		String body = textAreaElement.getText();
		String before = body.substring(0, textAreaElement.getCursorPos());
		String after = body.substring(textAreaElement.getCursorPos() + textAreaElement.getSelectionLength(),body.length());
		body = before + fakeEmbed + after;
		textAreaElement.setText(body);
	}
	
	@Override
	public void performInsert(String text) {
		String body = textAreaElement.getText();
		String before = body.substring(0, textAreaElement.getCursorPos());
		String after = body.substring(textAreaElement.getCursorPos() + textAreaElement.getSelectionLength(),body.length());
		body = before + text + after;
		textAreaElement.setText(body);
	}
	
	private void wrapSelection(String open, String close){
		performInsert(open + textAreaElement.getSelectedText() + close);
	}
	
//	private void createPost() {
//		executeCommand(PostActionType.CREATE);
//	}
	
	private void editPost(){
		executeCommand(PostActionType.UPDATE);
	}

	private void executeCommand(PostActionType action) {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(action);
		String body = applyRealEmbeding(textAreaElement.getText());
		cmd.setBody(body);
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		
		cmd.setPrivate(privateCheckBoxElement.getValue());
		cmd.setDeleted(deletedCheckBoxElement.getValue());
		cmd.setNws(nwsCheckBoxElement.getValue());
		cmd.setInf(infCheckBoxElement.getValue());
		cmd.setReview(reviewCheckBoxElement.getValue());
		cmd.setLocked(lockedCheckBoxElement.getValue());
		
		//Should only be used for non-auth users
//		cmd.setLogin(view.getUserName().getText());
//		cmd.setPassword(view.getPassword().getText());
		
		//Do i dump tagging completly?
//		for(RemovableTagPresenter tagPresenter : tagPresenterList){
//			GTag tag = tagPresenter.getTag();
//			cmd.addTag(tag);
//		}
		
		//Determine if the user is creating a new topic or a new conversation in a topic
		if(post == null){
			if(parentSuggestionOracle.getCurrentSuggestion() != null && !parentSuggestionOracle.getCurrentSuggestion().isCreateNew()){
				SuggestionObject suggestion = parentSuggestionOracle.getCurrentSuggestion();
				cmd.setParentId(suggestion.getPost().getPostId());
			}
			else{
				cmd.setTitle(parentSuggestionBox.getText());
			}
		}
		else{
			cmd.setParentId(post.getPostId());//For reply
			cmd.setPostId(post.getPostId());//For edit
		}
		CommandResultCallback<PostCommandResult> callback;
		if(PostActionType.UPDATE.equals(action)){
			callback = buildEditPostCallback();
		}
		else{
			callback = buildCreatePostCallback();
		}
			
		injector.getService().execute(cmd,callback);
	}
	
	

	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				//view.resetEditableFields();
				if(parentSuggestionOracle != null)
					parentSuggestionOracle.clear();
				close();
				//Window.Location.reload();
				//PostEvent event = new PostEvent(PostEventType.NEW_FORCE_REFRESH, result.getPost());
				//EventBus.fireEvent(event);
				PostEvent event = new PostEvent(PostEventType.LOCAL_NEW, result.getPost());
				EventBus.fireEvent(event);
				
				//EventBus.reload();
			}
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
		};
		return callback;
	}
	
	private CommandResultCallback<PostCommandResult> buildEditPostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				//view.close();
				Window.Location.reload();
				//PostEvent event = new PostEvent(PostEventType.NEW_FORCE_REFRESH, result.getPost());
				//EventBus.fireEvent(event);
				EventBus.reload();
			}
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
		};
		return callback;
	}
	

}
