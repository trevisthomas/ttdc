package org.ttdc.persistence.util;

import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class FilterFactoryForPostType {
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
	
//	private List<String> types;
//	
//	@Key
//	public FilterKey getKey(){
//		StandardFilterKey key = new StandardFilterKey();
//		key.addParameter(types);
//		return key;
//	}
//	
//	
//	@Factory
//	public Filter getFilter(){
//		
//		BooleanQuery totalQuery = new BooleanQuery();
//		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
//		if(types != null){
//			for(String type : types){
//				Term term = new Term("type", type);
//				Query query = new FuzzyQuery( term );
//				totalQuery.add(query,Occur.MUST);
//			}
//		}
//		
//		Filter filter = new QueryWrapperFilter( totalQuery );
//		
//		return filter;
//	}
//	public List<String> getTypes() {
//		return types;
//	}
//
//	public void setTypes(List<String> types) {
//		this.types = Collections.unmodifiableList(types) ;
//	}

	
	
}
