package org.ttdc.gwt.client.presenters;

import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;

public class MockHasText implements HasText, HasHTML{
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

	@Override
	public String getHTML() {
		return text;
	}

	@Override
	public void setHTML(String html) {
		this.text = text;
	}
	
	
	
}
