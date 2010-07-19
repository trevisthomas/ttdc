package org.ttdc.gwt.client.uibinder.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.PopupCalendarDatePresenter;
import org.ttdc.gwt.client.uibinder.Navigation;
import org.ttdc.gwt.client.uibinder.search.SearchBoxPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class StandardPageHeaderPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, StandardPageHeaderPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    private final SearchBoxPanel searchBoxPanel;
    private final PopupCalendarDatePresenter popupCalendarDatePresenter;
    
    private Navigation navigation;
        
    @UiField(provided = true) Widget navigationElement;
    @UiField Label titleElement;
    @UiField Label subTitleElement;
    
    @UiField(provided = true) Widget searchElement;
	@UiField(provided = true) Widget loginElement;
	@UiField(provided = true) Widget popupCalendarDateElement;
    
    @Inject
    public StandardPageHeaderPanel(Injector injector) { 
    	this.injector = injector;
    	searchBoxPanel = injector.createSearchBoxPanel();
    	navigation = injector.createNavigation();
    	
    	navigationElement = navigation;
    	searchElement = searchBoxPanel.getWidget();
    	
    	loginElement = injector.createUserIdentityPanel();
    	
    	popupCalendarDatePresenter = injector.getPopupCalendarDatePresenter();
    	popupCalendarDateElement = popupCalendarDatePresenter.getWidget();
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(final String title, final String subtitle) {
    	titleElement.setText(title);
    	subTitleElement.setText(subtitle);
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    public SearchBoxPanel getSearchBoxPresenter(){
    	return searchBoxPanel;
    }
    
}
