package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.nugets.PostBiz;
import org.ttdc.gwt.shared.commands.CreatePostCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.persistence.objects.Post;

@Deprecated
public class CreatePostCommandExecutor extends CommandExecutor<PostCommandResult>{
	@Override
	protected CommandResult execute() {
		CreatePostCommand command = (CreatePostCommand)getCommand();
		Post post;
		try{
			post = createPost(command.getData());
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		GPost gPost = GenericBeanConverter.convertPost(post);
		return new PostCommandResult(gPost); 
	}

	private Post createPost(PostFormData data) throws Exception {
		return PostBiz.createPost(getPerson(),data);
		
	}
}
