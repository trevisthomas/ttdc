package org.ttdc.gwt.client.uibinder.search;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.presenters.util.DateRangeLite;

/**
 * The idea is that this class is used as the to capture the paramerers used in a search
 * and notify an observer with the readble values.  I did this because SearchBoxPanel 
 * gets all of this information already but SearchResutsPanel wants to display it and i didnt
 * want to retrieve it in a 2nd place.
 * 
 * I was going to make a data class and a listener collection but decided to use 
 * an interface instead for the data and just pass back the collection instance itself as the 
 * data,
 *
 */
public class SearchDetailListenerSmartCollection implements SearchDetail{
	private List<SearchDetailListener> listeners = new ArrayList<SearchDetailListener>();
	
	public void addListener(final SearchDetailListener listener){
		listeners.add(listener);
		maybeNotifyListeners();
	}
	
	private String person = null;
	private String tags = null;
	private String threadTitle = null;
	private String phrase = null;
	private DateRangeLite dateRange = null;

	/**
	 * This method returns true when all of the fields are non null
	 * @return
	 */
	public boolean isPopulated(){
		if(person != null && tags != null && threadTitle != null && phrase != null && dateRange != null){
			return true;
		}
		else{
			return false;
		}
	}

	private void maybeNotifyListeners(){
		if(isPopulated() && listeners.size() > 0){ 
			for(SearchDetailListener listener : listeners){
				listener.onSearchDetail(this);
			}
			//TODO: Consider clearing everything out.
		}
	}
	
	@Override
	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
		maybeNotifyListeners();
	}

	@Override
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
		maybeNotifyListeners();
	}

	@Override
	public String getThreadTitle() {
		return threadTitle;
	}

	public void setThreadTitle(String threadTitle) {
		this.threadTitle = threadTitle;
		maybeNotifyListeners();
	}

	@Override
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
		maybeNotifyListeners();
	}

	@Override
	public DateRangeLite getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRangeLite dateRange) {
		this.dateRange = dateRange;
		maybeNotifyListeners();
	}
	
}
