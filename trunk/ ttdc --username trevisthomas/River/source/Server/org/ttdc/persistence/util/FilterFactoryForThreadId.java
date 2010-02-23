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

public class FilterFactoryForThreadId {
private String threadId;
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(threadId);
		return key;
	}
	
	
	@Factory
	public Filter getThreadFilter(){
		Term term = new Term("threadId", threadId);
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		return filter;
	}


	public String getThreadId() {
		return threadId;
	}


	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
}
