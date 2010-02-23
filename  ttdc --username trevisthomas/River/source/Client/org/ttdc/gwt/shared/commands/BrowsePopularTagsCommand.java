package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.SortOrder;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BrowsePopularTagsCommand extends Command<SearchTagsCommandResult> implements IsSerializable{
	private SortOrder sortOrder = SortOrder.POPULARITY;
	private int maxTags = 50;
	
	public BrowsePopularTagsCommand(){}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getMaxTags() {
		return maxTags;
	}

	public void setMaxTags(int maxTags) {
		this.maxTags = maxTags;
	};
	
	public boolean isSortAlphabetical(){
		return sortOrder == SortOrder.ALPHABETICAL;
	}
	
}
