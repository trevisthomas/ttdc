package org.ttdc.gwt.server.command;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.executors.ServerEventListCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventOpenConnectionCommandExecutor;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.ServerEventOpenConnectionCommand;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

@SuppressWarnings("unchecked")
public class CommandExecuteTest {
	
	@Test 
	public void serverEventOpenConnectionTest(){
		ServerEventOpenConnectionCommand cmd = new ServerEventOpenConnectionCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof ServerEventOpenConnectionCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		assertTrue("Result is not the proper class instance", result instanceof ServerEventCommandResult);
		assertNotNull("", ((ServerEventCommandResult)result).getConnectionId());
	}
	
	
	@Test 
	public void serverEventListConnectionTest(){
		Command cmd = new ServerEventOpenConnectionCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		CommandResult result = cmdexec.executeCommand();
		String connectionId = ((ServerEventCommandResult)result).getConnectionId();
		
		
		cmd = new ServerEventListCommand(connectionId);
		cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof ServerEventListCommandExecutor);
		result = cmdexec.executeCommand();
		assertTrue("Result is not the proper class instance", result instanceof ServerEventCommandResult);
		assertNotNull("Event list is null", ((ServerEventCommandResult)result).getEvents());
	}
	
	@Test 
	public void serverEventListConnectionBadConnectionIdTest(){
		Command cmd = new ServerEventListCommand("bad id");
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof ServerEventListCommandExecutor);
		try{
			CommandResult result = cmdexec.executeCommand();
			fail("This test should have thrown an exception, but didnt");	
		}
		catch(RuntimeException e){
		}
	}
}
