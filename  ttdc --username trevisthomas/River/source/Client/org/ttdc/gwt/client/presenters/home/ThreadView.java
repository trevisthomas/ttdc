package org.ttdc.gwt.client.presenters.home;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ThreadView implements ThreadPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel footer = new SimplePanel();
	private final SimplePanel posts = new SimplePanel();
	
	public ThreadView() {
		main.add(posts);
		main.add(footer);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets postFooterPanel() {
		return footer;
	}

	@Override
	public HasWidgets postPanel() {
		return posts;
	}
}
