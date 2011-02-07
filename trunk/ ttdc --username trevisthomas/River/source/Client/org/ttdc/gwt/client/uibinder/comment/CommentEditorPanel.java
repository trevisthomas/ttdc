package org.ttdc.gwt.client.uibinder.comment;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.comments.EmbedContentPopup;
import org.ttdc.gwt.client.presenters.comments.EmbedContentPopupSource;
import org.ttdc.gwt.client.presenters.comments.LinkDialog;
import org.ttdc.gwt.client.presenters.comments.LinkDialogSource;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.users.LoginPresenter;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.presenters.util.MyListBox;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.comment.InsertTrevTagPopup.InsertTrevTagPopupSource;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;
import org.ttdc.gwt.shared.util.StringTools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CommentEditorPanel extends Composite implements PersonEventListener, EmbedContentPopupSource, InsertTrevTagPopupSource, LinkDialogSource{
	interface MyUiBinder extends UiBinder<Widget, CommentEditorPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private static CommentEditorPanel oldInstance;
	
	public enum Mode{EDIT,CREATE}
	private Mode mode = Mode.CREATE;
	private Injector injector;	
	private Map<String,String> embedMap = new HashMap<String,String>();
	
	@UiField(provided = true) ClickableIconPanel embedButtonElement = createToolbarButton("tt-toolbar-icon-embed", "Embed Image or Video");
	@UiField(provided = true) ClickableIconPanel quoteButtonElement = createToolbarButtonEx("tt-toolbar-icon-quote", "Quote", "q[", "]q", true);
	@UiField(provided = true) ClickableIconPanel linkButtonElement = createToolbarButton("tt-toolbar-icon-link", "Generate Link");
	@UiField(provided = true) ClickableIconPanel blueButtonElement = createToolbarButtonEx("tt-toolbar-icon-blue", "Blue", "b{", "}b", false);
	@UiField(provided = true) ClickableIconPanel redButtonElement = createToolbarButtonEx("tt-toolbar-icon-red", "Red", "r{", "}r", false);
	@UiField(provided = true) ClickableIconPanel orangeButtonElement = createToolbarButtonEx("tt-toolbar-icon-orange", "Orange", "o{", "}o", false);
	@UiField(provided = true) ClickableIconPanel greenButtonElement = createToolbarButtonEx("tt-toolbar-icon-green", "Green", "g{", "}g", false);
	
	@UiField(provided = true) ClickableIconPanel offsiteButtonElement = createToolbarButtonEx("tt-toolbar-icon-offsite", "Offsite Quote", "o[", "]o", true);
	@UiField(provided = true) ClickableIconPanel italicButtonElement = createToolbarButtonEx("tt-toolbar-icon-italic", "Italic", "i[", "]i", false);
	@UiField(provided = true) ClickableIconPanel bigButtonElement = createToolbarButtonEx("tt-toolbar-icon-big", "Big", "B[", "]S", false);
	@UiField(provided = true) ClickableIconPanel smallButtonElement = createToolbarButtonEx("tt-toolbar-icon-small", "Small", "s[", "]s", false);
	
	@UiField(provided = true) ClickableIconPanel boldButtonElement = createToolbarButtonEx("tt-toolbar-icon-bold", "Bold", "b[", "]b", false);
	@UiField(provided = true) ClickableIconPanel strikeButtonElement = createToolbarButtonEx("tt-toolbar-icon-strike", "Strikethrough", "-[", "]-", false);
	@UiField(provided = true) ClickableIconPanel hiddenButtonElement = createToolbarButtonEx("tt-toolbar-icon-hidden", "Hidden Spoiler", "h{", "}h", false);
	@UiField(provided = true) ClickableIconPanel underlineButtonElement = createToolbarButtonEx("tt-toolbar-icon-underline", "Underline", "u[", "]u", false);
	@UiField(provided = true) ClickableIconPanel codeButtonElement = createToolbarButtonEx("tt-toolbar-icon-code", "Code - monospaced", "c[", "]c", true);
	@UiField(provided = true) ClickableIconPanel indentButtonElement = createToolbarButtonEx("tt-toolbar-icon-indent", "Indented", "p[", "]p", true);
	
	@UiField TextArea descriptionTextAreaElement;
	@UiField HTMLPanel topicFieldsElement;
	@UiField (provided = true) ListBox forumListBoxElement;
	
	private ClickableIconPanel createToolbarButton(String iconStyle, String toolTip) {
		ClickableIconPanel button = new ClickableIconPanel("tt-toolbar-icon "+iconStyle);
		button.setTitle(toolTip);
		return button;
	}
	
	ClickableIconPanel createToolbarButtonEx(final String iconStyle,final String toolTip,
			final String open, final String close, final boolean multiLine) {
		ClickableIconPanel button = new ClickableIconPanel("tt-toolbar-icon "+iconStyle);
		button.setTitle(toolTip);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				performSimpleStyleMarkup(open, close, multiLine);
			}
		});
		return button;
	}
	
	@UiField(provided = true) TextArea textAreaElement = new TextArea();
	@UiField SimplePanel topicSuggestionHolderElement;
	
	@UiField(provided = true) ClickableIconPanel postButtonElement = createGraphicButton("Post");
	@UiField(provided = true) ClickableIconPanel previewButtonElement = createGraphicButton("Preview");
	@UiField(provided = true) ClickableIconPanel cancelButtonElement = createGraphicButton("Cancel");
	@UiField(provided = true) ClickableIconPanel editButtonElement = createGraphicButton("Update");
	@UiField(provided = true) ClickableIconPanel loginButtonElement = createGraphicButton("Login");
	
	@UiField CheckBox deletedCheckBoxElement;
	@UiField CheckBox reviewCheckBoxElement;
	@UiField CheckBox infCheckBoxElement;
	@UiField CheckBox nwsCheckBoxElement;
	@UiField CheckBox privateCheckBoxElement;
	@UiField CheckBox lockedCheckBoxElement;
	
	@UiField SimplePanel ratingElement;
	@UiField Label ratingLabelElement;
	@UiField Label topicLabelElement;
	@UiField Label parentInfoElement;
	
	@UiField PasswordTextBox passwordTextElement;
	@UiField TextBox loginTextElement;
	@UiField HTMLPanel loginElement;
	@UiField SimplePanel previewElement;
	@UiField TableCellElement ratingLabelCellElement;
	@UiField TableCellElement commentLabelCellElement;
	
	private GPost post = null;
	private SuggestBox parentSuggestionBox;
	private SuggestionOracle parentSuggestionOracle;
	private final TextBox titleEditTextBox = new TextBox();
	
	boolean operationInProgress = false;
	
	
	@Inject
	public CommentEditorPanel(Injector injector) {
		this.injector = injector;
		//Pre realization stuff
		forumListBoxElement = new MyListBox(false);
				
		initWidget(binder.createAndBindUi(this));
		
		topicFieldsElement.setVisible(false);
		EventBus.getInstance().addListener(this);
		
		topicLabelElement.setVisible(false);
		topicSuggestionHolderElement.setVisible(false);
		
		textAreaElement.setFocus(true);
		
		try{
			if(oldInstance != null){
				oldInstance.removeFromParent();
			}
		}
		catch(IllegalStateException e){
			throw new RuntimeException("Illegal state has been found! Caused by ComentEditorPanel!", e);
		}
		oldInstance = this;
		
	}
	
	@UiHandler("loginButtonElement")
	public void onClickLogin(ClickEvent event){
		LoginPresenter loginPresenter = injector.getLoginPresenter();
		loginPresenter.loginInNow(loginTextElement.getText(), passwordTextElement.getText());
	}
	
	public void setFocus() {
		textAreaElement.setFocus(true);
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			init(mode,post, showSmall);
			textAreaElement.setFocus(true);
		}
	}
	
	private ClickableIconPanel createGraphicButton(String text) {
		ClickableIconPanel button = new ClickableIconPanel("tt-graphic-button-normal","tt-graphic-button-down");
		button.addStyleName("tt-comment-button");
		button.add(new Label(text));
		return button;
	}

	private boolean showSmall = false;
	public void init(Mode mode, GPost post, boolean showSmall) {
		this.showSmall = showSmall;
		
		if(showSmall){
			ratingLabelCellElement.setClassName("tt-hidden");
			commentLabelCellElement.setClassName("tt-hidden");
		}
		
		ratingElement.setVisible(false);
		ratingLabelElement.setVisible(false);
		parentInfoElement.setVisible(false);
		previewElement.setVisible(false);
		//topicSuggestionHolderElement.setVisible(false);
		
		this.mode = mode;
		this.post = post;
		
		GPerson person = ConnectionId.getInstance().getCurrentUser();
		
		loginElement.setVisible(person.isAnonymous());
		
		
		if(Mode.EDIT.equals(mode)){
			editButtonElement.setVisible(true);
			postButtonElement.setVisible(false);
			
			if(post != null){
				infCheckBoxElement.setValue(post.isINF());
				nwsCheckBoxElement.setValue(post.isNWS());
			}
		}
		else{
			editButtonElement.setVisible(false);
			postButtonElement.setVisible(true);
		}
		
		configureLocked(person, post);
		configureDeleted(person, post);
		configureReviewCheckbox(person, post);
		configurePrivateCheckbox(person, post);
		
		configure();
		
		textAreaElement.setFocus(true);
	}
	
	private void configure(){
		switch (mode) {
			case CREATE:
				parentInfoElement.setVisible(true);
				if(post == null){
					if(parentSuggestionOracle == null){
						setupParentSuggestionOracle();
					}
					parentInfoElement.setText("Note: No topic with this title exists, a new topic will be created.");
				}
				else{
					if(!post.isRootPost()){
						parentInfoElement.setVisible(false);
					}
					else{
						parentInfoElement.setText("Note: " + post.getMass()+ " comments in " + post.getReplyCount() + " conversations already exist on this topic.");
					}
					if(post.isRatable()){
						initForRatableParent();
					}
				}
				
				break;
			case EDIT:
				if(post == null){
					throw new RuntimeException("No post to edit! Cant edit null post.");
				}
				
				if(post.isRootPost()){
					topicSuggestionHolderElement.setVisible(true);
					topicSuggestionHolderElement.clear();
					
					titleEditTextBox.setText(post.getTitle());
					topicSuggestionHolderElement.add(titleEditTextBox);
				}
				parentInfoElement.setText("Note: No topic with this title exists, a new topic will be created.");
				
				textAreaElement.setText(post.getEntry());
				break;
			default:
				throw new RuntimeException("Not sure what to do");
		}
	}
	
	private void configureForTopicCreation(GPost parent){
		if(parent != null){
			//Do stuff to show the user that they are creating a new topic?
			topicFieldsElement.setVisible(true);
			createForumList();
		}
		else{
			//do stuff to show that it's just conversation or reply post
			topicFieldsElement.setVisible(false);
		}
	}
	
	private void createForumList() {
		if(forumListBoxElement.getItemCount() != 0)
			return;
		
		ForumCommand cmd = new ForumCommand();
		CommandResultCallback<GenericListCommandResult<GForum>> callback = buildForumListCallback();
		injector.getService().execute(cmd, callback);
		
	}

	private CommandResultCallback<GenericListCommandResult<GForum>> buildForumListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GForum>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GForum> result) {
				for(GForum forum : result.getList()){
					((MyListBox)forumListBoxElement).addItem(forum.getDisplayValue(), forum.getTagId());
				}
			}
		};
	}

	private void initForRatableParent() {
		//view.configureForTopicCreation(false);
		GPerson currentUser = ConnectionId.getInstance().getCurrentUser();
		
//		if(post.isMovie() && !post.isReviewedBy(currentUser.getPersonId())){
//			view.setReviewable(true);
//		}
		
		ratingLabelElement.setVisible(true);
		ratingElement.setVisible(true);
		if(post.getRatingByPerson(currentUser.getPersonId()) == null){
			MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
			ratingElement.clear();
			ratingElement.add(movieRatingPresenter.getWidget());
			movieRatingPresenter.setRatablePost(post);
			movieRatingPresenter.setAutohide(false);
		}else{
//			ratingElement.clear();
//			ratingElement.setVisible(false);
			MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
			ratingElement.clear();
			ratingElement.add(movieRatingPresenter.getWidget());
			movieRatingPresenter.setRating(post.getRatingByPerson(currentUser.getPersonId()));
		}
	}

	private void setupParentSuggestionOracle() {
		parentSuggestionOracle = injector.getTagSugestionOracle();
		parentSuggestionBox = parentSuggestionOracle.createSuggestBoxForTopics();
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
						injector.getService().execute(cmd, callbackParentPostSelected());
					}
					else{
						//view.setReviewable(false);
						init(Mode.CREATE, null, showSmall); //To reset display for no parent
						configureForTopicCreation(parent);
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
				GPost parent = result.getPost();
				init(mode, parent, showSmall);
//				if(parent.isMovie()){
//					view.setReviewable(true);
//				}
//				else{
//					view.setReviewable(false);
//				}
//				view.configureForTopicCreation(false);
			}
		};
		return rootPostCallback;
	}
	

	private void configureReviewCheckbox(GPerson person, GPost post) {
		if(post != null){
			boolean visible = false;
			boolean enabled = false;
			boolean checked = false;
			
			if(Mode.EDIT.equals(mode)){
				//visible = post.getParent() != null ? post.getParent().isMovie() : false;
				visible = post.isThreadPost() && post.getRoot().isMovie(); //A hack to determine if it is possible for me to be a movie
				enabled = true;
				checked = post.isReview();
			}
			else{
				visible = post.isMovie();
				enabled = !post.isReviewedBy(person.getPersonId());
				checked = post.isMovie() && !post.isReviewedBy(person.getPersonId());
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
		if(person.isAdministrator() && Mode.EDIT.equals(mode)){
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
	
	@UiHandler("editButtonElement")
	public void onClickEditButton(ClickEvent e){
		executeCommand(PostActionType.UPDATE);
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

	@UiHandler("previewButtonElement")
	public void onClickPreview(ClickEvent e){
		performPreview();
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
	
	@UiHandler("linkButtonElement")
	public void onLinkButton(ClickEvent e){
		LinkDialog popup = new LinkDialog(this, textAreaElement.getSelectedText());
		popup.showPositionRelativeTo(linkButtonElement);
	}
	
	private void performSimpleStyleMarkup(final String open, final String close, boolean multiLineInput) {
		String selected = textAreaElement.getSelectedText().trim();
		if(selected.length() == 0){
			InsertTrevTagPopup popup = new InsertTrevTagPopup(this, open, close, multiLineInput);
			popup.showPositionRelativeTo(quoteButtonElement);
		}
		else{
			wrapSelection(open,close);
		}
	}
	
	@Override
	public void performLink(String selectedText, String directSource) {
		String text = "<a target=\"_blank\" href=\""+directSource+"\">"+selectedText+"</a>";
		performInsert(text);
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

	private void executeCommand(PostActionType action) {
		
		if(isOperationInProgress()){
			Window.alert("Operation in progress");
			return;
		}
		
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(action);
		String body = applyRealEmbeding(textAreaElement.getText());
		cmd.setBody(body);
		cmd.setTopicDescription(descriptionTextAreaElement.getText());
		cmd.setForumId(((MyListBox)forumListBoxElement).getSelectedValue());
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		
		if(PostActionType.UPDATE.equals(action)){
			//Update
			GPost post = new GPost();
			if(nwsCheckBoxElement.getValue())
				post.toggleNws();
			if(infCheckBoxElement.getValue())
				post.toggleInf();
			if(privateCheckBoxElement.getValue())
				post.togglePrivate();
			if(lockedCheckBoxElement.getValue())
				post.toggleLocked();
			if(deletedCheckBoxElement.getValue())
				post.toggleDeleted();
			if(reviewCheckBoxElement.getValue())
				post.toggleReview();
			cmd.setMetaMask(post.getMetaMask());
		}
		else{
			//Create
			cmd.setPrivate(privateCheckBoxElement.getValue());
			cmd.setDeleted(deletedCheckBoxElement.getValue());
			cmd.setNws(nwsCheckBoxElement.getValue());
			cmd.setInf(infCheckBoxElement.getValue());
			cmd.setReview(reviewCheckBoxElement.getValue());
			cmd.setLocked(lockedCheckBoxElement.getValue());
		}
		
		
		
		
		//Should only be used for non-auth users
		cmd.setLogin(loginTextElement.getText());
		cmd.setPassword(passwordTextElement.getText());
		
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
			if(post.isRootPost()){
				cmd.setTitle(titleEditTextBox.getText());
			}
		}
		CommandResultCallback<PostCommandResult> callback;
		if(PostActionType.UPDATE.equals(action)){
			callback = buildEditPostCallback();
		}
		else{
			callback = buildCreatePostCallback();
		}
		
		injector.getService().execute(cmd,callback);
		setOperationInProgress(true);
	}
	
	

	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				//view.resetEditableFields();
				if(parentSuggestionOracle != null)
					parentSuggestionOracle.clear();
				close();
				
				GPerson creator = result.getPost().getCreator();
				if(creator.equals(ConnectionId.getInstance().getCurrentUser())){
					//Anon users should be the only ones creating posts as a different user than the one in their con ide
					ConnectionId.getInstance().getCurrentUser().setSiteReadDate(creator.getSiteReadDate());
				}
				
				PostEvent event = new PostEvent(PostEventType.LOCAL_NEW, result.getPost());
				EventBus.fireEvent(event);
			}
			@Override
			public void onFailure(Throwable caught) {
				setOperationInProgress(false);
				super.onFailure(caught);
			}
		};
		return callback;
	}
	
	private CommandResultCallback<PostCommandResult> buildEditPostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				setOperationInProgress(false);
				close();
				PostEvent event = new PostEvent(PostEventType.EDIT, result.getPost());
				EventBus.fireEvent(event);
				//TODO: somehow refresh titles when the root is edited.
			}
			@Override
			public void onFailure(Throwable caught) {
				setOperationInProgress(false);
				super.onFailure(caught);
			}
		};
		return callback;
	}

	/* This is here because the standard header puts the post editor into another
	 * component and that component has other things to do when it is closed 
	 */
	public void addCancelClickHandler(ClickHandler clickHandler) {
		cancelButtonElement.addClickHandler(clickHandler);
	}
	
	private CommandResultCallback<PostCommandResult> buildPreviewPostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				setOperationInProgress(false);
				previewElement.setVisible(true);
				previewElement.clear();
				previewElement.add(new HTML(result.getPost().getLatestEntry().getBody()));
			}
			@Override
			public void onFailure(Throwable caught) {
				setOperationInProgress(false);
				super.onFailure(caught);
			}
		};
		return callback;
	}

	private void performPreview(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.PREVIEW);
		String body = applyRealEmbeding(textAreaElement.getText());
		cmd.setBody(body);
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		
		CommandResultCallback<PostCommandResult> callback = buildPreviewPostCallback();
			
		injector.getService().execute(cmd,callback);
	}

	public boolean isOperationInProgress() {
		return operationInProgress;
	}

	public void setOperationInProgress(boolean operationInProgress) {
		this.operationInProgress = operationInProgress;
	}

}
