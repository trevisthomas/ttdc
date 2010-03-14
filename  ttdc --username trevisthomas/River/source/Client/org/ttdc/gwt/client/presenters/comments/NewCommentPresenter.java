package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.TagSugestionOracle;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.inject.Inject;

public class NewCommentPresenter extends BasePresenter<NewCommentPresenter.View>{
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
	}
	private String embedTargetPlaceholder = "EmbedTarget_PLACEHOLDER";
	private SuggestBox suggestionBox;
	private TagSugestionOracle oracle;
	@Inject
	public NewCommentPresenter(Injector injector){
		super(injector, injector.getNewCommentView());
		init();
		view.setEmbedTargetPlaceholder(embedTargetPlaceholder);
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
	}
	
	private void createPost() {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.CREATE);
		cmd.setBody(view.getCommentBody().getHTML());
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		cmd.setEmbedMarker(embedTargetPlaceholder);
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
