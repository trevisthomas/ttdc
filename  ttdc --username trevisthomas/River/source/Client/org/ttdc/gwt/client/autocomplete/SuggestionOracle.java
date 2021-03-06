package org.ttdc.gwt.client.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.search.DefaultMessageTextBox;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SuggestionOracle extends SuggestOracle implements SuggestionListener {
	private Injector injector;
	private SuggestionObject currentTagSuggestion = null;
	private TagSuggestionCommandMode commandMode;
	private List<String> excludeTagIdList = new ArrayList<String>();
	private List<String> unionTagIdList = new ArrayList<String>();
	private final static int SERVER_DELAY = 100;
	private TagSuggestionCommandTimer tagCommandTimer;
	private SuggestBox suggestBox;
	

	@Inject
	public SuggestionOracle(Injector injector){
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

	@Override
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
    
	public void killdashnine(){
		if(suggestBox != null){
			suggestBox.hideSuggestionList();
		}
		if(tagCommandTimer != null){
    		tagCommandTimer.cancel();
    	}
	}
	
	public void clear(){
		currentTagSuggestion = null;
		suggestBox.setText("");
	}
    
    public void onSuggestion(SuggestionObject tagSuggestion) {
    	currentTagSuggestion = tagSuggestion;
		//Window.alert(tagSuggestion.getDisplayString());
    	
	}
    
    public SuggestBox createSuggestBoxForTopics() {
    	setCommandMode(TagSuggestionCommandMode.TOPIC_POSTS);
    	suggestBox = new MySuggestBox(this);
    	return suggestBox;
	}
    
    public SuggestBox createSuggestBoxForPostSearch(){
    	setCommandMode(TagSuggestionCommandMode.SEARCH_POSTS);
    	suggestBox = new MySuggestBox(this, false);
    	return suggestBox;
    }
    
    public SuggestBox createSuggestBoxForSearch(List<String> currentTagIdList){
    	setCommandMode(TagSuggestionCommandMode.SEARCH);
    	suggestBox = new MySuggestBox(this);
    	unionTagIdList.addAll(currentTagIdList);
    	
    	suggestBox.getTextBox().addFocusHandler(new FocusHandler(){
    		public void onFocus(FocusEvent event) {
    			suggestBox.showSuggestionList();
    		}
    	});
    	
    	return suggestBox;
    }
    
    public SuggestBox createSuggestBoxForPostView(){
    	return createSuggestBoxForPostView(new ArrayList<String>());
    }
    public SuggestBox createSuggestBoxForPostView(List<String> currentTagIdList){
    	setCommandMode(TagSuggestionCommandMode.VIEW);
    	suggestBox = new MySuggestBox(this);
    	excludeTagIdList.addAll(currentTagIdList);
    	return suggestBox;
    }
    
    public SuggestBox createSuggestBoxPostTitle(){
    	setCommandMode(TagSuggestionCommandMode.CREATE);
    	suggestBox = new MySuggestBox(this);
    	return suggestBox;
    }
	public TagSuggestionCommandMode getCommandMode() {
		return commandMode;
	}
	public void setCommandMode(TagSuggestionCommandMode commandMode) {
		this.commandMode = commandMode;
	}
	public SuggestionObject getCurrentSuggestion() {
//		if(currentTagSuggestion == null || currentTagSuggestion.isCreateNew())
//			return null;
//		else
//			return currentTagSuggestion;
		return currentTagSuggestion;
	}
	
	
	/**
	 * 
	 * This is a call back which bridges the OracleSuggestion's async mechanism and the RPC async mechanism
	 *
	 */
	private class AutocompleteCallback extends CommandResultCallback<TagSuggestionCommandResult> {
        private SuggestOracle.Request oracleRequest;
        private SuggestOracle.Callback oracleCallback;
        private SuggestionOracle tagSuggestionOracle;
        
        public AutocompleteCallback(SuggestionOracle oracle, SuggestOracle.Request oracleRequest, SuggestOracle.Callback oracleCallback){
        	this.oracleRequest = oracleRequest;
        	this.oracleCallback = oracleCallback;
        	this.tagSuggestionOracle = oracle;
        }

        public void onSuccess(TagSuggestionCommandResult result) {
        	if(SuggestionOracle.this.activeAutocompleteCallback != this){
        		//Discard
        		//A newer request was activated
        	}
        	SuggestOracle.Response resp = result.getResponse();
        		if(resp.getSuggestions() != null){
        			tagSuggestionOracle.currentTagSuggestion = null;
        			for(Suggestion suggestion : resp.getSuggestions()){
        				SuggestionObject tagSuggestion = (SuggestionObject) suggestion;
        				if(tagSuggestionOracle.currentTagSuggestion == null){
        					//tagSuggestionOracle.suggestBox.getTextBox().setText(tagSuggestion.getReplacementStringValue());
        					tagSuggestionOracle.onSuggestion(tagSuggestion);
        				}
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
	private AutocompleteCallback activeAutocompleteCallback = null;
    private class TagSuggestionCommandTimer extends Timer{
		private TagSuggestionCommand command;
		private AutocompleteCallback callback;
		public TagSuggestionCommandTimer(TagSuggestionCommand command, AutocompleteCallback callback){
			this.command = command;
			this.callback = callback; 
		}
		@Override
		public void run() {
			activeAutocompleteCallback=callback;
			injector.getService().execute(command, callback);
		}
	}
    
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
}
