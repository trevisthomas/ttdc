package org.ttdc.gwt.client.presenters.tag;

import java.util.List;

import org.ttdc.gwt.client.autocomplete.SuggestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * 
 * View interface shared by PostTagListPresenter and SearchTagListPresenter
 *
 */
public interface TagListPresenterView extends BaseView{
	
	/**
	 * The list of widget presenters that represent the tags
	 * 
	 * Remember that the tagIdList is being passed in for auto complete
	 */
	public void showPresenters(List<String> tagIdList, List<BasePresenter<?>> presenters);
	
	/**
	 * The click handler for adding a tag
	 * @return
	 */
	HasClickHandlers getAddClickHandler();
	
	/**
	 * The TagSuggestion currently chosen by the user. (should return a class having an existing tag 
	 * (with guid) or a new tag value). 
	 * 
	 * NOTE: I'm not sure yet what to do if the user entered nothing. 
	 * That might be a good condition for this method to return null.
	 * 
	 * @return
	 */
	SuggestionObject getTagSuggestion();
	
	void setTagSuggestionOracle(SuggestionOracle oracle);
	
	/**
	 * List of tag id's to exclude.  (for tagging posts)
	 * @param excludeTagIdList
	 */
	void setExcludeTagIdList(final List<String> excludeTagIdList);
	
	/**
	 * List of tag id's that the result list must share a relationship with
	 * (for browsing)
	 * @param unionTagIdList
	 */
	void setUnionTagIdList(final List<String> unionTagIdList);
	
}
