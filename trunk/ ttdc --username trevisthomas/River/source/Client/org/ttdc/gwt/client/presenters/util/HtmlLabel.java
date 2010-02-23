package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.user.client.ui.HTML;

/**
 * The GWT HTML class implements hasText but you have to call setHtml to get it to treat the
 * text as HTML, so i wrote this class to make it work the way i think that it should work.   
 *
 */
public class HtmlLabel extends HTML{
	@Override
	public void setText(String text) {
		super.setHTML(text);
	}
}
