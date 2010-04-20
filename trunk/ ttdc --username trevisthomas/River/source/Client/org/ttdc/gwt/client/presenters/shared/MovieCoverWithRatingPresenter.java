package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class MovieCoverWithRatingPresenter extends BasePresenter<MovieCoverWithRatingPresenter.View>{
	public interface View extends BaseView{
		HasWidgets poster();
		HasWidgets rating();
	}
	
	@Inject
	public MovieCoverWithRatingPresenter(Injector injector) {
		super(injector, injector.getMovieCoverWithRatingView());
	}
	
	public void init(GPost movie){
		init(movie,null);
	}
	public void init(GPost movie, String personId){
		ImagePresenter imagePresenter = injector.getImagePresenter();
		//imagePresenter.setImage(movie.getImage(),movie.getTitle(),100,-1);
		imagePresenter.setImageAsMoviePoster(movie);
		view.poster().add(imagePresenter.getWidget());
		
		MovieRatingPresenter ratingPresenter = injector.getMovieRatingPresenter();
		PresenterHelpers.initializeMovieRatingPresenter(ratingPresenter,movie,personId);
		view.rating().add(ratingPresenter.getWidget());
		
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC); 
		token.setParameter(HistoryConstants.POST_ID_KEY,movie.getPostId());
		imagePresenter.setLinkToken(token);
		
	}
}	
