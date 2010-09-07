package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.uibinder.home.TrafficPersonPanel;
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
		public final int MAX_ENTRIES = 5;
		void addPerson(String personId, Widget w);
		void addOrUpdatePerson(String personId, Widget w);
		void clear();
	}
	private Injector injector;
	@Inject
	protected TrafficPresenter(Injector injector) {
		super(injector, injector.getTrafficView());
		this.injector = injector;

		refreshTraffic();
	}

	private void refreshTraffic() {
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
			TrafficPersonPanel trafficPersonPanel = injector.createTrafficPersonPanel();
			trafficPersonPanel.init(person);
			view.addOrUpdatePerson(person.getPersonId(), trafficPersonPanel);
		}
		else if(event.is(PersonEventType.USER_CHANGED)){
			refreshTraffic();
		}
			
	}
	
	public CommandResultCallback<PersonListCommandResult> createPersonListCallback(){
		return new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				view.clear();
				for(GPerson person : result.getResults().getList()){ 
					TrafficPersonPanel trafficPersonPanel = injector.createTrafficPersonPanel();
					trafficPersonPanel.init(person);
					view.addPerson(person.getPersonId(), trafficPersonPanel);
				}
				EventBus.getInstance().addListener(TrafficPresenter.this);
			}
		};
	} 
}
