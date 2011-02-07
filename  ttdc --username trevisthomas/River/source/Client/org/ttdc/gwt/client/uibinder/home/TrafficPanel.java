package org.ttdc.gwt.client.uibinder.home;

import java.util.LinkedList;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.home.TrafficPresenter.View;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TrafficPanel extends Composite implements PersonEventListener {
	interface MyUiBinder extends UiBinder<Widget, TrafficPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	private static final int MAX_ENTRIES = 5;
	private final LinkedList<Bundle> list = new LinkedList<Bundle>();
	
	private Injector injector;
	
	@UiField TabPanel trafficElement;
	private final HorizontalPanel personPanel = new HorizontalPanel();
	
		
	
	@Inject
	public TrafficPanel(Injector injector){
		this.injector = injector;
		
    	initWidget(binder.createAndBindUi(this));
    	
    	//these wasted simple panels were to get the sizing right in the tab
    	SimplePanel trafficHolderPanel = new SimplePanel();
    	SimplePanel wtfGWT = new SimplePanel();
    	wtfGWT.add(trafficHolderPanel);
    	trafficHolderPanel.add(personPanel);
    	trafficHolderPanel.setStyleName("tt-traffic-panel");
    	trafficElement.add(wtfGWT, "Traffic");
    	trafficElement.selectTab(0);
    	personPanel.addStyleName("tt-center");
    	
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
			TrafficPersonAvatarOnlyPanel trafficPersonPanel = injector.createTrafficPersonAvatarOnlyPanel();
			trafficPersonPanel.init(person);
			addOrUpdatePerson(person.getPersonId(), trafficPersonPanel);
		}
		else if(event.is(PersonEventType.USER_CHANGED)){
			refreshTraffic();
		}
			
	}
	
	public CommandResultCallback<PersonListCommandResult> createPersonListCallback(){
		return new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				clear();
				for(GPerson person : result.getResults().getList()){ 
					TrafficPersonAvatarOnlyPanel trafficPersonPanel = injector.createTrafficPersonAvatarOnlyPanel();
					trafficPersonPanel.init(person);
					addPerson(person.getPersonId(), trafficPersonPanel);
				}
				EventBus.getInstance().addListener(TrafficPanel.this);
			}
		};
	} 
	
	
	class Bundle{
		private String personId;
		private Widget widget;
		
		Bundle(String personId, Widget widget){
			this.personId = personId;
			this.widget = widget;
		}
		public String getPersonId() {
			return personId;
		}
		public void setPersonId(String personId) {
			this.personId = personId;
		}
		public Widget getWidget() {
			return widget;
		}
		public void setWidget(Widget widget) {
			this.widget = widget;
		}
	}
	
//	public TrafficView() {
//		tabPanel.add(personPanel, "Traffic");
//		tabPanel.addStyleName("tt-fill");
//	}
	
	
	public void addOrUpdatePerson(String personId, Widget w) {
		//Remove the old
		for(Bundle b : list){
			if(b.getPersonId().equals(personId)){
				list.remove(b);
				personPanel.remove(b.getWidget());
				break;
			}
		}
		//Add to the head
		personPanel.insert(w, 0);
		list.addFirst(new Bundle(personId,w));
		
		//cull list
		if(list.size() > MAX_ENTRIES){
			Bundle doomed = list.removeLast();
			personPanel.remove(doomed.getWidget());
		}
	}

	public void addPerson(String personId, Widget w) {
		if(list.size() < MAX_ENTRIES){
			list.add(new Bundle(personId,w));
			personPanel.add(w);
		}
		else
			return; //dont add any more
	}
	
	public void clear(){
		personPanel.clear();
		list.clear();
	}

}
