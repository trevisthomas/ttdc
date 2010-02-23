package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.server.command.executors.StyleCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.StyleCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class StyleCommandTest {
	// - smmoothness
	@Test
	public void testLoad(){
		StyleCommand cmd = new StyleCommand();
		cmd.setStyleId("D10516F2-82BB-466C-A039-3B357004387F");
		cmd.setAction(ActionType.READ);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof StyleCommandExecutor);
		GenericCommandResult<GStyle> result = (GenericCommandResult<GStyle>)cmdexec.executeCommand();
		
		assertEquals("ui-smoothness.css", result.getObject().getCss());
		
	}
}
