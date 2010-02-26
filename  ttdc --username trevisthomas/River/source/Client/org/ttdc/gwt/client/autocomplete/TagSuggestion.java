package org.ttdc.gwt.client.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;


import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class TagSuggestion implements IsSerializable, Suggestion  {
	private List<SuggestionListener> listeners = new ArrayList<SuggestionListener>();
    private String s;
    private GTag tag;
    private GPost post;
    
	// Required for IsSerializable to work
    public TagSuggestion() {
    }
    
    /*
    // Convenience method for creation of a suggestion
    public TagSuggestion(String s) {
       this.s = s;
    }
    */

    public String getDisplayString() {
        return s;
    }

    @Override
    public String getReplacementString() {
    	//Trevis, this method is called when a user chooes this tag.  Come up 
    	//with a cute way to make a call back or something so that you can capture this object
    	//when the user chooses it. That'll allow me to have the tagid when they select a tag.
    	
    	notifyListeners();
    	return getReplacementStringValue();	
    }
    
    public String getReplacementStringValue(){
    	if(tag != null)
    		return tag.getValue();
    	else if(post != null)
    		return post.getTitle();
    	else
    		throw new RuntimeException("Suggestion has no associated bean.");
    }
    
    public void addSuggestionListener(SuggestionListener listener){
    	listeners.add(listener);
    }
    public boolean removeSuggestionListener(SuggestionListener listener){
    	return listeners.remove(listener);
    }
    private void notifyListeners(){
    	for(SuggestionListener listener : listeners)
    		listener.onSuggestion(this);
    }

	public GTag getTag() {
		return tag;
	}

	public TagSuggestion(GTag tag, String displayValue) {
		this.tag = tag;
		this.s = displayValue;
	}
	
	public TagSuggestion(GPost post, String displayValue) {
		this.post = post;
		this.s = displayValue;
	}
	
	public GPost getPost() {
		return post;
	}

	public void setPost(GPost post) {
		this.post = post;
	}

}
