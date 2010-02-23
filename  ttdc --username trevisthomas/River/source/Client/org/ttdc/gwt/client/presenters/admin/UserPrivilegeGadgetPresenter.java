package org.ttdc.gwt.client.presenters.admin;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;

public class UserPrivilegeGadgetPresenter extends BasePresenter<UserPrivilegeGadgetPresenter.View>{
	public interface View extends BaseView{
		void addPriviledge(GPrivilege privilege, boolean checked, ValueChangeHandler<Boolean> handler);
		void clear();
	}
	private List<GPrivilege> allPriviledges = null;
	
	@Inject
	public UserPrivilegeGadgetPresenter(Injector injector) {
		super(injector, injector.getUserPrivilegeGadgetView());
	}
	
	public void init(List<GPrivilege> allPriviledges, GPerson person){
		this.allPriviledges = allPriviledges;
		for(GPrivilege priv : allPriviledges){
			view.addPriviledge(priv, person.hasPrivilege(priv.getValue()), createPriviledgeValueChangeHandler(person.getPersonId(), priv.getPrivilegeId()));
		}
	}

	private ValueChangeHandler<Boolean> createPriviledgeValueChangeHandler(final String personId, final String privilegeId) {
		return new ValueChangeHandler<Boolean>(){
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					PersonCommand cmd = new PersonCommand(personId,PersonStatusType.GRANT_PRIVILEGE, privilegeId);
					RpcServiceAsync service = injector.getService();
					service.execute(cmd, createCallback());
				}
				else{
					PersonCommand cmd = new PersonCommand(personId,PersonStatusType.REVOKE_PRIVILEGE, privilegeId);
					RpcServiceAsync service = injector.getService();
					service.execute(cmd, createCallback());
				}
				
			}
		};
	}
	
	private CommandResultCallback<GenericCommandResult<GPerson>> createCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				view.clear();
				init(allPriviledges, result.getObject());
			}
		};
	}

}
