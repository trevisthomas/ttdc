package org.ttdc.gwt.client.uibinder.post;


import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


public class PostExpanded extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PostExpanded> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
	@UiField SpanElement bodyElement;
	private Injector injector;
	
	@Inject
	public PostExpanded(Injector injector) {
		this.injector = injector;
		initWidget(binder.createAndBindUi(this));
	}
	
	public void init(GPost post){
		bodyElement.setInnerHTML(post.getEntry());
	}
}
