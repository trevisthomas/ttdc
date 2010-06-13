package org.ttdc.gwt.client.uibinder.post;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MoreOptionsPopupPanel extends PopupPanel{
    interface MoreOptionsPopupPanelUiBinder extends UiBinder<Widget, MoreOptionsPopupPanel> {}
    private static final MoreOptionsPopupPanelUiBinder binder = GWT.create(MoreOptionsPopupPanelUiBinder.class);
    private final Injector injector;
    private GPost post;
    
    @UiField Anchor replyElement;
    @UiField Anchor likeElement;
    @UiField Anchor unLikeElement;
    @UiField Anchor ratingElement;
    @UiField Anchor unRateElement;
    @UiField Anchor editElement;
    @UiField Anchor muteThreadElement;
    @UiField Anchor unMuteThreadElement;
    
    
    @Inject
    public MoreOptionsPopupPanel(Injector injector) { 
    	this.injector = injector;
    	//NoteToSelf: This is setwidget and not initWidget because i'm a popup panel and not a composite
    	setWidget(binder.createAndBindUi(this)); 
    }
    
    public void init(GPost post){
    	this.post = post;
    	
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
    	ratingElement.setVisible(false);
    	unRateElement.setVisible(false);
    	unLikeElement.setVisible(false);
    	likeElement.setVisible(false);
    	editElement.setVisible(false);
    	muteThreadElement.setVisible(false);
    	unMuteThreadElement.setVisible(false);
    	if(user.hasPrivilege(PrivilegeConstants.VOTER) || user.isAdministrator()){
    		if(post.isRatable()){
    			if(post.getRatingByPerson(user.getPersonId()) == null)
    				ratingElement.setVisible(true);
    			else
    				unRateElement.setVisible(true);
    		}
    		if(post.getLikedByPerson(user.getPersonId()) == null)
    			likeElement.setVisible(true);
			else
				unLikeElement.setVisible(true);
    		
    	}
    	
    	if(!user.isAnonymous()){
    		if(user.isThreadFiltered(post.getRoot().getPostId())){
        		unMuteThreadElement.setVisible(true);
        	}
        	else {
        		muteThreadElement.setVisible(true);
        	}
    	}
    	
    	
    	
    	if(user.isAdministrator() || (user.equals(post.getCreator()) && post.isInEditWindow())){
    		editElement.setVisible(true);
    	}
    }
    
	public void addReplyClickHandler(ClickHandler handler){
    	replyElement.addClickHandler(handler);
    }
    
    public void addRatingClickHandler(ClickHandler handler){
    	ratingElement.addClickHandler(handler);
    }
    
    public void addUnRateClickHandler(ClickHandler handler){
    	unRateElement.addClickHandler(handler);
    }
    
    public void addEditClickHandler(ClickHandler handler){
    	editElement.addClickHandler(handler);
    }
    
    public void addMuteThreadClickHandler(ClickHandler handler){
    	muteThreadElement.addClickHandler(handler);
    }
    
    public void addUnMuteThreadClickHandler(ClickHandler handler){
    	unMuteThreadElement.addClickHandler(handler);
    }
    
    public void addLikePostClickHandler(ClickHandler handler){
    	likeElement.addClickHandler(handler);
    }
    
    public void addUnLikePostClickHandler(ClickHandler handler){
    	unLikeElement.addClickHandler(handler);
    }
    
    @UiHandler("ratingElement")
    void onClickRating(ClickEvent event){
    	hide();
    }
    
    @UiHandler("unRateElement")
    void onClickUnRate(ClickEvent event){
    	hide();
    }
    
    @UiHandler("replyElement")
    void onClickReply(ClickEvent event){
    	hide();
    }
    
    @UiHandler("likeElement")
    void onClickLike(ClickEvent event){
    	hide();
    }    
    
    @UiHandler("unLikeElement")
    void onClickUnLike(ClickEvent event){
    	hide();
    }
    
    @UiHandler(value={"editElement","unMuteThreadElement","muteThreadElement"})
    void onClickEdit(ClickEvent event){
    	hide();
    }
    

    public void showRelativeTo(Widget source){
    	int left = source.getAbsoluteLeft() + 10;
        int top = source.getAbsoluteTop() + 10;
        setPopupPosition(left, top);
        show();
    }
}
