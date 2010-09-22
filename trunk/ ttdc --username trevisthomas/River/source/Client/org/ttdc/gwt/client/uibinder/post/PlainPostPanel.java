package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PlainPostPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PlainPostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private HyperlinkPresenter createDatePresenter;
    private HyperlinkPresenter creatorPresenter;
    
    private Injector injector;
    
    @UiField SpanElement bodyElement;
    @UiField Label headerLabelElement;
    @UiField HTMLPanel containerElement;
    
    
    @UiField (provided = true) Hyperlink creatorElement;
    @UiField (provided = true) Hyperlink dateElement;
    
    
    @Inject
    public PlainPostPanel(Injector injector) { 
    	this.injector = injector;
    	createDatePresenter = injector.getHyperlinkPresenter();
    	creatorPresenter = injector.getHyperlinkPresenter();
    	
    	creatorElement = creatorPresenter.getHyperlink();
    	dateElement = createDatePresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this)); 
    	
    	
	}
    
    public void init(GPost post){
    	headerLabelElement.setText("reply to: ");
    	
    	createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
    	creatorPresenter.setPerson(post.getCreator());
    	
    	bodyElement.setInnerHTML(post.getEntry());
    }

}
