package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.user.client.ui.DockPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PaginationView implements PaginationPresenter.View{
	private final SimplePanel prevTarget = new SimplePanel();
	private final SimplePanel nextTarget = new SimplePanel();
	private final FlowPanel pagesTarget = new FlowPanel();
	private final DockPanel main = new DockPanel();
	
	public PaginationView() {
		main.add(nextTarget, DockPanel.EAST);
		main.add(prevTarget, DockPanel.WEST);
		main.add(pagesTarget, DockPanel.CENTER);
		main.setStyleName("tt-paginator");
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets nextButton() {
		return nextTarget;
	}

	@Override
	public HasWidgets pageButtons() {
		return pagesTarget;
	}

	@Override
	public HasWidgets prevButton() {
		return prevTarget;
	}

	@Override
	public void setVisible(boolean b) {
		main.setVisible(b);
	}
	
}
