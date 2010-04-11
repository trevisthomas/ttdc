package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostSummaryPanel extends Composite implements PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, PostSummaryPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    private HyperlinkPresenter creatorLinkPresenter;
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField SpanElement bodySummaryElement;
    @UiField SpanElement spacerElement;
    @Inject
    public PostSummaryPanel(Injector injector) { 
    	this.injector = injector;
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(GPost post){
    	bodySummaryElement.setInnerHTML(post.getLatestEntry().getSummary());
    	creatorLinkPresenter.setPerson(post.getCreator());
    	setSpacer(post.getPath().split("\\.").length - 2);
    }
    
    public void setSpacer(int tabCount) {
    	StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i <= tabCount ; i++){
			sb.append("&nbsp;");
		}
		if(sb.length() > 0){
			spacerElement.setInnerHTML(sb.toString());
		}
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }
}
