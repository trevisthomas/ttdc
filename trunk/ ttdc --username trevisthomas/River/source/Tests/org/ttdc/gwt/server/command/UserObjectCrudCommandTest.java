package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.server.command.executors.UserObjectCrudCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class UserObjectCrudCommandTest {
	@Test
	public void testLoadCmd(){
		
		UserObjectCrudCommand cmd = new UserObjectCrudCommand();
		cmd.setAction(ActionType.READ);
		cmd.setObjectId("B49AEA21-E33E-4869-8C33-00173288D29B");
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof UserObjectCrudCommandExecutor);
		GenericCommandResult<GUserObject> result = (GenericCommandResult<GUserObject>)cmdexec.executeCommand();
		
		assertEquals("http://www.facebook.com/dawn.crain", result.getObject().getUrl());
		
	}
	//TODO test there rest.
		
}
