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

public class FilterFactoryTagIsTitle {
	private boolean title;
	
	public boolean isTitle() {
		return title;
	}
	public void setTitle(boolean title) {
		this.title = title;
	}
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(title);
		return key;
	}
	
	@Factory
	public Filter getThreadFilter(){
		Term term;
		if(isTitle())
			term = new Term("title", "yes");
		else
			term = new Term("title", "no");
		
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		return filter;
	}
	
}
