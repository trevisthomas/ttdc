package org.ttdc.gwt.server.command;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.command.executors.CreatePostCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventListCommandExecutor;
import org.ttdc.gwt.shared.commands.CreatePostCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

@Deprecated
public class CreatePostCommandTest extends CommandExecuteTestBase {
	@Test 
	public void createReplyPostTest(){
//		PostCrudCommand form = FormObjectMother.createNewReply();
//		
//		Command cmd = new CreatePostCommand(form);
//		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
//		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CreatePostCommandExecutor);
//		try{
//			CommandResult result = cmdexec.executeCommand();
//			assertPostCreatedForForm(result,form);
//		}
//		catch(RuntimeException e){
//			fail(e.getMessage());
//		}
	}
	
	private void assertPostCreatedForForm(CommandResult result, PostFormData form){
		assertNotNull("You didnt even get a result. sorry",result);
		PostCommandResult postResult = (PostCommandResult)result;
		GPost gPost = postResult.getPost();
		assertTrue("No entry in new post",gPost.getEntries().size() > 0);
		GEntry gEntry = gPost.getEntries().get(0);
		assertTrue("Entry doesn't match expected",gEntry.getBody().equals(form.getBody()));

		//if the post is not root, check the path.  (You need to fix post parents, and add new methods)
		/*
		if(!gPost.isRootPost())
			assertTrue("Post Path value is invalid", verifyPostPath(gPost) );
			*/
	}
}
