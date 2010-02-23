package org.ttdc.gwt.client.presenters.post;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.*;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryNotification;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class SearchWithinTaggedSubsetPresenter extends BasePresenter<SearchWithinTaggedSubsetPresenter.View> {
	private List<String> tagIdList = null;
	public interface View extends BaseView {
		HasClickHandlers getSearchButton();
		HasClickHandlers getBrowseButton();
		HasValue<String> getPhraseField();
		void showBrowseButton(boolean show);
	}
	
	@Inject
	protected SearchWithinTaggedSubsetPresenter(Injector injector) {
		super(injector, injector.getSearchWithinTaggedSubsetView());
		
		view.getPhraseField().setValue("Search within results");
		
		view.getSearchButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireSearchEvent();
			}
		});
		
		view.getBrowseButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setSearchPhrase("");
				fireSearchEvent();
			}
		});
	}
	
	private void fireSearchEvent(){
		String phrase = getSearchPhrase();
		if(tagIdList == null || tagIdList.size() == 0)
			throw new RuntimeException("SearchWithinTaggedSubsetPresenter is not being properly used.");
		
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_TAG_RESULTS);
		if(phrase.trim().length() > 0)
			token.setParameter(SEARCH_PHRASE_KEY, phrase);
		for(String tagId : tagIdList){
			token.addParameter(SEARCH_TAG_ID_KEY, tagId);
		}
		EventBus.getInstance().fireEvent(new HistoryNotification(token.toString()));
	}
	
	public void setSearchPhrase(String phrase){
		view.getPhraseField().setValue(phrase);
	}
	
	public String getSearchPhrase(){
		return view.getPhraseField().getValue().toString();
	}
	
	public List<String> getTagIdList() {
		return tagIdList;
	}

	public void setTagIdList(List<String> tagIdList) {
		this.tagIdList = tagIdList;
	}

	public void showBrowseButton(){
		view.showBrowseButton(true);
	}
}
