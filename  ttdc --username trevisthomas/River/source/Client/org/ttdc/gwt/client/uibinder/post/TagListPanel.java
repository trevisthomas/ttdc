package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter.PostRatingCallback;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TagListPanel extends Composite implements PersonEventListener, PostEventListener{
	interface MyUiBinder extends UiBinder<Widget, TagListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
//	private List<GAssociationPostTag> tagAssociationList = new ArrayList<GAssociationPostTag>();
	private List<RemovableTagPresenter> tagPresenterList = new ArrayList<RemovableTagPresenter>();
	private Mode mode;
	private SugestionOracle tagSuggestionOracle;
	private SuggestBox tagSuggestionBox;
	private GPost post;
	
	@UiField HTMLPanel tagListElement;
	@UiField HTMLPanel editableTagListElement;
	@UiField FlowPanel tagsElement;
	@UiField FlowPanel editableTagsElement;
	@UiField SimplePanel tagSelectorElement;
	@UiField Button addButtonElement;
	@UiField Button cancelButtonElement;
	@UiField Anchor editTagsLinkElement;
	@UiField HTMLPanel tagEditElement;
	
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
		tagListElement.setVisible(false);
		
		tagSuggestionOracle = injector.getTagSugestionOracle();
		tagSuggestionBox = tagSuggestionOracle.createSuggestBoxForPostView();
		tagSelectorElement.add(tagSuggestionBox);
		EventBus.getInstance().addListener((PersonEventListener)this);
		EventBus.getInstance().addListener((PostEventListener)this);

	}
	
	public void init(GPost post, Mode mode){
		this.post = post;
		this.mode = mode;
		
		changeToMode(mode);
		
		editTagsLinkElement.setText("edit");

		applyUserPrivilege();
	}

	private void applyUserPrivilege() {
		GPerson person = ConnectionId.getInstance().getCurrentUser();
		if(person.isAnonymous()){
			tagEditElement.setVisible(false);
		}
		else{
			tagEditElement.setVisible(true);
		}
		
		tagsElement.clear();
		editableTagsElement.clear();
		for(GAssociationPostTag ass : post.getTagAssociations()){
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
	}

	private void changeToMode(Mode mode) {
		if(Mode.EDIT_ONLY.equals(mode) || Mode.EDIT.equals(mode)){
			editableTagListElement.setVisible(true);
			tagListElement.setVisible(false);
		}
		else{ //Mode.STATIC.equals(mode) || Mode.EDITABLE.equals(mode)
			editableTagListElement.setVisible(false);
			tagListElement.setVisible(true);
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
	
//	private void createRemovableTagWithoutAssociation(GTag tag) {
//		RemovableTagPresenter tagPresenter = injector.getRemovableTagPresenter();
//		tagPresenterList.add(tagPresenter);
//		editableTagsElement.add(tagPresenter.getWidget());
//		tagPresenter.init(tag, new RemoveTagClickHandler(tagPresenter));
//	}
	
	private class RemoveTagClickHandler implements ClickHandler{
		private RemovableTagPresenter presenter;
		private GAssociationPostTag ass;
//		public RemoveTagClickHandler(RemovableTagPresenter presenter) {
//			this.presenter = presenter;
//		}
		
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
		}
		tagSuggestionOracle.clear();
		
		
		//createRemovableTagWithoutAssociation(tag);
		
		processTagCreation(tag);
		
	}
	
	@UiHandler("cancelButtonElement")
	public void onClickCancelEdit(ClickEvent event){
		changeToMode(Mode.STATIC);
	}
	
	@UiHandler("editTagsLinkElement")
	public void onClickEdit(ClickEvent event){
		changeToMode(Mode.EDIT);
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
	
//	private class CreateTagCallback extends CommandResultCallback<AssociationPostTagResult>{
//		private GPost post;
//		public CreateTagCallback(GPost post) {
//			this.post = post;
//		}
//		
//		/*
//		 * NOTE:  I'm firing the tag event locally so that the browser that tagged gets the refresh
//		 * message too.  Other browser will get the event from the server
//		 * 
//		 */
//		@Override
//		public void onSuccess(AssociationPostTagResult result) {
//			if(result.isCreate()){
//				PostEvent event = new PostEvent(PostEventType.EDIT,result.getAssociationPostTag().getPost());
//				EventBus.fireEvent(event);
//				TagEvent tagEvent = new TagEvent(TagEventType.NEW, result.getAssociationPostTag());
//				EventBus.fireEvent(tagEvent);
//			}
//			else if(result.isRemove()){
//				PostEvent event = new PostEvent(PostEventType.EDIT,result.getPost());
//				EventBus.fireEvent(event);
//				TagEvent tagEvent = new TagEvent(TagEventType.REMOVED, result.getAssociationPostTag());
//				EventBus.fireEvent(tagEvent);
//			}
//			else{
//				MessageEvent event = new MessageEvent(MessageEventType.SYSTEM_ERROR,result.getAssociationId());
//				EventBus.fireEvent(event);
//			}
//			
//		}
//	}
	
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
