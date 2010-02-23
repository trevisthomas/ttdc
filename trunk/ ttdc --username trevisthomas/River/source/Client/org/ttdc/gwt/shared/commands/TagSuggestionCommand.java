package org.ttdc.gwt.shared.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;

import com.google.gwt.user.client.ui.SuggestOracle;

public class TagSuggestionCommand extends Command<TagSuggestionCommandResult>{
	private TagSuggestionCommandMode mode;
	private SuggestOracle.Request request;
	private List<String> unionTagIdList = new ArrayList<String>();
	private List<String> excludeTagIdList = new ArrayList<String>();
	private boolean loadDefault;
		
	public TagSuggestionCommand() {}
	
	public TagSuggestionCommand(TagSuggestionCommandMode mode, SuggestOracle.Request request) {
		this.mode = mode;
		this.request = request;
	}

	public SuggestOracle.Request getRequest() {
		return request;
	}
	
	public List<String> getExcludeTagIdList() {
		return Collections.unmodifiableList(excludeTagIdList);
	}
	
	
	
	public void addTagIdExclude(String excludeTagId){
		excludeTagIdList.add(excludeTagId);
	}
	
	public void addTagIdUnion(String tagId){
		unionTagIdList.add(tagId);
	}
	
	public List<String> getUnionTagIdList(){
		return Collections.unmodifiableList(unionTagIdList);
	}

	public TagSuggestionCommandMode getMode() {
		return mode;
	}

	public void setUnionTagIdList(List<String> unionTagIdList) {
		this.unionTagIdList = unionTagIdList;
	}

	public void setExcludeTagIdList(List<String> excludeTagIdList) {
		this.excludeTagIdList = excludeTagIdList;
	}

	public boolean isLoadDefault() {
		return loadDefault;
	}

	public void setLoadDefault(boolean loadDefault) {
		this.loadDefault = loadDefault;
	}
}
