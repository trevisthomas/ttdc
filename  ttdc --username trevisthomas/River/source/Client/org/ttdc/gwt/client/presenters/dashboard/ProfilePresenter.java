package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class ProfilePresenter extends BasePresenter<ProfilePresenter.View>{
	public interface View extends BaseView{
		HasWidgets avatar();
		HasText loginText();
		HasText nameText();
		HasText emailText();
		HasText bioText();
		HasWidgets webLinks();
		void clear();
	}
	
	@Inject
	public ProfilePresenter(Injector injector) {
		super(injector,injector.getUserProfileView());
	}
	
	public void refresh(GPerson person) {
		init(person);
	}
	
	public void init(GPerson person){
		view.clear();
		ImagePresenter avatarPresenter = injector.getImagePresenter();
		avatarPresenter.setImage(person.getImage(),person.getLogin(),200,-1);
				
		view.avatar().add(avatarPresenter.getWidget());
		view.loginText().setText(person.getLogin());
		view.bioText().setText(person.getBio());
		
		if(!ConnectionId.isAnonymous()){
			view.nameText().setText(person.getName());
			view.emailText().setText(person.getEmail());
			view.webLinks().add(PresenterHelpers.buildWebLinksPresenter(injector, person).getWidget());
		}
		else{
			view.nameText().setText("...");
			view.emailText().setText("...");
		}
	}

	
}
