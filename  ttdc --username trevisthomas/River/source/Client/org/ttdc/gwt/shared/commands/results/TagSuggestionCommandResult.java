package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.services.CommandResult;

import com.google.gwt.user.client.ui.SuggestOracle;

public class TagSuggestionCommandResult implements CommandResult{
	private SuggestOracle.Response response;

	public TagSuggestionCommandResult(){}
	
	public TagSuggestionCommandResult(SuggestOracle.Response response){
		this.response = response;
	}
	
	public SuggestOracle.Response getResponse() {
		return response;
	}

	
	
}
