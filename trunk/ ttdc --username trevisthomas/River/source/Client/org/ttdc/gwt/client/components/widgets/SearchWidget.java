package org.ttdc.gwt.client.components.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

public final class SearchWidget extends Composite {
	private final TextBox root = new TextBox();
	private final String SEARCH_BOX_MESSAGE = "Go search go";
	private SearchWidget(){
		root.setText(SEARCH_BOX_MESSAGE);
		initWidget(root);
	}
	public static SearchWidget createInstance(){
		SearchWidget widget = new SearchWidget();
		
		return widget;
	}
}
