package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;

/*
 * I'm thinking that i'll try to capture as much as i can about a clickable icon in one 
 * place.  Well, some is also in the CSS obviously.  The thinking is that i may do fancy hover
 * stuff so i wanted to encapsulate that.
 * 
 * -7/17/2010 this may not be used anymore
 */
public class ClickableIconPanel extends FocusPanel{
	private final String normalStyle;
	private final String downStyle;
	///todo hover style?
	
	public ClickableIconPanel(String icon) {
		normalStyle = "";
		downStyle = "";
		setStyleName("tt-cursor-pointer");
		addStyleName(icon);
	}
	
	public ClickableIconPanel(String normalStyle, String downStyle){
		this.normalStyle = normalStyle;
		this.downStyle = downStyle;
		setStyleName("tt-graphic-button");
		addStyleName(normalStyle);
		
		addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				removeStyleName(ClickableIconPanel.this.normalStyle);
				addStyleName(ClickableIconPanel.this.downStyle);
			}
		});
		
		addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				removeStyleName(ClickableIconPanel.this.downStyle);
				addStyleName(ClickableIconPanel.this.normalStyle);
			}
		});
	}
	
	
}
