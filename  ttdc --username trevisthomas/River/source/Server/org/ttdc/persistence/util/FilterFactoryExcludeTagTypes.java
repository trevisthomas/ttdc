package org.ttdc.persistence.util;

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

public class FilterFactoryExcludeTagTypes {
	private List<String> excludeTypes;
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(excludeTypes);
		return key;
	}
	
	
	@Factory
	public Filter getFilter(){
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		
		if(excludeTypes != null){
			for(String type : excludeTypes){
				Term term = new Term("type", type);
				Query query = new TermQuery( term );
				totalQuery.add(query,Occur.MUST_NOT);
			}
		}
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
	}
	
	public List<String> getExcludeTypes() {
		return excludeTypes;
	}


	public void setExcludeTypes(List<String> excludeTypes) {
		this.excludeTypes = excludeTypes;
	}

}
