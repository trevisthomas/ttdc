package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

@Deprecated
public class CreatePostCommand extends Command<PostCommandResult>{
	private PostFormData data;
	//TODO if this works without implementing serializable, remove it from the commands
	public CreatePostCommand(){}
	
	public CreatePostCommand(PostFormData data){
		this.data = data;
	}

	public PostFormData getData() {
		return data;
	}
}
