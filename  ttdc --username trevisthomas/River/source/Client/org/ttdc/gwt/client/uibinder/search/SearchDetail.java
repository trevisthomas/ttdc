package org.ttdc.gwt.client.uibinder.search;

import org.ttdc.gwt.client.presenters.util.DateRangeLite;

public interface SearchDetail {

	public String getPerson();

	public String getTags();

	public String getThreadTitle() ;
	
	public String getPhrase();

	public DateRangeLite getDateRange();

}
