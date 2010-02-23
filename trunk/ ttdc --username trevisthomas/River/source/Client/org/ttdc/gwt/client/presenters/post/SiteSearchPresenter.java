package org.ttdc.gwt.client.presenters.post;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.*;

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
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class SiteSearchPresenter extends BasePresenter<SiteSearchPresenter.View> {
	private String threadId;
	private String rootId;
	private SearchMode mode = SearchMode.SITE;
	private enum SearchMode {ROOT,THREAD,SITE}
	
	public interface View extends BaseView {
		HasClickHandlers getSearchButton();
		HasText searchButtonText();
		HasValue<String> getPhraseField();
	}
	
	@Inject
	protected SiteSearchPresenter(Injector injector) {
		super(injector, injector.getSiteSearchView());
		
		view.getPhraseField().setValue("What chu look'in foe foo?");
		
		view.getSearchButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireSearchEvent();
			}
		});
		limitToSite();
	}
	
	public void setSearchPhrase(String phrase){
		view.getPhraseField().setValue(phrase);
	}
	
	public String getSearchPhrase(){
		return view.getPhraseField().getValue().toString();
	}
	
	public void limitToThread(String threadId){
		this.threadId = threadId;
		if(StringUtil.notEmpty(threadId)){
			mode = SearchMode.THREAD;
			view.searchButtonText().setText("Search Conversation");
			//view.getSearchButton().
			//Set search button text.
		}
	}
	
	public void limitToTopic(String rootId){
		this.rootId = rootId;
		if(StringUtil.notEmpty(rootId)){
			mode = SearchMode.ROOT;
			view.searchButtonText().setText("Search Topic");
			//Set search button text.
		}
	}
	
	//Probably unnecessary but, added for completion sake
	public void limitToSite(){
		threadId = null;
		rootId = null;
		//Set search button text.
	}
	
	private HistoryToken buildHistoryToken(){
		String phrase = getSearchPhrase();
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		
		switch(mode){
			case SITE:
				token.setParameter(SEARCH_MODE_KEY, SEARCH_MODE_VALUE_TOPICS);
				break;
			case ROOT:
				token.addParameter(HistoryConstants.ROOT_ID_KEY, rootId);
				token.setParameter(SEARCH_MODE_KEY, SEARCH_MODE_IN_ROOT);
				break;
			case THREAD:
				token.addParameter(HistoryConstants.THREAD_ID_KEY, threadId);
				token.setParameter(SEARCH_MODE_KEY, SEARCH_MODE_IN_THREAD);
				break;
		}
		
		token.setParameter(SEARCH_PHRASE_KEY, phrase);
		return token;
	}
	
	private void fireSearchEvent(){
		HistoryToken token = buildHistoryToken();
		EventBus.getInstance().fireEvent(new HistoryNotification(token.toString()));
	}
}
