package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;

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
				Window.alert("Unrate "+post.getTitle());
			}
		});
	}
	
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
