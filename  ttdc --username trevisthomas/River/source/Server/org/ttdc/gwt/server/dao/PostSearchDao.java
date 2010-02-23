package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.fullTextSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.SortField;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;

import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortOrder;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.util.BridgeForPostType;
import org.ttdc.persistence.util.EnglishAnalyzer;

public final class PostSearchDao extends PaginatedDaoBase{
	private final static Logger log = Logger.getLogger(PostSearchDao.class);
	
	
	private String phrase;
	private List<String> tagIds = new ArrayList<String>();
	private List<String> notTagIds = new ArrayList<String>();
	private String rootId;
	private String threadId;
	private StopWatch stopwatch = new StopWatch();
	private SortOrder sortOrder = SortOrder.BY_DATE; 
	private SortDirection sortDirection = SortDirection.DESC;
	private PostSearchType postSearchType;
	private boolean searchByTitle = false;
	private DateRange dateRange;
	
	
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
		
		if(tagIds.size() > 0 || notTagIds.size() > 0){
			ftquery = ftsess.createFullTextQuery(luceneQuery, Post.class)
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize());
		
			ftquery.enableFullTextFilter("postWithTagFilter")	
				.setParameter("tagIds", tagIds)
				.setParameter("notTagIds", notTagIds);
			
			addTopicOrConversationFilter(ftquery);
			
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
			}
			if(sortField != null){
				ftquery.setSort(new org.apache.lucene.search.Sort(sortField));
			}
			
			addTypeFilter(ftquery);
			addDateFilter(ftquery); // Probably shouldnt do this if it's already a part of the query (which it is when it's searching for blank)
			
			list = ftquery.list();
		}
		else{
			ftquery = ftsess.createFullTextQuery(luceneQuery, Post.class)
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize());
			
			addTopicOrConversationFilter(ftquery);
			addTypeFilter(ftquery);
			addDateFilter(ftquery);
			
			list = ftquery.list();
		}
		
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
	
	private org.apache.lucene.search.Query createLuceneQuery() {
		log.debug("User query: "+getPhrase());
		String preparedPhrase = LuceneUtils.prepPhraseForLucene(getPhrase(),getDateRange());
		
//		QueryParser parser = new QueryParser(lucineIndexName, new StandardAnalyzer());
//		QueryParser parser = new QueryParser(lucineIndexName, new KeywordAnalyzer());
//		QueryParser parser = new QueryParser(lucineIndexName, new EnglishAnalyzer());
		QueryParser parser;
		if(isSearchByTitle()){
			parser = new QueryParser("title", new EnglishAnalyzer());
		}
		else{
			parser = new MultiFieldQueryParser( new String[]{"body","title","creator","topic"}/*This is an array of which indexes to search*/, 
					  new EnglishAnalyzer());
		}
		
		//parser.setAllowLeadingWildcard(true);//Tried this for blanks but got a weird exception
		
		org.apache.lucene.search.Query luceneQuery;
		
		try {
			luceneQuery = parser.parse(preparedPhrase);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		log.debug("Lucene Query:"+luceneQuery.toString());
		return luceneQuery;
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

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
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
}
