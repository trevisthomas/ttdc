package org.ttdc.persistence.util;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.RangeFilter;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.ChainedFilter;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;
import org.ttdc.gwt.server.dao.DateRange;
import org.ttdc.gwt.server.dao.LuceneUtils;

public class FilterFactoryForPostDateRange {
	
	private DateRange dateRange;
	private String value;
	
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(value);
		return key;
	}
	
	@Factory
	public Filter getThreadFilter(){
		ChainedFilter chainedFilter = new ChainedFilter();
		chainedFilter.addFilter(RangeFilter.More("date", LuceneUtils.luceneDateFormater.format(dateRange.getStartDate())));
		chainedFilter.addFilter(RangeFilter.Less("date", LuceneUtils.luceneDateFormater.format(dateRange.getEndDate())));
		return chainedFilter;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
		value = String.format("[%s TO %s]",LuceneUtils.luceneDateFormater.format(dateRange.getStartDate()),LuceneUtils.luceneDateFormater.format(dateRange.getEndDate()));
	}
}
