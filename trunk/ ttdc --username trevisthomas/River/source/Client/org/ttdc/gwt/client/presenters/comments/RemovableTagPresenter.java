package org.ttdc.gwt.client.presenters.comments;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

public class RemovableTagPresenter  extends BasePresenter<RemovableTagPresenter.View>{
	public interface View extends BaseView{
		HasClickHandlers getRemoveButton();
		HasText getTagLabel();
	}
	
	private GTag tag;
	
	@Inject
	public RemovableTagPresenter(Injector injector) {
		super(injector,injector.getRemovableTagView());
	}
	
	public void init(GTag tag, ClickHandler removeClickHandler){
		this.tag = tag;
		view.getTagLabel().setText(tag.getValue());
		view.getRemoveButton().addClickHandler(removeClickHandler);
	}
	
	public GTag getTag() {
		return tag;
	}
}
