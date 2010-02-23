package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;


public class TagRemovePresenter extends BasePresenter<TagRemovePresenter.View> {
	@Inject
	public TagRemovePresenter(Injector injector) {
		super(injector,injector.getTagRemoveView());
	}
	
	public interface View extends BaseView{
		HasClickHandlers getRemoveTagClickHandler();
		HasText getTextTarget();
	}
	
	public void setTag(GTag tag){
		view.getTextTarget().setText(tag.getValue());
	}
	
	public void addClickHandler(ClickHandler handler){
		view.getRemoveTagClickHandler().addClickHandler(handler);
	}
}
