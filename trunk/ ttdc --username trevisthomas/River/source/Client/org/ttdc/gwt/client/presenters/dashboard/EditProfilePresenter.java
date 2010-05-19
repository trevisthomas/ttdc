package org.ttdc.gwt.client.presenters.dashboard;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.constants.UserObjectConstants;
import org.ttdc.gwt.client.constants.UserObjectTemplateConstants;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ImageActivityObserver;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand.UserObjectTemplateListAction;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.AccountActionType;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class EditProfilePresenter extends BasePresenter<EditProfilePresenter.View> implements ImageActivityObserver{
	public interface View extends BaseView{
		HasWidgets messages(); 
		HasWidgets avatar();
		HasWidgets avatarUploader();
		HasText nameText();
		HasText emailText();
		HasText verifyEmailText();
		HasValue<Date> birthdayDate();
		HasText bioText();
		HasWidgets WebLinks();
		
		HasClickHandlers updateUserInfoClickHandler();
		HasClickHandlers addWebLinkClickHandler();
		
		void addWebLinkTemplate(GUserObjectTemplate template);
		
		HasText webLinkUrlText();
		String getSelectedWebLinkTemplate();
		
		void clear();
		
	} 
			
	@Inject
	public EditProfilePresenter(Injector injector) {
		super(injector, injector.getEditProfileView());
		view.addWebLinkClickHandler().addClickHandler(buildAddWebLinkClickHandler());
		view.updateUserInfoClickHandler().addClickHandler(buildUpdateUserInfoClickHandler());
	}
	
	public void init(GPerson person){
		view.clear();
		
		ImageUploadPresenter imageUploadPresenter = injector.getImageUploadPresenter();
		imageUploadPresenter.setSubmitButtonVisable(true);
		imageUploadPresenter.setImageUploadObserver(this);
		view.avatarUploader().add(imageUploadPresenter.getWidget());
		
		showPersonInView(person);
		
		for(GUserObject uo : person.getWebPageUserObjects()){
			addUserObjectToView(uo);
		}
		
		reloadAvailableTemplateList();
	}

	public void refresh(GPerson person) {
		showPersonInView(person);
		reloadAvailableTemplateList();
	}
	
	private void addUserObjectToView(GUserObject uo) {
		RemovableWebLinkPresenter rwlvPresenter = injector.getRemovableWebLinkPresenter();
		rwlvPresenter.init(uo);
		view.WebLinks().add(rwlvPresenter.getWidget());
	}
	
	private void reloadAvailableTemplateList() {
		UserObjectTemplateListCommand cmd = new UserObjectTemplateListCommand();
		cmd.setAction(UserObjectTemplateListAction.GET_AVAILABLE_FOR_USER);
		cmd.setTemplateType(UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
		injector.getService().execute(cmd, buildAvailableTemplateCallback());
	}

	private ClickHandler buildUpdateUserInfoClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!view.emailText().getText().equals(view.verifyEmailText().getText())){
					EventBus.fireErrorMessage("Email addresses do not match.");
				}
				else{	
					AccountCommand cmd = new AccountCommand();
					cmd.setAction(AccountActionType.UPDATE);
					cmd.setBio(view.bioText().getText());
					cmd.setBirthday(view.birthdayDate().getValue());
					cmd.setEmail(view.emailText().getText());
					cmd.setName(view.nameText().getText());
					
					injector.getService().execute(cmd, buildPersonInfoUpdatedCallback());
				}
			}
		};
	}

	private ClickHandler buildAddWebLinkClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UserObjectCrudCommand cmd = new UserObjectCrudCommand();
				cmd.setAction(ActionType.CREATE);
				cmd.setType(UserObjectConstants.TYPE_WEBPAGE);
				cmd.setTemplateId(view.getSelectedWebLinkTemplate());
				cmd.setValue(view.webLinkUrlText().getText());
				injector.getService().execute(cmd, buildAddWebLinkCallback());
			}
		};
	}
	
	private void showPersonInView(GPerson person) {
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(person.getImage(),200, -1);
		imagePresenter.linkToFullImage(true);
		view.avatar().clear();
		view.avatar().add(imagePresenter.getWidget());
		
		view.nameText().setText(person.getName());
		view.emailText().setText(person.getEmail());
		view.verifyEmailText().setText(person.getEmail());
		view.birthdayDate().setValue(person.getBirthday());
		view.bioText().setText(person.getBio());
	}

	
	
	private CommandResultCallback<GenericCommandResult<GUserObject>> buildAddWebLinkCallback() {
		return new CommandResultCallback<GenericCommandResult<GUserObject>>(){
			@Override
			public void onSuccess(GenericCommandResult<GUserObject> result) {
				addUserObjectToView(result.getObject());	
				GPerson person = result.getObject().getOwner();
				EventBus.fireEvent(new PersonEvent(PersonEventType.USER_PROFILE_UPDATED,person));
			}
		};
	}
	
	private CommandResultCallback<GenericCommandResult<GPerson>> buildPersonInfoUpdatedCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.fireMessage(result.getMessage());
				GPerson person = result.getObject();
				showPersonInView(person);
				EventBus.fireEvent(new PersonEvent(PersonEventType.USER_PROFILE_UPDATED,person));
			}
		};
	}
	
	private CommandResultCallback<GenericListCommandResult<GUserObjectTemplate>> buildAvailableTemplateCallback(){
		return new CommandResultCallback<GenericListCommandResult<GUserObjectTemplate>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GUserObjectTemplate> result) {
				for(GUserObjectTemplate uot : result.getList()){
					view.addWebLinkTemplate(uot);
				}
			}
		};
	}

	@Override
	public void notifyImageActionCompletWithStatus(String imageId) {
		AccountCommand cmd = new AccountCommand();
		cmd.setAction(AccountActionType.UPDATE);
		cmd.setImageId(imageId);
		injector.getService().execute(cmd, buildPersonInfoUpdatedCallback());
	}
}
