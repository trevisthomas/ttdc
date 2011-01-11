package org.ttdc.gwt.client.presenters.home;


import java.util.LinkedList;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrafficView implements TrafficPresenter.View{
	private final TabPanel tabPanel = new TabPanel();
	private final VerticalPanel personPanel = new VerticalPanel();
	private final LinkedList<Bundle> list = new LinkedList<Bundle>();
	
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
	
	public TrafficView() {
		tabPanel.add(personPanel, "Traffic");
		tabPanel.addStyleName("tt-fill");
	}
	@Override
	public Widget getWidget() {
		tabPanel.selectTab(0);
		return tabPanel;
	}
	
	@Override
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

	@Override
	public void addPerson(String personId, Widget w) {
		if(list.size() < MAX_ENTRIES){
			list.add(new Bundle(personId,w));
			personPanel.add(w);
		}
		else
			return; //dont add any more
	}
	
	@Override
	public void clear(){
		personPanel.clear();
		list.clear();
	}
}
