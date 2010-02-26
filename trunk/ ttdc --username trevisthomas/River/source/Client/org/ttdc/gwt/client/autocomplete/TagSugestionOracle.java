package org.ttdc.gwt.client.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.inject.Inject;

public class TagSugestionOracle extends SuggestOracle implements SuggestionListener {
	private Injector injector;
	private TagSuggestion currentTagSuggestion = null;
	private TagSuggestionCommandMode commandMode;
	private List<String> excludeTagIdList = new ArrayList<String>();
	private List<String> unionTagIdList = new ArrayList<String>();
	private final static int SERVER_DELAY = 100;
	private TagSuggestionCommandTimer tagCommandTimer;
	
	public List<String> getExcludeTagIdList() {
		return excludeTagIdList;
	}
	public void setExcludeTagIdList(List<String> excludeTagIdList) {
		this.excludeTagIdList = excludeTagIdList;
	}
	public List<String> getUnionTagIdList() {
		return unionTagIdList;
	}
	public void setUnionTagIdList(List<String> unionTagIdList) {
		this.unionTagIdList = unionTagIdList;
	}
	public boolean isDisplayStringHTML() {
        return true;
    }
	@Inject
	public TagSugestionOracle(Injector injector){
		this.injector = injector;
	}
	
	@Override
	public void requestDefaultSuggestions(Request oracleRequest, Callback oracleCallback) {
		TagSuggestionCommand command = new TagSuggestionCommand(commandMode, oracleRequest);
    	command.setExcludeTagIdList(excludeTagIdList);
    	command.setUnionTagIdList(unionTagIdList);
    	command.setLoadDefault(true);
    	
    	injector.getService().execute(command, new AutocompleteCallback(this, oracleRequest, oracleCallback));
	}

	public void requestSuggestions(SuggestOracle.Request oracleRequest, SuggestOracle.Callback oracleCallback) {
    	TagSuggestionCommand command = new TagSuggestionCommand(commandMode, oracleRequest);
    	command.setExcludeTagIdList(excludeTagIdList);
    	command.setUnionTagIdList(unionTagIdList);
    	
    	if(tagCommandTimer != null){
    		tagCommandTimer.cancel();
    	}
    	tagCommandTimer = new TagSuggestionCommandTimer(command, new AutocompleteCallback(this, oracleRequest, oracleCallback));
    	tagCommandTimer.schedule(SERVER_DELAY);
    }
    
    
    
    public void onSuggestion(TagSuggestion tagSuggestion) {
    	currentTagSuggestion = tagSuggestion;
		Window.alert(tagSuggestion.getDisplayString());
	}
    
    public SuggestBox createSuggestBoxForTopics() {
    	SuggestBox suggestBox;
    	setCommandMode(TagSuggestionCommandMode.TOPIC_POSTS);
    	suggestBox = new SuggestBox(this);
    	return suggestBox;
	}
    
    public SuggestBox createSuggestBoxForSearch(List<String> currentTagIdList){
    	final SuggestBox suggestBox;
    	setCommandMode(TagSuggestionCommandMode.SEARCH);
    	suggestBox = new SuggestBox(this);
    	unionTagIdList.addAll(currentTagIdList);
    	
    	suggestBox.getTextBox().addFocusHandler(new FocusHandler(){
    		public void onFocus(FocusEvent event) {
    			suggestBox.showSuggestionList();
    		}
    	});
    	
    	return suggestBox;
    }
    
    public SuggestBox createSuggestBoxForPostView(List<String> currentTagIdList){
    	SuggestBox suggestBox;
    	setCommandMode(TagSuggestionCommandMode.VIEW);
    	suggestBox = new SuggestBox(this);
    	excludeTagIdList.addAll(currentTagIdList);
    	return suggestBox;
    }
    public SuggestBox createSuggestBoxPostTitle(){
    	SuggestBox suggestBox;
    	setCommandMode(TagSuggestionCommandMode.CREATE);
    	suggestBox = new SuggestBox(this);
    	
    	return suggestBox;
    }
	public TagSuggestionCommandMode getCommandMode() {
		return commandMode;
	}
	public void setCommandMode(TagSuggestionCommandMode commandMode) {
		this.commandMode = commandMode;
	}
	public TagSuggestion getCurrentTagSuggestion() {
		return currentTagSuggestion;
	}
	
	/**
	 * 
	 * This is a call back which bridges the OracleSuggestion's async mechanism and the RPC async mechanism
	 *
	 */
	private static class AutocompleteCallback extends CommandResultCallback<TagSuggestionCommandResult> {
        private SuggestOracle.Request oracleRequest;
        private SuggestOracle.Callback oracleCallback;
        private TagSugestionOracle tagSuggestionOracle;
        
        public AutocompleteCallback(TagSugestionOracle oracle, SuggestOracle.Request oracleRequest, SuggestOracle.Callback oracleCallback){
        	this.oracleRequest = oracleRequest;
        	this.oracleCallback = oracleCallback;
        	this.tagSuggestionOracle = oracle;
        }

        public void onSuccess(TagSuggestionCommandResult result) {
        	SuggestOracle.Response resp = result.getResponse();
        		if(resp.getSuggestions() != null){
		        	for(Suggestion suggestion : resp.getSuggestions()){
		        		TagSuggestion tagSuggestion = (TagSuggestion) suggestion;
		        		tagSuggestion.addSuggestionListener(tagSuggestionOracle);
		        	}
		        	oracleCallback.onSuggestionsReady(oracleRequest,result.getResponse());
        		}
        		else{
        			//Suggestions came back null... server probably couldn't parse the request
        		}
        	}
        
        public void onFailure(Throwable error) {
        	oracleCallback.onSuggestionsReady(oracleRequest, new SuggestOracle.Response());
        	super.onFailure(error);
        }
    }
    
	/**
	 * When a user presses a key i create a timer to request info.  If the user presses 
	 * another key before the timer fires the command request is abandoned and a new timer is
	 * set with the new search phrase as a command.  This is so that typing quickly 
	 * wont pummel the server with a lot of useless requests for suggestions.
	 * 
	 */
    private class TagSuggestionCommandTimer extends Timer{
		private TagSuggestionCommand command;
		private AutocompleteCallback callback;
		public TagSuggestionCommandTimer(TagSuggestionCommand command, AutocompleteCallback callback){
			this.command = command;
			this.callback = callback; 
		}
		@Override
		public void run() {
			injector.getService().execute(command, callback);
		}
	}

	

}
