package org.ttdc.gwt.client.components.widgets;

import org.ttdc.gwt.client.components.widgets.login.LoginWidget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public final class HeaderWidget extends Composite{
	
	private final HorizontalPanel root;
	private final Label titleLabel = new Label(siteTitle);
	private final LoginWidget loginWidget;
	
	
	private final static String siteTitle = "We Be Friends GWT!"; 
	private HeaderWidget(){
		root = new HorizontalPanel();
		loginWidget = LoginWidget.createInstance();
		root.add(titleLabel);
		root.add(loginWidget);
		root.add(SearchWidget.createInstance());
		
		
		
		initWidget(root);
	}
	public static HeaderWidget createInstance(){
		HeaderWidget widget = new HeaderWidget();
		return widget;
	}
	
}
