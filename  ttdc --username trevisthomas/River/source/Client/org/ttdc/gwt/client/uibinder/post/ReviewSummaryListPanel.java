package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * I think that this class encapsulates a movie.  It shows the review summaries and average rating.
 *
 */
public class ReviewSummaryListPanel extends PostBaseComposite implements PostEventListener, PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, ReviewSummaryListPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
	private GPost post;
    private ImagePresenter imagePresenter;
    private HyperlinkPresenter postLinkPresenter;
    private MovieRatingPresenter averageMovieRatingPresenter;
    private TagListPanel tagListPanel;
    
    @UiField(provided = true) Hyperlink titleElement;
    @UiField(provided = true) Widget posterElement;
    @UiField(provided = true) Widget averageRatingElement;
    @UiField VerticalPanel reviewsElement = new VerticalPanel();
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    @UiField (provided = true) PostDetailPanel postDetailPanelElement;    
    @UiField(provided = true) Widget tagsElement;
        
	@Inject
    public ReviewSummaryListPanel(Injector injector) { 
		super(injector);
		imagePresenter = injector.getImagePresenter();
		averageMovieRatingPresenter = injector.getMovieRatingPresenter();
		postLinkPresenter = injector.getHyperlinkPresenter();
		postDetailPanelElement = injector.createPostDetailPanel();
		tagListPanel = injector.createTagListPanel();
		
		titleElement = postLinkPresenter.getHyperlink();
		posterElement = imagePresenter.getWidget();
		averageRatingElement = averageMovieRatingPresenter.getWidget();
		tagsElement = tagListPanel;
		
		initWidget(binder.createAndBindUi(this)); 
		
//		averageRatingElement.addStyleName("tt-center");
		
		
		EventBus.getInstance().addListener(this);
	}
	
	public void init(GPost post){
		super.init(post, commentElement, tagListPanel);
		//postDetailPanelElement.init(post, commentElement, tagListPanel, inReplyPostElement);
		postDetailPanelElement.init(post, commentElement, tagListPanel, null);
		
		this.post = post;
		imagePresenter.setImageAsSmallMoviePoster(post);
		imagePresenter.init();
		
		averageMovieRatingPresenter.setRating(post.getAvgRatingTag(), post.getRateCount(), true );
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
		List<String> personIdsWithReviews = new ArrayList<String>();
		reviewsElement.clear();
		for(GPost p : post.getPosts()){
			ReviewSummaryPanel summaryPanel = injector.createReviewSummaryPanel();
			summaryPanel.init(p);
			reviewsElement.add(summaryPanel);
			personIdsWithReviews.add(p.getCreator().getPersonId());
		}
		
		List<GAssociationPostTag> ratingAssList = post.readTagAssociations(TagConstants.TYPE_RATING);
		for(GAssociationPostTag rating : ratingAssList){
			if(!personIdsWithReviews.contains(rating.getCreator().getPersonId())){
				ReviewSummaryPanel summaryPanel = injector.createReviewSummaryPanel();
				summaryPanel.init(rating);
				reviewsElement.add(summaryPanel);
			}
		}
		
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
	
	}

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().getPostId().equals(post.getPostId())){
			init(postEvent.getSource());
		}
	}

	@Override
	public void contractPost() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getPostId() {
		return post.getPostId();
	}

	@Override
	public void expandPost() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Widget getWidget() {
		//return super.getWidget();
		return this;
	}
}
