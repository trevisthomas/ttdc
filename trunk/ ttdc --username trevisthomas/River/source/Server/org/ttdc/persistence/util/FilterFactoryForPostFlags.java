package org.ttdc.persistence.util;

import java.util.Collections;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;
import org.ttdc.gwt.shared.util.PostFlag;

public class FilterFactoryForPostFlags {
	private Set<PostFlag> flags;
	private Set<PostFlag> notFlags;
 	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(flags);
		key.addParameter(notFlags);		
		return key;
	}
	
	@Factory
	public Filter getFilter(){
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		if(flags != null){
			for(PostFlag flag : flags){
				Term term = new Term("flag", flag.name().toLowerCase());
				Query query = new FuzzyQuery( term );
				totalQuery.add(query,Occur.MUST);
			}
		}
		if(notFlags != null){
			for(PostFlag flag : notFlags){
				Term term = new Term("flag", flag.name().toLowerCase());
				Query query = new FuzzyQuery( term );
				totalQuery.add(query,Occur.MUST_NOT);
			}
		}
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
	}
	
	public Set<PostFlag> getFlags() {
		return flags;
	}

	public void setFlags(Set<PostFlag> flags) {
		this.flags = Collections.unmodifiableSet(flags) ;
	}

	public Set<PostFlag> getNotFlags() {
		return notFlags;
	}

	public void setNotFlags(Set<PostFlag> notFlags) {
		this.notFlags = Collections.unmodifiableSet(notFlags);
	}
}
