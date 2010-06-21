package org.ttdc.gwt.client.presenters.movies;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class MovieRatingPresenter extends BasePresenter<MovieRatingPresenter.View> implements RatableContentProcessor{
	private Map<Float,GTag> ratingTagMap = new HashMap<Float,GTag>(); 
	private GPost post;
	
	boolean autohide = true;
	
	public interface View extends BaseView{
		void setRating(String rating);
		void initVoteMode(RatableContentProcessor ratingProcessor);
		void initShowMode(String rating);
	}
	
	
	@Inject
	public MovieRatingPresenter(Injector injector) {
		super(injector, injector.getMovieRatingView());
		loadTags();
	}

	private void loadTags(){
		TagCommand cmd = new TagCommand();
		cmd.setAction(TagActionType.LOAD_RATINGS);
		injector.getService().execute(cmd, createTagRatingListCallback());
	}
	
	public void setRating(String rating){
		view.setRating(rating);
	}
	
	public void setRating(GTag ratingTag){
		if(ratingTag == null)
			return;
		view.setRating(ratingTag.getValue()); //TODO make awesomer
	}

	public void setRating(GAssociationPostTag ratingByPerson) {
		if(ratingByPerson != null)
			setRating(ratingByPerson.getTag());
		
	}

	public void setRatablePost(GPost parentPost) {
		this.post = parentPost;
		view.initVoteMode(this);
	}
	
	public void reInititalize(String personId){
		initializeMovieRatingPresenter(post, personId);
	}
	
	public void initializeMovieRatingPresenter(GPost post, String personId) {
		GAssociationPostTag ass;
		this.post = post;
		if(StringUtil.notEmpty(personId)){
			ass = post.getRatingByPerson(personId);
			if(ass != null){
				setRating(ass.getTag().getValue());
			}
			else{
				view.initVoteMode(this);
			}
		}
		else{
			setRating(post.getAvgRatingTag().getValue());
		}
	}
	
	@Override
	public void processRatingRequest(float rating) {
		//This cant work before the list comes back. it's asynch so check first, i guess.
		if(ratingTagMap.size() == 0)
			throw new RuntimeException("Rating tag list hasn't been populated. This should not happen.");
		
		GTag tag = ratingTagMap.get(rating);
		
		RpcServiceAsync service = injector.getService();
		//AssociationPostTagCommand cmd = AssociationPostTagCommand.createTagCommand(tag, post.getPostId());
		
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setTag(tag);
		cmd.setPostId(post.getPostId());
		cmd.setMode(AssociationPostTagCommand.Mode.CREATE);
		
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		service.execute(cmd, new PostRatingCallback(post));
		if(autohide)
			getWidget().removeFromParent();	
		else
			view.initShowMode(tag.getValue());
	}

	private AsyncCallback<AssociationPostTagResult> createRatingCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	private CommandResultCallback<GenericListCommandResult<GTag>> createTagRatingListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GTag>>(){
			public void onSuccess(GenericListCommandResult<GTag> result) {
				for(GTag t : result.getList()){
					ratingTagMap.put(Float.parseFloat(t.getValue()),t);
				}
			};
		};
	}
	
	/**
	 * 
	 *	This was originally used for movie ratings but i'm also trying to use it for like and unlike 
	 *
	 */
	//TODO: If this is looking good, you might want to pull this class out of the movie area and use it as a generic post refresh
	public static class PostRatingCallback extends CommandResultCallback<AssociationPostTagResult>{
		private GPost post;
		public PostRatingCallback(GPost post) {
			this.post = post;
		}
		
		/*
		 * NOTE:  I'm firing the tag event locally so that the browser that tagged gets the refresh
		 * message too.  Other browser will get the event from the server
		 * 
		 */
		@Override
		public void onSuccess(AssociationPostTagResult result) {
			if(result.isCreate()){
				PostEvent event = new PostEvent(PostEventType.EDIT,result.getAssociationPostTag().getPost());
				EventBus.fireEvent(event);
				TagEvent tagEvent = new TagEvent(TagEventType.NEW, result.getAssociationPostTag());
				EventBus.fireEvent(tagEvent);
			}
			else if(result.isRemove()){
				PostEvent event = new PostEvent(PostEventType.EDIT,result.getPost());
				EventBus.fireEvent(event);
				TagEvent tagEvent = new TagEvent(TagEventType.REMOVED, result.getAssociationPostTag());
				EventBus.fireEvent(tagEvent);
			}
			else{
				MessageEvent event = new MessageEvent(MessageEventType.SYSTEM_ERROR,result.getAssociationId());
				EventBus.fireEvent(event);
			}
			
		}
	}
	
	public boolean isAutohide() {
		return autohide;
	}

	public void setAutohide(boolean autohide) {
		this.autohide = autohide;
	}

}
