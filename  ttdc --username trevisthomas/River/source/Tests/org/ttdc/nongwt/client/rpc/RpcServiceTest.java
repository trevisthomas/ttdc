package org.ttdc.nongwt.client.rpc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.GetLatestFlatCommand;
import org.ttdc.gwt.shared.commands.GetLatestHierarchyCommand;
import org.ttdc.gwt.shared.commands.GetPersonDetailsCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;

public class RpcServiceTest {
	RpcServiceAsync service;
	GetLatestFlatCommand getLatestFlatCommand;
	CommandResultCallback<PostListCommandResult> gotLatestFlat;
	GetLatestHierarchyCommand getLatestHierarchyCommand;
	CommandResultCallback<PostListCommandResult> gotLatestHierarchy;
	PostListCommandResult postListResult;
	PersonCommandResult personResult;
	
	
	@Before
	public void setup(){
		//service = createMock(RpcServiceAsync.class);
		ArrayList<GPost> list = new ArrayList<GPost>();
		list.add(new GPost());
		postListResult = new PostListCommandResult(list);
		
		personResult = new PersonCommandResult(new GPerson());
		
		getLatestFlatCommand = new GetLatestFlatCommand();
		
		gotLatestFlat = new CommandResultCallback<PostListCommandResult>(){
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}
			public void onSuccess(PostListCommandResult result) {
				assertTrue(result.getPosts().size()==1);
			}
		};
		
		getLatestHierarchyCommand = new GetLatestHierarchyCommand();
		
		gotLatestHierarchy = new CommandResultCallback<PostListCommandResult>(){
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}
			public void onSuccess(PostListCommandResult result) {
				assertTrue(result.getPosts().size()==1);
			}
		};
		
	}
	
	@Test
	public void testDispatcherFrontPageFlat(){
		service = new MockRpcServiceAsync(postListResult);
		
		service.execute(getLatestFlatCommand, gotLatestFlat);
	}
	
	@Test
	public void testDispatcherFrontPageHierarchy(){
		service = new MockRpcServiceAsync(postListResult);
		service.execute(getLatestHierarchyCommand, gotLatestHierarchy);
	}
	
	@Test
	public void testBatchRpc(){
		service = new MockRpcServiceAsync(postListResult);
		
		BatchCommandTool batch = new BatchCommandTool();
		batch.add(getLatestFlatCommand,gotLatestFlat);
		batch.add(getLatestHierarchyCommand, gotLatestHierarchy);
		service.execute(batch.getActionList(), batch);
	}
	
	@Test 
	public void testGetPersonDetails(){
		GPerson person = new GPerson();
		person.setPersonId("50E7F601-71FD-40BD-9517-9699DDA611D6");
		personResult = new PersonCommandResult(person);
		service = new MockRpcServiceAsync(personResult);
		
		GetPersonDetailsCommand getPersonDetails = new GetPersonDetailsCommand(person.getPersonId());

		
		service.execute(getPersonDetails, new CommandResultCallback<PersonCommandResult>(){
			public void onSuccess(PersonCommandResult result) {
				assertEquals("50E7F601-71FD-40BD-9517-9699DDA611D6", result.getPerson().getPersonId());
			}
		});
	}
	
}
