package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;

import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

public class WaitPresenter  extends BasePresenter<WaitPresenter.View> {
	public interface View extends BaseView{
		HasText text();
	}
	
	@Inject
	protected WaitPresenter(Injector injector) {
		super(injector, injector.getWaitView());
		view.text().setText("Loading...");
	}
	
	void setMessage(String text){
		view.text().setText(text);
	}
	
	//TODO show graphic?

}
