package org.ttdc.gwt.client.presenters.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.uibinder.post.PostPanel;
import org.ttdc.gwt.client.uibinder.post.PostSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.ReviewSummaryListPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public final class PostCollectionPresenter extends BasePresenter<PostCollectionPresenter.View> implements PostEventListener{
	private final List<PostPresenterCommon> postPresenters = new ArrayList<PostPresenterCommon>();  //Trevis, you may not need to hold these.
	private boolean expanded = true;
	

	/**
	 *  View for a PostCollectionPresenter 
	 *
	 */
	public static interface View extends BaseView{
		HasClickHandlers getToggleExpandHandler();
		HasWidgets getPostWidgets();
		void setExpanded(boolean expanded);
	}
	
	@Inject
	public PostCollectionPresenter(Injector injector) {
		super(injector,injector.getPostCollectionView());
		bind();
		EventBus bus = EventBus.getInstance();
		bus.addListener(this);
	}

	private void bind(){
		getView().getToggleExpandHandler().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				toggleExpand();
			}
		});
	}

	public boolean isExpanded() {
		return expanded;
	}

	

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public void toggleExpand() {
		setExpanded(!isExpanded());			
	}
	public void setPostList(List<GPost> postList) {
		setPostList(postList,Mode.FLAT);
	}
	public void setPostList(List<GPost> postList, Mode mode) {
		postPresenters.clear();
		getView().getPostWidgets().clear();
		addPostsToPostList(postList,mode);
	}

	public void addPostsToPostList(List<GPost> postList, Mode mode) {
		for(GPost post : postList){
			if(post.isMovie()){
				ReviewSummaryListPanel reviewSummaryListPanel = injector.createReviewSummaryListPanel();
				reviewSummaryListPanel.init(post);
				getView().getPostWidgets().add(reviewSummaryListPanel);
			}
			else if(post.isSuggestSummary()){
				PostSummaryPanel postSummaryPanel = injector.createPostSummaryPanel();
				postSummaryPanel.init(post);
				getView().getPostWidgets().add(postSummaryPanel);
				postPresenters.add(postSummaryPanel);
			}
			else{
				PostPanel postPanel = injector.createPostPanel();
				postPanel.setPost(post,mode);
				getView().getPostWidgets().add(postPanel);
				postPresenters.add(postPanel);
			}
		}
	}
	
	public void insertPostsToPostList(List<GPost> postList, Mode mode) {
		for(GPost post : postList){
			if(post.isSuggestSummary()){
//				PostSummaryPresenter postPresenter = injector.getPostSummaryPresenter();
//				postPresenter.setPost(post);
//				postPresenters.add(0,postPresenter);
				
				PostSummaryPanel postSummaryPanel = injector.createPostSummaryPanel();
				postSummaryPanel.init(post);
				getView().getPostWidgets().add(postSummaryPanel);
				postPresenters.add(0,postSummaryPanel);
			}
			else{
//				PostPresenter postPresenter = injector.getPostPresenter();
//				postPresenter.setPost(post,mode);
//				postPresenters.add(0,postPresenter);
				PostPanel postPanel = injector.createPostPanel();
				postPanel.setPost(post,mode);
				//getView().getPostWidgets().add(postUiB);
				postPresenters.add(0,postPanel);
			}
		}
		getView().getPostWidgets().clear();
		for(PostPresenterCommon presenter : postPresenters){
			getView().getPostWidgets().add(presenter.getWidget());
		}
	}
	
//	private SimplePanel createParentDelegateContainer(PostSummaryPanel postSummaryPanel) {
//		SimplePanel parent = new SimplePanel();
//		postSummaryPanel.addToWidget(parent);
//		return parent;
//	}

	public int size(){
		return postPresenters.size();
	}
	
	public List<PostPresenterCommon> getPostPresenterList(){
		return postPresenters;
	}

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.getType().isExpandContract()){
			for(PostPresenterCommon postPresenter : postPresenters){
				//TODO think about adding a useful method to the PostPresenterCommon interface for this?
//				if(postPresenter instanceof PostSummaryPresenter)
//					((PostSummaryPresenter)postPresenter).contractPost();
				postPresenter.contractPost();
			}
		}
	}
}
