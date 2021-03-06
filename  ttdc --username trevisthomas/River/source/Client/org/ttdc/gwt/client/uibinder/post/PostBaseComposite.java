package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.constants.UserObjectConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.comment.CommentEditorPanel;
import org.ttdc.gwt.client.uibinder.comment.ReparentPanel;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand.Mode;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;

abstract public class PostBaseComposite extends Composite{
		
	protected Injector injector;
	private HasWidgets commentElement;
	private TagListPanel tagListPanel;
	
	private GPost post;
	
	//Not using the annotation was intentional. I didnt think that i should.
	public PostBaseComposite(Injector injector){
		this.injector = injector;
	}
	
	public void init(GPost post, HasWidgets commentElement, TagListPanel tagListPanel){
		this.post = post;
		this.commentElement = commentElement;
		this.tagListPanel = tagListPanel;
	}	
	
	protected SimplePanel createPostActionLinks(FocusPanel hoverDivElement) {
		final SimplePanel actionLinks = new SimplePanel();
		actionLinks.addStyleName("tt-active-post-box");
    	actionLinks.addStyleName("tt-active-post-links");
    	hoverDivElement.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				actionLinks.addStyleName("tt-active-post-links-hover");
				actionLinks.removeStyleName("tt-active-post-links");
			}
		});
    	
    	hoverDivElement.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				actionLinks.removeStyleName("tt-active-post-links-hover");
				actionLinks.addStyleName("tt-active-post-links");
			}
		});
    	return actionLinks;
	}
	
	public IconOptionsPanel buildBoundOptionsIconPanel(final GPost post){
		IconOptionsPanel optionsListPanel = injector.createIconOptionPanel();
		optionsListPanel.init(post);
		
		optionsListPanel.addReplyClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCommentEditor();
			}
		});
		
		optionsListPanel.addRatingClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
				movieRatingPresenter.setRatablePost(post);
				commentElement.clear();
				commentElement.add(movieRatingPresenter.getWidget());
			}
		});
		
		optionsListPanel.addUnRateClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Window.alert("Unrate "+post.getTitle());
				processRemoveRatingRequest();
			}
		});
		
		optionsListPanel.addEditClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(post.isMovie()){
					showMovieEditor();
				}
				else{
					showEditCommentEditor();
				}
			}
		});
		
		optionsListPanel.addReparentClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//There are probably conditions underwhich this should not show...
				showReparentPanel();
			}
		});
		
		optionsListPanel.addMuteThreadClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processMuteThreadRequest();
			}
		});
		
		optionsListPanel.addUnMuteThreadClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processUnMuteThreadRequest();
			}
		});
		
		optionsListPanel.addLikePostClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				processLikePostRequest();
			}
		});
		
		optionsListPanel.addUnLikePostClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				processUnLikePostRequest();
			}
		});
		
		optionsListPanel.addEarmarkClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				processEarmarkPostRequest();
			}
		});
		
		optionsListPanel.addUnEarmarkClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				processUnEarmarkPostRequest();
			}
		});
		
		optionsListPanel.addTagClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//processMuteThreadRequest();
				processTagPostRequest();
			}
		});
		
		return optionsListPanel;
	}
	
//	public PostOptionsListPanel buildBoundOptionsListPanel(final GPost post) {
//		PostOptionsListPanel optionsListPanel = injector.createPostOptionsListPanel();
//		optionsListPanel.init(post);
//		
//		optionsListPanel.addReplyClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showCommentEditor();
//			}
//		});
//		
//		optionsListPanel.addRatingClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
//				movieRatingPresenter.setRatablePost(post);
//				commentElement.clear();
//				commentElement.add(movieRatingPresenter.getWidget());
//			}
//		});
//		
//		optionsListPanel.addUnRateClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				//Window.alert("Unrate "+post.getTitle());
//				processRemoveRatingRequest();
//			}
//		});
//		
//		optionsListPanel.addEditClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if(post.isMovie()){
//					showMovieEditor();
//				}
//				else{
//					showEditCommentEditor();
//				}
//			}
//		});
//		
//		optionsListPanel.addMuteThreadClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				processMuteThreadRequest();
//			}
//		});
//		
//		optionsListPanel.addUnMuteThreadClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnMuteThreadRequest();
//			}
//		});
//		
//		optionsListPanel.addLikePostClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processLikePostRequest();
//			}
//		});
//		
//		optionsListPanel.addUnLikePostClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnLikePostRequest();
//			}
//		});
//		
//		optionsListPanel.addEarmarkClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processEarmarkPostRequest();
//			}
//		});
//		
//		optionsListPanel.addUnEarmarkClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnEarmarkPostRequest();
//			}
//		});
//		
//		optionsListPanel.addTagClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				//processMuteThreadRequest();
//				processTagPostRequest();
//			}
//		});
//		
//		return optionsListPanel;
//	}
	
//	private void initializeOptionsPopup(final GPost post, Widget showRelativeTo) {
//		optionsPanel = injector.createOptionsPanel();
//		optionsPanel.setAutoHideEnabled(true);
//		optionsPanel.init(post);
//		optionsPanel.showRelativeTo(showRelativeTo);
//		
//		optionsPanel.addReplyClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showNewCommentEditor();
//			}
//		});
//		
//		optionsPanel.addRatingClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
//				movieRatingPresenter.setRatablePost(post);
//				commentElement.clear();
//				commentElement.add(movieRatingPresenter.getWidget());
//			}
//		});
//		
//		optionsPanel.addUnRateClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				//Window.alert("Unrate "+post.getTitle());
//				processRemoveRatingRequest();
//			}
//		});
//		
//		optionsPanel.addEditClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if(post.isMovie()){
//					showMovieEditor();
//				}
//				else{
//					showEditCommentEditor();
//				}
//			}
//		});
//		
//		optionsPanel.addMuteThreadClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				processMuteThreadRequest();
//			}
//		});
//		
//		optionsPanel.addUnMuteThreadClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnMuteThreadRequest();
//			}
//		});
//		
//		optionsPanel.addLikePostClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processLikePostRequest();
//			}
//		});
//		
//		optionsPanel.addUnLikePostClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnLikePostRequest();
//			}
//		});
//		
//		optionsPanel.addEarmarkClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processEarmarkPostRequest();
//			}
//		});
//		
//		optionsPanel.addUnEarmarkClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				processUnEarmarkPostRequest();
//			}
//		});
//		
//		optionsPanel.addTagClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				//processMuteThreadRequest();
//				processTagPostRequest();
//			}
//		});
//		
//	}
	
//	public TagListPanel getTagListPanel() {
//		return tagListPanel;
//	}
//
//	public void setTagListPanel(TagListPanel tagListPanel) {
//		this.tagListPanel = tagListPanel;
//	}
//	
	private void processUnEarmarkPostRequest() {
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		user.setEarmarks(user.getEarmarks()-1);
		GAssociationPostTag association = post.getEarmarkByPerson(user.getPersonId());
		
		PersonEvent event = new PersonEvent(PersonEventType.USER_EARMKARK_COUNT_CHANGED, user);
		EventBus.fireEvent(event);
		
		removeAssociation(association);
	}
	
	private void processEarmarkPostRequest() {
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		user.setEarmarks(user.getEarmarks()+1);
		
		PersonEvent event = new PersonEvent(PersonEventType.USER_EARMKARK_COUNT_CHANGED, user);
		EventBus.fireEvent(event);
		
		createAssociation(TagConstants.TYPE_EARMARK, user.getPersonId());
	}
	
	protected void processUnLikePostRequest(){
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		GAssociationPostTag association = post.getLikedByPerson(user.getPersonId());
		removeAssociation(association);
	}

	protected void processLikePostRequest(){
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		createAssociation(TagConstants.TYPE_LIKE, user.getPersonId());
	}
	
	private void removeAssociation(GAssociationPostTag association) {
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setMode(AssociationPostTagCommand.Mode.REMOVE);
		cmd.setAssociationId(association.getGuid());
		cmd.setMode(Mode.REMOVE);
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		//TODO: If this is looking good, you might want to pull this class out of the movie area and use it as a generic post refresh
		injector.getService().execute(cmd, new MovieRatingPresenter.PostRatingCallback(post));
	}
	
	private void createAssociation(String type, String value) {
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		
		GTag tag = new GTag();
		tag.setValue(value);
		tag.setType(type);
		cmd.setTag(tag);
		cmd.setPostId(post.getPostId());
		cmd.setMode(AssociationPostTagCommand.Mode.CREATE);
		//TODO: If this is looking good, you might want to pull this class out of the movie area and use it as a generic post refresh
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		injector.getService().execute(cmd, new MovieRatingPresenter.PostRatingCallback(post));
	}
	
	
//	private class AssociationPostTagCallback extends CommandResultCallback<AssociationPostTagResult>{
//		@Override
//		public void onSuccess(AssociationPostTagResult result) {
//			if(result.isCreate()){
//				//addTagAssociationToList(result.getAssociationPostTag());
//			}
//			else if(result.isRemove()){
//				//nothing to do
//			}
//			else{
//				MessageEvent event = new MessageEvent(MessageEventType.SYSTEM_ERROR,result.getAssociationId());
//				EventBus.getInstance().fireEvent(event);
//			}
//		}
//	}
//	
	protected void processUnMuteThreadRequest() {
		UserObjectCrudCommand cmd = new UserObjectCrudCommand();
		cmd.setType(UserObjectConstants.TYPE_FILTER_THREAD);
		cmd.setAction(ActionType.DELETE);
		cmd.setValue(post.getRoot().getPostId());
		
		injector.getService().execute(cmd, createRefreshPageCallback());
	}

	protected void processMuteThreadRequest() {
		UserObjectCrudCommand cmd = new UserObjectCrudCommand();
		cmd.setType(UserObjectConstants.TYPE_FILTER_THREAD);
		cmd.setAction(ActionType.CREATE);
		cmd.setValue(post.getRoot().getPostId());
		
		injector.getService().execute(cmd,createRefreshPageCallback());
	}

	private CommandResultCallback<GenericCommandResult<GUserObject>> createRefreshPageCallback() {
		return new CommandResultCallback<GenericCommandResult<GUserObject>>(){
				@Override
				public void onSuccess(
						GenericCommandResult<GUserObject> result) {
					PostEvent event = new PostEvent(PostEventType.NEW_FORCE_REFRESH, post);
					EventBus.fireEvent(event);
				}
			};
	}

	public void processRemoveRatingRequest() {
		RpcServiceAsync service = injector.getService();
		//AssociationPostTagCommand cmd = AssociationPostTagCommand.createTagCommand(tag, post.getPostId());
		
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setMode(AssociationPostTagCommand.Mode.REMOVE);
		
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		GAssociationPostTag ratingAssociation = post.getRatingByPerson(user.getPersonId());
		
		cmd.setAssociationId(ratingAssociation.getGuid());
		cmd.setMode(Mode.REMOVE);
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		service.execute(cmd, new MovieRatingPresenter.PostRatingCallback(post));
		
	}
	
//	private class PostRatingCallback extends CommandResultCallback<AssociationPostTagResult>{
//		@Override
//		public void onSuccess(AssociationPostTagResult result) {
//			if(result.isCreate()){
//				//addTagAssociationToList(result.getAssociationPostTag());
//				Window.alert("Rated"+post.getTitle()+" "+result.getAssociationPostTag().getTag().getValue());
//			}
//			else if(result.isRemove()){
//				//nothing to do
//				Window.alert("Removed Rating"+post.getTitle()+" "+result.getAssociationPostTag().getTag().getValue());
//			}
//			else{
//				MessageEvent event = new MessageEvent(MessageEventType.SYSTEM_ERROR,result.getMessage());
//				EventBus.getInstance().fireEvent(event);
//			}
//		}
//	}
	
	
	/*
	 * Override this to show the small comment editor
	 */
	protected boolean isShowSmallCommentEditor(){
		return true;
	}
	
	private void processTagPostRequest() {
		tagListPanel.showEditor();
	}
	
	protected void showCommentEditor(){
		CommentEditorPanel commentEditor = injector.createCommentEditorPanel();
		commentEditor.init(CommentEditorPanel.Mode.CREATE, post, isShowSmallCommentEditor());
		commentElement.clear();
		commentElement.add(commentEditor);
		commentEditor.setFocus();
	}
	
	protected void showMovieEditor() {
		commentElement.clear();
		NewMoviePanel newMoviePanel = injector.createNewMoviePanel();
		newMoviePanel.init(post);
		commentElement.add(newMoviePanel);
		newMoviePanel.setFocus();
	}
	
	protected void showReparentPanel(){
		commentElement.clear();
		ReparentPanel reparentPanel = injector.createReparentPanel();
		reparentPanel.init(post);
		commentElement.add(reparentPanel);
	}

	
	private void showEditCommentEditor() {
//		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
//		commentPresneter.init(NewCommentPresenter.Mode.EDIT, post);
//		commentElement.clear();
//		commentElement.add(commentPresneter.getWidget());
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.READ);
		cmd.setPostId(post.getPostId());
		cmd.setUnformatted(true);
		
		CommandResultCallback<PostCommandResult> callback = buildEditPostCallback();
		injector.getService().execute(cmd,callback);
		
	}
	
	private CommandResultCallback<PostCommandResult> buildEditPostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				CommentEditorPanel commentEditor = injector.createCommentEditorPanel();
				commentEditor.init(CommentEditorPanel.Mode.EDIT, result.getPost(),isShowSmallCommentEditor());
				commentElement.clear();
				commentElement.add(commentEditor);
				commentEditor.setFocus();
			}
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
		};
		return callback;
	}
	
	
	//@UiHandler("moreOptionsElement")
//	void onClickMoreOptions(ClickEvent event){
//		Widget source = (Widget) event.getSource();
//		initializeOptionsPopup(post,source);
//        //optionsPanel.showRelativeTo(source);
//		//injector.createSiteUpdatePanel();
//	}
	
		
//	protected void setupLikesElement(GPost post, SimplePanel likesElement) {
//		List<GAssociationPostTag> likeList = post.readTagAssociations(TagConstants.TYPE_LIKE);
//		if(likeList.size() > 0){
//			likesElement.setVisible(true);
//			LikesPresenter likesPresenter = injector.getLikesPresenter();
//			likesPresenter.init(likeList);
//			likesElement.clear();
//			likesElement.add(likesPresenter.getWidget());
//		}
//		else{
//			likesElement.setVisible(false);
//		}
//	}

	public GPost getPost() {
		return post;
	}

	public void setPost(GPost post) {
		this.post = post;
	}
	
	
	
}
