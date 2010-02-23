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
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class FilterFactoryForTokenizedTagIds {
	private List<String> tagIds;
	private List<String> notTagIds;
 	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(tagIds);
		key.addParameter(notTagIds);		
		return key;
	}
	
	
	@Factory
	public Filter getFilter(){
		
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		if(tagIds != null){
			for(String tagId : tagIds){
				Term term = new Term("tagIds", tagId);
				Query query = new FuzzyQuery( term );
				totalQuery.add(query,Occur.MUST);
			}
		}
		if(notTagIds != null){
			for(String tagId : notTagIds){
				Term term = new Term("tagIds", tagId);
				Query query = new FuzzyQuery( term );
				totalQuery.add(query,Occur.MUST_NOT);
			}
		}
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
	}
	public List<String> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<String> tagIds) {
		this.tagIds = Collections.unmodifiableList(tagIds) ;
	}

	public List<String> getNotTagIds() {
		return notTagIds;
	}

	public void setNotTagIds(List<String> notTagIds) {
		this.notTagIds = notTagIds;
	}
}
