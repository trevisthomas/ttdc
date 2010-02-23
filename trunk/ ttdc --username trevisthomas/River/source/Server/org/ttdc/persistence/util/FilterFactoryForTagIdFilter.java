package org.ttdc.persistence.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class FilterFactoryForTagIdFilter {
	private List<String> tagIds;
	
	@Key
	public FilterKey getKey(){
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(tagIds);
		return key;
	}
	
	
	@Factory
	public Filter getFilter(){
		
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		
		for(String tagId : tagIds){
			Term term = new Term("tagId", tagId);
			Query query = new TermQuery( term );
			totalQuery.add(query,Occur.MUST);
		}
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
		
		/*
		//Term term = new Term("bridge", "yes");
		Term term = new Term("bridge", threadId);
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		
		return filter;
		*/
		
		/*
		Term term = new Term("bridge", "yes");
		Query query = new TermQuery( term );
		
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		totalQuery.add(query,Occur.MUST);
		
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
		*/
		
		/*
		Term term = new Term("post.root.postId", "18F31393-5285-4463-9BCA-44201B59E3DB");
		//Term term = new Term("body", "this movie is very, very bad.");
		
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		
		return filter;
		*/
		//WildcardFilter wildcardFilter = new WildcardFilter(); 
		/*
		Term term = new Term("post.root.postId", "18F31393-5285-4463-9BCA-44201B59E3DB");
		Query query = new TermQuery( term );
		
		
		
		Filter filter = new QueryWrapperFilter( query );
		
		return filter;
		*/
		
	}


	public List<String> getTagIds() {
		return tagIds;
	}


	public void setTagIds(List<String> tagIds) {
		this.tagIds = tagIds;
		//tagIds.addAll(tagIds);
	}

	
}
