package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.server.command.executors.StyleListCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.StyleListCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

public class StyleListCommandTest {
	@Test
	public void testGetAll(){
		StyleListCommand cmd = new StyleListCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof StyleListCommandExecutor);
		GenericListCommandResult<GStyle> result = (GenericListCommandResult<GStyle>)cmdexec.executeCommand();
		
		assertTrue(result.getList().size() > 0); //Not a good test
	}
}
