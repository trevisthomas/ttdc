package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;

import com.google.inject.Inject;

public class PageMessagesPresenter extends BasePresenter<PageMessagesPresenter.View> implements MessageEventListener{
	public interface View extends BaseView{
		void error(String err);
		void message(String msg);
		void clear();	
	}
	
	@Inject
	public PageMessagesPresenter(Injector injector) {
		super(injector,injector.getPageMessagesView());
		EventBus.getInstance().addListener(this);
	}

	@Override
	public void onMessageEvent(MessageEvent event) {
		switch (event.getType()) {
		case CLEAR:
			view.clear();
			break;
		case SYSTEM_ERROR:
			view.error(event.getSource());
			break;
		case INFO:
			view.message(event.getSource());
			break;
		case ERROR:
			view.error(event.getSource());
		default:
			break;
		}
	}

}
