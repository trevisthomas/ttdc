package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class GenericTabularFlowPresenter extends BasePresenter<GenericTabularFlowPresenter.View>{
	@Inject
	protected GenericTabularFlowPresenter(Injector injector) {
		super(injector, injector.getGenericTabularFlowView());
	}

	public interface View extends BaseView{
		void addWidgetToFlow(Widget w);
		void setMaxColumns(int cols);
	}
	
	public void setMaxColumns(int cols){
		view.setMaxColumns(cols);
	}
	
	public void stackWidget(Widget w){
		view.addWidgetToFlow(w);
	}

}
