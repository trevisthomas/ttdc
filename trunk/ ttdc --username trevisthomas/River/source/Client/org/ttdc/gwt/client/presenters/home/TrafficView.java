package org.ttdc.gwt.client.presenters.home;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrafficView implements TrafficPresenter.View{
	private final TabPanel tabPanel = new TabPanel();
	private final VerticalPanel personPanel = new VerticalPanel();
	private final Map<String, Widget> map = new TrafficMap<String, Widget>(MAX_ENTRIES);
	
	
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
		if(map.containsKey(personId)){
			Widget tmp = map.remove(personId);
			personPanel.remove(tmp);
		}	
		else{
			personPanel.remove(map.size()-1);
		}
		
		map.put(personId, w);
		personPanel.insert(w, 0);
		
	}

	@Override
	public void addPerson(String personId, Widget w) {
		if(map.size() < MAX_ENTRIES){
			personPanel.add(w);
			map.put(personId,w);
		}
		else
			return; //dont add any more
	}
	
	@Override
	public void clear(){
		personPanel.clear();
		map.clear();
	}
	/**
	 * 
	 * This custom LinkedHashMap caps the number of items that it will hold.
	 *
	 * @param <K>
	 * @param <V>
	 */
	class TrafficMap<K, V> extends LinkedHashMap<K, V>{
		private int maxEntries;
		public TrafficMap(int maxEntries) {
			this.maxEntries = maxEntries;
		}
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
			return size() > maxEntries;
		}
	}
}
