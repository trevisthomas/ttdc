package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

@Deprecated
public class TrafficPersonPresenter extends BasePresenter<TrafficPersonPresenter.View>{
	public interface View extends BaseView{
		HasWidgets namePanel();
		HasWidgets datePanel();
		HasWidgets linkPanel();
		HasWidgets avatarPanel();
	}
	
	@Inject
	protected TrafficPersonPresenter(Injector injector) {
		super(injector, injector.getTrafficPersonView());
	}
	
	public void init(GPerson person){
		HyperlinkPresenter personLinkPresenter = injector.getHyperlinkPresenter();
		personLinkPresenter.setPerson(person);
		view.namePanel().add(personLinkPresenter.getWidget());
		
		DatePresenter datePresenter = injector.getDatePresenter();
		datePresenter.init(person.getLastAccessDate());
		view.datePanel().add(datePresenter.getWidget());
		
		view.linkPanel().add(PresenterHelpers.buildWebLinksPresenter(injector, person).getWidget());
		
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(person.getImage(), person.getLogin(), 50, 50);
		imagePresenter.useThumbnail(true);
		view.avatarPanel().add(imagePresenter.getWidget());
	}
}
