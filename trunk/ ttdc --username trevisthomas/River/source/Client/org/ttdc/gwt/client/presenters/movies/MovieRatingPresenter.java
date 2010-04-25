package org.ttdc.gwt.client.presenters.movies;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

public class MovieRatingPresenter extends BasePresenter<MovieRatingPresenter.View> implements RatableContentProcessor{
	@Inject
	public MovieRatingPresenter(Injector injector) {
		super(injector, injector.getMovieRatingView());
	}

	public interface View extends BaseView{
		void setRating(String rating);
		void initVoteMode(RatableContentProcessor ratingProcessor);
	}
	
	public void setRating(String rating){
		view.setRating(rating);
	}
	
	public void setRating(GTag ratingTag){
		if(ratingTag == null)
			return;
		view.setRating(ratingTag.getValue()); //TODO make awesomer
	}

	public void setRating(GAssociationPostTag ratingByPerson) {
		if(ratingByPerson != null)
			setRating(ratingByPerson.getTag());
		
	}

	public void setRatablePost(GPost parentPost) {
		view.initVoteMode(this);
	}
	
	@Override
	public void processRatingRequest(float rating) {
		// TODO Auto-generated method stub
		Window.alert("Vote: "+rating);
	}

}
