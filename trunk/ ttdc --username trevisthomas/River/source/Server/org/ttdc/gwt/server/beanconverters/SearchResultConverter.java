package org.ttdc.gwt.server.beanconverters;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

public class SearchResultConverter {

	public static PaginatedList<GTag> convertSearchResults(PaginatedList<Tag> results){
		PaginatedList<GTag> gResults = new PaginatedList<GTag>();
		
		gResults.setCurrentPage(results.getCurrentPage());
		gResults.setPageSize(results.getPageSize());
		gResults.setPhrase(results.getPhrase());
		gResults.setTotalResults(results.getTotalResults());
		gResults.setList(GenericBeanConverter.convertTags(results.getList()));
		
		return gResults;
	}
}
