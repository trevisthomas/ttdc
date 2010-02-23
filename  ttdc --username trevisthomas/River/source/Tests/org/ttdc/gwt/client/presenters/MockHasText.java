package org.ttdc.gwt.client.presenters;

import com.google.gwt.user.client.ui.HasText;

public class MockHasText implements HasText{
	String text;
	public MockHasText(){}
	
	public MockHasText(String text){
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		
	}
	
}
