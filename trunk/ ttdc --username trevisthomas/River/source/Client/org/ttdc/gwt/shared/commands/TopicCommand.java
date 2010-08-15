package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;

import org.ttdc.gwt.shared.commands.results.TopicCommandResult;


public class TopicCommand extends Command<TopicCommandResult>  {
	private String rootId;
	private String postId;//Post ID to find. (when this is used the page number should be determined
	private String conversationId;
	private int pageNumber = -1;
	private TopicCommandType type;
	private boolean sortByDate = true; //By date sorting is not used for all types.  First use of this is in THREAD_SUMMARY
	
	public boolean isSortByDate() {
		return sortByDate;
	}
	public void setSortByDate(boolean sortByDate) {
		this.sortByDate = sortByDate;
	}
	public String getRootId() {
		return rootId;
	}
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public TopicCommandType getType() {
		return type;
	}
	public void setType(TopicCommandType type) {
		this.type = type;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
}
