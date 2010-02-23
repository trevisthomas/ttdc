package org.ttdc.gwt.client.components.widgets;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public final class PostHeaderPanelWidget extends Composite{
	private final HorizontalPanel root;  
	
	private PostHeaderPanelWidget(GPost post){
		root = new HorizontalPanel();
		root.add(createAvatar(post.getCreator().getImage()));
		root.add(PersonLinkWidget.createInstance(post.getCreator()));

		initWidget(root);
		
	};
	
	private final Widget createAvatar(GImage image){
		//DOM.createImg();
		//Image img = new Image();
		//img.setU
		/*
		Widget widget = DOM.createImg();
		if(image != null)
			( GWT.getHostPageBaseURL()+"images/"+image.getThumbnailName());
		else
		*/
		
		HTML html;
		if(image != null)
			html = new HTML("<img src=\""+GWT.getHostPageBaseURL()+"images/"+image.getThumbnailName()+"\"/>");
		else
			html = new HTML("<img src=\""+GWT.getHostPageBaseURL()+"images/defaultavitar.jpg\"/>");
		
		return html;
		
	}
	
	public static final PostHeaderPanelWidget createInstance(GPost post){
		return new PostHeaderPanelWidget(post);
	}
}
