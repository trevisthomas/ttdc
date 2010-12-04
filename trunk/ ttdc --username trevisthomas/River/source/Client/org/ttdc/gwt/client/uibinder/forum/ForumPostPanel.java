package org.ttdc.gwt.client.uibinder.forum;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


public class ForumPostPanel  extends Composite{
	interface MyUiBinder extends UiBinder<Widget, ForumPostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField Label replyCountElement;
    @UiField Label conversationCountElement;
    @UiField (provided=true) Hyperlink creatorElement;
    @UiField (provided=true) Hyperlink dateCreatedElement;
    @UiField (provided=true) Hyperlink titleElement;
    
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter dateCreatedLinkPresenter;
    private HyperlinkPresenter titleLinkPresenter;
    
        
    @Inject
    public ForumPostPanel(Injector injector) { 
    	this.injector = injector;
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	dateCreatedLinkPresenter = injector.getHyperlinkPresenter();
    	titleLinkPresenter = injector.getHyperlinkPresenter();

    	creatorElement = creatorLinkPresenter.getHyperlink();
    	dateCreatedElement = dateCreatedLinkPresenter.getHyperlink();
    	titleElement = titleLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	replyCountElement.setStyleName("tt-reply-count");
    	conversationCountElement.setStyleName("tt-conversation-count");
	}
    
    public void init(GPost post){
    	if(!post.isRootPost()){
    		throw new RuntimeException("Non root post in forum.  This should never happen.  Notify admin!");
    	}
    	
    	
    	titleLinkPresenter.setPost(post);
    	dateCreatedLinkPresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
    	creatorLinkPresenter.setPerson(post.getCreator());
    	
    	replyCountElement.setText(""+post.getMass());
    	replyCountElement.setTitle( post.getMass() + " replies in this conversation.");
    	
		conversationCountElement.setVisible(true);
		conversationCountElement.setText(""+post.getReplyCount());
		conversationCountElement.setTitle(post.getReplyCount() + " conversations within this topic.");
    }
	
}
