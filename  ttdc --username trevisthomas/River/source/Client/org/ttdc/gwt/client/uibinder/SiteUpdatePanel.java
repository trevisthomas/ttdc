package org.ttdc.gwt.client.uibinder;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SiteUpdatePanel extends Composite implements PostEventListener, MessageEventListener{
	interface MyUiBinder extends UiBinder<Widget, SiteUpdatePanel> {}

	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	private final Injector injector;
	@UiField Label siteRefresh; 
	@UiField (provided=true) Widget waitMessage;
	private int postCount = 0;
	
	@Inject
	public SiteUpdatePanel(Injector injector) {
		this.injector = injector;
		waitMessage = injector.getWaitPresenter().getWidget();
		
		initWidget(binder.createAndBindUi(this));
		siteRefresh.setVisible(false);
		EventBus.getInstance().addListener((PostEventListener)this);
		EventBus.getInstance().addListener((MessageEventListener)this);
		
		
		waitMessage.setVisible(false);
	}

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW)){
			siteRefresh.setVisible(true);
			postCount++;
			String msg;
			
			if(postCount == 1)
				msg = "New content! Click to refresh!";
			else
				msg = postCount+" "+" new comments! Click to refresh!";
				
			siteRefresh.setText(msg);
		}
	}
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.REFRESH_COMPLETE)){
			waitMessage.setVisible(false);
		}
	}
	
	
	@UiHandler("siteRefresh")
	void onClickRefresh(ClickEvent event){
		postCount = 0;
		waitMessage.setVisible(true);
		siteRefresh.setVisible(false);
		EventBus.fireEvent(new MessageEvent(MessageEventType.RELOAD_HOMEPAGE_TABS, ""));
		
	}
}
