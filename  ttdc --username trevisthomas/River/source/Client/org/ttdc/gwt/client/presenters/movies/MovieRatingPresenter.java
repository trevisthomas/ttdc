package org.ttdc.gwt.client.presenters.movies;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

public class MovieRatingPresenter extends BasePresenter<MovieRatingPresenter.View> implements RatableContentProcessor{
	private Map<Float,GTag> ratingTagMap = new HashMap<Float,GTag>(); 
	
	public interface View extends BaseView{
		void setRating(String rating);
		void initVoteMode(RatableContentProcessor ratingProcessor);
	}
	
	
	@Inject
	public MovieRatingPresenter(Injector injector) {
		super(injector, injector.getMovieRatingView());
		loadTags();
	}

	private void loadTags(){
		TagCommand cmd = new TagCommand();
		cmd.setAction(TagActionType.LOAD_RATINGS);
		injector.getService().execute(cmd, createTagRatingListCallback());
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
		//Window.alert("Vote: "+rating);
		//This cant work before the list comes back. it's asynch so check first, i guess.

		if(ratingTagMap.size() == 0)
			throw new RuntimeException("Rating tag list hasn't been populated. This should not happen.");
		
		Window.alert("Vote: "+ratingTagMap.get(rating));
		
	}

	private CommandResultCallback<GenericListCommandResult<GTag>> createTagRatingListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GTag>>(){
			public void onSuccess(GenericListCommandResult<GTag> result) {
				for(GTag t : result.getList()){
					ratingTagMap.put(Float.parseFloat(t.getValue()),t);
				}
			};
		};
	}
}
