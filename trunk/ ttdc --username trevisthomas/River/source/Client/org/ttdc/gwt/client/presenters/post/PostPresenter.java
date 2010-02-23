package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class PostPresenter extends BasePresenter<PostPresenter.View> implements PostPresenterCommon{
	private GPost post;
	private PostCollectionPresenter postCollection = null;
	private int childPostPage = 1;
	
	public enum Mode {NESTED_SUMMARY,FLAT}
	
	@Inject
	public PostPresenter(Injector injector) {
		super(injector,injector.getPostView());
	}
	
	public interface View extends BaseView{
		
		Mode getMode();
		void setMode(Mode mode);
		
		//HasText getPostTitle();
		HasWidgets getChildWidgetBucket();
		HasText getPostEntry();
		//void setCreatorWidget(Widget widget)
		HasWidgets creatorWidget();
		HasWidgets title();
		
		HasText fetchMoreTitle();
		HasClickHandlers fetchMoreTarget();
		
		HasWidgets creationDateTarget();
		
		/*
		HasText getPostTitle();
		HasClickHandlers getEarmarkButton();
		HasClickHandlers getLikeButton();
		HasClickHandlers getMuteButton();
		HasClickHandlers getEditButton();
		HasClickHandlers getReplyButton();
		HasClickHandlers getTagEditorButton();
		
		tagList
		postbody
		childcount?
		switchToThreadView
		showChildren
		markAsReadButton
		setUnread
		inReplyToButton
		
		*/
	}

	
	public GPost getPost() {
		return post;
	}

	public void setPost(GPost post) {
		setPost(post, Mode.FLAT);
	}
	public void setPost(GPost post, Mode mode) {
		this.post = post;
		
		HyperlinkPresenter hyperlinkPresenter = injector.getHyperlinkPresenter();
		hyperlinkPresenter.setPerson(post.getCreator());
		
		//view.setCreatorWidget(hyperlinkPresenter.getWidget());
		view.creatorWidget().add(hyperlinkPresenter.getWidget());
		//view.getPostTitle().setText(post.getTitle());
		hyperlinkPresenter = injector.getHyperlinkPresenter();
		hyperlinkPresenter.setPost(post);
		view.title().add(hyperlinkPresenter.getWidget());
		
		view.getPostEntry().setText(post.getEntry());
		view.setMode(mode);
		//TODO do the rest of what is needed to show the post
		
		DatePresenter datePresener = injector.getDatePresenter();
		datePresener.init(post.getDate());
		view.creationDateTarget().add(datePresener.getWidget());
		
		if(post.getPosts().size() != 0){
			postCollection = injector.getPostCollectionPresenter();
			postCollection.setPostList(post.getPosts());
			//A post will have only one child widget that widget will be 
			//a widget containing all of the children
			view.getChildWidgetBucket().add(postCollection.getWidget());
			
			if(Mode.NESTED_SUMMARY.equals(mode) && postCollection.size() < post.getMass()){
				TopicCommand cmd = new TopicCommand();
				cmd.setPostId(post.getPostId());
				cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
				setupFetchMoreClickHandlerTitle();
				view.fetchMoreTarget().addClickHandler(buildFetchMoreResultsClickHandler(cmd));
			}
		}
		//Do what is needed to display a post.
	}

	private ClickHandler buildFetchMoreResultsClickHandler(final TopicCommand cmd) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommandResultCallback<TopicCommandResult> fetchMorePostsCallback = new CommandResultCallback<TopicCommandResult>(){
					@Override
					public void onSuccess(TopicCommandResult result) {
						postCollection.insertPostsToPostList(result.getResults().getList(), Mode.NESTED_SUMMARY);
						setupFetchMoreClickHandlerTitle();
					}
				};
				cmd.setPageNumber(++childPostPage);
				RpcServiceAsync service = injector.getService();
				service.execute(cmd,fetchMorePostsCallback);
			}
		};
	}

	private void setupFetchMoreClickHandlerTitle() {
		if(postCollection.size() < post.getMass())
			view.fetchMoreTitle().setText("Now showing "+postCollection.size()+ " of "+post.getMass() +" comments. Click for more." );
		else
			view.fetchMoreTitle().setText("");
	}
	public PostCollectionPresenter getPostCollection() {
		return postCollection;
	}
	
}
