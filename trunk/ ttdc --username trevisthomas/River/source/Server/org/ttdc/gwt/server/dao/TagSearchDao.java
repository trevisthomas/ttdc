package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.fullTextSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.SortField;
import org.hibernate.search.FullTextQuery;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.util.EnglishAnalyzer;

/**
 * Search or browse tags.  A lucene full text search is performed if there are not 
 * filtering restrictions if you limit by 
 * 
 * @author Trevis
 *
 */
final public class TagSearchDao {
	private final static Logger log = Logger.getLogger(TagSearchDao.class);
	
	private final static int DEFAULT_PAGE_SIZE = 20;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int currentPage = 1;
	private String phrase;
	private final List<String> unionTagIdList = new ArrayList<String>();
	private final List<String> tagTypeFilters = new ArrayList<String>();
	private final List<String> defaultTagTypeFilters = Collections.unmodifiableList(Arrays.asList(Tag.TYPE_TOPIC));
	private final List<String> excludeTagIdList = new ArrayList<String>();
	private final StopWatch stopwatch = new StopWatch();
	private boolean titlesOnly = false;
	private boolean tagsOnly = false; // Ignores Title only tag associations.
	private DateRange dateRange = null;
	
	private final List<String> allTagTypeFilters = Collections.unmodifiableList(Arrays.asList(
			Tag.TYPE_AVERAGE_RATING,
			Tag.TYPE_RATING,
			Tag.TYPE_TOPIC));
	

	public PaginatedList<Tag> search(){
		PaginatedList<Tag> results;
		stopwatch.start();
		try{
			results = fullTextSearchForPhrase();
		}
		finally{
			stopwatch.stop();
			log.debug("load() completed in: " +stopwatch);
		}
		
		return results;
	}
	


	/* 
	 * Think about this.  
	 * 
	 */
	private boolean validateForTagsWithTagsSearch(){
		if(unionTagIdList.size() > 0 && excludeTagIdList.size() > 0)
			throw new RuntimeException("A search can exclude a list of tags or search for tags with a post union of a list of tags. Not both.");
		else if(unionTagIdList.size() > 0 || excludeTagIdList.size() > 0)
			return true;
		else
			return false;
		
	}
	
	//Share this if it works. The thing is  
	private void addDateFilter(FullTextQuery ftquery) {
		if(dateRange != null && dateRange.isValidDateRange()){
			ftquery.enableFullTextFilter("tagDateRangeFilter").setParameter("dateRange", dateRange);
		}
	}
	
	private PaginatedList<Tag> fullTextSearchForPhrase(){
		PaginatedList<Tag> results = new PaginatedList<Tag>();
		
		log.debug("Searching for: "+phrase);
		
		
		org.apache.lucene.search.Query luceneQuery = createLuceneQuery();
		FullTextQuery ftquery = fullTextSession().createFullTextQuery(luceneQuery, Tag.class)
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize());
		
		
		ftquery.enableFullTextFilter("tagWithTagFilter")	
			.setParameter("tagIds", unionTagIdList)
			.setParameter("notTagIds", excludeTagIdList);
		
		
		ftquery.enableFullTextFilter("tagTypeExcludeFilter")
			.setParameter("excludeTypes", getInternalTypeFilterList());	
		
		if(isTitlesOnly()){
			ftquery.enableFullTextFilter("tagIsTitleFilter")
				.setParameter("title", true);
		}
		if(isTagsOnly()){
			ftquery.enableFullTextFilter("tagIsTitleFilter")
				.setParameter("title", false);
		}
		
		ftquery.setSort(new org.apache.lucene.search.Sort(new SortField("mass_tag",true)));//todo consider renaming this to just mass?
		
		addDateFilter(ftquery);
		
		@SuppressWarnings("unchecked") 
		List<Tag> list = ftquery.list();
		//TODO: MASS
		
		results.setPhrase(phrase);
		results.setTotalResults(ftquery.getResultSize());
		results.setList(list);
		results.setCurrentPage(getCurrentPage());
		results.setPageSize(pageSize);
		
		log.debug("Results: "+results.toString());
		
		return results;
	}
	

	private org.apache.lucene.search.Query createLuceneQuery() {
		String preparedPhrase = LuceneUtils.prepPhraseForLucene2(getPhrase(),getDateRange());
		org.apache.lucene.search.Query luceneQuery;
		//QueryParser parser = new MultiFieldQueryParser( new String[]{"topic","creator"}, new EnglishAnalyzer());
		QueryParser parser = new MultiFieldQueryParser( new String[]{"topic"}, new EnglishAnalyzer());
		try {
			luceneQuery = parser.parse(preparedPhrase);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		log.debug("Lucene Query:"+luceneQuery.toString());
		return luceneQuery;
	}
	
	private int calculatePageStartIndex() {
		return (getCurrentPage() - 1)*getPageSize();
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public void addTagId(String tagId){
		unionTagIdList.add(tagId);
	}
	
	public void setTagIds(List<String> tagIds){
		unionTagIdList.clear();
		unionTagIdList.addAll(tagIds);
	}
	
	public void addFilterForTagType(String type){
		tagTypeFilters.add(type);	
	}
	public List<String> getTagIds(){
		return Collections.unmodifiableList(unionTagIdList);
	}

	private List<String> getInternalTypeFilterList(){
		List<String> filters;
		List<String> source;
		if(tagTypeFilters.size() > 0){
			source = tagTypeFilters;
			
		}
		else{
			source = defaultTagTypeFilters;
		}
		
		filters = new ArrayList<String>();
		filters.addAll(allTagTypeFilters);
		filters.removeAll(source);
		
		return Collections.unmodifiableList(filters);
	}
	
	public List<String> getTagTypeFilterList() {
		if(tagTypeFilters.size() > 0){
			return tagTypeFilters;
		}
		else{
			return defaultTagTypeFilters;
		}
	}

	public List<String> getExcludeTagIdList() {
		return Collections.unmodifiableList(excludeTagIdList);
	}
	
	public void addTagIdExclude(String excludeTagId){
		excludeTagIdList.add(excludeTagId);
	}
	public boolean isTitlesOnly() {
		return titlesOnly;
	}
	public void setTitlesOnly(boolean titlesOnly) {
		this.titlesOnly = titlesOnly;
	}

	public boolean isTagsOnly() {
		return tagsOnly;
	}

	public void setTagsOnly(boolean tagsOnly) {
		this.tagsOnly = tagsOnly;
	}


	public DateRange getDateRange() {
		return dateRange;
	}


	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	
}
