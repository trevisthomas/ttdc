package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;

import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

public class TextPresenter extends BasePresenter<TextPresenter.View> {
	public interface View extends BaseView{
		HasText text();
	}
	
	@Inject
	protected TextPresenter(Injector injector) {
		super(injector, injector.getTextView());
	}
	
	public void setText(String text){
		view.text().setText(text);
	}
}
