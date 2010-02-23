package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.constants.PersonConstants;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class PersonStatusGadgetPresenter extends BasePresenter<PersonStatusGadgetPresenter.View>{
	public interface View extends BaseView{
		HasClickHandlers activateButton();
		HasClickHandlers deactivateButton();
		HasClickHandlers lockButton();
		HasClickHandlers unlockButton();
		
		void setVisableActivate(boolean visible);
		void setVisableDeactivate(boolean visible);
		void setVisableLock(boolean visible);
		void setVisableUnlock(boolean visible);
	}
	
	@Inject
	public PersonStatusGadgetPresenter(Injector injector) {
		super(injector, injector.getPersonStatusGadgetView());
		
		
	}
	
	public void init(final GPerson person){
		view.activateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PersonCommand cmd = new PersonCommand(person.getPersonId(),PersonStatusType.ACTIVATE);
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, createStatusUpdateCallback());
			}
		});
		
		view.deactivateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PersonCommand cmd = new PersonCommand(person.getPersonId(),PersonStatusType.DEACTIVATE);
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, createStatusUpdateCallback());
			}
		});
		
		view.lockButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PersonCommand cmd = new PersonCommand(person.getPersonId(),PersonStatusType.LOCK);
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, createStatusUpdateCallback());
			}
		});
		
		view.unlockButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PersonCommand cmd = new PersonCommand(person.getPersonId(),PersonStatusType.UNLOCK);
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, createStatusUpdateCallback());
			}
		});
		
		refresh(person);
	}

	private void refresh(GPerson person) {
		String status = person.getStatus();
		view.setVisableActivate(false);
		view.setVisableDeactivate(false);
		view.setVisableLock(false);
		view.setVisableUnlock(false);
		
		if(PersonConstants.ACTIVE.equals(status)){
			view.setVisableDeactivate(true);
			view.setVisableLock(true);
		}
		else if(PersonConstants.LOCKED.equals(status)){
			view.setVisableUnlock(true);	
		}
		else{ //if(PersonConstants.INACTIVE.equals(status)){
			view.setVisableActivate(true);
		}
	}

	
	private CommandResultCallback<GenericCommandResult<GPerson>> createStatusUpdateCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				refresh(result.getObject());
			}
		};
	}
	

}
