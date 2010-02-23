package org.ttdc.gwt.client.presenters.users;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.AccountActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class CreateAccountPresenter extends BasePresenter<CreateAccountPresenter.View>{
	public interface View extends BaseView{
		HasText loginTextBox();
		HasText passwordTextBox();
		HasText passwordVerifyTextBox();
		HasValue<Date> birthdayDateBox();
		HasText bioTextBox();
		HasText nameTextBox();
		HasText emailTextBox();
		HasText valOneTextBox();
		HasText valTwoTextBox();
		HasText sumTextBox();
		HasText emailVerifyTextBox();
		void showSuccess(String successMessage);
		
		HasClickHandlers createAccountClickHandler();
	}
	
	private int sum;
	
	@Inject
	public CreateAccountPresenter(Injector injector) {
		super(injector,injector.getCreateAccountView());
	}
	
	public void init(){
		randomProblem();
		view.createAccountClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(validate()){
					createAccount();
				}
			}
		});
	}
	
	private boolean validate() {
		boolean retval = true;
		if(!(""+sum).equals(view.sumTextBox().getText().trim())){
			randomProblem();
			EventBus.fireErrorMessage("Try adding the number this time.");
			retval=false;	
		}
		if (!view.passwordTextBox().getText().equals(view.passwordVerifyTextBox().getText())){
			EventBus.fireErrorMessage("Passwords do not match.");
			retval=false;
		}
		if (!view.emailTextBox().getText().equals(view.emailVerifyTextBox().getText())){
			EventBus.fireErrorMessage("Email addresses dont match");
			retval=false;
		}
		return retval;
	}
	
	public void randomProblem(){
		int val1 = (int)(Math.random() * 10);
		int val2 = (int)(Math.random() * 10);
		sum = val1+val2;
		
		view.valOneTextBox().setText(""+val1);
		view.valTwoTextBox().setText(""+val2);
		view.sumTextBox().setText("");
		
	}
	
	private void createAccount(){
		AccountCommand cmd = new AccountCommand();
		cmd.setAction(AccountActionType.CREATE);
		cmd.setBio(view.bioTextBox().getText());
		cmd.setBirthday(view.birthdayDateBox().getValue());
		cmd.setEmail(view.emailTextBox().getText());
		cmd.setLogin(view.loginTextBox().getText());
		cmd.setName(view.nameTextBox().getText());
		cmd.setPassword(view.passwordTextBox().getText());
		
		
		
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildCallback());
	}

	private CommandResultCallback<GenericCommandResult<GPerson>> buildCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>() {
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.fireMessage(result.getMessage());
				view.showSuccess("Account created You should recieve an email shortly.  If it doesnt arrive in a few moments, contact me.");
			}
		};
	}
}
