package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionListener;
import org.ttdc.gwt.client.autocomplete.TagSugestionOracle;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
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
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.inject.Inject;

public class NewCommentPresenter extends BasePresenter<NewCommentPresenter.View> implements PersonEventListener{
	public interface View extends BaseView{
		HasText getUserName();
		HasText getPassword();
		
		//HasWidgets getToolbarPanel();
		//HasWidgets getTextArea();
		HasHTML getCommentBody();
		HasWidgets replyToPanel();
		HasClickHandlers getAddCommentClickHandlers();
		HasWidgets getMessagePanel();
		void setEmbedTargetPlaceholder(String embedTargetPlaceholder);
		
		HasValue<Boolean> getDeletedCheckbox();
		HasValue<Boolean> getReviewCheckbox();
		HasValue<Boolean> getInfCheckbox();
		HasValue<Boolean> getNwsCheckbox();
		HasValue<Boolean> getPrivateCheckbox();
		HasValue<Boolean> getLockedCheckbox();
		
		void setReviewable(boolean enabled);
		void setForAdmin(boolean enabled);
		void setForPrivate(boolean enabled);
		
	}
	private String embedTargetPlaceholder = "EmbedTarget_PLACEHOLDER";
	private SuggestBox suggestionBox;
	private TagSugestionOracle oracle;
	@Inject
	public NewCommentPresenter(Injector injector){
		super(injector, injector.getNewCommentView());
		init();
		view.setEmbedTargetPlaceholder(embedTargetPlaceholder);
		
		EventBus.getInstance().addListener(this);
		GPerson currentUser = ConnectionId.getInstance().getCurrentUser();
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
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.getType().isUserChanged()){
			configureForUser(event.getSource());
		}
		
	}
	
	public void init(){
		oracle = injector.getTagSugestionOracle();
		suggestionBox = oracle.createSuggestBoxForTopics();
		view.replyToPanel().add(suggestionBox);
		
		view.getAddCommentClickHandlers().addClickHandler(
			new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					createPost();
				}
			}
		);

		suggestionBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				TagSuggestion suggestion = oracle.getCurrentTagSuggestion();
				if(suggestion != null){
					GPost parent = suggestion.getPost();
					if(parent != null){
						PostCrudCommand cmd = new PostCrudCommand();
						cmd.setPostId(parent.getPostId());
						injector.getService().execute(cmd, callbackParentPostSelected());
					}
					else{
						view.setReviewable(false);
					}
				}
			}
		});
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
			}
		};
		return rootPostCallback;
	}
	
	
	private void createPost() {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.CREATE);
		cmd.setBody(view.getCommentBody().getHTML());
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		cmd.setEmbedMarker(embedTargetPlaceholder);
		
		cmd.setPrivate(view.getPrivateCheckbox().getValue());
		cmd.setPrivate(view.getDeletedCheckbox().getValue());
		cmd.setPrivate(view.getNwsCheckbox().getValue());
		cmd.setPrivate(view.getInfCheckbox().getValue());
		cmd.setPrivate(view.getReviewCheckbox().getValue());
		cmd.setPrivate(view.getLockedCheckbox().getValue());
		
//		cmd.setLogin(login)
//		cmd.setPassword(password)
		
		//Determine if the user is creating a new topic or a new conversation in a topic
		
		if(oracle.getCurrentTagSuggestion() != null){
			TagSuggestion suggestion = oracle.getCurrentTagSuggestion();
			cmd.setParentId(suggestion.getPost().getPostId());
		}
		else{
			cmd.setTitle(suggestionBox.getText());
		}
		
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				Window.alert("Created");
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				super.onFailure(caught);
			}
		};
		
		getService().execute(cmd,callback);
		
	}

}
