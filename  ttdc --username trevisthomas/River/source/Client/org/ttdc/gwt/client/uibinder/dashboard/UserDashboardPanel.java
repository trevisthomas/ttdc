package org.ttdc.gwt.client.uibinder.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.dashboard.EditProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.ResetPasswordPresenter;
import org.ttdc.gwt.client.presenters.dashboard.SettingsPresenter;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserDashboardPanel extends BasePageComposite implements PersonEventListener {
	interface MyUiBinder extends UiBinder<Widget, UserDashboardPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField TabPanel tabPanelElement;
	@UiField (provided = true) Widget pageFooterElement;
	
	private static final SimplePanel profilePanel = new SimplePanel();
	private static final SimplePanel editProfilePanel = new SimplePanel();
	private static final SimplePanel resetPasswordPanel = new SimplePanel();
	private static final SimplePanel settingsPanel = new SimplePanel();
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	private ProfilePresenter userProfilePresenter;
	private EditProfilePresenter editProfilePresenter;
	private ResetPasswordPresenter resetPasswordPresenter;
	private SettingsPresenter settingsPresenter;
	private GPerson person;
	
	@Inject
	public UserDashboardPanel(Injector injector){
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	pageFooterElement = injector.createStandardFooter().getWidget();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	tabPanelElement.add(profilePanel,"Profile");
    	tabPanelElement.add(editProfilePanel,"Edit Profile");
    	tabPanelElement.add(resetPasswordPanel,"Reset Password");
    	tabPanelElement.add(settingsPanel,"Settings");
		
		tabPanelElement.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!tabPanelElement.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectTabSelection(index);
			}
		});
		
		tabPanelElement.addStyleName("tt-TabPanel-fullpage");
		
		EventBus.getInstance().addListener(this);
	}
	
	@Override
	protected void onShow(HistoryToken token) {
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
				displayProfileTab();
			}
			else if(HistoryConstants.DASHBOARD_EDIT_PROFILE_TAB.equals(tab)){
				displayEditProfileTab();
			}
			else if(HistoryConstants.DASHBOARD_SETTINGS_TAB.equals(tab)){
				displaySettingsTab();
			}
			else if(HistoryConstants.DASHBOARD_PASSWORD_TAB.equals(tab)){
				displayPasswordTab();
			}
			else{
				displayProfileTab();
			}
		}
		else{
			displayProfileTab();
		}
		
	}
	
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_DASHBOARD);
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_PROFILE_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_EDIT_PROFILE_TAB);
				break;
			case 2:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_SETTINGS_TAB);
				break; 
			case 3:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.DASHBOARD_PASSWORD_TAB);
				break; 	
				
		}
		History.newItem(token.toString(),false);
	}
	
	public void displayProfileTab() {
		tabPanelElement.selectTab(0);
	}
	
	public void displayEditProfileTab() {
		tabPanelElement.selectTab(1);
	}
	
	public void displayPasswordTab() {
		tabPanelElement.selectTab(3);
	}

	public void displaySettingsTab() {
		tabPanelElement.selectTab(2);
	}
	
	CommandResultCallback<GenericCommandResult<GPerson>> buildCallback(){
		profilePanel.clear();
		editProfilePanel.clear();
		resetPasswordPanel.clear();
		settingsPanel.clear();
		
		profilePanel.add(injector.getWaitPresenter().getWidget());
		editProfilePanel.add(injector.getWaitPresenter().getWidget());
		resetPasswordPanel.add(injector.getWaitPresenter().getWidget());
		settingsPanel.add(injector.getWaitPresenter().getWidget());
		
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				person = result.getObject();
				initializeTabs(person);
				
				pageHeaderPanel.init(person.getLogin()+"'s Dashboard", "tweak account and profile settings");
				pageHeaderPanel.getSearchBoxPresenter().init(person);
			}
		};
	}
	
	private void initializeTabs(GPerson person) {
		userProfilePresenter.init(person);
		profilePanel.clear();
		profilePanel.add(userProfilePresenter.getWidget());
		
		
		editProfilePresenter.init(person);
		editProfilePanel.clear();
		editProfilePanel.add(editProfilePresenter.getWidget());
		
		
		resetPasswordPresenter.init(person);
		resetPasswordPanel.clear();
		resetPasswordPanel.add(resetPasswordPresenter.getWidget());
		
		settingsPresenter.init(person);
		settingsPanel.clear();
		settingsPanel.add(settingsPresenter.getWidget());
		
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
}
