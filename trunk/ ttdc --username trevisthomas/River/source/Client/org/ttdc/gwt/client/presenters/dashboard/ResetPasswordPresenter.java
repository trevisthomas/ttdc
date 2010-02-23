package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.types.AccountActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

public class ResetPasswordPresenter extends BasePresenter<ResetPasswordPresenter.View>{
	public interface View extends BaseView{
		HasText oldPasswordText();
		HasText newPasswordText();
		HasText verifyPasswordText();
		HasClickHandlers submitClickHandler();
	}
	
	private GPerson person;
	
	@Inject
	public ResetPasswordPresenter(final Injector injector) {
		super(injector,injector.getResetPasswordView());
		
		view.submitClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!view.newPasswordText().getText().equals(view.verifyPasswordText().getText())){
					EventBus.fireErrorMessage("New password not verified. The entries dont match.");
				}
				else{
					RpcServiceAsync service = injector.getService();
					service.authenticate(person.getLogin(), view.oldPasswordText().getText(), loginCallback());
				}
			}
		});
	}
	
	
	public void init(GPerson person){
		this.person = person;
	}
	
	
	
	private CommandResultCallback<PersonCommandResult> loginCallback() {
		CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
//				GPerson person = result.getPerson();
				AccountCommand cmd = new AccountCommand();
				cmd.setAction(AccountActionType.RESET_PASSWORD);
				cmd.setPassword(view.newPasswordText().getText());
				injector.getService().execute(cmd, buildPersonInfoUpdatedCallback());	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				EventBus.fireErrorMessage("That's not your current password.  If you need help, contact the admin.");
			}
		};
		return callback;
	}
	
	private CommandResultCallback<GenericCommandResult<GPerson>> buildPersonInfoUpdatedCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				//GPerson person = result.getObject();
				EventBus.fireRedirectToLogin();
			}
		};
	}
	
}
