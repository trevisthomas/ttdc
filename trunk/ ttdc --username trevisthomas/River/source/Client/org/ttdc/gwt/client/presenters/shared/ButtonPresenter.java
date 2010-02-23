package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

public class ButtonPresenter extends BasePresenter<ButtonPresenter.View>{
	public interface View extends BaseView{
		HasClickHandlers clickHandler();
		HasText text();
	}
	
	@Inject
	public ButtonPresenter(Injector injector) {
		super(injector,injector.getButtonView());
	}
	
	public void init(ClickHandler handler, String text){
		view.clickHandler().addClickHandler(handler);
		view.text().setText(text);
	}
}

