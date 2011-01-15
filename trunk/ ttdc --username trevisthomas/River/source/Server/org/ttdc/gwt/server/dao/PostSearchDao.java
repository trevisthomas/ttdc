package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.fullTextSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;

import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SearchSortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.util.DateScoreSorter;
import org.ttdc.persistence.util.BridgeForPostType;
import org.ttdc.persistence.util.EnglishAnalyzer;

public final class PostSearchDao extends FilteredPostPaginatedDaoBase{
	private final static Logger log = Logger.getLogger(PostSearchDao.class);
	
	private String phrase;
	private List<String> tagIds = new ArrayList<String>();
	private List<String> notTagIds = new ArrayList<String>();
	private String rootId;
	private String threadId;
	private StopWatch stopwatch = new StopWatch();
	private SearchSortBy sortOrder = SearchSortBy.BY_DATE; 
	private SortDirection sortDirection = SortDirection.DESC;
	private PostSearchType postSearchType;
	private boolean searchByTitle = false;
	private DateRange dateRange;
	private boolean invertFilterFuction = false;
	private Person creator = null;
	
	
	public PaginatedList<Post> search(){
		log.debug("load() started.");
		stopwatch.start();
		try{
			PaginatedList<Post> results;
			results = searchForPhrase();
			
			return results;
		}
		finally{
			stopwatch.stop();
			log.debug("load() completed in: " +stopwatch);
			stopwatch.reset();
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private PaginatedList<Post> searchForPhrase() {
		PaginatedList<Post> results = new PaginatedList<Post>();
		
		List<Post> list;
		org.apache.lucene.search.Query luceneQuery = createLuceneQuery();
		FullTextSession ftsess = fullTextSession();
		
		FullTextQuery ftquery = null;
		
//		if(tagIds.size() > 0 || notTagIds.size() > 0){
//			ftquery = createFullTextQuery(luceneQuery, ftsess);
//		
//			ftquery.enableFullTextFilter("postWithTagFilter")	
//				.setParameter("tagIds", tagIds)
//				.setParameter("notTagIds", notTagIds);
//			
//			addTopicOrConversationFilter(ftquery);
//			
//			boolean reverse = sortDirection != SortDirection.ASC; 
//			SortField sortField = null;
//			switch(sortOrder){
//				case BY_DATE:
//					sortField = new SortField("date",reverse);
//					break;
//				case POPULARITY:
//					sortField = new SortField("mass",reverse);
//					break;
//				case ALPHABETICAL:
//					sortField = new SortField("title_sort",reverse);
//					break;
//			}
//			if(sortField != null){
//				ftquery.setSort(new org.apache.lucene.search.Sort(sortField));
//			}
//			
//			addTypeFilter(ftquery);
//			addDateFilter(ftquery); // Probably shouldnt do this if it's already a part of the query (which it is when it's searching for blank)
//			
//			list = ftquery.list();
//		}
//		else{
//			ftquery = createFullTextQuery(luceneQuery, ftsess);
//			
//			addTopicOrConversationFilter(ftquery);
//			addTypeFilter(ftquery);
//			addDateFilter(ftquery);
//			
//			list = ftquery.list();
//		}
//		
		ftquery = createFullTextQuery(luceneQuery, ftsess);
		if(tagIds.size() > 0 || notTagIds.size() > 0){
			ftquery.enableFullTextFilter("postWithTagFilter")	
				.setParameter("tagIds", tagIds)
				.setParameter("notTagIds", notTagIds);
		}
		
		addTopicOrConversationFilter(ftquery);
		
		/*
		 * Normal filter operation excludes the content that matches the filter flag.  
		 * If you invert the filter operation it shows only the flagged content. 
		 * 
		 * Ex.  Normal operation with a MOVIE filter, movies would be excluded. In inverted
		 * 		everything but movies would be excluded.
		 */
		if(getFilterFlags().size() > 0){
			if(isInvertFilterFuction()){
				ftquery.enableFullTextFilter("postFlagFilter")
					.setParameter("flags", getFilterFlags());
			}
			else{
				ftquery.enableFullTextFilter("postFlagFilter")
					.setParameter("notFlags", getFilterFlags());
			}
		}
		
		if(getCreator() != null){
			ftquery.enableFullTextFilter("postCreatorFilter")
				.setParameter("creator", getCreator());
		}
		
		
		/*
		 *  Trevis, you removed these because you wanted things sorted by relevance.
		 *  Make sure this makes sense everywhere this is used/
		 */
		
		// 9/12/2010 putting them back because some places need this (user history view) should fix them  
		
		if(sortOrder != null){
			boolean reverse = sortDirection != SortDirection.ASC; 
			SortField sortField = null;
			switch(sortOrder){
				case BY_DATE:
					sortField = new SortField("date",reverse);
					break;
				case POPULARITY:
					sortField = new SortField("mass",reverse);
					break;
				case ALPHABETICAL:
					sortField = new SortField("title_sort",reverse);
					break;
				case RELEVANCE:
					//do nothing
					DateScoreSorter blendSorter = new DateScoreSorter();
					sortField = new SortField("postId", blendSorter, reverse);
					ftquery.setSort(new org.apache.lucene.search.Sort(sortField));
					break;
			}
			if(sortField != null){
				ftquery.setSort(new org.apache.lucene.search.Sort(sortField));
			}
			
		}
		
		//
//		BlendSorter blendSorter = new BlendSorter(new BlendSorter.FieldConverter(), 1.0f);
//		SortField sortField;
//		sortField = new SortField("postId",blendSorter);
//		ftquery.setSort(new org.apache.lucene.search.Sort(sortField));
		//
		
		//addDateFilter(ftquery); //Added Dec 29 2010 in an attempt to have results sorted by relevence then by date
		
		addTypeFilter(ftquery);
		//addDateFilter(ftquery); // Probably shouldnt do this if it's already a part of the query (which it is when it's searching for blank)
		
		list = ftquery.list();
		
		
		results.setPhrase(getPhrase());
		
		if(list.size() > 0)
		{	
			results.setTotalResults(ftquery.getResultSize());
			results.setList(list);
			results.setCurrentPage(getCurrentPage());
			results.setPageSize(getPageSize());
		}
		log.debug("Results: "+results.toString());
		
		return results;
	}


	/*
	 * Uses the lucene query to make a FullTextQuery object for the session
	 * 
	 */
	private FullTextQuery createFullTextQuery(org.apache.lucene.search.Query luceneQuery, FullTextSession ftsess) {
		FullTextQuery ftquery;
		if(getPageSize() > 0){
			ftquery = ftsess.createFullTextQuery(luceneQuery, Post.class)
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize());
		}
		else{
			ftquery = ftsess.createFullTextQuery(luceneQuery, Post.class);
		}
		return ftquery;
	}

	private void addDateFilter(FullTextQuery ftquery) {
		if(dateRange != null && dateRange.isValidDateRange()){
			ftquery.enableFullTextFilter("postDateRangeFilter").setParameter("dateRange", dateRange);
		}
	}

	private void addTypeFilter(FullTextQuery ftquery) {
		if(postSearchType == null) return;
		
//		List<String> types = new ArrayList<String>();
		switch(postSearchType){
			case CONVERSATIONS:
				ftquery.enableFullTextFilter("postWithTypeFilter").setParameter("type", BridgeForPostType.CONVERSATION);
				break;
			case TOPICS:
				ftquery.enableFullTextFilter("postWithTypeFilter").setParameter("type", BridgeForPostType.TOPIC);
				break;
			case REPLIES:
				ftquery.enableFullTextFilter("postWithTypeFilter").setParameter("type", BridgeForPostType.REPLY);
				break;
			case NOT_REPLIES:
				//At the moment this problem isnt solved.
				ftquery.enableFullTextFilter("postWithTypeFilter").setParameter("type", BridgeForPostType.TOPIC);
				//ftquery.enableFullTextFilter("postWithType").setParameter("type", BridgeForPostType.CONVERSATION);
				break;
			case ALL:
			default:
		}
//		if(types.size() > 0){
//			ftquery.enableFullTextFilter("postWithType").setParameter("types", types);
//		}
		
	}

	private void addTopicOrConversationFilter(FullTextQuery ftquery) {
		if(rootId != null){
			ftquery.enableFullTextFilter("postWithRootIdFilter").setParameter("rootId", rootId);
		}
		else if(threadId != null){
			ftquery.enableFullTextFilter("postWithThreadIdFilter").setParameter("threadId", threadId);
		}
	}
	
//	private org.apache.lucene.search.Query createLuceneQuery() {
//		log.debug("User query: "+getPhrase());
//		String preparedPhrase = LuceneUtils.prepPhraseForLucene(getPhrase(),getDateRange());
//		
////		QueryParser parser = new QueryParser(lucineIndexName, new StandardAnalyzer());
////		QueryParser parser = new QueryParser(lucineIndexName, new KeywordAnalyzer());
////		QueryParser parser = new QueryParser(lucineIndexName, new EnglishAnalyzer());
//		QueryParser parser;
//		if(isSearchByTitle()){
//			parser = new QueryParser("title", new EnglishAnalyzer());
//		}
//		else{
//			parser = new MultiFieldQueryParser( new String[]{"body","title","creator","topic"}/*This is an array of which indexes to search*/, 
//					  new EnglishAnalyzer());
//		}
//		
//		//parser.setAllowLeadingWildcard(true);//Tried this for blanks but got a weird exception
//		
//		org.apache.lucene.search.Query luceneQuery;
//		
//		try {
//			luceneQuery = parser.parse(preparedPhrase);
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
//		
//		log.debug("Lucene Query:"+luceneQuery.toString());
//		return luceneQuery;
//	}
	
//	private org.apache.lucene.search.Query createLuceneQuery() {
//		log.debug("User query: "+getPhrase());
////		String preparedPhrase = LuceneUtils.prepPhraseForLucene(getPhrase(),getDateRange());
////		
//////		QueryParser parser = new QueryParser(lucineIndexName, new StandardAnalyzer());
//////		QueryParser parser = new QueryParser(lucineIndexName, new KeywordAnalyzer());
//////		QueryParser parser = new QueryParser(lucineIndexName, new EnglishAnalyzer());
////		QueryParser parser;
////		if(isSearchByTitle()){
////			parser = new QueryParser("title", new EnglishAnalyzer());
////		}
////		else{
////			parser = new MultiFieldQueryParser( new String[]{"body","title","creator","topic"}/*This is an array of which indexes to search*/, 
////					  new EnglishAnalyzer());
////		}
////		
////		//parser.set
////		//parser.setAllowLeadingWildcard(true);//Tried this for blanks but got a weird exception
////		
////		org.apache.lucene.search.Query luceneQuery;
////		
////		try {
////			luceneQuery = parser.parse(preparedPhrase);
////		} catch (ParseException e) {
////			throw new RuntimeException(e);
////		}
//		////
//		///
//		///
//		
//		
//		//MultiFieldQueryParser.
//		
////		MultiFieldQueryParser mfParser = new MultiFieldQueryParser(new String[]{"body","title","creator","topic"}/*This is an array of which indexes to search*/, 
////					  new EnglishAnalyzer());
//		
//		StringTokenizer st = new StringTokenizer(getPhrase().toLowerCase().trim()," ");
//		String newPhrase = "";
//			while(st.hasMoreTokens()){
//				String word = st.nextToken();
//				if(isFilteredSearchWords(word))
//					continue;
//				newPhrase+=word;
//				newPhrase+=" ";
////				query.add(new Term("body",word));
//	//			query.add(new Term("title",word));
//	//			query.add(new Term("creator",word));
//	//			query.add(new Term("topic",word));
//			}
//		
//		
//		org.apache.lucene.search.Query luceneQuery;
//		try{
//			luceneQuery = MultiFieldQueryParser.parse(newPhrase, new String[]{"body" /*,"title","creator","topic"*/}, 
//					new BooleanClause.Occur[]{BooleanClause.Occur.MUST},  new SimpleAnalyzer());
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
//		
//		//luceneQuery = Query.mergeBooleanQueries(arg0);
//		
//		log.debug("Lucene Query: "+luceneQuery.toString());
//		return luceneQuery;
//		
//		
////		PhraseQuery query = new PhraseQuery();
////		StringTokenizer st = new StringTokenizer(getPhrase().toLowerCase().trim()," ");
////		while(st.hasMoreTokens()){
////			String word = st.nextToken();
////			if(isFilteredSearchWords(word))
////				continue;
////			query.add(new Term("body",word));
//////			query.add(new Term("title",word));
//////			query.add(new Term("creator",word));
//////			query.add(new Term("topic",word));
////		}
////		query.setSlop(8);
////		
////		log.debug("Lucene Query: "+query.toString());
////		return query;
//		
//		
//	}
	
	private org.apache.lucene.search.Query createLuceneQuery() {
		try{
			
			log.debug("User query: \""+getPhrase()+"\"");
			List<PhraseQuery> phraseQueries = new ArrayList<PhraseQuery>();
			
			String phrase = getPhrase().toLowerCase().trim();
			
			if(!phrase.isEmpty()){
				if(searchByTitle){
					phraseQueries.add(buildPhraseQuery(phrase, "title", 4, 4));
				}
				else{
					phraseQueries.add(buildPhraseQuery(phrase, "body", 8, 1));
					phraseQueries.add(buildPhraseQuery(phrase, "title", 4, 4));
//					phraseQueries.add(buildPhraseQuery(phrase, "creator", 0, 8));
//					phraseQueries.add(buildPhraseQuery(phrase, "topic", 0, 1));
				}
			}
			if(phraseQueries.size() > 0){
				StringBuilder sb = new StringBuilder();
				sb.append("(");
				for(PhraseQuery pq : phraseQueries){
					
					//ESCAPE CHARACTERS TREVIS...
					sb.append(pq.toString());
				}
				sb.append(")");
				phrase = sb.toString();
			}
			
			QueryParser parser = new QueryParser("body", new StandardAnalyzer());//This default will always be overridden but it is required by the parser
			String preparedPhrase = LuceneUtils.addDateRangeToLuceneQuery2(phrase,getDateRange());
			
			org.apache.lucene.search.Query luceneQuery = parser.parse(preparedPhrase);
			
			log.debug("Lucene Query: "+luceneQuery.toString());
			return luceneQuery;
			
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
	}

	private PhraseQuery buildPhraseQuery(String phrase, String field, int slop, float boost) {
		PhraseQuery query = new PhraseQuery();
		
		StringTokenizer st = new StringTokenizer(phrase.toLowerCase()," ");
		if(st.countTokens() == 0)
			return null;
		while(st.hasMoreTokens()){
			String word = st.nextToken();
			if(isFilteredSearchWords(word))
				continue;
			query.add(new Term(field,word));
		}
		query.setSlop(slop);
		query.setBoost(boost);
		return query;
	}
			
	
	private final static List<String> filterwords = Arrays.asList("a","all","am","an","and","any","are","as","at","be",
			"but","can","did","do","does","for","from","had","has","have","here","how",
		    "i","if","in","is","it","no","not","of","on","or","so","that","the","then",
		    "there","this","to","too","up","use","what","when","where","who","why","you");
	
	private boolean isFilteredSearchWords(String word){
		return filterwords.contains(word);
	}

	public void addTagId(String tagId){
		tagIds.add(tagId);
	}
	
	public void addNotTagId(String tagId){
		notTagIds.add(tagId);
	}
	
	public List<String> getTagIdList(){
		return Collections.unmodifiableList(tagIds);
	}
		
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		if(StringUtils.isEmpty(phrase)){
			this.phrase = "";
		}
		else{
			this.phrase = phrase;
		}
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public List<String> getNotTagIdList() {
		return Collections.unmodifiableList(notTagIds);
	}

	public void setNotTagIdList(List<String> notTagIds) {
		this.notTagIds.clear();
		if(notTagIds != null)
			this.notTagIds.addAll(notTagIds);
	}

	public void setTagIdList(List<String> tagIds) {
		this.tagIds.clear();
		if(tagIds != null)
			this.tagIds.addAll(tagIds);

	}

	public SearchSortBy getSortOrder() {
		return sortOrder;
	}

	public void setSortBy(SearchSortBy sortOrder) {
		this.sortOrder = sortOrder;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public boolean isSearchByTitle() {
		return searchByTitle;
	}

	public void setSearchByTitle(boolean searchByTitle) {
		this.searchByTitle = searchByTitle;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	public DateRange getDateRange() {
		return dateRange;
	}

	public void setPostSearchType(PostSearchType postSearchType) {
		this.postSearchType = postSearchType;
	}

	public PostSearchType getPostSearchType() {
		return postSearchType;
	}

	public boolean isInvertFilterFuction() {
		return invertFilterFuction;
	}

	public void setInvertFilterFuction(boolean invertFilterFuction) {
		this.invertFilterFuction = invertFilterFuction;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	
	
	
}
