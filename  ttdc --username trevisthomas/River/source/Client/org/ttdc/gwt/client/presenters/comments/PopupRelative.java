package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

public class PopupRelative extends PopupPanel implements MessageEventListener{
	public void showPositionRelativeTo(UIObject parent){
		 int left = parent.getAbsoluteLeft();
         int top = parent.getAbsoluteTop() + parent.getOffsetHeight() - 1;
         setPopupPosition(left, top);
         show();
	}
	
	public PopupRelative() {
		EventBus.getInstance().addListener(this);
//		setGlassEnabled(true);
//		setAnimationEnabled(true);
	}
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.VIEW_CHANGE)){
			hidePopup();
		}
	}
	
	public void hidePopup() {
		if(isShowing()){
			hide();
		}	
	}
}
