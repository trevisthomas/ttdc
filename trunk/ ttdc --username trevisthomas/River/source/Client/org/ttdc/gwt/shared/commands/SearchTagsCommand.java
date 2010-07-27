package org.ttdc.gwt.shared.commands;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Search tags for user entered values.
 * 
 * @author Trevis
 *
 */
public class SearchTagsCommand extends Command<SearchTagsCommandResult> implements IsSerializable{
	private String phrase;
	private int pageNumber = 1;
	private List<String> tagIdList = new ArrayList<String>();
	private List<String> tagIdExcludeList = new ArrayList<String>();
	private Date startDate;
	private Date endDate;
	private TagSearchMode mode = TagSearchMode.SEARCH;
	
	public enum TagSearchMode{
		SEARCH,
		UNION
	}
	
	public SearchTagsCommand(){};
	
	public SearchTagsCommand(List<String> tagIds){
		if(tagIds != null)
			this.tagIdList.addAll(tagIds);
	}
	
	public SearchTagsCommand(String phrase){
		this.phrase = phrase;
		this.pageNumber = 1;
	}
	
	public SearchTagsCommand(String phrase, int pageNumber){
		this.phrase = phrase;
		this.pageNumber = pageNumber;
	}
	
	public String getPhrase() {
		return phrase;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public List<String> getTagIdList() {
		return tagIdList;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public TagSearchMode getMode() {
		return mode;
	}

	public void setMode(TagSearchMode mode) {
		this.mode = mode;
	}

	public List<String> getTagIdExcludeList() {
		return tagIdExcludeList;
	}

	public void addTagIdExclude(String tagId){
		tagIdExcludeList.add(tagId);
	}

	public void setExcludeTagIds(List<String> tagIds) {
		tagIdExcludeList.addAll(tagIds);
	}
	
}
