package org.ttdc.gwt.shared.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortOrder;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchPostsCommand extends Command<SearchPostsCommandResult> implements IsSerializable{
	private List<String> tagIdList = new ArrayList<String>();
	private List<String> notTagIdList = new ArrayList<String>();
	private String phrase = "";
	private boolean titleSearch = false;
	//private boolean conversationsOnly = false;
	private PostSearchType postSearchType;
	private boolean reviewsOnly = false;
	private boolean nonReviewsOnly = false;
	private Date startDate;
	private Date endDate;

	private String rootId;
	private String threadId;
	
	private SortOrder sortOrder = null;
	private SortDirection sortDirection = null;
	
	private int pageNumber = 1;
	private int pageSize = -1;
	
	public SearchPostsCommand(){};
	
	public SearchPostsCommand(String phrase, List<String> tagIds){
		setTagIdList(tagIds);
		this.phrase = phrase;
	}
	
	public SearchPostsCommand(String phrase){
		this.phrase = phrase;
	}
	
	public SearchPostsCommand(List<String> tagIds){
		setTagIdList(tagIds);
		this.phrase = "";
	}

	public List<String> getTagIdList() {
		return tagIdList;
	}

	public void setTagIdList(List<String> tagIdList) {
		if(tagIdList != null)
			this.tagIdList = tagIdList;
		else
			this.tagIdList.clear();
	}
	
	public void addTagId(String tagId){
		tagIdList.add(tagId);
	}
	
	public void addNotTagId(String tagId){
		notTagIdList.add(tagId);
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public List<String> getNotTagIdList() {
		return notTagIdList;
	}

	public void setNotTagIdList(List<String> notTagIdList) {
		if(notTagIdList != null)
			this.notTagIdList = notTagIdList;
		else
			this.notTagIdList.clear();
	}

	public boolean isTitleSearch() {
		return titleSearch;
	}

	public void setTitleSearch(boolean titlesOnly) {
		this.titleSearch = titlesOnly;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

//	public boolean isSortByDate() {
//		return SortType.BY_DATE.equals(sortOrder);
//	}
//
//	public boolean isSortByPopularity() {
//		return SortType.BY_POPULARITY.equals(sortOrder);
//	}

//	public SortType getSortOrder() {
//		return sortOrder;
//	}
//
//	public void setSortOrder(SortType sortOrder) {
//		this.sortOrder = sortOrder;
//	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void setReviewsOnly(boolean reviewsOnly) {
		this.reviewsOnly = reviewsOnly;
	}

	public boolean isReviewsOnly() {
		return reviewsOnly;
	}
	
	public boolean isNonReviewsOnly() {
		return nonReviewsOnly;
	}

	public void setNonReviewsOnly(boolean nonReviewsOnly) {
		this.nonReviewsOnly = nonReviewsOnly;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date fromDate) {
		this.startDate = fromDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date toDate) {
		this.endDate = toDate;
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

	public PostSearchType getPostSearchType() {
		return postSearchType;
	}

	public void setPostSearchType(PostSearchType postSearchType) {
		this.postSearchType = postSearchType;
	}

	
}