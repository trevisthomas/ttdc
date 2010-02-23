package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;

public class PostCommandResult implements CommandResult{
	private GPost post;
	public PostCommandResult(){}
	public PostCommandResult(GPost post){
		
		this.post = post;
	}
	public GPost getPost() {
		return post;
	}
}
