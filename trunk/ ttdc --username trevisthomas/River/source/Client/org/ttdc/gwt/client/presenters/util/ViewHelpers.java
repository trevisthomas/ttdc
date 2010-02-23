package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

public class ViewHelpers {
	/**
	 * Just wanted to share this code because it is used in at least two presenters. (SearchWithinTagged and SearchWithin)
	 */
	public static void configureSearchTextBox(final TextBox phraseField, final Button searchButton) {
		phraseField.addFocusHandler(new FocusHandler(){
			@Override
				public void onFocus(FocusEvent event) {
					phraseField.setSelectionRange(0, phraseField.getText().length());					
				}	
		});
		
		phraseField.addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchButton.click();
				}
			}
		});
	}
}
