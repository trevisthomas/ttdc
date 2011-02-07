package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter.PostRatingCallback;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TagListPanel extends Composite implements PersonEventListener, PostEventListener{
	interface MyUiBinder extends UiBinder<Widget, TagListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	private List<RemovableTagPresenter> tagPresenterList = new ArrayList<RemovableTagPresenter>();
	private Mode mode;
	private SuggestionOracle tagSuggestionOracle;
	private SuggestBox tagSuggestionBox;
	private GPost post;
	
	@UiField HTMLPanel tagListElement;
	@UiField HTMLPanel editableTagListElement;
	@UiField HTMLPanel forumListElement;
	@UiField FlowPanel tagsElement;
	@UiField FlowPanel editableTagsElement;
	@UiField SimplePanel tagSelectorElement;
	@UiField Button addButtonElement;
	@UiField Button cancelButtonElement;
	//@UiField Anchor editTagsLinkElement;
	//@UiField HTMLPanel tagEditElement;
	
	@UiField CheckBox nwsCheckBoxElement;
	@UiField CheckBox infCheckBoxElement;
	@UiField CheckBox privateCheckBoxElement;
	@UiField CheckBox lockedCheckBoxElement;
	@UiField CheckBox deletedCheckBoxElement;
	@UiField CheckBox reviewCheckBoxElement;
	
	public enum Mode{
		STATIC,
		EDITABLE,
		EDIT_ONLY,
		EDIT
	}
	
	@Inject
	public TagListPanel(Injector injector) {
		this.injector = injector;
		
		//
		
		initWidget(binder.createAndBindUi(this));
		
		cancelButtonElement.setText("Close");
		addButtonElement.setText("Add");
		
		editableTagListElement.setVisible(false);
		tagListElement.setVisible(true);
		
		tagSuggestionOracle = injector.getTagSugestionOracle();
		tagSuggestionBox = tagSuggestionOracle.createSuggestBoxForPostView();
		tagSelectorElement.add(tagSuggestionBox);
		EventBus.getInstance().addListener((PersonEventListener)this);
		EventBus.getInstance().addListener((PostEventListener)this);
		
	}
	
	public void init(GPost post, Mode mode){
		this.post = post;
		this.mode = mode;
		setEnabled(true);
		applyUserPrivilege();
		changeToMode(mode);
	}

	private void applyUserPrivilege() {
		GPerson person = ConnectionId.getInstance().getCurrentUser();
		
		if(post == null){
			return;
		}
		
		tagsElement.clear();
		editableTagsElement.clear();
		for(GAssociationPostTag ass : post.getTopicTagAssociations()){
			HyperlinkPresenter tagLink = injector.getHyperlinkPresenter();
			tagLink.setTag(ass.getTag());
			tagLink.init();//TODO: refactor this call out
			tagsElement.add(tagLink.getHyperlink());
			
			if(person.isAdministrator() || person.equals(ass.getCreator())){
				createRemovableTag(ass);
			}
			else{
				createNonRemovableTag(ass.getTag());
			}
		}
		
		
		nwsCheckBoxElement.setValue(post.isNWS());
		infCheckBoxElement.setValue(post.isINF());
		privateCheckBoxElement.setValue(post.isPrivate());
		lockedCheckBoxElement.setValue(post.isLocked());
		deletedCheckBoxElement.setValue(post.isDeleted());
		reviewCheckBoxElement.setValue(post.isReview());
		
		if(person.isPrivateAccessAccount()){
			privateCheckBoxElement.setVisible(true);
		}
		else{
			privateCheckBoxElement.setVisible(false);
		}
		
		if(person.isAdministrator()){
			deletedCheckBoxElement.setVisible(true);
			lockedCheckBoxElement.setVisible(true);
			forumListElement.setVisible(true);
		}
		else{
			deletedCheckBoxElement.setVisible(false);
			lockedCheckBoxElement.setVisible(false);
			forumListElement.setVisible(false);
		}
		
		if((post.getParent() != null && post.getParent().isMovie()) && (person.isAdministrator() || person.equals(post.getCreator()))){
			reviewCheckBoxElement.setVisible(false);
		}
		else{
			reviewCheckBoxElement.setVisible(false);
		}
	}

	public void showEditor(){
		changeToMode(Mode.EDIT);
	}
	
	private void changeToMode(Mode mode) {
		if(Mode.EDIT_ONLY.equals(mode) || Mode.EDIT.equals(mode)){
			editableTagListElement.setVisible(true);
			tagListElement.setVisible(false);
		}
		else if(post.getTopicTagAssociations().size() > 0){ //Mode.STATIC.equals(mode) || Mode.EDITABLE.equals(mode)
			editableTagListElement.setVisible(false);
			tagListElement.setVisible(true);
		}
		else{
			editableTagListElement.setVisible(false);
			tagListElement.setVisible(false);
		}
	}
	
	private void createNonRemovableTag(GTag tag){
		editableTagsElement.add(new Label(tag.getValue()));
	}
	
	private void createRemovableTag(GAssociationPostTag ass) {
		RemovableTagPresenter tagPresenter = injector.getRemovableTagPresenter();
		tagPresenterList.add(tagPresenter);
		editableTagsElement.add(tagPresenter.getWidget());
		tagPresenter.init(ass.getTag(), new RemoveTagClickHandler(tagPresenter, ass));
	}
	
	private class RemoveTagClickHandler implements ClickHandler{
		private RemovableTagPresenter presenter;
		private GAssociationPostTag ass;
		
		public RemoveTagClickHandler(RemovableTagPresenter presenter, GAssociationPostTag ass) {
			this.presenter = presenter;
			this.ass = ass;
		}
		@Override
		public void onClick(ClickEvent event) {
			tagPresenterList.remove(presenter);
			editableTagsElement.remove(presenter.getWidget());
			if(ass != null)//This may be null if i use this TagListPanel in side of New Post Creation!
				processTagRemove(ass);
		}
	
	} 
	
	@UiHandler("addButtonElement")
	public void onClickAdd(ClickEvent event){
		SuggestionObject suggestion = tagSuggestionOracle.getCurrentSuggestion();
		GTag tag;
		if(suggestion != null){
			tag = suggestion.getTag();
		}
		else{
			tag = new GTag();
			tag.setValue(tagSuggestionBox.getValue());
			tag.setType(TagConstants.TYPE_TOPIC);
		}
		tagSuggestionOracle.clear();
		
		processTagCreation(tag);
	}
	
	@UiHandler("cancelButtonElement")
	public void onClickCancelEdit(ClickEvent event){
		changeToMode(Mode.STATIC);
	}
	
//	@UiHandler("editTagsLinkElement")
//	public void onClickEdit(ClickEvent event){
//		changeToMode(Mode.EDIT);
//	}
	
	@UiHandler("nwsCheckBoxElement")
	public void onNwsClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.toggleNws(); 
		updateMetaMask(post);
	}
	
	@UiHandler("infCheckBoxElement")
	public void onInfClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.toggleInf(); 
		updateMetaMask(post);
	}
	
	@UiHandler("privateCheckBoxElement")
	public void onPrivateClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.togglePrivate(); 
		updateMetaMask(post);
	}
	
	@UiHandler("lockedCheckBoxElement")
	public void onLockedClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.toggleLocked(); 
		updateMetaMask(post);
	}
	
	@UiHandler("deletedCheckBoxElement")
	public void onDeletedClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.toggleDeleted(); 
		updateMetaMask(post);
	}
	
	@UiHandler("reviewCheckBoxElement")
	public void onReviewClickBoxValueChange(ValueChangeEvent<Boolean> event){
		post.toggleReview(); 
		updateMetaMask(post);
	}
	
	
	private void setEnabled(boolean enabled){
		nwsCheckBoxElement.setEnabled(enabled);
		infCheckBoxElement.setEnabled(enabled);
		privateCheckBoxElement.setEnabled(enabled);
		lockedCheckBoxElement.setEnabled(enabled);
		deletedCheckBoxElement.setEnabled(enabled);
		reviewCheckBoxElement.setEnabled(enabled);
		addButtonElement.setEnabled(enabled);
		cancelButtonElement.setEnabled(enabled);
		tagSuggestionBox.getTextBox().setEnabled(enabled);
	}
	
	private void updateMetaMask(GPost p) {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.UPDATE_META);
		cmd.setMetaMask(p.getMetaMask());
		cmd.setPostId(p.getPostId());
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		setEnabled(false);
		injector.getService().execute(cmd, buildCreatePostCallback());
	}
	
	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
		
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				setEnabled(true);
				PostEvent event = new PostEvent(PostEventType.EDIT, result.getPost());
				EventBus.fireEvent(event);
			}
			@Override
			public void onFailure(Throwable caught) {
				setEnabled(true);
				EventBus.fireErrorMessage(caught.getMessage());
			}
		};
		return callback;
	}
	
	private void processTagRemove(GAssociationPostTag ass){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setPostId(post.getPostId());
		cmd.setAssociationId(ass.getGuid());
		cmd.setMode(AssociationPostTagCommand.Mode.REMOVE);
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		injector.getService().execute(cmd, new PostRatingCallback(post));
	}

	
	private void processTagCreation(GTag tag){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setTag(tag);
		cmd.setPostId(post.getPostId());
		cmd.setMode(AssociationPostTagCommand.Mode.CREATE);
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		injector.getService().execute(cmd, new PostRatingCallback(post));
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			applyUserPrivilege();
		}
	}
	
	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().equals(post)){
			post = postEvent.getSource();
			applyUserPrivilege();
		}
	}
}
