package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetPersonDetailsCommand extends Command<PersonCommandResult> implements IsSerializable{
	private String personId;
	public GetPersonDetailsCommand(){};
	
	public GetPersonDetailsCommand(String personId){
		this.personId = personId;
	}
	public String getPersonId() {
		return personId;
	}
}
