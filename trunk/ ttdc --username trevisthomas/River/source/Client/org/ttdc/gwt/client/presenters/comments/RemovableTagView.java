package org.ttdc.gwt.client.presenters.comments;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RemovableTagView implements RemovableTagPresenter.View{
	private final HorizontalPanel main = new HorizontalPanel();
	private final Anchor label = new Anchor();
	
	public RemovableTagView() {
		main.add(label);
		label.addStyleName("tt-cursor-pointer");
		label.setTitle("Click to Remove Tag");
	}

	@Override
	public HasClickHandlers getRemoveButton() {
		return label;
	}

	@Override
	public HasText getTagLabel() {
		return label;
	}

	@Override
	public Widget getWidget() {
		return main;
	}
}
