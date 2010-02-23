package org.ttdc.gwt.client.presenters.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

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
			if(post.isSuggestSummary()){
				PostSummaryPresenter postPresenter = injector.getPostSummaryPresenter();
				postPresenter.setPost(post);
				getView().getPostWidgets().add(postPresenter.getWidget());
				postPresenters.add(postPresenter);
			}
			else{
				PostPresenter postPresenter = injector.getPostPresenter();
				postPresenter.setPost(post,mode);
				getView().getPostWidgets().add(postPresenter.getWidget());
				postPresenters.add(postPresenter);
			}
		}
	}
	
	public void insertPostsToPostList(List<GPost> postList, Mode mode) {
		for(GPost post : postList){
			if(post.isSuggestSummary()){
				PostSummaryPresenter postPresenter = injector.getPostSummaryPresenter();
				postPresenter.setPost(post);
				postPresenters.add(0,postPresenter);
			}
			else{
				PostPresenter postPresenter = injector.getPostPresenter();
				postPresenter.setPost(post,mode);
				postPresenters.add(0,postPresenter);
			}
		}
		getView().getPostWidgets().clear();
		for(PostPresenterCommon presenter : postPresenters)
			getView().getPostWidgets().add(presenter.getWidget());
	}
	
	public int size(){
		return postPresenters.size();
	}

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.getType().isExpandContract()){
			for(PostPresenterCommon postPresenter : postPresenters){
				//TODO think about adding a useful method to the PostPresenterCommon interface for this?
				if(postPresenter instanceof PostSummaryPresenter)
					((PostSummaryPresenter)postPresenter).contractPost();
			}
		}
	}
}
