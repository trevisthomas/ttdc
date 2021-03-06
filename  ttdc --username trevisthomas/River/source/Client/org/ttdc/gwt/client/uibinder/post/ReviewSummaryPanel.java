package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


public class ReviewSummaryPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ReviewSummaryPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
		    
	private Injector injector;
//	private GPost post;
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter postLinkPresenter;
    private MovieRatingPresenter movieRatingPresenter;
    
	@UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField SpanElement summaryElement;
    @UiField(provided = true) Hyperlink postLinkElement;
    @UiField(provided = true) Widget ratingElement;
	
	@Inject
	public ReviewSummaryPanel(Injector injector) {
		this.injector = injector;
		
		postLinkPresenter = injector.getHyperlinkPresenter();
		creatorLinkPresenter = injector.getHyperlinkPresenter();
		movieRatingPresenter = injector.getMovieRatingPresenter();
		
		postLinkElement = postLinkPresenter.getHyperlink();
		creatorLinkElement = creatorLinkPresenter.getHyperlink();
		ratingElement = movieRatingPresenter.getWidget();
		
		initWidget(binder.createAndBindUi(this));
	}
	
	public void init(GPost post){
		summaryElement.setInnerText(post.getLatestEntry().getSummary());
		postLinkPresenter.setPost(post, "[read]");
		postLinkPresenter.init();
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();

		// If the parent doesnt reference the kids the getRatingByPerson method fails. That circular reference was
		// causing jackson isues so i removed it. 11/3
		// movieRatingPresenter.setRating(post.getParent().getRatingByPerson(post.getCreator().getPersonId()));
		if (post.getReviewRating() != null) {
			movieRatingPresenter.setRating(post.getReviewRating() + "");
		}
	}
	
	public void init(GAssociationPostTag ratingAss){
		creatorLinkPresenter.setPerson(ratingAss.getCreator());
		creatorLinkPresenter.init();
		movieRatingPresenter.setRating(ratingAss);
	}
	
}
