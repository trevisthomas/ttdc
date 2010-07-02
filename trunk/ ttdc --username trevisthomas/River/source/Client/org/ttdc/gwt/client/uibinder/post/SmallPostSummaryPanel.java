package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.calender.CalendarPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SmallPostSummaryPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, SmallPostSummaryPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter titleLinkPresenter;
    private DatePresenter createDatePresenter;
    private CalendarPost cp;
        
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField(provided = true) Hyperlink titleElement;
    @UiField(provided = true) Widget createDateElement;
    @UiField Label postUnReadElement;
    
    @Inject
    public SmallPostSummaryPanel(Injector injector) { 
    	this.injector = injector;
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	titleLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getDatePresenter();
    	
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	titleElement = titleLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget(); 
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(CalendarPost cp) {
    	this.cp = cp;
    	
    	GPerson liteCreator = new GPerson();
    	liteCreator.setLogin(cp.getCreatorLogin());
    	liteCreator.setPersonId(cp.getCreatorId());
    	creatorLinkPresenter.setPerson(liteCreator);
    	creatorLinkPresenter.init();
    	
    	GPost litePost = new GPost();
    	GTag liteTag = new GTag();
    	liteTag.setValue(cp.getTitle());
    	
    	litePost.setTitleTag(liteTag);
    	litePost.setPostId(cp.getPostId());
    	titleLinkPresenter.setPost(litePost);
    	titleLinkPresenter.init();
    	
    	createDatePresenter.init(cp.getDate());
    	
    	    	GPerson user = ConnectionId.getInstance().getCurrentUser();
//    	Figure out how to make the calender post have the read/unread info!
//		if(!user.isAnonymous() && !post.isRead()){
//			postUnReadElement.setVisible(true);
//			postUnReadElement.setText("*");
//			postUnReadElement.addStyleName("tt-alert");
//		}
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    

}
