package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.user.client.ui.RichTextArea;

public class HtmlToPlainTextHack {
	private static RichTextArea rta = new RichTextArea();
	public static String extractPlainText(String html){
		rta.setHTML(html);
		return rta.getText();
	}
}
