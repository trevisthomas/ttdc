package org.ttdc.gwt.client.presenters.home;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@Deprecated
public class TrafficPersonView implements TrafficPersonPresenter.View{
	private final Grid main = new Grid(1,2);
	private SimplePanel avatarPanel = new SimplePanel();
	private SimplePanel datePanel = new SimplePanel();
	private SimplePanel linkPanel = new SimplePanel();
	private SimplePanel namePanel = new SimplePanel();
	private VerticalPanel details = new VerticalPanel();
	
	public TrafficPersonView() {
		main.setWidget(0, 0, avatarPanel);
		main.setWidget(0, 1, details);
		details.add(namePanel);
		details.add(datePanel);
		details.add(linkPanel);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets avatarPanel() {
		return avatarPanel;
	}

	@Override
	public HasWidgets datePanel() {
		return datePanel;
	}

	@Override
	public HasWidgets linkPanel() {
		return linkPanel;
	}

	@Override
	public HasWidgets namePanel() {
		return namePanel;
	}

}
