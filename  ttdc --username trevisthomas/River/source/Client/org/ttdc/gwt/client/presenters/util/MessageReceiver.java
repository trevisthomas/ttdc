package org.ttdc.gwt.client.presenters.util;

/**
 * I'm intending to use this to allow GWT presenters or views to 
 * pass notification messages to a view. 
 * 
 * Initial use is in the image upload section.
 *
 */
@Deprecated
public interface MessageReceiver {
	void error(String err);
	void message(String msg);
	void clear();
}
