package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.StyleListCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.AccountActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class SettingsPresenter extends BasePresenter<SettingsPresenter.View>{
	public interface View extends BaseView{
		HasClickHandlers updateStyleClick();
		void addAvailableStyle(GStyle style);
		String getSelectedStyleId();
		void setSelectedStyleId(String styleId);
		
		HasClickHandlers nwsCheckBoxClick();
		HasValue<Boolean> enableNwsValue();
		
		void clearAvailableStyles();
	}
	
	private GPerson person;
	
	
	@Inject
	public SettingsPresenter(Injector injector) {
		super(injector,injector.getSettingsView());
	}

	public void refresh(GPerson person) {
		this.person = person;
		view.enableNwsValue().setValue(person.isNwsEnabled());
		view.setSelectedStyleId(person.getStyle().getStyleId());
	}
	
	public void init(GPerson person) {
		EventBus.fireMessage("This is a test of the message broadcast system.");
		
		this.person = person;
		view.enableNwsValue().setValue(person.isNwsEnabled());
		
		StyleListCommand styleListCmd = new StyleListCommand();
		injector.getService().execute(styleListCmd, buildStyleListCallback());
		
		view.nwsCheckBoxClick().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AccountCommand cmd = new AccountCommand();
				cmd.setAction(AccountActionType.ENABLE_NWS);
				cmd.setEnableNws(view.enableNwsValue().getValue());
				injector.getService().execute(cmd, buildUserObjectUpdatedCallback());
			}
		});
		
		view.updateStyleClick().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AccountCommand cmd = new AccountCommand();
				cmd.setAction(AccountActionType.UPDATE);
				cmd.setStyleId(view.getSelectedStyleId());
				injector.getService().execute(cmd, buildStyleUpdatedCallback());
			}
		});
		
		view.setSelectedStyleId(person.getStyle().getStyleId());
	}
	
	protected CommandResultCallback<GenericCommandResult<GPerson>> buildUserObjectUpdatedCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.fireMessage(result.getMessage());
			}
		};
	}

	private CommandResultCallback<GenericListCommandResult<GStyle>> buildStyleListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GStyle>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GStyle> result) {
				view.clearAvailableStyles();
				for(GStyle style : result.getList()){
					view.addAvailableStyle(style);
				}
				GStyle style = person.getStyle();
				view.setSelectedStyleId(style.getStyleId());
			}
		};
	}
	
	private CommandResultCallback<GenericCommandResult<GPerson>> buildStyleUpdatedCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
//				person = result.getObject();
//				EventBus.fireEvent(new PersonEvent(PersonEventType.USER_PROFILE_UPDATED,person));
//				EventBus.fireMessage(result.getMessage());
				EventBus.reload();
			}
		};
	}

}
