package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.services.RpcServiceAsync;

import com.google.gwt.user.client.ui.Widget;

abstract public class BasePresenter<V extends BaseView> {
	protected final Injector injector;
	protected final V view;
	
	protected BasePresenter(Injector injector, V view) {
		this.injector = injector;
		this.view = view;
	}

	protected Injector getInjector() {
		return injector;
	}
	
	//ForTesting :-(
	/*protected*/public V getView(){
		return view;
	}
	
	protected RpcServiceAsync getService() {
		return getInjector().getService();
	}
	
	public Widget getWidget(){
		return getView().getWidget();
	}
}
