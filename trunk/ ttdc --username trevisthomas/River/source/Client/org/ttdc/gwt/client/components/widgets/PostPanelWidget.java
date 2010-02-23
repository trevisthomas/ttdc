package org.ttdc.gwt.client.components.widgets;

import org.ttdc.gwt.client.beans.GPost;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;

public final class PostPanelWidget extends Composite{
	public PostPanelWidget(){};
	/*
	public final static PostPanelWidget createInstance(GPost post){
		PostPanelWidget mainPanel = new PostPanelWidget();
		Panel panel = new FlowPanel();
		
		panel.add(PostHeaderPanelWidget.createInstance(post));
		
		HTMLPanel entryPanel = new HTMLPanel(post.getEntry());
		panel.add(entryPanel);
		
		
		mainPanel.initWidget(panel);
		
		
		return mainPanel;
	}
	*/
	public void setPost(GPost post){
		Panel panel = new FlowPanel();
		
		panel.add(PostHeaderPanelWidget.createInstance(post));
		
		HTMLPanel entryPanel = new HTMLPanel(post.getEntry());
		panel.add(entryPanel);
		
		initWidget(panel);
		
	}
}
