package org.ttdc.gwt.client.presenters.comments;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionListener;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.inject.Inject;

public class NewCommentPresenter extends BasePresenter<NewCommentPresenter.View> implements PersonEventListener{
	public interface View extends BaseView{
		HasText getUserName();
		HasText getPassword();
		
		//HasWidgets getToolbarPanel();
		//HasWidgets getTextArea();
		HasHTML getCommentBody();
		HasWidgets ratingPanel();
		HasClickHandlers getAddCommentClickHandlers();
		HasClickHandlers getEditCommentClickHandlers();
		HasClickHandlers getCancelClickHandlers();
		//HasClickHandlers getAddReviewClickHandlers();
		
		HasWidgets getMessagePanel();
		//void setEmbedTargetPlaceholder(String embedTargetPlaceholder);
		
		HasValue<Boolean> getDeletedCheckbox();
		HasValue<Boolean> getReviewCheckbox();
		HasValue<Boolean> getInfCheckbox();
		HasValue<Boolean> getNwsCheckbox();
		HasValue<Boolean> getPrivateCheckbox();
		HasValue<Boolean> getLockedCheckbox();
		
		void setReviewable(boolean enabled);
		void setForAdmin(boolean enabled);
		void setForPrivate(boolean enabled);
		
		HasClickHandlers addTagClickHandler();
		HasWidgets tagsPanel();
		HasWidgets tagSelectorPanel();
		void setMode(Mode mode);
		void close();
		
		void showLoginFields();
		void configureForTopicCreation(boolean b);
		boolean isEnableCloseHandler();
		void setEnableCloseHandler(boolean enable);
		void installParentSuggestionBox(SuggestBox parentSuggestionBox);
	}
	
	public enum Mode{EDIT,CREATE}
	private GPost post = null;
	//private String embedTargetPlaceholder = "EmbedTarget_PLACEHOLDER";
	private SuggestBox parentSuggestionBox;
	private SugestionOracle parentSuggestionOracle;
	
	private SuggestBox tagSuggestionBox;
	private SugestionOracle tagSuggestionOracle;
	private Mode mode = Mode.CREATE;
	private GPerson currentUser;
	
	private List<RemovableTagPresenter> tagPresenterList = new ArrayList<RemovableTagPresenter>();
	
	@Inject
	public NewCommentPresenter(Injector injector){
		super(injector, injector.getNewCommentView());
		//view.setEmbedTargetPlaceholder(embedTargetPlaceholder);
		
		EventBus.getInstance().addListener(this);
		currentUser = ConnectionId.getInstance().getCurrentUser();
		configureForUser(currentUser);
	}

	private void configureForUser(GPerson currentUser) {
		if(currentUser.isAdministrator()){
			view.setForAdmin(true);
		}
		else{
			view.setForAdmin(false);
		}
		
		if(currentUser.hasPrivilege(PrivilegeConstants.PRIVATE) || currentUser.isAdministrator()){
			view.setForPrivate(true);
		}
		else{
			view.setForPrivate(false);
		}
		
		if(currentUser.isAnonymous()){
			view.showLoginFields();
		}
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.getType().isUserChanged()){
			configureForUser(event.getSource());
		}
		
	}
	
	
	public void init(Mode mode, GPost post){
		this.mode = mode;
		this.post = post;
		init();
	}
	
	public void init(){
		switch (mode) {
		case CREATE:
			if(post == null){
				initAsNewRootMode();
			}
			else if(post.isRatable()){
				initForRatableParent();
			}
			
			break;
		case EDIT:
			if(post == null){
				throw new RuntimeException("No post to edit! Cant edit null post.");
			}
			view.getCommentBody().setHTML(post.getEntry());
			break;
		default:
			throw new RuntimeException("Not sure what to do");
		}
		
		view.setMode(mode);
		
		view.getAddCommentClickHandlers().addClickHandler(
			new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					createPost();
				}
			}
		);
		view.getEditCommentClickHandlers().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						editPost();
					}
				}
			);
		
		tagSuggestionOracle = injector.getTagSugestionOracle();
		tagSuggestionBox = tagSuggestionOracle.createSuggestBoxForPostView();
		view.tagSelectorPanel().add(tagSuggestionBox);
		
		view.addTagClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SuggestionObject suggestion = tagSuggestionOracle.getCurrentSuggestion();
				if(suggestion != null){
					createRemovableTag(suggestion.getTag());
				}
				else{
					GTag tag = new GTag();
					tag.setValue(tagSuggestionBox.getValue());
					createRemovableTag(tag);
				}
				tagSuggestionOracle.clear();
			}
		});
	}

	private void initForRatableParent() {
		view.configureForTopicCreation(false);
		if(post.isMovie() && !post.isReviewedBy(currentUser.getPersonId())){
			view.setReviewable(true);
		}
		if(post.getRatingByPerson(currentUser.getPersonId()) == null){
			MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
			view.ratingPanel().clear();
			view.ratingPanel().add(movieRatingPresenter.getWidget());
			movieRatingPresenter.setRatablePost(post);
		}
	}

	private void initAsNewRootMode() {
		parentSuggestionOracle = injector.getTagSugestionOracle();
		parentSuggestionBox = parentSuggestionOracle.createSuggestBoxForTopics();
		view.installParentSuggestionBox(parentSuggestionBox);
		
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
						view.setReviewable(false);
						view.configureForTopicCreation(true);
					}
				}
				else{
					throw new RuntimeException("Suggestion came back null. Bad juju, this shouldnt happen.");
				}
			}
		});
	}
	
	private void createRemovableTag(GTag tag) {
		RemovableTagPresenter tagPresenter = injector.getRemovableTagPresenter();
		tagPresenterList.add(tagPresenter);
		view.tagsPanel().add(tagPresenter.getWidget());
		tagPresenter.init(tag, new RemoveTagClickHandler(tagPresenter));
	}
	
	private class RemoveTagClickHandler implements ClickHandler{
		private RemovableTagPresenter presenter;
		public RemoveTagClickHandler(RemovableTagPresenter presenter) {
			this.presenter = presenter;
		}
		@Override
		public void onClick(ClickEvent event) {
			//Remove
			tagPresenterList.remove(presenter);
			view.tagsPanel().remove(presenter.getWidget());
		}
	} 
	
	
	
	private CommandResultCallback<PostCommandResult> callbackParentPostSelected() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost parent = result.getPost();
				if(parent.isMovie()){
					view.setReviewable(true);
				}
				else{
					view.setReviewable(false);
				}
				view.configureForTopicCreation(false);
			}
		};
		return rootPostCallback;
	}
	
	
	private void createPost() {
		executeCommand(PostActionType.CREATE);
		
	}

	private void executeCommand(PostActionType action) {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(action);
		cmd.setBody(view.getCommentBody().getHTML());
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		//cmd.setEmbedMarker(embedTargetPlaceholder);
		
		cmd.setPrivate(view.getPrivateCheckbox().getValue());
		cmd.setDeleted(view.getDeletedCheckbox().getValue());
		cmd.setNws(view.getNwsCheckbox().getValue());
		cmd.setInf(view.getInfCheckbox().getValue());
		cmd.setReview(view.getReviewCheckbox().getValue());
		cmd.setLocked(view.getLockedCheckbox().getValue());
		
		//Should only be used for non-auth users
		cmd.setLogin(view.getUserName().getText());
		cmd.setPassword(view.getPassword().getText());
		
		for(RemovableTagPresenter tagPresenter : tagPresenterList){
			GTag tag = tagPresenter.getTag();
			cmd.addTag(tag);
		}
		
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
		CommandResultCallback<PostCommandResult> callback = buildCreatePostCallback();
		
		getService().execute(cmd,callback);
	}
	
	private void editPost(){
		executeCommand(PostActionType.UPDATE);
	}

	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
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
	
	public void addCancelClickHandler(ClickHandler handler){
		view.setEnableCloseHandler(false); //This is because the cloes button does different things depending on where it is being shown
		view.getCancelClickHandlers().addClickHandler(handler);
	}

}
