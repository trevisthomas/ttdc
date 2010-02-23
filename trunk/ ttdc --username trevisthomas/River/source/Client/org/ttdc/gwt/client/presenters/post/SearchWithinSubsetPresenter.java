package org.ttdc.gwt.client.presenters.post;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_PHRASE_KEY;


import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryNotification;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class SearchWithinSubsetPresenter extends BasePresenter<SearchWithinSubsetPresenter.View> {
	private String rootId;
	private String threadId;
	
	public interface View extends BaseView {
		HasClickHandlers getSearchButton();
		HasValue<String> getPhraseField();
	}
	
	@Inject
	protected SearchWithinSubsetPresenter(Injector injector) {
		super(injector, injector.getSearchWithinSubsetView());
		
		view.getPhraseField().setValue("Not Set");
		
		view.getSearchButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireSearchEvent();
			}
		});
	}
	
	private void fireSearchEvent(){
		String phrase = getSearchPhrase();
		if(StringUtil.empty(rootId) && StringUtil.empty(threadId))
			throw new RuntimeException("SearchWithinSubsetPresenter is not being properly used. Missing id argument");
		
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		token.setParameter(SEARCH_PHRASE_KEY, phrase);
		
		if(StringUtil.notEmpty(rootId)){
			token.addParameter(HistoryConstants.ROOT_ID_KEY, rootId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_ROOT);
		}
		else if(StringUtil.notEmpty(threadId)){
			token.addParameter(HistoryConstants.THREAD_ID_KEY, threadId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_THREAD);
		}

		EventBus.getInstance().fireEvent(new HistoryNotification(token.toString()));
	}

	public void setSearchPhrase(String phrase){
		view.getPhraseField().setValue(phrase);
	}
	
	public String getSearchPhrase(){
		return view.getPhraseField().getValue().toString();
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
		if(StringUtil.notEmpty(rootId))
			view.getPhraseField().setValue("Search Within Topic");
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
		if(StringUtil.notEmpty(threadId))
			view.getPhraseField().setValue("Search Within Conversation");
	}
}
