package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;

public class TagCommandResult implements CommandResult{
	private List<GTag> tagList;
	private GTag tag;
	
	public List<GTag> getTagList() {
		return tagList;
	}
	public void setTagList(List<GTag> tagList) {
		this.tagList = tagList;
	}
	public GTag getTag() {
		return tag;
	}
	public void setTag(GTag tag) {
		this.tag = tag;
	}
}
