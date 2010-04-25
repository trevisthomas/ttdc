package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ReviewSummaryListPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, ReviewSummaryListPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
	private Injector injector;
	
	
	private MoreOptionsPopupPanel optionsPanel;
    private GPost post;
    private ImagePresenter imagePresenter;
    //private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter postLinkPresenter;
    private DatePresenter createDatePresenter;
    private PostCollectionPresenter postCollectionPresenter;
    private MovieRatingPresenter averageMovieRatingPresenter;
    
    
	@UiField(provided = true) Hyperlink titleElement;
    @UiField SpanElement replyCountElement;
    @UiField(provided = true) Widget posterElement;
//    @UiField(provided = true) Hyperlink creatorLinkElement;
//    @UiField(provided = true) Widget createDateElement;
    @UiField(provided = true) Widget averageRatingElement;
    @UiField VerticalPanel reviewsElement = new VerticalPanel();
    @UiField Anchor moreOptionsElement;
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    
	@Inject
    public ReviewSummaryListPanel(Injector injector) { 
		this.injector = injector;
		imagePresenter = injector.getImagePresenter();
		averageMovieRatingPresenter = injector.getMovieRatingPresenter();
		postLinkPresenter = injector.getHyperlinkPresenter();
		
		titleElement = postLinkPresenter.getHyperlink();
		posterElement = imagePresenter.getWidget();
		averageRatingElement = averageMovieRatingPresenter.getWidget();
		
		initWidget(binder.createAndBindUi(this)); 
	}
	
	public void init(GPost post){
		this.post = post;
		imagePresenter.setImageAsMoviePoster(post);
		imagePresenter.init();
		
		averageMovieRatingPresenter.setRating(post.getAvgRatingTag());
		
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
		moreOptionsElement.setText("> More Options");
		moreOptionsElement.setStyleName("tt-cursor-pointer");
		
		for(GPost p : post.getPosts()){
			ReviewSummaryPanel summaryPanel = injector.createReviewSummaryPanel();
			summaryPanel.init(p);
			reviewsElement.add(summaryPanel);
		}
		
		
	}
	
	private void initializeOptionsPopup(final GPost post) {
		optionsPanel = injector.createOptionsPanel();
		optionsPanel.setAutoHideEnabled(true);
		optionsPanel.init(post);
		
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
	protected void showNewCommentEditor() {
		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
		commentPresneter.init(post);
		commentElement.clear();
		commentElement.add(commentPresneter.getWidget());
		
	}
	
	@UiHandler("moreOptionsElement")
	void onClickMoreOptions(ClickEvent event){
		initializeOptionsPopup(post);
		Widget source = (Widget) event.getSource();
        optionsPanel.showRelativeTo(source);
	}
}
