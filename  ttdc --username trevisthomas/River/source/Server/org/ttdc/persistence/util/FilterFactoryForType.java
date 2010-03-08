package org.ttdc.persistence.util;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class FilterFactoryForType {
	private String type;
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(type);
		return key;
	}
	
	@Factory
	public Filter getThreadFilter(){
		Term term = new Term("type", type);
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		return filter;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
