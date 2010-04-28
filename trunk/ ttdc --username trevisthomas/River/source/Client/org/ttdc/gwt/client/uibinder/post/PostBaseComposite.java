package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter.PostRatingCallback;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand.Mode;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

abstract public class PostBaseComposite extends Composite{
	private MoreOptionsPopupPanel optionsPanel;
	
	protected Injector injector;
	private HasWidgets commentElement;
	private GPost post;
	
	//Not using the annotation was intentional. I didnt think that i should.
	public PostBaseComposite(Injector injector){
		this.injector = injector;
	}
	
	public void init(GPost post, HasWidgets commentElement){
		this.post = post;
		this.commentElement = commentElement;
		
	}	
	private void initializeOptionsPopup(final GPost post, Widget showRelativeTo) {
		optionsPanel = injector.createOptionsPanel();
		optionsPanel.setAutoHideEnabled(true);
		optionsPanel.init(post);
		optionsPanel.showRelativeTo(showRelativeTo);
		
		optionsPanel.addReplyClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showNewCommentEditor();
			}
		});
		
		optionsPanel.addRatingClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MovieRatingPresenter movieRatingPresenter = injector.getMovieRatingPresenter();
				movieRatingPresenter.setRatablePost(post);
				commentElement.clear();
				commentElement.add(movieRatingPresenter.getWidget());
			}
		});
		
		optionsPanel.addUnRateClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Window.alert("Unrate "+post.getTitle());
				processRemoveRatingRequest();
			}
		});
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
	
	private void showNewCommentEditor() {
		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
		commentPresneter.init(post);
		commentElement.clear();
		commentElement.add(commentPresneter.getWidget());
	}
	
	@UiHandler("moreOptionsElement")
	void onClickMoreOptions(ClickEvent event){
		Widget source = (Widget) event.getSource();
		initializeOptionsPopup(post,source);
        //optionsPanel.showRelativeTo(source);
	}
}
