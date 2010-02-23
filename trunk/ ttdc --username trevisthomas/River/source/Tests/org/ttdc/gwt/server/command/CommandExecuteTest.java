package org.ttdc.gwt.server.command;


import static org.junit.Assert.*;

import org.junit.Test;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecuteTestBase;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.command.executors.LatestFlatCommandExecutor;
import org.ttdc.gwt.server.command.executors.LatestHierarchyCommandExecutor;
import org.ttdc.gwt.server.command.executors.PersonDetailsCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventListCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventOpenConnectionCommandExecutor;
import org.ttdc.gwt.shared.commands.GetLatestFlatCommand;
import org.ttdc.gwt.shared.commands.GetLatestHierarchyCommand;
import org.ttdc.gwt.shared.commands.GetPersonDetailsCommand;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.ServerEventOpenConnectionCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

@SuppressWarnings("unchecked")
public class CommandExecuteTest extends CommandExecuteTestBase{
	
	@Test
	public void latestFlatTest(){
		GetLatestFlatCommand cmd = new GetLatestFlatCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof LatestFlatCommandExecutor);
		assertTrue("Wrong user",!cmdexec.getPerson().isAnonymous());
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		assertNotNull(((PostListCommandResult)result).getPosts().get(0).getEntry());
		
	}
	@Test
	public void latestHierarchyTest(){
		GetLatestHierarchyCommand cmd = new GetLatestHierarchyCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof LatestHierarchyCommandExecutor);
		
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
	}
	@Test
	public void personDetailTest(){
		GetPersonDetailsCommand cmd = new GetPersonDetailsCommand("50E7F601-71FD-40BD-9517-9699DDA611D6");
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PersonDetailsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		assertEquals("50E7F601-71FD-40BD-9517-9699DDA611D6", ((PersonCommandResult)result).getPerson().getPersonId());
	}
	
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
