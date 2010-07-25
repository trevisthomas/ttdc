package org.ttdc.gwt.client.presenters.post;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.commands.BrowsePopularTagsCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.SearchSortBy;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TagCloudPresenter extends BasePresenter<TagCloudPresenter.View>{
	@Inject
	public TagCloudPresenter(Injector injector) {
		super(injector,injector.getTagCloudView());
	}
	
	public void loadMostPopularTags() {
		BrowsePopularTagsCommand command = new BrowsePopularTagsCommand();
		command.setSortOrder(SearchSortBy.ALPHABETICAL);
		command.setMaxTags(100);
		CommandResultCallback<SearchTagsCommandResult> callback = new CommandResultCallback<SearchTagsCommandResult>(){
			public void onSuccess(SearchTagsCommandResult result) {
				setTagList(result.getResults().getList());
			};
		};
		getService().execute(command, callback);
	}

	//TODO show for tag
	
	public interface View extends BaseView{
		HasWidgets getTagWidgets();
	}
	
	public void setTagList(List<GTag> tagList){
		for(GTag tag : tagList){
			HyperlinkPresenter hyperLink = injector.getHyperlinkPresenter();
			hyperLink.setStyleName(""); //Clearing out the inline
			hyperLink.setTag(tag); //This sets up the history stuff all on it's own.
			view.getTagWidgets().add(hyperLink.getWidget());
		}
	}
}
