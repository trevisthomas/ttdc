package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;

public class PersonListCommandResult implements CommandResult {
	private List<GPerson> personList;
	private PaginatedList<GPerson> results;
	
	public PersonListCommandResult() {}
	
	public PersonListCommandResult(List<GPerson> personList){
		this.personList = personList;
	}
	
	public PersonListCommandResult(PaginatedList<GPerson> results){
		this.results = results;
	}

	public List<GPerson> getPersonList() {
		return personList;
	}

	public PaginatedList<GPerson> getResults() {
		return results;
	}

}
