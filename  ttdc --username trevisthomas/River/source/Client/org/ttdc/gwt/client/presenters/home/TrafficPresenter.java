package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TrafficPresenter extends BasePresenter<TrafficPresenter.View> implements PersonEventListener{
	public interface View extends BaseView{
		public final int MAX_ENTRIES = 3;
		void addPerson(String personId, Widget w);
		void addOrUpdatePerson(String personId, Widget w);
	}
	
	@Inject
	protected TrafficPresenter(Injector injector) {
		super(injector, injector.getTrafficView());

		PersonListCommand personListCmd = new PersonListCommand(PersonListType.ACTIVE);
		
		personListCmd.setSortOrder(SortBy.BY_LAST_ACCESSED);
		personListCmd.setSortDirection(SortDirection.ASC);
		personListCmd.setPageSize(View.MAX_ENTRIES);
		personListCmd.setLoadFullDetails(true);
		injector.getService().execute(personListCmd, createPersonListCallback());
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.TRAFFIC)){
			GPerson person = event.getSource();
			TrafficPersonPresenter trafficPersonPresenter = injector.getTrafficPersonPresenter();
			trafficPersonPresenter.init(person);
			view.addOrUpdatePerson(person.getPersonId(), trafficPersonPresenter.getWidget());
		}
			
	}
	
	public CommandResultCallback<PersonListCommandResult> createPersonListCallback(){
		return new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				for(GPerson person : result.getResults().getList()){ 
					TrafficPersonPresenter trafficPersonPresenter = injector.getTrafficPersonPresenter();
					trafficPersonPresenter.init(person);
					view.addPerson(person.getPersonId(), trafficPersonPresenter.getWidget());
				}
				EventBus.getInstance().addListener(TrafficPresenter.this);
			}
		};
	} 
}
