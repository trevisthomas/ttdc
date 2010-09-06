package org.ttdc.gwt.client.uibinder.shared;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class UiHelpers {
	public static final FocusPanel createTableHeaderPanel(String string) {
		FocusPanel fp = new FocusPanel();
		fp.setStyleName("tt-cursor-pointer");
		fp.add(new Label(string));
		return fp;
	}
}
