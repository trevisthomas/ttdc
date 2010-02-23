package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.server.command.executors.UserObjectTemplateCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.UserObjectTemplateCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class UserObjectTemplateCommandTest {
	@Test
	public void testLoad(){
		UserObjectTemplateCommand cmd = new UserObjectTemplateCommand();
		cmd.setTemplateId(Helpers.userObjectTemplateFlickr);
		cmd.setAction(ActionType.READ);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof UserObjectTemplateCommandExecutor);
		GenericCommandResult<GUserObjectTemplate> result = (GenericCommandResult<GUserObjectTemplate>)cmdexec.executeCommand();
		
		assertEquals("http://www.flickr.com", result.getObject().getValue());
		
	}
	//TODO test there rest.
	
}
