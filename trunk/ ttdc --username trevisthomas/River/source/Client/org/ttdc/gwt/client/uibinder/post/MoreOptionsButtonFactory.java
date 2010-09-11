package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.user.client.ui.Label;

public class MoreOptionsButtonFactory {
	public static ClickableHoverSyncPanel createMoreOptionsButton(){
		ClickableHoverSyncPanel button = new ClickableHoverSyncPanel("tt-more-options-button","tt-more-options-button-hover");
		button.add(new Label("options"));
    	return button;
	}
}
	