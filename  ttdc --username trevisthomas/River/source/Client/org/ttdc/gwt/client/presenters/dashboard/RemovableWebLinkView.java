package org.ttdc.gwt.client.presenters.dashboard;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RemovableWebLinkView implements RemovableWebLinkPresenter.View{
	private final HorizontalPanel main = new HorizontalPanel();
	private final Button deleteButton = new Button("Delete");
	private final SimplePanel link = new SimplePanel();
	private final SimplePanel iconPanel = new SimplePanel();
	
	public RemovableWebLinkView() {
		main.add(deleteButton);
		main.add(iconPanel);
		main.add(link);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasClickHandlers deleteButton() {
		return deleteButton;
	}

	@Override
	public HasWidgets webLinkIcon() {
		return iconPanel;
	}
	
	@Override
	public HasWidgets link() {
		return link;
	}

	@Override
	public void remove() {
		main.removeFromParent();//hmm, seems cool. is it?
	}
}
