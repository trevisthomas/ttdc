package org.ttdc.gwt.shared.commands.results;

import java.util.ArrayList;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;

public class PostListCommandResult implements CommandResult{
	public PostListCommandResult(){}
	private ArrayList<GPost> posts;

	public PostListCommandResult(ArrayList<GPost> posts){
		this.posts = posts;
	}
	
	public ArrayList<GPost> getPosts() {
		return posts;
	}
}
