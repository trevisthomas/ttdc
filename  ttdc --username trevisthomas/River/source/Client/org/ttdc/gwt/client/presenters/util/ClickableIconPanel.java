package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.user.client.ui.FocusPanel;

/*
 * I'm thinking that i'll try to capture as much as i can about a clickable icon in one 
 * place.  Well, some is also in the CSS obviously.  The thinking is that i may do fancy hover
 * stuff so i wanted to encapsulate that.
 */
public class ClickableIconPanel extends FocusPanel{
	public ClickableIconPanel(String icon) {
		setStyleName("tt-cursor-pointer");
		addStyleName(icon);
	}
}
