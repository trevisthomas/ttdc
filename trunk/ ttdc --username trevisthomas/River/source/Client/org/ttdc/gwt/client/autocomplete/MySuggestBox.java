package org.ttdc.gwt.client.autocomplete;

import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class MySuggestBox extends SuggestBox{
	private String defaultMessage;
	private String message;
	
	public MySuggestBox(SuggestOracle suggestOracle) {
		this(suggestOracle, true);
	}
	public MySuggestBox(SuggestOracle suggestOracle, boolean autoSelect){
		super(suggestOracle);
		setAutoSelectEnabled(autoSelect);
		
		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE){
					hideSuggestionList();
				}
			}
		});
		
//		addKeyDownHandler(new KeyDownHandler() {
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				if(getText().equals(defaultMessage)){
//					setText("");
//					removeStyleName(style);
//				}
//				
//			}
//		});
		
		addFocusListener(new FocusListener() {
			@Override
			public void onLostFocus(Widget sender) {
				if(getText().trim().equals("") || getText().equals(MySuggestBox.this.message)){
					loadDefaultTextIntoSearchBox();
				}
			}
			
			@Override
			public void onFocus(Widget sender) {
				removeStyleName("tt-message-textbox-disabled");
				if(getText().equals(MySuggestBox.this.defaultMessage)){
					if(StringUtil.notEmpty(MySuggestBox.this.message)){
						setText(MySuggestBox.this.message);
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
		
		getTextBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(StringUtil.empty(getText())){
					MySuggestBox.this.message = "";
				}
			}
		});
		getTextBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if(getText().trim().equals("") || getText().equals(MySuggestBox.this.message)){
					loadDefaultTextIntoSearchBox();
				}
			}
		});
	}
	
	private void highlightSearchBoxText() {
		getTextBox().setSelectionRange(0, getText().length());
	}
	public void setDefaultMessage(String message) {
		defaultMessage = message;
		loadDefaultTextIntoSearchBox();
	}
	
	private void loadDefaultTextIntoSearchBox() {
		setText(defaultMessage);
		addStyleName("tt-message-textbox-disabled");
	}
	
	public void setActiveText(String phrase) {
		message = phrase;
		if(StringUtil.empty(phrase)){
			loadDefaultTextIntoSearchBox();
		}
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
}
