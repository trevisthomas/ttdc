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
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
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
    //private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter postLinkPresenter;
    private DatePresenter createDatePresenter;
    private PostCollectionPresenter postCollectionPresenter;
    private MovieRatingPresenter averageMovieRatingPresenter;
    private TagListPanel tagListPanel;
    
    
    
	@UiField(provided = true) Hyperlink titleElement;
    @UiField Label replyCountElement;
    @UiField  Label conversationCountElement;
    @UiField(provided = true) Widget posterElement;
//    @UiField(provided = true) Hyperlink creatorLinkElement;
//    @UiField(provided = true) Widget createDateElement;
    @UiField(provided = true) Widget averageRatingElement;
    @UiField VerticalPanel reviewsElement = new VerticalPanel();
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    //@UiField(provided = true) ClickableHoverSyncPanel moreOptionsElement = MoreOptionsButtonFactory.createMoreOptionsButton();
    @UiField (provided = true) PostDetailPanel postDetailPanelElement;    
    //@UiField SimplePanel inReplyPostElement;
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
		
		conversationCountElement.setStyleName("tt-conversation-count");
		
		
		
		EventBus.getInstance().addListener(this);
	}
	
	public void init(GPost post){
		super.init(post, commentElement, tagListPanel);
		//postDetailPanelElement.init(post, commentElement, tagListPanel, inReplyPostElement);
		postDetailPanelElement.init(post, commentElement, tagListPanel, null);
		
		this.post = post;
		imagePresenter.setImageAsMoviePoster(post);
		imagePresenter.init();
		
		averageMovieRatingPresenter.setRating(post.getAvgRatingTag());
		
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
		
		conversationCountElement.setText(""+post.getReplyCount());
		conversationCountElement.setTitle(post.getReplyCount() + " conversations on this topic.");
		
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
		return super.getWidget();
	}
}
