package org.ttdc.gwt.client.presenters.users;

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
import com.google.inject.Inject;

public class RequestPasswordResetPresenter extends BasePresenter<RequestPasswordResetPresenter.View>{
	public interface View extends BaseView{
		HasClickHandlers submitClickHandler();
		HasText loginTextBox();
		HasText emailTextBox();
		void showSuccess(String message);
	}
	
	@Inject
	public RequestPasswordResetPresenter(Injector injector) {
		super(injector,injector.getRequestPasswordView());
	}
	
	public void init(){
		view.submitClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestPasswordReset();
			}
		});
	}
	
	private void requestPasswordReset(){
		AccountCommand cmd = new AccountCommand();
		cmd.setAction(AccountActionType.REQUEST_PASSWORD_RESET);
		cmd.setEmail(view.emailTextBox().getText());
		cmd.setLogin(view.loginTextBox().getText());

		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildCallback());
	}
	
	private CommandResultCallback<GenericCommandResult<GPerson>> buildCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>() {
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.fireMessage(result.getMessage());
				view.showSuccess("Request recieved. You should recieve an email shortly.  If it doesnt arrive in a few moments, contact me.");
			}
		};
	}
}
