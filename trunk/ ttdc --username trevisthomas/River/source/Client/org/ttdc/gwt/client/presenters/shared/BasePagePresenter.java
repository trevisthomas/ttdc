package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.util.MessageReceiver;
import org.ttdc.gwt.client.uibinder.Navigation;

/**
 * This abstraction is to capture things unique to views that respond directly to history.
 * 
 * @author Trevis
 *
 * @param <V>
 */
abstract public class BasePagePresenter <V extends BasePageView> extends BasePresenter<V>{
	private PageMessagesPresenter messageReceiver;
	private Navigation navigation;
	
	protected BasePagePresenter(Injector injector, V view) {
		super(injector, view);
		
		if(messageReceiver == null){
			PageMessagesPresenter msgPresenter = injector.getPageMessagesPresenter();
			messageReceiver = msgPresenter;
			view.messagePanel().clear(); //I dont know why i have to clear this...
			view.messagePanel().add(msgPresenter.getWidget());
		}
		
		if(navigation == null){
			Navigation nav = injector.createNavigation();
			navigation = nav;
			view.navigationPanel().clear();
			view.navigationPanel().add(navigation);
		}
		
	}
	abstract public void show(HistoryToken token);
	
//	@Override
//	public void clear() {
//		messageReceiver.clear();
//		
//	}
//	@Override
//	public void error(String err) {
//		messageReceiver.error(err);
//	}
//	@Override
//	public void message(String msg) {
//		messageReceiver.message(msg);
//	}
	
//	public final MessageReceiver getMessagesReceiver(){
//		return messageReceiver;
//	}
//	
//	private final void setMessageReceiver(MessageReceiver messageReceiver){
//		this.messageReceiver = messageReceiver;
//	}
}
