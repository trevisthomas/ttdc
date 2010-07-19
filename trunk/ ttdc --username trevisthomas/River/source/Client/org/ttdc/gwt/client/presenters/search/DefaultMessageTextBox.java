package org.ttdc.gwt.client.presenters.search;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class DefaultMessageTextBox extends TextBox{
	private String defaultMessage = "";
	private String message;
	
	private List<EnterKeyPressedListener> listeners = new ArrayList<EnterKeyPressedListener>();
	
	public interface EnterKeyPressedListener{
		void onEnterKeyPressed();
	}
	
	public DefaultMessageTextBox(String defaultMessage) {
		
		this.defaultMessage = defaultMessage;
		
		addFocusHandler(new FocusHandler(){
			@Override
			public void onFocus(FocusEvent event) {
				removeStyleName("tt-textbox-disabled");
				if(getText().equals(DefaultMessageTextBox.this.defaultMessage)){
					if(StringUtil.notEmpty(DefaultMessageTextBox.this.message)){
						setText(DefaultMessageTextBox.this.message);
						highlightSearchBoxText();
					}
					else{
						setText("");
					}
				}
				else{
					highlightSearchBoxText();
				}
			}
		});
		
		addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(StringUtil.empty(getText())){
					DefaultMessageTextBox.this.message = "";
				}
			}
		});
		addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if(getText().trim().equals("") || getText().equals(DefaultMessageTextBox.this.message)){
					loadDefaultTextIntoSearchBox();
				}
			}
		});
		
		addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					for(EnterKeyPressedListener listener : DefaultMessageTextBox.this.listeners){
						listener.onEnterKeyPressed();
					}
				}
			}
		});
	}
	
	public void addEnterKeyPressedListener(EnterKeyPressedListener listener){
		listeners.add(listener);
	}
	
	private void highlightSearchBoxText() {
		setSelectionRange(0, getText().length());
	}
	
	public void setDefaultMessage(String message) {
		defaultMessage = message;
		loadDefaultTextIntoSearchBox();
	}

	//TODO: rename
	public void setActiveText(String phrase) {
		message = phrase;
	}
	
	public String getActiveText(){
		if(getText().equals(defaultMessage)){
			if(StringUtil.notEmpty(message))
				return message;
			else
				return "";
		}
		else{
			return getText();
		}
	}
	
	
	private void loadDefaultTextIntoSearchBox() {
		setText(defaultMessage);
		addStyleName("tt-textbox-disabled");
	}
	
	
	
}
