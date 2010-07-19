package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.uibinder.search.SearchResultsPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * See {@link SearchResultsPanel}
 *
 */
@Deprecated
public class SearchResultsView extends Composite implements SearchResultsPresenter.View{
	private final Label summaryDetail = new Label();
	private final VerticalPanel postListPanel = new VerticalPanel(); 
	private final VerticalPanel rootPanel = new VerticalPanel();
	private final SimplePanel tagResultsTarget = new SimplePanel();
	private final SimplePanel switchSearchResultsControlTarget = new SimplePanel();
	private final SimplePanel siteSearchTarget = new SimplePanel();
	private final SimplePanel paginationTarget = new SimplePanel();
	
	public void show() {
		rootPanel.add(siteSearchTarget);
		
		rootPanel.add(tagResultsTarget);
		rootPanel.add(switchSearchResultsControlTarget);
		rootPanel.add(postListPanel);
		rootPanel.add(summaryDetail);
		rootPanel.add(paginationTarget);
		
		
		initWidget(rootPanel);
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(this);
	}

	@Override
	public HasText getSummaryDetail() {
		return summaryDetail;
	}
	
	@Override
	public Widget getWidget() {
		return super.getWidget();
	}

	//TODO: Trevis this seems a little weird figure out wtf?
	public void refreshResults(Widget widget) {
		postListPanel.clear();
		postListPanel.add(widget);
	}

	
	@Override
	public HasWidgets getResultsTarget() {
		return postListPanel;
	}

	@Override
	public HasWidgets toggleResultsTarget() {
		return switchSearchResultsControlTarget;
	}

	@Override
	public HasWidgets getTagResultsTarget() {
		return tagResultsTarget;
	}

	@Override
	public HasWidgets getSiteSearchTarget() {
		return siteSearchTarget;
	}

	@Override
	public HasWidgets paginationTarget() {
		return paginationTarget;
	}

}
