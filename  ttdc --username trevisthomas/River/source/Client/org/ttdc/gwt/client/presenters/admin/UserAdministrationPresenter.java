package org.ttdc.gwt.client.presenters.admin;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ButtonPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.PrivilegeCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserAdministrationPresenter extends BasePresenter<UserAdministrationPresenter.View>{
	public interface View extends BaseView{
		void addPerson(Widget stateControls, Widget nameWidget, String fullName, 
					   String email, Widget priviledgeWidget, Widget loginAsUser);
		HasValue<Boolean> filterActiveOnly();
		HasWidgets paginatorPanel();
		HasValueChangeHandlers<Boolean> filterActiveOnlyValueChange();
		void clearPersonTable();
	}
	
	HistoryToken previousToken;
	private List<GPrivilege> privileges = null;
	private List<GPerson> personList = null;
	PersonListType personListType = null;
	
	@Inject
	public UserAdministrationPresenter(Injector injector) {
		super(injector,injector.getUserAdministrationView());
		view.filterActiveOnlyValueChange().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				HistoryToken newToken = new HistoryToken();
				newToken.load(previousToken);
				newToken.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
				if(event.getValue())
					newToken.setParameter(HistoryConstants.ADMIN_PERSON_LIST_TYPE_KEY, HistoryConstants.ADMIN_PERSON_LIST_TYPE_ACTIVE);
				else
					newToken.setParameter(HistoryConstants.ADMIN_PERSON_LIST_TYPE_KEY, HistoryConstants.ADMIN_PERSON_LIST_TYPE_ALL);
				
				EventBus.fireHistoryToken(newToken);
			}
		});
	}
	
	public void init(HistoryToken token) {
		previousToken = new HistoryToken();
		previousToken.load(token);
		personList = null;
		
		BatchCommandTool batcher = new BatchCommandTool();
		
		if(token.isParameterEq(HistoryConstants.ADMIN_PERSON_LIST_TYPE_KEY, HistoryConstants.ADMIN_PERSON_LIST_TYPE_ACTIVE) ){
			personListType = PersonListType.ACTIVE;
			view.filterActiveOnly().setValue(true);
		}
		else{
			personListType = PersonListType.ALL;
			view.filterActiveOnly().setValue(false);
		}
		
		PersonListCommand personListCmd = new PersonListCommand(personListType);
		
		if(token.hasParameter(HistoryConstants.PAGE_NUMBER_KEY))
			personListCmd.setCurrentPage(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY));
		if(view.filterActiveOnly().getValue()){
			personListCmd.setSortOrder(SortBy.BY_HITS);
			personListCmd.setSortDirection(SortDirection.ASC);
		}
		batcher.add(personListCmd, createPersonListCallback());
		
		PrivilegeCrudCommand privilegeCmd = new PrivilegeCrudCommand(ActionType.READ);
		batcher.add(privilegeCmd, createPriviledgesCallback());
		
		RpcServiceAsync service = injector.getService();
		service.execute(batcher.getActionList(), batcher);
		
	}
	
	public CommandResultCallback<GenericListCommandResult<GPrivilege>> createPriviledgesCallback(){
		return new CommandResultCallback<GenericListCommandResult<GPrivilege>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GPrivilege> result) {
				privileges = result.getList();
				if(personList != null){
					refresh(personList,privileges);
				}
			}
		};
	} 
	
	public CommandResultCallback<PersonListCommandResult> createPersonListCallback(){
		return new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				personList = result.getResults().getList();
				if(privileges != null){
					refresh(personList,privileges);
				}
				PaginationPresenter pagination = injector.getPaginationPresenter();
				pagination.initialize(previousToken, result.getResults());
				view.paginatorPanel().add(pagination.getWidget());
			}
		};
	} 
	

	/**
	 * Notice how the above call backs each check to see if other has completed? Thats because
	 * i need both lists to make this work!
	 *
	 * @param list
	 * @param privileges
	 */
	public void refresh(List<GPerson> list, List<GPrivilege> privileges){
		for(GPerson person : list){
			PersonStatusGadgetPresenter userStateGadget = injector.getUserStateGadgetPresenter();
			userStateGadget.init(person);
			
			HyperlinkPresenter userLoginLinkPresenter = injector.getHyperlinkPresenter();
			userLoginLinkPresenter.setPerson(person);
			
			UserPrivilegeGadgetPresenter userPriviledgeGadget = injector.getUserPrivilegeGadgetPresenter();
			userPriviledgeGadget.init(privileges, person);
			
			ButtonPresenter loginAsUserPresenter = injector.getButtonPresenter();
			loginAsUserPresenter.init(loginAsUserClickHandler(person),"Login As "+person.getLogin());
			 
			view.addPerson(userStateGadget.getWidget(),
					userLoginLinkPresenter.getWidget(), 
					person.getName(), 
					person.getEmail(), 
					userPriviledgeGadget.getWidget(), 
					loginAsUserPresenter.getWidget());
		}
		
		
	}
	
	private ClickHandler loginAsUserClickHandler(final GPerson person) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(ConnectionId.getInstance().getCurrentUser().getPersonId().equals(person.getPersonId())){
					throw new RuntimeException("Hi "+person.getLogin());
				}
				RpcServiceAsync service = injector.getService();
				CommandResultCallback<PersonCommandResult> callback = identitySwitchCallback();
				service.identity(person.getPersonId(), callback);
			}
		};
	}
	private CommandResultCallback<PersonCommandResult> identitySwitchCallback() {
		return new CommandResultCallback<PersonCommandResult>(){
			@Override
			public void onSuccess(PersonCommandResult result) {
				EventBus.fireReturnHomeEvent();
			}
		};
	}
}
