package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserRowPresenter extends BasePresenter<UserRowPresenter.View>{
	public interface View extends BaseView{
		Widget getLoginWidget();
		Widget getNameWidget();
		Widget getEmailWidget();
		Widget getMemberSinceWidget();
		Widget getHitsWidget();
		
		HasWidgets loginPanel();
		HasWidgets lastAccessedPanel();
		HasWidgets memberSincePanel();
		HasText emailText();
		HasText nameText();
		HasText hitCountText();
		HasWidgets imagePanel();
		
		void hasPrivateAccess(boolean flag);
		
	}
	
	@Inject
	public UserRowPresenter(Injector injector) {
		super(injector, injector.getUserRowView());
	}

	public void init(GPerson person) {
		HyperlinkPresenter personLinkPresenter = injector.getHyperlinkPresenter();
		personLinkPresenter.setPerson(person);
		view.loginPanel().add(personLinkPresenter.getWidget());
		
		DatePresenter datePresenter = injector.getDatePresenter();
		datePresenter.init(person.getLastAccessDate(),DateFormatUtil.dateTimeFormatter);
		view.lastAccessedPanel().add(datePresenter.getWidget());
		
		datePresenter = injector.getDatePresenter();
		datePresenter.init(person.getDate(),DateFormatUtil.mediumDayFormatter);
		view.memberSincePanel().add(datePresenter.getWidget());
		
		view.emailText().setText(person.getEmail());
		
		view.nameText().setText(person.getName());
		view.hitCountText().setText(""+person.getHits());
		
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(person.getImage(), person.getLogin());
		imagePresenter.useThumbnail(true);
		
		view.imagePanel().add(imagePresenter.getWidget());
	}
	
	public Widget getLoginWidget(){
		return view.getLoginWidget();
	}
	public Widget getNameWidget(){
		return view.getNameWidget();
	}
	public Widget getEmailWidget(){
		return view.getEmailWidget();
	}
	public Widget getMemberSinceWidget(){
		return view.getMemberSinceWidget();
	}
	public Widget getHitsWidget(){
		return view.getHitsWidget();
	}
}
