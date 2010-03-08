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
import org.ttdc.persistence.objects.Person;

public class FilterFactoryCreator {
	private Person creator;
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(creator.getPersonId());
		return key;
	}
	
	@Factory
	public Filter getThreadFilter(){
		Term term = new Term("creator", creator.getLogin().toLowerCase());
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		return filter;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}
}
