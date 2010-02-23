package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchPresenter extends BasePagePresenter<SearchPresenter.View> /*implements HistoryEventListener*/{
	public interface View extends BasePageView {
		HasWidgets getSiteSearchTarget();
		HasText getSummaryDetail();
		void refreshResults(Widget resultsWidget);
		void setTagCloudWidget(Widget widget);
		HasWidgets getResultsTarget();
		HasWidgets getTagResultsTarget();
		HasWidgets toggleResultsTarget();
	}
	
	@Inject
	public SearchPresenter(Injector injector) {
		super(injector, injector.getSearchView());
		
		SearchBoxPresenter searchBox = injector.getSearchBoxPresenter();
		searchBox.init();
		view.getSiteSearchTarget().add(searchBox.getWidget());
	}
	
	private void performPopularCloudLookup(){
		TagCloudPresenter tagCloudPresenter = injector.getTagCloudPresenter();
		tagCloudPresenter.loadMostPopularTags();
		view.setTagCloudWidget(tagCloudPresenter.getWidget());
	}
	
	/**
	 * My thinking is that top level presenters have this show method so that a 
	 * presenter can take over the whole screen.
	 * 
	 * @param args
	 * 
	 * TODO: make a special presenter type for top level presenters
	 */
	@Override
	public void show(HistoryToken args){
		view.show();
		performPopularCloudLookup();
	}

}

