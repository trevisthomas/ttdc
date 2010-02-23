package org.ttdc.gwt.server.command;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.constants.UserObjectTemplateConstants;
import org.ttdc.gwt.server.command.executors.UserObjectTemplateListCommandExecutor;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand.UserObjectTemplateListAction;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

public class UserObjectTemplateListCommandTest {
	@Test
	public void testAvailableForPerson(){
		UserObjectTemplateListCommand cmd = new UserObjectTemplateListCommand();
		cmd.setAction(UserObjectTemplateListAction.GET_AVAILABLE_FOR_USER);
		cmd.setTemplateType(UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof UserObjectTemplateListCommandExecutor);
		GenericListCommandResult<GUserObjectTemplate> result = (GenericListCommandResult<GUserObjectTemplate>)cmdexec.executeCommand();
		
		assertTrue(result.getList().size() > 0); //Not a good test
	}
	
	@Test
	public void testAllOfType(){
		UserObjectTemplateListCommand cmd = new UserObjectTemplateListCommand();
		cmd.setAction(UserObjectTemplateListAction.GET_ALL_OF_TYPE);
		cmd.setTemplateType(UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof UserObjectTemplateListCommandExecutor);
		GenericListCommandResult<GUserObjectTemplate> result = (GenericListCommandResult<GUserObjectTemplate>)cmdexec.executeCommand();
		
		assertTrue(result.getList().size() > 0); //Not a good test
	}
}
