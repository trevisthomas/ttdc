package org.ttdc.gwt.client.presenters.demo;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;

import com.google.inject.Inject;

public class DemoPresenter extends BasePagePresenter<DemoPresenter.View>{
	@Inject
	protected DemoPresenter(Injector injector) {
		super(injector, injector.getDemoView());
		
	}

	public interface View extends BasePageView{
		
	}

	@Override
	public void show(HistoryToken args) {
		view.show();
	}
}
