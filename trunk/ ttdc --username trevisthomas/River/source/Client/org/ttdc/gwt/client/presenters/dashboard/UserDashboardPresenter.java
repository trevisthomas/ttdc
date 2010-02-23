package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;

import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class UserDashboardPresenter extends BasePagePresenter<UserDashboardPresenter.View> implements PersonEventListener{
	
	public interface View extends BasePageView{
		void displayProfileTab();
		void displayEditProfileTab();
		void displaySettingsTab();
		void displayPasswordTab();
		
		HasWidgets profilePanel();
		HasWidgets editProfilePanel();
		HasWidgets settingsPanel();
		HasWidgets passwordPanel();
	}
	
	@Inject
	public UserDashboardPresenter(Injector injector) {
		super(injector, injector.getUserDashboardView());
		EventBus.getInstance().addListener(this);
	}

	@Override
	public void show(HistoryToken token) {
		view.show();		
		String personId = token.getParameter(HistoryConstants.PERSON_ID);
		PersonCommand cmd = new PersonCommand(personId,PersonStatusType.LOAD);
		injector.getService().execute(cmd, buildCallback());
		
		//setMessageReceiver(injector.getPageMessagesPresenter());
		
		editProfilePresenter = injector.getEditProfilePresenter();
		resetPasswordPresenter = injector.getResetPasswordPresenter();
		settingsPresenter = injector.getSettingsPresenter();
		userProfilePresenter = injector.getUserProfilePresenter();
		
		
//		view.messages().add(messagePresenter.getWidget());
		
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		if(StringUtil.notEmpty(tab)){
			if(HistoryConstants.DASHBOARD_PROFILE_TAB.equals(tab)){
				view.displayProfileTab();
			}
			else if(HistoryConstants.DASHBOARD_EDIT_PROFILE_TAB.equals(tab)){
				view.displayEditProfileTab();
			}
			else if(HistoryConstants.DASHBOARD_SETTINGS_TAB.equals(tab)){
				view.displaySettingsTab();
			}
			else if(HistoryConstants.DASHBOARD_PASSWORD_TAB.equals(tab)){
				view.displayPasswordTab();
			}
			else{
				view.displayProfileTab();
			}
		}
		else{
			view.displayProfileTab();
		}
	}
	
	
	private GPerson person;
	CommandResultCallback<GenericCommandResult<GPerson>> buildCallback(){
		view.profilePanel().clear();
		view.editProfilePanel().clear();
		view.passwordPanel().clear();
		view.settingsPanel().clear();
		
		view.profilePanel().add(injector.getWaitPresenter().getWidget());
		view.editProfilePanel().add(injector.getWaitPresenter().getWidget());
		view.passwordPanel().add(injector.getWaitPresenter().getWidget());
		view.settingsPanel().add(injector.getWaitPresenter().getWidget());
		
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				person = result.getObject();
				initializeTabs(person);
			}
		};
	}
	
	private void initializeTabs(GPerson person) {
		userProfilePresenter.init(person);
		view.profilePanel().clear();
		view.profilePanel().add(userProfilePresenter.getWidget());
		
		
		editProfilePresenter.init(person);
		view.editProfilePanel().clear();
		view.editProfilePanel().add(editProfilePresenter.getWidget());
		
		
		resetPasswordPresenter.init(person);
		view.passwordPanel().clear();
		view.passwordPanel().add(resetPasswordPresenter.getWidget());
		
		settingsPresenter.init(person);
		view.settingsPanel().clear();
		view.settingsPanel().add(settingsPresenter.getWidget());
		
	}

	@Override
	public void onPersonEvent(PersonEvent event) {
		if(person == null){
			return;  //This should really never happen.  I guess it could if a server event arrived
				     //before this object's call back responded but... that's extremely unlikely 
		}
		if(event.getSource().getPersonId().equals(person.getPersonId()) &&
		   event.getType().equals(PersonEventType.USER_PROFILE_UPDATED)){
			person = event.getSource();
			settingsPresenter.refresh(person);
			userProfilePresenter.refresh(person);
			editProfilePresenter.refresh(person);
		}
		else{
			// not the current user.  Could be an update to someone elses profile on the bus. ( a server sourced event)
		}
		
	}
	
	private ProfilePresenter userProfilePresenter;
	private EditProfilePresenter editProfilePresenter;
	private ResetPasswordPresenter resetPasswordPresenter;
	private SettingsPresenter settingsPresenter;
	
	
	
	
}
