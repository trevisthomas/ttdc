package org.ttdc.gwt.client.uibinder.post;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;

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
    
    @Inject
    public MoreOptionsPopupPanel(Injector injector) { 
    	this.injector = injector;
    	//NoteToSelf: This is setwidget and not initWidget because i'm a popup panel and not a composite
    	setWidget(binder.createAndBindUi(this)); 
    }
    
    public void init(GPost post){
    	this.post = post;
    }
    
    public void addReplyClickHandler(ClickHandler handler){
    	replyElement.addClickHandler(handler);
    }
    
    @UiHandler("replyElement")
    void onClickReply(ClickEvent event){
    	Window.alert("Reply clicked for: "+post.getTitle());
    	hide();
    }
    
    @UiHandler("likeElement")
    void onClickLike(ClickEvent event){
    	Window.alert("Like clicked for: "+post.getTitle());
    	hide();
    }    

    public void showRelativeTo(Widget source){
    	int left = source.getAbsoluteLeft() + 10;
        int top = source.getAbsoluteTop() + 10;
        setPopupPosition(left, top);
        show();
    }
}
