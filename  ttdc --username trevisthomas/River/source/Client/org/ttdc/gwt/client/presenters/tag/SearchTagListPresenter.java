package org.ttdc.gwt.client.presenters.tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.Inject;

public class SearchTagListPresenter extends BasePresenter<TagListPresenterView>{
	private final Map<String, TagRemovePresenter> tagRemovePresenterMap;
	@Inject
	public SearchTagListPresenter(Injector injector) {
		super(injector, injector.getCommonTagListView());
		view.setTagSuggestionOracle(injector.getTagSugestionOracle());
		tagRemovePresenterMap = new LinkedHashMap<String, TagRemovePresenter>();
	}

	/**
	 * Configure TagListPresenter presenter for browse mode.   Remember in browse mode 
	 * you only have removable tags and the add box.
	 */
	public void setTagIdList(final List<GTag> tagList){
		//editMode = true; //Always edit mode for browse mode.
		final List<String> tagIds = new ArrayList<String>();
		for(GTag tag : tagList){
			tagIds.add(tag.getTagId());
		}
		
		for(GTag tag : tagList){
			TagRemovePresenter removeLinkPresenter = injector.getTagRemovePresenter();
			removeLinkPresenter.setTag(tag);
			
			RemoveTagInBrowseModeClickHandler handler = new RemoveTagInBrowseModeClickHandler(tagIds, tag.getTagId());
			removeLinkPresenter.addClickHandler(handler);
			
			tagRemovePresenterMap.put(tag.getTagId(), removeLinkPresenter);
			
		}	
		
		
		notifyViewOfNewPresenterList();
		
		//Configure the add button for adding a tag to the browse list
		view.getAddClickHandler().addClickHandler(new AddTagInBrowseModeClickHandler(tagIds));
	}
	
	public List<String> getTagIdList(){
		List<String> tagIds = new ArrayList<String>();
		tagIds.addAll(tagRemovePresenterMap.keySet());
		return tagIds;
	} 
	
	/*
	 * For testing
	 */
	Map<String, TagRemovePresenter> getTagRemovePresenterMap() {
		return tagRemovePresenterMap;
	}
	
	private void notifyViewOfNewPresenterList() {
		view.showPresenters(getTagIdList(), new ArrayList<BasePresenter<?>>(tagRemovePresenterMap.values()));
	}
	
	private static class RemoveTagInBrowseModeClickHandler implements ClickHandler{
		private final List<String> tagIdList;
		private final String tagIdToRemove;
		RemoveTagInBrowseModeClickHandler(List<String> tagIdList, String tagIdToRemove){
			this.tagIdList = tagIdList; 
			this.tagIdToRemove = tagIdToRemove;
		}
	
		public void onClick(ClickEvent event) {
			HistoryToken token = new HistoryToken();
			token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_TAG_RESULTS);
			for(String tagId : tagIdList){
				if(!tagId.equals(tagIdToRemove))
					token.addParameter(HistoryConstants.SEARCH_TAG_ID_KEY, tagId);
			}
			EventBus.getInstance().fireHistory(token);
		}
	}
	
	/**
	 * 
	 * ClickHandler class for handling the add button in Browse mode.
	 *
	 */
	private class AddTagInBrowseModeClickHandler implements ClickHandler{
		final private List<String> tagIds;
		public AddTagInBrowseModeClickHandler(List<String> tagIds) {
 			this.tagIds = tagIds;
		}
		public void onClick(ClickEvent event) {
			SuggestionObject tagSuggestion = view.getTagSuggestion();
			
			HistoryToken token = new HistoryToken();
			token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_TAG_RESULTS);
			for(String tagId : tagIds){
				token.addParameter(HistoryConstants.SEARCH_TAG_ID_KEY, tagId);
			}
			token.addParameter(HistoryConstants.SEARCH_TAG_ID_KEY, tagSuggestion.getTag().getTagId());
	
			EventBus.getInstance().fireHistory(token);
		}
	}
}
