package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.uibinder.post.PostPanel.MyUiBinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
		
		imagePresenter.setImageAsMoviePoster(post);
		imagePresenter.init();
		
		averageMovieRatingPresenter.setAverageRating(post.getAvgRatingTag());
		
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
		
		for(GPost p : post.getPosts()){
			
		}
		
		
	}
}
