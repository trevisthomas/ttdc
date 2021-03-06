package org.ttdc.gwt.client.uibinder.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.constants.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class StandardFooterPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, StandardFooterPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    @UiField Label copyrightElement;
    @UiField Label buildNumberElement;
	
	@Inject
    public StandardFooterPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this)); 
    	init();
	}
    
	public void init(){
		buildNumberElement.setText("TrevisThomasDocCom v7 build:" + AppConstants.BUILD_NUMBER);
	}
	
    @Override
    public Widget getWidget() {
    	return this;
    }
    
}
