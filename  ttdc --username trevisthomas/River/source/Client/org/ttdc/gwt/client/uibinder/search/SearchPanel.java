package org.ttdc.gwt.client.uibinder.search;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.TagCloudPresenter;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchPanel extends BasePageComposite {
	interface MyUiBinder extends UiBinder<Widget, SearchPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField SimplePanel tagCloudWidgetElement;
	@UiField (provided = true) Widget pageFooterElement;
	
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	
	@Inject
	public SearchPanel(Injector injector){
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	pageFooterElement = injector.createStandardFooter().getWidget();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	
	}
	
	private void performPopularCloudLookup(){
		TagCloudPresenter tagCloudPresenter = injector.getTagCloudPresenter();
		tagCloudPresenter.loadMostPopularTags();
		tagCloudWidgetElement.add(tagCloudPresenter.getWidget());
	}
	
	@Override
	protected void onShow(HistoryToken token) {
		performPopularCloudLookup();
	}
	
}
